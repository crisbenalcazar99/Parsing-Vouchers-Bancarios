package net.security.data.microservicesocr.models.entities;
import java.util.Date;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@NoArgsConstructor
@AllArgsConstructor
@Entity
@Data
@Builder
@Table(name = "auditoria", schema = "public", catalog = "db_microservicesOCR")
public class AuditoryEntity {

    private static final Logger log = LoggerFactory.getLogger(AuditoryEntity.class);
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Column(name = "estado", nullable = false, columnDefinition = "boolean default true")
    private boolean stateRegister;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date recordDate;

    @Column(name = "end_point", nullable = false, updatable = false)
    private String endPoint;

    @Column(name = "metodo_controlador", nullable = false, updatable = false)
    private String method;

    @ManyToOne(targetEntity = CatalogEntity.class)
    @JoinColumn(name = "estado_auditoria", referencedColumnName = "codigo_catalogo") //Antes el mnemonico
    @JsonBackReference
    private CatalogEntity catalogEntity;

    @Column(name = "estado_solcitud", nullable = false, updatable = false)
    private String statusCode;

    @Column(name = "body_response", updatable = false, length = 2048)
    private String response;

    @Column(name = "body_request", updatable = false, length = 1024)
    private String request;

    @Column(name = "direccion_ip", updatable = false)
    private String direccionIP;

    @OneToOne(mappedBy = "auditoryEntity", fetch = FetchType.EAGER)
    @JsonManagedReference
    private ConsumosEntity consumosEntity;

    @ManyToOne
    @JoinColumn(name = "id_usuario", updatable = false, referencedColumnName = "id")
    private UserEntity user;
}
