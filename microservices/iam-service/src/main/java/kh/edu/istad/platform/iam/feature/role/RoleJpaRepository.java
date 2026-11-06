package kh.edu.istad.platform.iam.feature.role;


import kh.edu.istad.platform.iam.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleJpaRepository extends JpaRepository<Role, Long> {
}
