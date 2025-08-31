package locser.toy.domain.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import locser.toy.domain.model.entity.Permission;
import locser.toy.domain.model.entity.Role;
import locser.toy.domain.model.entity.RolePermission;
import locser.toy.domain.model.entity.Toy;
import locser.toy.domain.model.entity.UserRole;
import locser.toy.domain.repository.PermissionRepository;
import locser.toy.domain.repository.RolePermissionRepository;
import locser.toy.domain.repository.RoleRepository;
import locser.toy.domain.repository.ToyRepository;
import locser.toy.domain.repository.UserRoleRepository;
import locser.toy.domain.service.RBACService;

/**
 * Implementation of RBAC service for permission and role management.
 */
@Service
@Transactional
public class RBACServiceImpl implements RBACService {

    private final UserRoleRepository userRoleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final ToyRepository toyRepository;

    public RBACServiceImpl(
            UserRoleRepository userRoleRepository,
            RolePermissionRepository rolePermissionRepository,
            RoleRepository roleRepository,
            PermissionRepository permissionRepository,
            ToyRepository toyRepository) {
        this.userRoleRepository = userRoleRepository;
        this.rolePermissionRepository = rolePermissionRepository;
        this.roleRepository = roleRepository;
        this.permissionRepository = permissionRepository;
        this.toyRepository = toyRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasPermission(Long userId, String permissionName) {
        // Get user's active roles
        List<UserRole> userRoles = userRoleRepository.findActiveByUserId(userId);
        if (userRoles.isEmpty()) {
            return false;
        }

        // Get role IDs
        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        // Get permissions for these roles
        List<RolePermission> rolePermissions = rolePermissionRepository.findActiveByRoleIds(roleIds);

        // Check if any permission matches
        return rolePermissions.stream()
                .anyMatch(rp -> {
                    return permissionRepository.findById(rp.getPermissionId())
                            .map(Permission::getName)
                            .filter(name -> name.equals(permissionName))
                            .isPresent();
                });
    }

    @Override
    @Transactional(readOnly = true)
    public Set<String> getUserPermissions(Long userId) {
        List<UserRole> userRoles = userRoleRepository.findActiveByUserId(userId);

        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toList());

        List<RolePermission> rolePermissions = rolePermissionRepository.findActiveByRoleIds(roleIds);

        return rolePermissions.stream()
                .map(rp -> permissionRepository.findById(rp.getPermissionId()))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get().getName())
                .collect(Collectors.toSet());
    }

    @Override
    @Transactional(readOnly = true)
    public List<String> getUserRoles(Long userId) {
        List<UserRole> userRoles = userRoleRepository.findActiveByUserId(userId);

        return userRoles.stream()
                .map(ur -> roleRepository.findById(ur.getRoleId()))
                .filter(opt -> opt.isPresent())
                .map(opt -> opt.get().getName())
                .collect(Collectors.toList());
    }

    @Override
    public void assignRoleToUser(Long userId, String roleName, Long assignedBy) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        // Check if already assigned
        if (userRoleRepository.existsByUserIdAndRoleIdAndIsActive(userId, role.getId(), true)) {
            return; // Already assigned
        }

        // Deactivate existing assignment if exists
        userRoleRepository.findByUserIdAndRoleId(userId, role.getId())
                .ifPresent(ur -> {
                    ur.setIsActive(false);
                    userRoleRepository.save(ur);
                });

        // Create new assignment
        UserRole userRole = new UserRole(userId, role.getId(), assignedBy);
        userRoleRepository.save(userRole);
    }

    @Override
    public void removeRoleFromUser(Long userId, String roleName) {
        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RuntimeException("Role not found: " + roleName));

        userRoleRepository.findByUserIdAndRoleId(userId, role.getId())
                .ifPresent(ur -> {
                    ur.setIsActive(false);
                    userRoleRepository.save(ur);
                });
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasRole(Long userId, String roleName) {
        Role role = roleRepository.findByName(roleName).orElse(null);
        if (role == null) {
            return false;
        }

        return userRoleRepository.existsByUserIdAndRoleIdAndIsActive(userId, role.getId(), true);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAnyPermission(Long userId, String... permissions) {
        for (String permission : permissions) {
            if (hasPermission(userId, permission)) {
                return true;
            }
        }
        return false;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean hasAllPermissions(Long userId, String... permissions) {
        for (String permission : permissions) {
            if (!hasPermission(userId, permission)) {
                return false;
            }
        }
        return true;
    }

    @Override
    @Transactional(readOnly = true)
    public boolean isResourceOwner(Long userId, String resourceType, Long resourceId) {
        switch (resourceType.toUpperCase()) {
            case "TOY":
                Toy toy = toyRepository.findById(resourceId);
                return toy != null && toy.getUserId().equals(userId);
            // Add other resource types as needed
            default:
                return false;
        }
    }

    @Override
    public void cleanupExpiredRoles() {
        userRoleRepository.deactivateExpiredRoles(LocalDateTime.now());
    }
}