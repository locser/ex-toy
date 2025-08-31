package locser.persistence.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import locser.persistence.mapper.RoleJPAMapper;
import locser.toy.domain.model.entity.Role;
import locser.toy.domain.repository.RoleRepository;

/**
 * Implementation of RoleRepository using JPA.
 */
@Service
public class RoleRepositoryImpl implements RoleRepository {

    private final RoleJPAMapper roleJPAMapper;

    public RoleRepositoryImpl(RoleJPAMapper roleJPAMapper) {
        this.roleJPAMapper = roleJPAMapper;
    }

    /**
     * Find role by ID.
     */
    @Override
    public Optional<Role> findById(Long id) {
        return roleJPAMapper.findById(id);
    }

    /**
     * Find role by name.
     */
    @Override
    public Optional<Role> findByName(String name) {
        return roleJPAMapper.findByName(name);
    }

    /**
     * Find all active roles.
     */
    @Override
    public List<Role> findAllActive() {
        return roleJPAMapper.findAllActive();
    }

    /**
     * Save role.
     */
    @Override
    public Role save(Role role) {
        return roleJPAMapper.save(role);
    }

    /**
     * Check if role exists by name.
     */
    @Override
    public boolean existsByName(String name) {
        return roleJPAMapper.existsByName(name);
    }

    /**
     * Find all roles.
     */
    @Override
    public List<Role> findAll() {
        return roleJPAMapper.findAll();
    }

    /**
     * Delete role by ID.
     */
    @Override
    public void deleteById(Long id) {
        roleJPAMapper.deleteById(id);
    }
}