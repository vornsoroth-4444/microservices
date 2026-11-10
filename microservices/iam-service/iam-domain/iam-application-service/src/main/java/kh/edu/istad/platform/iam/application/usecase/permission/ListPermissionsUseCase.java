package kh.edu.istad.platform.iam.application.usecase.permission;

import kh.edu.istad.platform.iam.application.dto.PageResult;
import kh.edu.istad.platform.iam.application.dto.PermissionFilterQuery;
import kh.edu.istad.platform.iam.application.dto.PermissionResult;
import kh.edu.istad.platform.iam.application.port.out.PermissionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ListPermissionsUseCase {

    private final PermissionRepository permissionRepository;

    public ListPermissionsUseCase(PermissionRepository permissionRepository) {
        this.permissionRepository = permissionRepository;
    }

    @Transactional(readOnly = true)
    public PageResult<PermissionResult> execute(PermissionFilterQuery query) {
        if (query == null) {
            query = new PermissionFilterQuery();
        }
        return permissionRepository.findAll(query);
    }
}
