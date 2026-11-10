package kh.edu.istad.platform.iam.persistence.repository;

import kh.edu.istad.platform.iam.persistence.entity.RolePermissionJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RolePermissionSpringDataRepository extends JpaRepository<RolePermissionJpaEntity, Long> {
    List<RolePermissionJpaEntity> findByRoleId(Long roleId);
}
