package net.security.data.microservicesocr.repository;

import net.security.data.microservicesocr.models.entities.ConsumosEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ConsumoRepository extends JpaRepository<ConsumosEntity, Long> {
}
