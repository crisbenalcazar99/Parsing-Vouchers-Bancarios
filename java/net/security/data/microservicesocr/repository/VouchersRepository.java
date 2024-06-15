package net.security.data.microservicesocr.repository;
import net.security.data.microservicesocr.models.entities.VouchersEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;


@Repository
public interface VouchersRepository extends JpaRepository<VouchersEntity, Long> {
//VouchersEntity findVouchersEntitiesByIdSolicitud(Long idTransaction);
    List<VouchersEntity> findVouchersEntitiesByIdTransactionAndStateRegisterTrue(Long idTransaction);
}
