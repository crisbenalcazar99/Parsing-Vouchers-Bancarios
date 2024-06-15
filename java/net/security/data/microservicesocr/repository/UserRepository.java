package net.security.data.microservicesocr.repository;
import net.security.data.microservicesocr.models.entities.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<UserEntity, Long> {
    Optional<UserEntity> findByUsernameAndStatusTrue(String username);
    Optional<UserEntity> findByUsername(String username);
}
