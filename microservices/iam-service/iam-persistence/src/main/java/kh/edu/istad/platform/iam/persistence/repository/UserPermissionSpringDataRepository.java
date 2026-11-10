package kh.edu.istad.platform.iam.persistence.repository;

import kh.edu.istad.platform.iam.persistence.entity.UserPermissionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPermissionSpringDataRepository extends JpaRepository<UserPermissionJpaEntity, Long> {
    List<UserPermissionJpaEntity> findByUserId(Long userId);
}
