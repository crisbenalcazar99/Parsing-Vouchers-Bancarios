package net.security.data.microservicesocr.Utils;

import lombok.Data;
import net.security.data.microservicesocr.enums.MnemonicsEnum;
import net.security.data.microservicesocr.models.entities.CatalogEntity;
import net.security.data.microservicesocr.repository.CatalogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

// @Component
@Data
@Component
public class DataBaseInitializer {
        // Inicialziacion de la base de datos de Catalaogos
        // Borrar este documento a posteriorir
        @Autowired
        private CatalogRepository catalogRepository;

        private List<CatalogEntity> catalogEntityList;

        public void catalogInitializer() {

                List<CatalogEntity> catalogEntitiesParents = new ArrayList<>();

                CatalogEntity cat = CatalogEntity.builder()
                                .stateRegister(true)
                                .description("estado de la cuenta del cliente")
                                .mnemonic(MnemonicsEnum.STATUS_CLIENT.getValue())
                                .build();
                CatalogEntity cat2 = catalogRepository.save(cat);
                if (cat2 != null) {
                        catalogEntitiesParents.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .description("La cuenta de usuario está habilitada")
                                                        .mnemonic(MnemonicsEnum.USER_IS_ACTIVATED.getValue())
                                                        .parentCatalog(cat2)
                                                        .build());
                        catalogEntitiesParents.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .description("La cuenta de usuario está deshabilitada")
                                                        .mnemonic(MnemonicsEnum.USER_IS_DISABLED.getValue())
                                                        .parentCatalog(cat2)
                                                        .build());
                        catalogEntitiesParents.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .description("El usuario fue deshabilitado")
                                                        .mnemonic(MnemonicsEnum.DELETE_USER.getValue())
                                                        .parentCatalog(cat2)
                                                        .build());

                        catalogRepository.saveAll(catalogEntitiesParents);
                }

                /**
                 * Catalogo de autenticacion
                 */

                CatalogEntity catAuth = CatalogEntity.builder()
                                .stateRegister(true)
                                .description("Estados del proceso de autenticación")
                                .mnemonic(MnemonicsEnum.AUTHENTICATION_PROCESS.getValue())
                                .build();

                CatalogEntity cat3 = catalogRepository.saveAndFlush(catAuth);
                if (cat3 != null) {
                        List<CatalogEntity> catalogEntitiesParents2 = new ArrayList<>();

                        catalogEntitiesParents2.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .description("Se requiere autenticación completa para acceder a este recurso")
                                                        .mnemonic(MnemonicsEnum.INVALID_TOKEN.getValue())
                                                        .parentCatalog(cat3)
                                                        .build());

                        catalogEntitiesParents2.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .idCatalog(22L)
                                                        .description("Usuario creado exitosamente")
                                                        .mnemonic(MnemonicsEnum.USER_CREATED_SUCCESSFULLY.getValue())
                                                        .parentCatalog(cat3)
                                                        .build());
                        catalogEntitiesParents2.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .description("Proceso de autenticación y generación de token existente")
                                                        .mnemonic(MnemonicsEnum.SUCCESSFUL_AUTHENTICATION.getValue())
                                                        .parentCatalog(cat3)
                                                        .build());
                        catalogEntitiesParents2.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .description("Error de Autenticacion: contrasena invalida")
                                                        .mnemonic(MnemonicsEnum.WRONG_PASSWORD.getValue())
                                                        .parentCatalog(cat3)
                                                        .build());
                        catalogEntitiesParents2.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .idCatalog(25L)
                                                        .description("Error de Autenticacion: Usuario no encontrado")
                                                        .mnemonic(MnemonicsEnum.USERNAME_NOT_FOUND.getValue())
                                                        .parentCatalog(cat3)
                                                        .build());

                        catalogRepository.saveAll(catalogEntitiesParents2);
                }

                /**
                 * Catalogo estado Documento
                 */

                CatalogEntity catDoc = CatalogEntity.builder()
                                .stateRegister(true)
                                .description("Estados relacionados con los procesos de escritura y lectura de la base de datos/archivos")
                                .mnemonic(MnemonicsEnum.SERVICES_OPERATION_STATES.getValue())
                                .build();

                CatalogEntity cat4 = catalogRepository.save(catDoc);
                if (cat4 != null) {
                        List<CatalogEntity> catalogEntitiesParents3 = new ArrayList<>();
                        // Agregar cada uno de los registros del catalogo

                        catalogEntitiesParents3.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .description("Consulta incorrecta. La entidad no existe en la base de datos")
                                                        .mnemonic(MnemonicsEnum.CATALOG_ENTITY_NOT_FOUND.getValue())
                                                        .parentCatalog(cat4)
                                                        .build());
                        catalogEntitiesParents3.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .description("Errores de violacion de integridad de datos en Base de Datos.")
                                                        .mnemonic(MnemonicsEnum.DATA_INTEGRITY_VIOLATION.getValue())
                                                        .parentCatalog(cat4)
                                                        .build());

                        catalogEntitiesParents3.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .description("Extracción exitosa de datos de comprobantes bancarios")
                                                        .mnemonic(MnemonicsEnum.SUCCESSFULLY_ENTITY_EXTRACTION
                                                                        .getValue())
                                                        .parentCatalog(cat4)
                                                        .build());
                        catalogEntitiesParents3.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .description("Archivo corrupto o no encontrado. No pudo ser procesado.")
                                                        .mnemonic(MnemonicsEnum.VOUCHER_SOURCE_ERROR.getValue())
                                                        .parentCatalog(cat4)
                                                        .build());
                        catalogEntitiesParents3.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .description("Campos incorrectos nombre/valor en el cuerpo de la solicitud")
                                                        .mnemonic(MnemonicsEnum.INCORRECT_FIELDS_IN_REQUEST.getValue())
                                                        .parentCatalog(cat4)
                                                        .build());
                        catalogEntitiesParents3.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .description("Proceso Exitoso")
                                                        .mnemonic(MnemonicsEnum.SUCCESSFULLY_PROCESS.getValue())
                                                        .parentCatalog(cat4)
                                                        .build());
                        catalogEntitiesParents3.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .description("La direccion de guardado de archivos en el server no fue encontrada")
                                                        .mnemonic(MnemonicsEnum.NOT_FOUND_PATH_SERVER.getValue())
                                                        .parentCatalog(cat4)
                                                        .build());
                        catalogRepository.saveAll(catalogEntitiesParents3);
                }

                /**
                 * Catalogo 3
                 */

                CatalogEntity catGoogleCloud = CatalogEntity.builder()
                                .stateRegister(true)
                                .description("Excepciones generadas en el procedimiento realizado por Google Cloud")
                                .mnemonic(MnemonicsEnum.EXCEPTION_RETURNED_GOOGLE_SERVICE.getValue())
                                .build();

                CatalogEntity cat5 = catalogRepository.save(catGoogleCloud);
                if (cat5 != null) {
                        List<CatalogEntity> catalogEntitiesParents4 = new ArrayList<>();
                        catalogEntitiesParents4.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .description("Excepción en tiempo de ejecución, generado en el servicio de Google. Normalmente se genera debido a credenciales incorrectas del servicio de Google.")
                                                        .mnemonic(MnemonicsEnum.STATUS_RUNTIME_EXCEPTION.getValue())
                                                        .parentCatalog(cat5)
                                                        .build());
                        catalogEntitiesParents4.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .description("Procesador inválido o deshabilitado. Por favor, verifica el estado del procesador en la consola de Google Cloud.")
                                                        .mnemonic(MnemonicsEnum.INVALID_PROCESSOR.getValue())
                                                        .parentCatalog(cat5)
                                                        .build());

                        catalogRepository.saveAll(catalogEntitiesParents4);
                }

                /**
                 * Catalogo 3
                 */

                CatalogEntity catDocSave = CatalogEntity.builder()
                                .stateRegister(true)
                                .description("Estados de archivos guardados")
                                .mnemonic(MnemonicsEnum.FILE_MANAGEMENT_ACTIONS.getValue())
                                .build();

                CatalogEntity cat6 = catalogRepository.save(catDocSave);
                if (cat5 != null) {
                        List<CatalogEntity> catalogEntitiesParents4 = new ArrayList<>();
                        catalogEntitiesParents4.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .description("Archivo guardado en el servidor y Url en base")
                                                        .mnemonic(MnemonicsEnum.SAVE_FILE_SERVER.getValue())
                                                        .parentCatalog(cat6)
                                                        .build());
                        catalogEntitiesParents4.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .description("Archivo guardado solo en base de datos")
                                                        .mnemonic(MnemonicsEnum.SAVE_FILE_DB.getValue())
                                                        .parentCatalog(cat6)
                                                        .build());
                        catalogEntitiesParents4.add(
                                        CatalogEntity.builder()
                                                        .stateRegister(true)
                                                        .description("Archivo no cuenta con proceso de guardado")
                                                        .mnemonic(MnemonicsEnum.NO_SAVE.getValue())
                                                        .parentCatalog(cat6)
                                                        .build());

                        catalogRepository.saveAll(catalogEntitiesParents4);
                }

        }

        public CatalogEntity getCatalogEntityByMnemonic(String mnemonic) {
                List<CatalogEntity> catalogEntities = this.getCatalogEntityList();
                return catalogEntities.stream()
                                .filter(catalogEntity -> catalogEntity.getMnemonic().equals(mnemonic))
                                .findFirst()
                                .orElse(null);
        }
}
