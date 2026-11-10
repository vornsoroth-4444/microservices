package kh.edu.istad.platform.iam.domain.service;

import kh.edu.istad.platform.iam.domain.model.PermissionEffect;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

public final class EffectivePermissionCalculator {

    private EffectivePermissionCalculator() {}

    /**
     * Effective permissions = active role permissions + direct GRANT − direct DENY.
     */
    public static Set<String> calculate(Collection<String> rolePermissions,
                                        Map<String, PermissionEffect> directPermissions) {
        Set<String> effective = new LinkedHashSet<>();
        if (rolePermissions != null) {
            effective.addAll(rolePermissions);
        }
        if (directPermissions != null) {
            for (Map.Entry<String, PermissionEffect> entry : directPermissions.entrySet()) {
                if (entry.getValue() == PermissionEffect.GRANT) {
                    effective.add(entry.getKey());
                } else if (entry.getValue() == PermissionEffect.DENY) {
                    effective.remove(entry.getKey());
                }
            }
        }
        return effective;
    }
}
