package kh.edu.istad.platform.iam.application.port.out;

import kh.edu.istad.platform.iam.application.dto.PageResult;
import kh.edu.istad.platform.iam.application.dto.UserFilterQuery;
import kh.edu.istad.platform.iam.application.dto.UserSummary;
import kh.edu.istad.platform.iam.domain.model.Identity;
import kh.edu.istad.platform.iam.domain.model.IdentityProvider;
import kh.edu.istad.platform.iam.domain.model.User;
import kh.edu.istad.platform.iam.domain.model.UserPermission;
import kh.edu.istad.platform.iam.domain.model.UserRole;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserRepository {
    User save(User user);
    Optional<User> findById(Long id);
    Optional<User> findByUuid(UUID uuid);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUsernameOrEmail(String usernameOrEmail);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    boolean existsByPhoneNumber(String phoneNumber);
    PageResult<UserSummary> findAll(UserFilterQuery query);

    // User Roles
    List<UserRole> findRolesByUserId(Long userId);
    UserRole saveUserRole(UserRole userRole);

    // User Permissions
    List<UserPermission> findPermissionsByUserId(Long userId);
    UserPermission saveUserPermission(UserPermission userPermission);

    // Identities
    List<Identity> findIdentitiesByUserId(Long userId);
    Optional<Identity> findIdentityByUuid(UUID identityUuid);
    Optional<Identity> findIdentityByProviderAndSubject(IdentityProvider provider, String providerSubject);
    Identity saveIdentity(Identity identity);
}
