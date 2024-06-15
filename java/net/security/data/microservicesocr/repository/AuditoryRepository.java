package net.security.data.microservicesocr.repository;


import net.security.data.microservicesocr.models.entities.AuditoryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuditoryRepository extends JpaRepository<AuditoryEntity, Long> {
}
