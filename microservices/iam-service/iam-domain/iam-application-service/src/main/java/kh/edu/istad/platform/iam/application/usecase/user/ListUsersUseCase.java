package kh.edu.istad.platform.iam.application.usecase.user;

import kh.edu.istad.platform.iam.application.dto.PageResult;
import kh.edu.istad.platform.iam.application.dto.UserFilterQuery;
import kh.edu.istad.platform.iam.application.dto.UserSummary;
import kh.edu.istad.platform.iam.application.port.out.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListUsersUseCase {

    private final UserRepository userRepository;

    public ListUsersUseCase(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public PageResult<UserSummary> execute(UserFilterQuery query) {
        if (query == null) {
            query = new UserFilterQuery();
        }
        return userRepository.findAll(query);
    }
}
