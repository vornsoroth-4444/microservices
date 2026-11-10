package kh.edu.istad.platform.iam.application.port.out;

import kh.edu.istad.platform.iam.application.dto.PageResult;
import kh.edu.istad.platform.iam.application.dto.PermissionFilterQuery;
import kh.edu.istad.platform.iam.application.dto.PermissionResult;
import kh.edu.istad.platform.iam.domain.model.Permission;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PermissionRepository {
    Permission save(Permission permission);
    Optional<Permission> findById(Long id);
    Optional<Permission> findByUuid(UUID uuid);
    Optional<Permission> findByName(String name);
    boolean existsByName(String name);
    PageResult<PermissionResult> findAll(PermissionFilterQuery query);
    List<Permission> findAllByIds(Collection<Long> ids);
    List<Permission> findAllByUuids(Collection<UUID> uuids);
}
