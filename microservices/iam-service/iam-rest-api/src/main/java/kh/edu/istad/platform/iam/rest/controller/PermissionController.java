package kh.edu.istad.platform.iam.rest.controller;

import jakarta.validation.Valid;
import kh.edu.istad.platform.iam.application.dto.*;
import kh.edu.istad.platform.iam.application.usecase.permission.*;
import kh.edu.istad.platform.iam.rest.dto.RestDtoMapper;
import kh.edu.istad.platform.iam.rest.dto.request.CreatePermissionRequest;
import kh.edu.istad.platform.iam.rest.dto.request.UpdatePermissionRequest;
import kh.edu.istad.platform.iam.rest.dto.response.PageResponse;
import kh.edu.istad.platform.iam.rest.dto.response.PermissionResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/permissions")
public class PermissionController {

    private final CreatePermissionUseCase createPermissionUseCase;
    private final ListPermissionsUseCase listPermissionsUseCase;
    private final GetPermissionUseCase getPermissionUseCase;
    private final UpdatePermissionUseCase updatePermissionUseCase;
    private final DeletePermissionUseCase deletePermissionUseCase;
    private final ActivatePermissionUseCase activatePermissionUseCase;
    private final DeactivatePermissionUseCase deactivatePermissionUseCase;

    public PermissionController(CreatePermissionUseCase createPermissionUseCase,
                                ListPermissionsUseCase listPermissionsUseCase,
                                GetPermissionUseCase getPermissionUseCase,
                                UpdatePermissionUseCase updatePermissionUseCase,
                                DeletePermissionUseCase deletePermissionUseCase,
                                ActivatePermissionUseCase activatePermissionUseCase,
                                DeactivatePermissionUseCase deactivatePermissionUseCase) {
        this.createPermissionUseCase = createPermissionUseCase;
        this.listPermissionsUseCase = listPermissionsUseCase;
        this.getPermissionUseCase = getPermissionUseCase;
        this.updatePermissionUseCase = updatePermissionUseCase;
        this.deletePermissionUseCase = deletePermissionUseCase;
        this.activatePermissionUseCase = activatePermissionUseCase;
        this.deactivatePermissionUseCase = deactivatePermissionUseCase;
    }

    @PostMapping
    public ResponseEntity<PermissionResponse> createPermission(@Valid @RequestBody CreatePermissionRequest request) {
        CreatePermissionCommand cmd = new CreatePermissionCommand(
                request.getResource(),
                request.getAction(),
                request.getDescription()
        );
        PermissionResult result = createPermissionUseCase.execute(cmd);
        return ResponseEntity.status(HttpStatus.CREATED).body(RestDtoMapper.toPermissionResponse(result));
    }

    @GetMapping
    public ResponseEntity<PageResponse<PermissionResponse>> listPermissions(
            @RequestParam(name = "resource", required = false) String resource,
            @RequestParam(name = "action", required = false) String action,
            @RequestParam(name = "page", defaultValue = "0") int page,
            @RequestParam(name = "size", defaultValue = "20") int size,
            @RequestParam(name = "sort", defaultValue = "name,asc") String sort) {
        PermissionFilterQuery query = PermissionFilterQuery.builder()
                .resource(resource)
                .action(action)
                .page(page)
                .size(size)
                .sort(sort)
                .build();

        PageResult<PermissionResult> pageResult = listPermissionsUseCase.execute(query);
        List<PermissionResponse> items = pageResult.getItems().stream()
                .map(RestDtoMapper::toPermissionResponse)
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
    public ResponseEntity<PermissionResponse> getPermission(@PathVariable UUID uuid) {
        PermissionResult result = getPermissionUseCase.execute(uuid);
        return ResponseEntity.ok(RestDtoMapper.toPermissionResponse(result));
    }

    @PatchMapping("/{uuid}")
    public ResponseEntity<PermissionResponse> updatePermission(@PathVariable UUID uuid,
                                                               @Valid @RequestBody UpdatePermissionRequest request) {
        UpdatePermissionCommand cmd = new UpdatePermissionCommand(
                uuid,
                request.getResource(),
                request.getAction(),
                request.getDescription()
        );
        PermissionResult result = updatePermissionUseCase.execute(cmd);
        return ResponseEntity.ok(RestDtoMapper.toPermissionResponse(result));
    }

    @DeleteMapping("/{uuid}")
    public ResponseEntity<Void> deletePermission(@PathVariable UUID uuid) {
        deletePermissionUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/activate")
    public ResponseEntity<Void> activatePermission(@PathVariable UUID uuid) {
        activatePermissionUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{uuid}/deactivate")
    public ResponseEntity<Void> deactivatePermission(@PathVariable UUID uuid) {
        deactivatePermissionUseCase.execute(uuid);
        return ResponseEntity.noContent().build();
    }
}
