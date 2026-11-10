package kh.edu.istad.platform.iam.application.usecase.identity;

import kh.edu.istad.platform.iam.application.dto.IdentityResult;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import kh.edu.istad.platform.iam.domain.exception.ResourceNotFoundException;
import kh.edu.istad.platform.iam.domain.model.Identity;
import kh.edu.istad.platform.iam.domain.model.User;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class ListUserIdentitiesUseCase {

    private final UserRepository userRepository;

    public ListUserIdentitiesUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<IdentityResult> execute(UUID userUuid) {
        User user = userRepository.findByUuid(userUuid)
                .filter(u -> !u.isDeleted())
                .orElseThrow(() -> new ResourceNotFoundException("User not found with uuid: " + userUuid));

        List<Identity> identities = userRepository.findIdentitiesByUserId(user.getId());
        return identities.stream()
                .filter(i -> !i.isDeleted())
                .map(IdentityDtoMapper::toResult)
                .toList();
    }
}
