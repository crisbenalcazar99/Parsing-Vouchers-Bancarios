package net.security.data.microservicesocr.repository;

import net.security.data.microservicesocr.models.entities.CatalogEntity;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CatalogRepository extends JpaRepository<CatalogEntity, Long> {
    @Cacheable("catalog")
    Optional<CatalogEntity> findCatalogEntityByMnemonic(String mnemonic);
}
