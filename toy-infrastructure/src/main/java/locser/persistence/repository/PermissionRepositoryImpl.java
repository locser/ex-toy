package locser.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import locser.persistence.mapper.PermissionJPAMapper;
import locser.toy.domain.model.entity.Permission;
import locser.toy.domain.repository.PermissionRepository;

/**
 * Implementation of PermissionRepository using JPA.
 */
@Service
public class PermissionRepositoryImpl implements PermissionRepository {

    private final PermissionJPAMapper permissionJPAMapper;

    public PermissionRepositoryImpl(PermissionJPAMapper permissionJPAMapper) {
        this.permissionJPAMapper = permissionJPAMapper;
    }

    /**
     * Find permission by ID.
     */
    @Override
    public Optional<Permission> findById(Long id) {
        return permissionJPAMapper.findById(id);
    }

    /**
     * Find permission by name.
     */
    @Override
    public Optional<Permission> findByName(String name) {
        return permissionJPAMapper.findByName(name);
    }

    /**
     * Find all active permissions.
     */
    @Override
    public List<Permission> findAllActive() {
        return permissionJPAMapper.findAllActive();
    }

    /**
     * Find permissions by resource.
     */
    @Override
    public List<Permission> findByResource(String resource) {
        return permissionJPAMapper.findByResource(resource);
    }

    /**
     * Find permissions by resource and action.
     */
    @Override
    public List<Permission> findByResourceAndAction(String resource, String action) {
        return permissionJPAMapper.findByResourceAndAction(resource, action);
    }

    /**
     * Save permission.
     */
    @Override
    public Permission save(Permission permission) {
        return permissionJPAMapper.save(permission);
    }

    /**
     * Check if permission exists by name.
     */
    @Override
    public boolean existsByName(String name) {
        return permissionJPAMapper.existsByName(name);
    }

    /**
     * Find all permissions.
     */
    @Override
    public List<Permission> findAll() {
        return permissionJPAMapper.findAll();
    }

    /**
     * Delete permission by ID.
     */
    @Override
    public void deleteById(Long id) {
        permissionJPAMapper.deleteById(id);
    }
}