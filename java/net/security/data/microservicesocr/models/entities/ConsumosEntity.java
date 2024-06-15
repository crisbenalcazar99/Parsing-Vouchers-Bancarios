package net.security.data.microservicesocr.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "consumos", schema = "public", catalog = "db_microservicesOCR")
public class ConsumosEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @Basic
    @Column(name = "estado", nullable = false, columnDefinition = "boolean default true")
    private boolean stateRegister;

    @Basic
    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date recordTime;

    @Basic
    @Column(name = "estado_solicitud")
    private String statusCode;


    @OneToOne(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "id_auditoria", referencedColumnName = "id")
    @JsonBackReference
    private AuditoryEntity auditoryEntity;

    @OneToOne(cascade = CascadeType.DETACH)
    @JoinColumn(name = "id_datos_voucher", referencedColumnName = "id")
    @JsonBackReference
    private VouchersEntity vouchersEntity;
    
    @ManyToOne
    @JoinColumn(name = "id_usuario", referencedColumnName="id", updatable = false)
    @JsonBackReference
    private UserEntity user;
}
