package kh.edu.istad.platform.iam.persistence.adapter;

import jakarta.persistence.criteria.Predicate;
import kh.edu.istad.platform.iam.application.dto.PageResult;
import kh.edu.istad.platform.iam.application.dto.RoleFilterQuery;
import kh.edu.istad.platform.iam.application.dto.RoleSummary;
import kh.edu.istad.platform.iam.application.port.out.RoleRepository;
import kh.edu.istad.platform.iam.application.usecase.role.RoleDtoMapper;
import kh.edu.istad.platform.iam.domain.model.Role;
import kh.edu.istad.platform.iam.domain.model.RolePermission;
import kh.edu.istad.platform.iam.persistence.entity.RoleJpaEntity;
import kh.edu.istad.platform.iam.persistence.entity.RolePermissionJpaEntity;
import kh.edu.istad.platform.iam.persistence.mapper.AssignmentEntityMapper;
import kh.edu.istad.platform.iam.persistence.mapper.RoleEntityMapper;
import kh.edu.istad.platform.iam.persistence.repository.RolePermissionSpringDataRepository;
import kh.edu.istad.platform.iam.persistence.repository.RoleSpringDataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleSpringDataRepository roleSpringDataRepository;
    private final RolePermissionSpringDataRepository rolePermissionSpringDataRepository;

    public RoleRepositoryImpl(RoleSpringDataRepository roleSpringDataRepository,
                              RolePermissionSpringDataRepository rolePermissionSpringDataRepository) {
        this.roleSpringDataRepository = roleSpringDataRepository;
        this.rolePermissionSpringDataRepository = rolePermissionSpringDataRepository;
    }

    @Override
    public Role save(Role role) {
        RoleJpaEntity entity = RoleEntityMapper.toEntity(role);
        RoleJpaEntity saved = roleSpringDataRepository.save(entity);
        return RoleEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Role> findById(Long id) {
        return roleSpringDataRepository.findById(id).map(RoleEntityMapper::toDomain);
    }

    @Override
    public Optional<Role> findByUuid(UUID uuid) {
        return roleSpringDataRepository.findByUuid(uuid).map(RoleEntityMapper::toDomain);
    }

    @Override
    public Optional<Role> findByName(String name) {
        return roleSpringDataRepository.findByName(name).map(RoleEntityMapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return roleSpringDataRepository.existsByName(name);
    }

    @Override
    public PageResult<RoleSummary> findAll(RoleFilterQuery query) {
        Specification<RoleJpaEntity> spec = (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isFalse(root.get("deleted")));

            if (query.getActive() != null) {
                predicates.add(cb.equal(root.get("active"), query.getActive()));
            }

            if (query.getSearch() != null && !query.getSearch().isBlank()) {
                String pattern = "%" + query.getSearch().trim().toLowerCase() + "%";
                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("name")), pattern),
                        cb.like(cb.lower(root.get("description")), pattern)
                );
                predicates.add(searchPredicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.ASC, "name");
        if (query.getSort() != null && !query.getSort().isBlank()) {
            String[] parts = query.getSort().split(",");
            String prop = parts[0].trim();
            Sort.Direction dir = (parts.length > 1 && parts[1].trim().equalsIgnoreCase("desc"))
                    ? Sort.Direction.DESC : Sort.Direction.ASC;
            sort = Sort.by(dir, prop);
        }

        Pageable pageable = PageRequest.of(Math.max(0, query.getPage()), Math.max(1, query.getSize()), sort);
        Page<RoleJpaEntity> page = roleSpringDataRepository.findAll(spec, pageable);

        List<RoleSummary> summaries = page.getContent().stream()
                .map(RoleEntityMapper::toDomain)
                .map(RoleDtoMapper::toSummary)
                .toList();

        return new PageResult<>(summaries, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public List<Role> findAllByIds(Collection<Long> ids) {
        return roleSpringDataRepository.findAllById(ids)
                .stream()
                .map(RoleEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Role> findAllByUuids(Collection<UUID> uuids) {
        return roleSpringDataRepository.findAll((root, q, cb) -> root.get("uuid").in(uuids))
                .stream()
                .map(RoleEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<RolePermission> findPermissionsByRoleId(Long roleId) {
        return rolePermissionSpringDataRepository.findByRoleId(roleId)
                .stream()
                .map(AssignmentEntityMapper::toDomain)
                .toList();
    }

    @Override
    public RolePermission saveRolePermission(RolePermission rolePermission) {
        RolePermissionJpaEntity entity = AssignmentEntityMapper.toEntity(rolePermission);
        RolePermissionJpaEntity saved = rolePermissionSpringDataRepository.save(entity);
        return AssignmentEntityMapper.toDomain(saved);
    }
}
