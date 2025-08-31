package locser.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import locser.persistence.mapper.RolePermissionJPAMapper;
import locser.toy.domain.model.entity.RolePermission;
import locser.toy.domain.repository.RolePermissionRepository;

/**
 * Implementation of RolePermissionRepository using JPA.
 */
@Service
public class RolePermissionRepositoryImpl implements RolePermissionRepository {

    private final RolePermissionJPAMapper rolePermissionJPAMapper;

    public RolePermissionRepositoryImpl(RolePermissionJPAMapper rolePermissionJPAMapper) {
        this.rolePermissionJPAMapper = rolePermissionJPAMapper;
    }

    /**
     * Find role permission by ID.
     */
    @Override
    public Optional<RolePermission> findById(Long id) {
        return rolePermissionJPAMapper.findById(id);
    }

    /**
     * Find active permissions for role.
     */
    @Override
    public List<RolePermission> findActiveByRoleId(Long roleId) {
        return rolePermissionJPAMapper.findActiveByRoleId(roleId);
    }

    /**
     * Find roles with specific permission.
     */
    @Override
    public List<RolePermission> findActiveByPermissionId(Long permissionId) {
        return rolePermissionJPAMapper.findActiveByPermissionId(permissionId);
    }

    /**
     * Find specific role-permission mapping.
     */
    @Override
    public Optional<RolePermission> findByRoleIdAndPermissionId(Long roleId, Long permissionId) {
        return rolePermissionJPAMapper.findByRoleIdAndPermissionId(roleId, permissionId);
    }

    /**
     * Save role permission.
     */
    @Override
    public RolePermission save(RolePermission rolePermission) {
        return rolePermissionJPAMapper.save(rolePermission);
    }

    /**
     * Check if role has permission.
     */
    @Override
    public boolean existsByRoleIdAndPermissionIdAndIsActive(Long roleId, Long permissionId, Boolean isActive) {
        return rolePermissionJPAMapper.existsByRoleIdAndPermissionIdAndIsActive(roleId, permissionId, isActive);
    }

    /**
     * Find all role permissions.
     */
    @Override
    public List<RolePermission> findAll() {
        return rolePermissionJPAMapper.findAll();
    }

    /**
     * Delete role permission by ID.
     */
    @Override
    public void deleteById(Long id) {
        rolePermissionJPAMapper.deleteById(id);
    }

    /**
     * Find permissions by multiple role IDs.
     */
    @Override
    public List<RolePermission> findActiveByRoleIds(List<Long> roleIds) {
        return rolePermissionJPAMapper.findActiveByRoleIds(roleIds);
    }
}