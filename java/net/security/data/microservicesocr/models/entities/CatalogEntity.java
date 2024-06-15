package net.security.data.microservicesocr.models.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import java.util.Date;



@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Builder
@Table(name = "catalogos", schema = "public", catalog = "db_microservicesOCR")
public class CatalogEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "codigo_catalogo", nullable = false)
    private Long idCatalog;

    @Basic
    @Column(name = "estado", nullable = false, columnDefinition = "boolean default true")
    private boolean stateRegister;

    @Basic
    @Column(name = "descripcion", nullable = false, updatable = false)
    private String description;

    @Basic
    @Column(name = "mnemonico", nullable = false, updatable = false, unique = true)
    private String mnemonic;

    @Basic
    @Column(name = "nombre", updatable = false)
    private String name;

    @Column(name = "fecha_registro", nullable = false, updatable = false)
    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    private Date dateRecord;


    @ManyToOne
    @JoinColumn(name = "catalogo_padre", referencedColumnName = "codigo_catalogo")
    @JsonBackReference
    private CatalogEntity parentCatalog;
}
