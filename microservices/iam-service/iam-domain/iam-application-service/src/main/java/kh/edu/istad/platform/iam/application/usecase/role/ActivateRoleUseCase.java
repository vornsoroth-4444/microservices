package kh.edu.istad.platform.iam.application.usecase.role;

import kh.edu.istad.platform.iam.application.port.out.CurrentActorProvider;
import kh.edu.istad.platform.iam.application.port.out.RoleRepository;
import kh.edu.istad.platform.iam.domain.exception.ResourceNotFoundException;
import kh.edu.istad.platform.iam.domain.model.Role;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ActivateRoleUseCase {

    private final RoleRepository roleRepository;
    private final CurrentActorProvider currentActorProvider;

    public ActivateRoleUseCase(RoleRepository roleRepository, CurrentActorProvider currentActorProvider) {
        this.roleRepository = roleRepository;
        this.currentActorProvider = currentActorProvider;
    }

    @Transactional
    public void execute(UUID roleUuid) {
        Role role = roleRepository.findByUuid(roleUuid)
                .filter(r -> !r.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with uuid: " + roleUuid));

        role.activate(currentActorProvider.getCurrentActor());
        roleRepository.save(role);
    }
}
