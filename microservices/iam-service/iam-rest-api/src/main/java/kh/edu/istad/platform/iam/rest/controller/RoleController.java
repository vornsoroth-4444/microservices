package kh.edu.istad.platform.iam.rest.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iam.application.dto.*;
import kh.edu.istad.platform.iam.application.usecase.role.*;
import kh.edu.istad.platform.iam.rest.dto.RestDtoMapper;
import kh.edu.istad.platform.iam.rest.dto.request.AssignPermissionsToRoleRequest;
import kh.edu.istad.platform.iam.rest.dto.request.CreateRoleRequest;
import kh.edu.istad.platform.iam.rest.dto.request.UpdateRoleRequest;
import kh.edu.istad.platform.iam.rest.dto.response.PageResponse;
import kh.edu.istad.platform.iam.rest.dto.response.RoleResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/roles")
public class RoleController {

    private final CreateRoleUseCase createRoleUseCase;
    private final ListRolesUseCase listRolesUseCase;
    private final GetRoleUseCase getRoleUseCase;
    private final UpdateRoleUseCase updateRoleUseCase;
    private final DeleteRoleUseCase deleteRoleUseCase;
    private final ActivateRoleUseCase activateRoleUseCase;
    private final DeactivateRoleUseCase deactivateRoleUseCase;
    private final AssignPermissionsToRoleUseCase assignPermissionsToRoleUseCase;
    private final RevokePermissionFromRoleUseCase revokePermissionFromRoleUseCase;

    public RoleController(CreateRoleUseCase createRoleUseCase,
                          ListRolesUseCase listRolesUseCase,
                          GetRoleUseCase getRoleUseCase,
                          UpdateRoleUseCase updateRoleUseCase,
                          DeleteRoleUseCase deleteRoleUseCase,
                          ActivateRoleUseCase activateRoleUseCase,
                          DeactivateRoleUseCase deactivateRoleUseCase,
                          AssignPermissionsToRoleUseCase assignPermissionsToRoleUseCase,
                          RevokePermissionFromRoleUseCase revokePermissionFromRoleUseCase) {
        this.createRoleUseCase = createRoleUseCase;
        this.listRolesUseCase = listRolesUseCase;
        this.getRoleUseCase = getRoleUseCase;
        this.updateRoleUseCase = updateRoleUseCase;
        this.deleteRoleUseCase = deleteRoleUseCase;
        this.activateRoleUseCase = activateRoleUseCase;
        this.deactivateRoleUseCase = deactivateRoleUseCase;
        this.assignPermissionsToRoleUseCase = assignPermissionsToRoleUseCase;
        this.revokePermissionFromRoleUseCase = revokePermissionFromRoleUseCase;
    }

    @PostMapping
    public ResponseEntity<RoleResponse> createRole(@Valid @RequestBody CreateRoleRequest request) {
        CreateRoleCommand cmd = new CreateRoleCommand(
                request.getName(),
                request.getDescription(),
                request.getPermissionUuids()
        );
        RoleResult result = createRoleUseCase.execute(cmd);
        return ResponseEntity.status(HttpStatus.CREATED).body(RestDtoMapper.toRoleResponse(result));
    }

    @GetMapping
    public ResponseEntity<PageResponse<RoleResponse>> listRoles(
            @RequestParam(name = "search", required = false) String search,
            @RequestParam(name = "active", required = false) Boolean active,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "name,asc") String sort) {
        RoleFilterQuery query = RoleFilterQuery.builder()
                .search(search)
                .active(active)
                .page(page)
                .size(size)
                .sort(sort)
                .build();

        PageResult<RoleSummary> pageResult = listRolesUseCase.execute(query);
        List<RoleResponse> items = pageResult.getItems().stream()
                .map(RestDtoMapper::toRoleResponse)
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
    public ResponseEntity<RoleResult> getRole(@PathVariable UUID uuid) {
        RoleResult result = getRoleUseCase.execute(uuid);
        return ResponseEntity.ok(result);
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<RoleResult> updateRole(@PathVariable UUID uuid,
                                                 @Valid @RequestBody UpdateRoleRequest request) {
        UpdateRoleCommand cmd = new UpdateRoleCommand(uuid, request.getDescription());
        RoleResult result = updateRoleUseCase.execute(cmd);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deleteRole(@PathVariable UUID uuid) {
        deleteRoleUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/activate")
    public ResponseEntity<Void> activateRole(@PathVariable UUID uuid) {
        activateRoleUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/deactivate")
    public ResponseEntity<Void> deactivateRole(@PathVariable UUID uuid) {
        deactivateRoleUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/permissions")
    public ResponseEntity<List<PermissionAssignmentResult>> assignPermissions(
            @PathVariable UUID uuid,
            @Valid @RequestBody AssignPermissionsToRoleRequest request) {
        AssignPermissionsToRoleCommand cmd = new AssignPermissionsToRoleCommand(
                uuid,
                request.getPermissionUuids(),
                request.getExpiresAt(),
                request.getReason()
        );
        List<PermissionAssignmentResult> results = assignPermissionsToRoleUseCase.execute(cmd);
        return ResponseEntity.ok(results);
    }

    @DeleteMapping("/{uuid}/permissions/{permissionUuid}")
    public ResponseEntity<Void> revokePermission(
            @PathVariable(name = "uuid") UUID uuid,
            @PathVariable(name = "permissionUuid") UUID permissionUuid,
            @RequestParam(name = "reason", required = false) String reason) {
        revokePermissionFromRoleUseCase.execute(uuid, permissionUuid, reason != null ? reason : "Revoked by admin");
        return ResponseEntity.noContent().build();
    }
}
