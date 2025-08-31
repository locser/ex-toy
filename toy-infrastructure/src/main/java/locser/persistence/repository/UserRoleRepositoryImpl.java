package locser.persistence.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import locser.persistence.mapper.UserRoleJPAMapper;
import locser.toy.domain.model.entity.UserRole;
import locser.toy.domain.repository.UserRoleRepository;

@Service
public class UserRoleRepositoryImpl implements UserRoleRepository {

    private static final long ALL_RECORDS = -1L;
    private final UserRoleJPAMapper userRoleJPAMapper;

    public UserRoleRepositoryImpl(UserRoleJPAMapper userRoleJPAMapper) {
        this.userRoleJPAMapper = userRoleJPAMapper;
    }

    /**
     * Find user role by ID.
     */
    @Override
    public Optional<UserRole> findById(Long id) {
        return userRoleJPAMapper.findById(id);
    }

    /**
     * Find users with specific role.
     */
    @Override
    public List<UserRole> findActiveByRoleId(Long roleId) {
        return userRoleJPAMapper.findActiveByRoleId(roleId);
    }

    /**
     * Find specific user-role mapping.
     */
    @Override
    public Optional<UserRole> findByUserIdAndRoleId(Long userId, Long roleId) {
        return userRoleJPAMapper.findByUserIdAndRoleId(userId, roleId);
    }

    /**
     * Find expired role assignments.
     */
    @Override
    public List<UserRole> findExpiredRoles(LocalDateTime currentTime) {
        return userRoleJPAMapper.findExpiredRoles(currentTime);
    }

    /**
     * Save user role.
     */
    @Override
    public UserRole save(UserRole userRole) {
        return userRoleJPAMapper.save(userRole);
    }

    /**
     * Check if user has role.
     */
    @Override
    public boolean existsByUserIdAndRoleIdAndIsActive(Long userId, Long roleId, Boolean isActive) {
        return userRoleJPAMapper.existsByUserIdAndRoleIdAndIsActive(userId, roleId, isActive);
    }

    /**
     * Find all user roles.
     */
    @Override
    public List<UserRole> findAll() {
        return userRoleJPAMapper.findAll();
    }

    /**
     * Delete user role by ID.
     */
    @Override
    public void deleteById(Long id) {
        userRoleJPAMapper.deleteById(id);
    }

    /**
     * Deactivate expired roles.
     */
    @Override
    public int deactivateExpiredRoles(LocalDateTime currentTime) {
        return userRoleJPAMapper.deactivateExpiredRoles(currentTime);
    }

    /**
     * Find active roles for user.
     */
    @Override
    public List<UserRole> findActiveByUserId(Long userId) {
        return userRoleJPAMapper.findActiveByUserId(userId);
    }
}