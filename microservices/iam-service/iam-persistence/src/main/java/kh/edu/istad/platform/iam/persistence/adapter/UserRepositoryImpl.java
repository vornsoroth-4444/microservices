package kh.edu.istad.platform.iam.persistence.adapter;

import jakarta.persistence.criteria.Predicate;
import kh.edu.istad.platform.iam.application.dto.PageResult;
import kh.edu.istad.platform.iam.application.dto.RoleSummary;
import kh.edu.istad.platform.iam.application.dto.UserFilterQuery;
import kh.edu.istad.platform.iam.application.dto.UserSummary;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.application.usecase.user.UserDtoMapper;
import kh.edu.istad.platform.iam.domain.model.Identity;
import kh.edu.istad.platform.iam.domain.model.IdentityProvider;
import kh.edu.istad.platform.iam.domain.model.User;
import kh.edu.istad.platform.iam.domain.model.UserPermission;
import kh.edu.istad.platform.iam.domain.model.UserRole;
import kh.edu.istad.platform.iam.persistence.entity.IdentityJpaEntity;
import kh.edu.istad.platform.iam.persistence.entity.RoleJpaEntity;
import kh.edu.istad.platform.iam.persistence.entity.UserJpaEntity;
import kh.edu.istad.platform.iam.persistence.entity.UserPermissionJpaEntity;
import kh.edu.istad.platform.iam.persistence.entity.UserRoleJpaEntity;
import kh.edu.istad.platform.iam.persistence.mapper.AssignmentEntityMapper;
import kh.edu.istad.platform.iam.persistence.mapper.IdentityEntityMapper;
import kh.edu.istad.platform.iam.persistence.mapper.UserEntityMapper;
import kh.edu.istad.platform.iam.persistence.repository.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.*;

@Component
public class UserRepositoryImpl implements UserRepository {

    private final UserSpringDataRepository userSpringDataRepository;
    private final UserRoleSpringDataRepository userRoleSpringDataRepository;
    private final RoleSpringDataRepository roleSpringDataRepository;
    private final UserPermissionSpringDataRepository userPermissionSpringDataRepository;
    private final IdentitySpringDataRepository identitySpringDataRepository;

    public UserRepositoryImpl(UserSpringDataRepository userSpringDataRepository,
                              UserRoleSpringDataRepository userRoleSpringDataRepository,
                              RoleSpringDataRepository roleSpringDataRepository,
                              UserPermissionSpringDataRepository userPermissionSpringDataRepository,
                              IdentitySpringDataRepository identitySpringDataRepository) {
        this.userSpringDataRepository = userSpringDataRepository;
        this.userRoleSpringDataRepository = userRoleSpringDataRepository;
        this.roleSpringDataRepository = roleSpringDataRepository;
        this.userPermissionSpringDataRepository = userPermissionSpringDataRepository;
        this.identitySpringDataRepository = identitySpringDataRepository;
    }

    @Override
    public User save(User user) {
        UserJpaEntity entity = UserEntityMapper.toEntity(user);
        UserJpaEntity saved = userSpringDataRepository.save(entity);
        return UserEntityMapper.toDomain(saved);
    }

    @Override
    public Optional<User> findById(Long id) {
        return userSpringDataRepository.findById(id).map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByUuid(UUID uuid) {
        return userSpringDataRepository.findByUuid(uuid).map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsername(String username) {
        return userSpringDataRepository.findByUsername(username).map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userSpringDataRepository.findByEmail(email).map(UserEntityMapper::toDomain);
    }

    @Override
    public Optional<User> findByUsernameOrEmail(String usernameOrEmail) {
        return userSpringDataRepository.findByUsernameOrEmail(usernameOrEmail).map(UserEntityMapper::toDomain);
    }

    @Override
    public boolean existsByUsername(String username) {
        return userSpringDataRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userSpringDataRepository.existsByEmail(email);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return userSpringDataRepository.existsByPhoneNumber(phoneNumber);
    }

    @Override
    public PageResult<UserSummary> findAll(UserFilterQuery query) {
        Specification<UserJpaEntity> spec = (root, q, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.isFalse(root.get("deleted")));

            if (query.getEnabled() != null) {
                predicates.add(cb.equal(root.get("enabled"), query.getEnabled()));
            }

            if (query.getSearch() != null && !query.getSearch().isBlank()) {
                String pattern = "%" + query.getSearch().trim().toLowerCase() + "%";
                Predicate searchPredicate = cb.or(
                        cb.like(cb.lower(root.get("username")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern),
                        cb.like(cb.lower(root.get("givenName")), pattern),
                        cb.like(cb.lower(root.get("familyName")), pattern)
                );
                predicates.add(searchPredicate);
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Sort sort = Sort.by(Sort.Direction.DESC, "createdAt");
        if (query.getSort() != null && !query.getSort().isBlank()) {
            String[] parts = query.getSort().split(",");
            String prop = parts[0].trim();
            Sort.Direction dir = (parts.length > 1 && parts[1].trim().equalsIgnoreCase("asc"))
                    ? Sort.Direction.ASC : Sort.Direction.DESC;
            sort = Sort.by(dir, prop);
        }

        Pageable pageable = PageRequest.of(Math.max(0, query.getPage()), Math.max(1, query.getSize()), sort);
        Page<UserJpaEntity> page = userSpringDataRepository.findAll(spec, pageable);

        Instant now = Instant.now();
        List<UserSummary> summaries = new ArrayList<>();
        for (UserJpaEntity entity : page.getContent()) {
            User user = UserEntityMapper.toDomain(entity);
            List<UserRoleJpaEntity> urEntities = userRoleSpringDataRepository.findByUserId(entity.getId());
            List<RoleSummary> roles = new ArrayList<>();
            for (UserRoleJpaEntity ur : urEntities) {
                if (ur.getRevokedAt() == null && (ur.getExpiresAt() == null || ur.getExpiresAt().isAfter(now))) {
                    Optional<RoleJpaEntity> r = roleSpringDataRepository.findById(ur.getRoleId());
                    r.filter(role -> !role.isDeleted()).ifPresent(role -> roles.add(new RoleSummary(role.getUuid(), role.getName(), role.isActive())));
                }
            }
            summaries.add(UserDtoMapper.toSummary(user, roles));
        }

        return new PageResult<>(summaries, page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages());
    }

    @Override
    public List<UserRole> findRolesByUserId(Long userId) {
        return userRoleSpringDataRepository.findByUserId(userId)
                .stream()
                .map(AssignmentEntityMapper::toDomain)
                .toList();
    }

    @Override
    public UserRole saveUserRole(UserRole userRole) {
        UserRoleJpaEntity entity = AssignmentEntityMapper.toEntity(userRole);
        UserRoleJpaEntity saved = userRoleSpringDataRepository.save(entity);
        return AssignmentEntityMapper.toDomain(saved);
    }

    @Override
    public List<UserPermission> findPermissionsByUserId(Long userId) {
        return userPermissionSpringDataRepository.findByUserId(userId)
                .stream()
                .map(AssignmentEntityMapper::toDomain)
                .toList();
    }

    @Override
    public UserPermission saveUserPermission(UserPermission userPermission) {
        UserPermissionJpaEntity entity = AssignmentEntityMapper.toEntity(userPermission);
        UserPermissionJpaEntity saved = userPermissionSpringDataRepository.save(entity);
        return AssignmentEntityMapper.toDomain(saved);
    }

    @Override
    public List<Identity> findIdentitiesByUserId(Long userId) {
        return identitySpringDataRepository.findByUserId(userId)
                .stream()
                .map(IdentityEntityMapper::toDomain)
                .toList();
    }

    @Override
    public Optional<Identity> findIdentityByUuid(UUID identityUuid) {
        return identitySpringDataRepository.findByUuid(identityUuid).map(IdentityEntityMapper::toDomain);
    }

    @Override
    public Optional<Identity> findIdentityByProviderAndSubject(IdentityProvider provider, String providerSubject) {
        return identitySpringDataRepository.findByProviderAndProviderSubject(provider, providerSubject)
                .map(IdentityEntityMapper::toDomain);
    }

    @Override
    public Identity saveIdentity(Identity identity) {
        IdentityJpaEntity entity = IdentityEntityMapper.toEntity(identity);
        IdentityJpaEntity saved = identitySpringDataRepository.save(entity);
        return IdentityEntityMapper.toDomain(saved);
    }
}
