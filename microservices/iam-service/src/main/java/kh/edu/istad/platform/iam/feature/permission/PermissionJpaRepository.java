package kh.edu.istad.platform.iam.feature.permission;

import kh.edu.istad.platform.iam.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;


public interface PermissionJpaRepository extends JpaRepository<Permission, Long> {
}
