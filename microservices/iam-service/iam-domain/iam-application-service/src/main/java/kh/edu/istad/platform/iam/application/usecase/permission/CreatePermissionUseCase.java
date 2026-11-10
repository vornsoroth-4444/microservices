package kh.edu.istad.platform.iam.application.usecase.permission;

import kh.edu.istad.platform.iam.application.dto.CreatePermissionCommand;
import kh.edu.istad.platform.iam.application.dto.PermissionResult;
import kh.edu.istad.platform.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.application.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.domain.exception.ConflictException;
import kh.edu.istad.platform.iam.domain.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
public class CreatePermissionUseCase {

    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;

    public CreatePermissionUseCase(PermissionRepository permissionRepository,
                                   CurrentActorProvider currentActorProvider) {
        this.permissionRepository = permissionRepository;
        this.currentActorProvider = currentActorProvider;
    }

    @Transactional
    public PermissionResult execute(CreatePermissionCommand cmd) {
        String resource = cmd.getResource().trim().toLowerCase();
        String action = cmd.getAction().trim().toLowerCase();
        String permissionName = resource + ":" + action;

        if (permissionRepository.existsByName(permissionName)) {
            throw new ConflictException("Permission already exists with name: " + permissionName);
        }

        String actor = currentActorProvider.getCurrentActor();
        Instant now = Instant.now();

        Permission permission = Permission.builder()
                .uuid(UUID.randomUUID())
                .name(permissionName)
                .resource(resource)
                .action(action)
                .description(cmd.getDescription())
                .system(false)
                .active(true)
                .deleted(false)
                .createdAt(now)
                .createdBy(actor)
                .updatedAt(now)
                .updatedBy(actor)
                .version(0L)
                .build();

        Permission saved = permissionRepository.save(permission);
        return PermissionDtoMapper.toResult(saved);
    }
}
