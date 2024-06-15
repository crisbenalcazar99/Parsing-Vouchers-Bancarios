package net.security.data.microservicesocr.Filters;

import com.fasterxml.jackson.core.JsonParseException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import net.security.data.microservicesocr.Utils.GlobalFlags;
import net.security.data.microservicesocr.enums.MnemonicsEnum;
import net.security.data.microservicesocr.models.DTOS.ConsumosDTO;
import net.security.data.microservicesocr.models.Mappler;
import net.security.data.microservicesocr.models.DTOS.AuditoryDTO;
import net.security.data.microservicesocr.models.entities.AuditoryEntity;
import net.security.data.microservicesocr.models.entities.CatalogEntity;
import net.security.data.microservicesocr.models.entities.ConsumosEntity;
import net.security.data.microservicesocr.models.entities.UserEntity;
import net.security.data.microservicesocr.repository.AuditoryRepository;
import net.security.data.microservicesocr.repository.CatalogRepository;
import net.security.data.microservicesocr.repository.ConsumoRepository;
import net.security.data.microservicesocr.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;
import org.springframework.stereotype.Component;
import org.springframework.web.util.ContentCachingRequestWrapper;
import org.springframework.web.util.ContentCachingResponseWrapper;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class PostRequestFilter implements Filter {

    private static final Logger log = LoggerFactory.getLogger(PostRequestFilter.class);
    @Autowired
    private AuditoryDTO auditoryDTO;
    @Autowired
    private AuditoryRepository auditoryRepository;
    @Autowired
    private CatalogRepository catalogRepository;
    @Autowired
    private ConsumoRepository consumoRepository;
    @Autowired
    private ConsumosDTO consumosDTO;
    @Autowired
    private UserRepository userRepository;

    private static final String[] AUTH_WHITELIST = {
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            "/v3/api-docs/**",
            "/api/public/**",
            "/api/public/authenticate",
            "/actuator/*",
            "/swagger-ui/",
            // "/auth/**"
    };

    private final OrRequestMatcher requestMatcher;

    public PostRequestFilter() {
        List<RequestMatcher> requestMatcherList = Arrays.stream(AUTH_WHITELIST)
                .map(AntPathRequestMatcher::new)
                .collect(Collectors.toList());
        this.requestMatcher = new OrRequestMatcher(requestMatcherList);
    }

    @Override
    public void doFilter(ServletRequest servletRequest,
            ServletResponse servletResponse,
            FilterChain filterChain) throws IOException, ServletException, JsonParseException {
        ContentCachingRequestWrapper httpServletRequest = new ContentCachingRequestWrapper(
                (HttpServletRequest) servletRequest);
        ContentCachingResponseWrapper httpServletResponse = new ContentCachingResponseWrapper(
                (HttpServletResponse) servletResponse);
        ContentCachingResponseWrapper responseWrapper = new ContentCachingResponseWrapper(httpServletResponse);

        SecurityContext context = SecurityContextHolder.getContext();

        String requestBody;
        String responseBody;
        UserEntity userEntity;

        try {
            filterChain.doFilter(httpServletRequest, httpServletResponse);
        } finally {
            requestBody = Mappler.jsonToTextPlain(
                    new String(httpServletRequest.getContentAsByteArray(), httpServletRequest.getCharacterEncoding()));
            responseBody = new String(httpServletResponse.getContentAsByteArray(),
                    httpServletResponse.getCharacterEncoding());
            httpServletResponse.copyBodyToResponse();
        }

        if (!requestMatcher.matches(httpServletRequest)) {
            String endpoint = httpServletRequest.getRequestURI();
            if (auditoryDTO.getCatalogEntity() == null) {
                CatalogEntity catalogEntity = catalogRepository
                        .findCatalogEntityByMnemonic(MnemonicsEnum.ENDPOINT_NOT_FOUND.getValue())
                        .orElse(null);
                auditoryDTO.setCatalogEntity(catalogEntity);
            }

            if (endpoint.contains("api/v/verify/voucher/")) {
                endpoint = "/api/v/verify/voucher";
            }

            if (auditoryDTO.getUser() == null
                    && context.getAuthentication().getPrincipal().toString() != "anonymousUser") {
                context.getAuthentication().getPrincipal();
                userEntity = userRepository.findByUsername(context.getAuthentication().getPrincipal().toString())
                        .orElse(null);
            } else
                userEntity = null;


            AuditoryEntity auditoryEntity = AuditoryEntity.builder()
                    .stateRegister(true)
                    .endPoint(endpoint)
                    .method(httpServletRequest.getMethod())
                    .statusCode(String.valueOf(responseWrapper.getStatus()))
                    .catalogEntity(auditoryDTO.getCatalogEntity() != null ? auditoryDTO.getCatalogEntity() : null)
                    .user(userEntity) // agregar user del contexto
                    .direccionIP(httpServletRequest.getRemoteAddr())
                    .response(auditoryDTO.getResponse() == null ? responseBody : auditoryDTO.getResponse())
                    .request(auditoryDTO.getRequest() == null ? requestBody : auditoryDTO.getRequest())
                    .build();

            log.info("Se detecto un consumo dentro del Servicio de Google Cloud: {}", GlobalFlags.myFlag.get());
            try {
                if (GlobalFlags.myFlag.get()) {
                    ConsumosEntity consumosEntity = Mappler.convertAuditorytoConsumos(auditoryEntity);
                    consumosEntity.setAuditoryEntity(auditoryEntity);
                    consumosEntity.setVouchersEntity(consumosDTO.getVouchersEntity());
                    consumoRepository.save(consumosEntity);
                    log.info("Registro creado en la tabla de Consumos y Auditoria sin errores");
                    GlobalFlags.myFlag.remove();
                }else{
                    auditoryRepository.save(auditoryEntity);
                    log.info("Registro creado en la tabla de Auditoria sin errores");
                }

            } catch (DataIntegrityViolationException ex) {
                log.error(
                        "Se presento un error dentro de postRequestFilter. Relacionado a problemas en en voucher enviado por el cliente, {}",
                        ex.getMessage());
                CatalogEntity catalogEntity = catalogRepository
                        .findCatalogEntityByMnemonic(MnemonicsEnum.VOUCHER_SOURCE_ERROR.getValue())
                        .orElseThrow(
                                () -> new EntityNotFoundException(MnemonicsEnum.CATALOG_ENTITY_NOT_FOUND.getValue()));
                auditoryEntity.setStatusCode(String.valueOf(HttpStatus.CONFLICT.value()));
                auditoryEntity.setCatalogEntity(catalogEntity);
                auditoryRepository.save(auditoryEntity);
                log.info("Regitro de Error creado en la tabla de auditoria desde el PostRequestFilter{}", ex.getMessage());
                ex.getStackTrace();
            }
        }
        responseWrapper.copyBodyToResponse();
        auditoryDTO.cleanup();
        consumosDTO.cleanup();
    }

}
