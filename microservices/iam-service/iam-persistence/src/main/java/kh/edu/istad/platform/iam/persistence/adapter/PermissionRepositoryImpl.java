package kh.edu.istad.platform.iam.persistence.adapter;

import jakarta.persistence.criteria.Predicate;
import kh.edu.istad.platform.iam.application.dto.PageResult;
import kh.edu.istad.platform.iam.application.dto.PermissionFilterQuery;
import kh.edu.istad.platform.iam.application.dto.PermissionResult;
import kh.edu.istad.platform.iam.application.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.application.usecase.permission.PermissionDtoMapper;
import kh.edu.istad.platform.iam.domain.model.Permission;
import kh.edu.istad.platform.iam.persistence.entity.PermissionJpaEntity;
import kh.edu.istad.platform.iam.persistence.mapper.PermissionEntityMapper;
import kh.edu.istad.platform.iam.persistence.repository.PermissionSpringDataRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class PermissionRepositoryImpl implements PermissionRepository {

    private final PermissionSpringDataRepository permissionSpringDataRepository;

    public PermissionRepositoryImpl(PermissionSpringDataRepository permissionSpringDataRepository) {
        this.permissionSpringDataRepository = permissionSpringDataRepository;
    }

    @Override
    public Permission save(Permission permission) {
        PermissionJpaEntity entity = PermissionEntityMapper.toEntity(permission);
        PermissionJpaEntity saved = permissionSpringDataRepository.save(entity);
        return PermissionEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<Permission> findById(Long id) {
        return permissionSpringDataRepository.findById(id).map(PermissionEntityMapper::toDomain);
    }

    @Override
    public Optional<Permission> findByUuid(UUID uuid) {
        return permissionSpringDataRepository.findByUuid(uuid).map(PermissionEntityMapper::toDomain);
    }

    @Override
    public Optional<Permission> findByName(String name) {
        return permissionSpringDataRepository.findByName(name).map(PermissionEntityMapper::toDomain);
    }

    @Override
    public boolean existsByName(String name) {
        return permissionSpringDataRepository.existsByName(name);
    }

    @Override
    public PageResult<PermissionResult> findAll(PermissionFilterQuery query) {
        Specification<PermissionJpaEntity> spec = (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isFalse(root.get("deleted")));

            if (query.getResource() != null && !query.getResource().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("resource")), query.getResource().trim().toLowerCase()));
            }

            if (query.getAction() != null && !query.getAction().isBlank()) {
                predicates.add(cb.equal(cb.lower(root.get("action")), query.getAction().trim().toLowerCase()));
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
        Page<PermissionJpaEntity> page = permissionSpringDataRepository.findAll(spec, pageable);

        List<PermissionResult> results = page.getContent().stream()
                .map(PermissionEntityMapper::toDomain)
                .map(PermissionDtoMapper::toResult)
                .toList();

        return new PageResult<>(results, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public List<Permission> findAllByIds(Collection<Long> ids) {
        return permissionSpringDataRepository.findAllById(ids)
                .stream()
                .map(PermissionEntityMapper::toDomain)
                .toList();
    }

    @Override
    public List<Permission> findAllByUuids(Collection<UUID> uuids) {
        return permissionSpringDataRepository.findAll((root, q, cb) -> root.get("uuid").in(uuids))
                .stream()
                .map(PermissionEntityMapper::toDomain)
                .toList();
    }
}
