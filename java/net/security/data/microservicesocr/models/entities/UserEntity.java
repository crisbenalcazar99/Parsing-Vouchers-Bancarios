package net.security.data.microservicesocr.models.entities;


import java.util.Date;
import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.TemporalType;



@NoArgsConstructor
@AllArgsConstructor
@Entity
@Builder
@Data
@Table(name = "usuarios", schema = "public", catalog = "db_vouchers_test", uniqueConstraints = {@UniqueConstraint(name="uniqueUsername", columnNames = {"username"})})
public class UserEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", nullable = false)
    private Long id;

    @ManyToOne(targetEntity = CatalogEntity.class)
    @JoinColumn(name = "estado_registro", referencedColumnName = "codigo_catalogo", nullable = false)
    @JsonBackReference
    private CatalogEntity userStatus;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date recordDate;

    @Column(name = "fecha_actualizacion", nullable = false, updatable = true)
    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date updateDate;

    @Column(name = "username", nullable = false, precision = 30, unique = true)
    private String username;

    @JsonIgnore
    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "estado", nullable = false, columnDefinition = "boolean default true")
    private Boolean status;

    @PrePersist
    private void prePersis(){
        this.status = true;
    }

}