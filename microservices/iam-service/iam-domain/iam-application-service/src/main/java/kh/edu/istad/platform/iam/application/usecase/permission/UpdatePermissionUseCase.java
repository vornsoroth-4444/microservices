package kh.edu.istad.platform.iam.application.usecase.permission;

import kh.edu.istad.platform.iam.application.dto.PermissionResult;
import kh.edu.istad.platform.iam.application.dto.UpdatePermissionCommand;
import kh.edu.istad.platform.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.application.port.out.PermissionRepository;
import kh.edu.istad.platform.iam.domain.exception.ResourceNotFoundException;
import kh.edu.istad.platform.iam.domain.model.Permission;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdatePermissionUseCase {

    private final PermissionRepository permissionRepository;
    private final CurrentActorProvider currentActorProvider;

    public UpdatePermissionUseCase(PermissionRepository permissionRepository,
                                  CurrentActorProvider currentActorProvider) {
        this.permissionRepository = permissionRepository;
        this.currentActorProvider = currentActorProvider;
    }

    @Transactional
    public PermissionResult execute(UpdatePermissionCommand cmd) {
        Permission perm = permissionRepository.findByUuid(cmd.getPermissionUuid())
                .filter(p -> !p.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found with uuid: " + cmd.getPermissionUuid()));

        String actor = currentActorProvider.getCurrentActor();
        perm.update(cmd.getResource(), cmd.getAction(), cmd.getDescription(), actor);
        Permission saved = permissionRepository.save(perm);
        return PermissionDtoMapper.toResult(saved);
    }
}
