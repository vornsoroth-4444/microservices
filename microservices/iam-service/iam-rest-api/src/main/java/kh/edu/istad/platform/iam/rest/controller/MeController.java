package kh.edu.istad.platform.iam.rest.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iam.application.dto.ChangePasswordCommand;
import kh.edu.istad.platform.iam.application.dto.IdentityResult;
import kh.edu.istad.platform.iam.application.dto.UpdateUserProfileCommand;
import kh.edu.istad.platform.iam.application.dto.UserResult;
import kh.edu.istad.platform.iam.application.usecase.identity.ListUserIdentitiesUseCase;
import kh.edu.istad.platform.iam.application.usecase.identity.UnlinkIdentityUseCase;
import kh.edu.istad.platform.iam.application.usecase.user.ChangePasswordUseCase;
import kh.edu.istad.platform.iam.application.usecase.user.GetUserUseCase;
import kh.edu.istad.platform.iam.application.usecase.user.UpdateUserProfileUseCase;
import kh.edu.istad.platform.iam.domain.model.Address;
import kh.edu.istad.platform.iam.rest.dto.RestDtoMapper;
import kh.edu.istad.platform.iam.rest.dto.request.ChangePasswordRequest;
import kh.edu.istad.platform.iam.rest.dto.request.UpdateUserProfileRequest;
import kh.edu.istad.platform.iam.rest.dto.response.IdentityResponse;
import kh.edu.istad.platform.iam.rest.dto.response.UserResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/me")
public class MeController {

    private final GetUserUseCase getUserUseCase;
    private final UpdateUserProfileUseCase updateUserProfileUseCase;
    private final ChangePasswordUseCase changePasswordUseCase;
    private final ListUserIdentitiesUseCase listUserIdentitiesUseCase;
    private final UnlinkIdentityUseCase unlinkIdentityUseCase;

    public MeController(GetUserUseCase getUserUseCase,
                        UpdateUserProfileUseCase updateUserProfileUseCase,
                        ChangePasswordUseCase changePasswordUseCase,
                        ListUserIdentitiesUseCase listUserIdentitiesUseCase,
                        UnlinkIdentityUseCase unlinkIdentityUseCase) {
        this.getUserUseCase = getUserUseCase;
        this.updateUserProfileUseCase = updateUserProfileUseCase;
        this.changePasswordUseCase = changePasswordUseCase;
        this.listUserIdentitiesUseCase = listUserIdentitiesUseCase;
        this.unlinkIdentityUseCase = unlinkIdentityUseCase;
    }

    @GetMapping
    public ResponseEntity<UserResponse> getProfile(@AuthenticationPrincipal Jwt jwt) {
        UUID userUuid = UUID.fromString(jwt.getSubject());
        UserResult result = getUserUseCase.execute(userUuid);
        return ResponseEntity.ok(RestDtoMapper.toUserResponse(result));
    }

    @PatchMapping
    public ResponseEntity<UserResponse> updateProfile(@AuthenticationPrincipal Jwt jwt,
                                                      @Valid @RequestBody UpdateUserProfileRequest request) {
        UUID userUuid = UUID.fromString(jwt.getSubject());
        Address address = request.getAddress() != null ? Address.builder()
                .streetAddress(request.getAddress().getStreetAddress())
                .locality(request.getAddress().getLocality())
                .region(request.getAddress().getRegion())
                .postalCode(request.getAddress().getPostalCode())
                .country(request.getAddress().getCountry())
                .build() : null;

        UpdateUserProfileCommand command = UpdateUserProfileCommand.builder()
                .userUuid(userUuid)
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

        UserResult result = updateUserProfileUseCase.execute(command);
        return ResponseEntity.ok(RestDtoMapper.toUserResponse(result));
    }

    @PutMapping("/password")
    @PostMapping("/password")
    public ResponseEntity<Void> changePassword(@AuthenticationPrincipal Jwt jwt,
                                               @Valid @RequestBody ChangePasswordRequest request) {
        UUID userUuid = UUID.fromString(jwt.getSubject());
        ChangePasswordCommand command = new ChangePasswordCommand(
                userUuid,
                request.getCurrentPassword(),
                request.getNewPassword(),
                request.getConfirmPassword()
        );
        changePasswordUseCase.execute(command);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/identities")
    public ResponseEntity<List<IdentityResponse>> getIdentities(@AuthenticationPrincipal Jwt jwt) {
        UUID userUuid = UUID.fromString(jwt.getSubject());
        List<IdentityResult> results = listUserIdentitiesUseCase.execute(userUuid);
        return ResponseEntity.ok(results.stream().map(RestDtoMapper::toIdentityResponse).toList());
    }

    @DeleteMapping("/identities/{identityUuid}")
    public ResponseEntity<Void> unlinkIdentity(@AuthenticationPrincipal Jwt jwt,
                                               @PathVariable UUID identityUuid) {
        UUID userUuid = UUID.fromString(jwt.getSubject());
        unlinkIdentityUseCase.execute(userUuid, identityUuid);
        return ResponseEntity.noContent().build();
    }
}
