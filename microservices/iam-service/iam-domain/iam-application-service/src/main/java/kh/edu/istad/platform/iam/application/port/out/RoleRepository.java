package kh.edu.istad.platform.iam.application.port.out;

import kh.edu.istad.platform.iam.application.dto.PageResult;
import kh.edu.istad.platform.iam.application.dto.RoleFilterQuery;
import kh.edu.istad.platform.iam.application.dto.RoleSummary;
import kh.edu.istad.platform.iam.domain.model.Role;
import kh.edu.istad.platform.iam.domain.model.RolePermission;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface RoleRepository {
    Role save(Role role);
    Optional<Role> findById(Long id);
    Optional<Role> findByUuid(UUID uuid);
    Optional<Role> findByName(String name);
    boolean existsByName(String name);
    PageResult<RoleSummary> findAll(RoleFilterQuery query);
    List<Role> findAllByIds(Collection<Long> ids);
    List<Role> findAllByUuids(Collection<UUID> uuids);

    // Role Permissions
    List<RolePermission> findPermissionsByRoleId(Long roleId);
    RolePermission saveRolePermission(RolePermission rolePermission);
}
