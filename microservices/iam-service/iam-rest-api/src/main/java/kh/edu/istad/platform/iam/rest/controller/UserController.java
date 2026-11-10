package kh.edu.istad.platform.iam.rest.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iam.application.dto.*;
import kh.edu.istad.platform.iam.application.usecase.identity.ListUserIdentitiesUseCase;
import kh.edu.istad.platform.iam.application.usecase.identity.UnlinkIdentityUseCase;
import kh.edu.istad.platform.iam.application.usecase.user.*;
import kh.edu.istad.platform.iam.domain.model.Address;
import kh.edu.istad.platform.iam.rest.dto.RestDtoMapper;
import kh.edu.istad.platform.iam.rest.dto.request.*;
import kh.edu.istad.platform.iam.rest.dto.response.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/users")
public class UserController {

    private final CreateUserUseCase createUserUseCase;
    private final ListUsersUseCase listUsersUseCase;
    private final GetUserUseCase getUserUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final DeleteUserUseCase deleteUserUseCase;
    private final EnableUserUseCase enableUserUseCase;
    private final DisableUserUseCase disableUserUseCase;
    private final LockUserUseCase lockUserUseCase;
    private final UnlockUserUseCase unlockUserUseCase;
    private final ResetUserPasswordUseCase resetUserPasswordUseCase;
    private final GetUserAuthoritiesUseCase getUserAuthoritiesUseCase;
    private final AssignRolesToUserUseCase assignRolesToUserUseCase;
    private final RevokeRoleFromUserUseCase revokeRoleFromUserUseCase;
    private final GrantPermissionsToUserUseCase grantPermissionsToUserUseCase;
    private final RevokePermissionFromUserUseCase revokePermissionFromUserUseCase;
    private final ListUserIdentitiesUseCase listUserIdentitiesUseCase;
    private final UnlinkIdentityUseCase unlinkIdentityUseCase;

    public UserController(CreateUserUseCase createUserUseCase,
                          ListUsersUseCase listUsersUseCase,
                          GetUserUseCase getUserUseCase,
                          UpdateUserProfileUseCase updateUserProfileUseCase,
                          DeleteUserUseCase deleteUserUseCase,
                          EnableUserUseCase enableUserUseCase,
                          DisableUserUseCase disableUserUseCase,
                          LockUserUseCase lockUserUseCase,
                          UnlockUserUseCase unlockUserUseCase,
                          ResetUserPasswordUseCase resetUserPasswordUseCase,
                          GetUserAuthoritiesUseCase getUserAuthoritiesUseCase,
                          AssignRolesToUserUseCase assignRolesToUserUseCase,
                          RevokeRoleFromUserUseCase revokeRoleFromUserUseCase,
                          GrantPermissionsToUserUseCase grantPermissionsToUserUseCase,
                          RevokePermissionFromUserUseCase revokePermissionFromUserUseCase,
                          ListUserIdentitiesUseCase listUserIdentitiesUseCase,
                          UnlinkIdentityUseCase unlinkIdentityUseCase) {
        this.createUserUseCase = createUserUseCase;
        this.listUsersUseCase = listUsersUseCase;
        this.getUserUseCase = getUserUseCase;
        this.updateUserProfileUseCase = updateUserProfileUseCase;
        this.deleteUserUseCase = deleteUserUseCase;
        this.enableUserUseCase = enableUserUseCase;
        this.disableUserUseCase = disableUserUseCase;
        this.lockUserUseCase = lockUserUseCase;
        this.unlockUserUseCase = unlockUserUseCase;
        this.resetUserPasswordUseCase = resetUserPasswordUseCase;
        this.getUserAuthoritiesUseCase = getUserAuthoritiesUseCase;
        this.assignRolesToUserUseCase = assignRolesToUserUseCase;
        this.revokeRoleFromUserUseCase = revokeRoleFromUserUseCase;
        this.grantPermissionsToUserUseCase = grantPermissionsToUserUseCase;
        this.revokePermissionFromUserUseCase = revokePermissionFromUserUseCase;
        this.listUserIdentitiesUseCase = listUserIdentitiesUseCase;
        this.unlinkIdentityUseCase = unlinkIdentityUseCase;
    }

    @PostMapping
    public ResponseEntity<UserResponse> createUser(@Valid @RequestBody CreateUserRequest request) {
        Address address = request.getAddress() != null ? Address.builder()
                .streetAddress(request.getAddress().getStreetAddress())
                .locality(request.getAddress().getLocality())
                .region(request.getAddress().getRegion())
                .postalCode(request.getAddress().getPostalCode())
                .country(request.getAddress().getCountry())
                .build() : null;

        CreateUserCommand cmd = CreateUserCommand.builder()
                .username(request.getUsername())
                .email(request.getEmail())
                .password(request.getPassword())
                .givenName(request.getGivenName())
                .familyName(request.getFamilyName())
                .gender(request.getGender())
                .birthdate(request.getBirthdate())
                .phoneNumber(request.getPhoneNumber())
                .zoneinfo(request.getZoneinfo())
                .locale(request.getLocale() != null ? request.getLocale() : "en")
                .address(address)
                .roleUuids(request.getRoleUuids())
                .build();

        UserResult result = createUserUseCase.execute(cmd);
        return ResponseEntity.status(HttpStatus.CREATED).body(RestDtoMapper.toUserResponse(result));
    }

    @GetMapping
    public ResponseEntity<PageResponse<UserResponse>> listUsers(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "enabled", required = false) Boolean enabled,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "createdAt,desc") String sort) {
        UserFilterQuery query = UserFilterQuery.builder()
                .search(search)
                .role(role)
                .enabled(enabled)
                .page(page)
                .size(size)
                .sort(sort)
                .build();

        PageResult<UserSummary> pageResult = listUsersUseCase.execute(query);
        List<UserResponse> items = pageResult.getItems().stream()
                .map(RestDtoMapper::fromSummary)
                .toList();

        return ResponseEntity.ok(new PageResponse<>(
                items,
                pageResult.getPage(),
                pageResult.getSize(),
                pageResult.getTotalElements(),
                pageResult.getTotalPages()
        ));
    }

    @GetMapping("/{uuid}")
    public ResponseEntity<UserResponse> getUser(@PathVariable UUID uuid) {
        UserResult result = getUserUseCase.execute(uuid);
        return ResponseEntity.ok(RestDtoMapper.toUserResponse(result));
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<UserResponse> updateUser(@PathVariable UUID uuid,
                                                   @Valid @RequestBody UpdateUserProfileRequest request) {
        Address address = request.getAddress() != null ? Address.builder()
                .streetAddress(request.getAddress().getStreetAddress())
                .locality(request.getAddress().getLocality())
                .region(request.getAddress().getRegion())
                .postalCode(request.getAddress().getPostalCode())
                .country(request.getAddress().getCountry())
                .build() : null;

        UpdateUserProfileCommand cmd = UpdateUserProfileCommand.builder()
                .userUuid(uuid)
                .givenName(request.getGivenName())
                .familyName(request.getFamilyName())
                .middleName(request.getMiddleName())
                .nickname(request.getNickname())
                .gender(request.getGender())
                .birthdate(request.getBirthdate())
                .profileUrl(request.getProfileUrl())
                .pictureUrl(request.getPictureUrl())
                .websiteUrl(request.getWebsiteUrl())
                .zoneinfo(request.getZoneinfo())
                .locale(request.getLocale())
                .phoneNumber(request.getPhoneNumber())
                .address(address)
                .build();

        UserResult result = updateUserProfileUseCase.execute(cmd);
        return ResponseEntity.ok(RestDtoMapper.toUserResponse(result));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteUser(@PathVariable UUID uuid) {
        deleteUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/enable")
    public ResponseEntity<Void> enableUser(@PathVariable UUID uuid) {
        enableUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/disable")
    public ResponseEntity<Void> disableUser(@PathVariable UUID uuid) {
        disableUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/lock")
    public ResponseEntity<Void> lockUser(@PathVariable UUID uuid) {
        lockUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/unlock")
    public ResponseEntity<Void> unlockUser(@PathVariable UUID uuid) {
        unlockUserUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PutMapping(path = {"/{uuid}/password", "/{uuid}/reset-password"})
    @PostMapping("/{uuid}/reset-password")
    public ResponseEntity<Void> resetPassword(@PathVariable UUID uuid,
                                              @Valid @RequestBody ResetUserPasswordRequest request) {
        ResetUserPasswordCommand cmd = new ResetUserPasswordCommand(uuid, request.getNewPassword());
        resetUserPasswordUseCase.execute(cmd);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{uuid}/authorities")
    public ResponseEntity<UserAuthoritiesResponse> getAuthorities(@PathVariable UUID uuid) {
        UserAuthoritiesResult result = getUserAuthoritiesUseCase.execute(uuid);
        return ResponseEntity.ok(RestDtoMapper.toUserAuthoritiesResponse(result));
    }

    @PostMapping("/{uuid}/roles")
    public ResponseEntity<List<RoleAssignmentResult>> assignRoles(@PathVariable UUID uuid,
                                                                 @Valid @RequestBody AssignRolesToUserRequest request) {
        AssignRolesToUserCommand cmd = new AssignRolesToUserCommand(
                uuid,
                request.getRoleUuids(),
                request.getExpiresAt(),
                request.getReason()
        );
        List<RoleAssignmentResult> results = assignRolesToUserUseCase.execute(cmd);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{uuid}/roles/{roleUuid}")
    public ResponseEntity<Void> revokeRole(@PathVariable(name = "uuid") UUID uuid,
                                           @PathVariable(name = "roleUuid") UUID roleUuid,
                                           @RequestParam(name = "reason", required = false) String reason) {
        revokeRoleFromUserUseCase.execute(uuid, roleUuid, reason != null ? reason : "Revoked by admin");
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/permissions")
    public ResponseEntity<List<PermissionGrantResult>> grantPermissions(@PathVariable UUID uuid,
                                                                       @Valid @RequestBody GrantPermissionsToUserRequest request) {
        GrantPermissionsToUserCommand cmd = new GrantPermissionsToUserCommand(
                uuid,
                request.getPermissionUuids(),
                request.getEffect(),
                request.getExpiresAt(),
                request.getReason()
        );
        List<PermissionGrantResult> results = grantPermissionsToUserUseCase.execute(cmd);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{uuid}/permissions/{permissionUuid}")
    public ResponseEntity<Void> revokePermission(@PathVariable(name = "uuid") UUID uuid,
                                                 @PathVariable(name = "permissionUuid") UUID permissionUuid,
                                                 @RequestParam(name = "reason", required = false) String reason) {
        revokePermissionFromUserUseCase.execute(uuid, permissionUuid, reason != null ? reason : "Revoked by admin");
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{uuid}/identities")
    public ResponseEntity<List<IdentityResponse>> getIdentities(@PathVariable UUID uuid) {
        List<IdentityResult> results = listUserIdentitiesUseCase.execute(uuid);
        return ResponseEntity.ok(results.stream().map(RestDtoMapper::toIdentityResponse).toList());
    }

    @DeleteMapping("/{uuid}/identities/{identityUuid}")
    public ResponseEntity<Void> unlinkIdentity(@PathVariable UUID uuid,
                                               @PathVariable UUID identityUuid) {
        unlinkIdentityUseCase.execute(uuid, identityUuid);
        return ResponseEntity.noContent().build();
    }
}
