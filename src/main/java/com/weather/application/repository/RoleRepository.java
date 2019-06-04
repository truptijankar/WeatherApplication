package com.weather.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weather.application.model.Role;

/**
 * @author trupti.jankar
 *	This is Repository to access CRUD methods on Role table using in built JpaRepository
 */
@Repository("roleRepository")
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Role findByRole(String role);

}
