package com.weather.application.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weather.application.model.User;

/**
 * @author trupti.jankar
 *	This is Repository to access CRUD methods on User table using in built JpaRepository
 */
@Repository("userRepository")
public interface UserRepository extends JpaRepository<User, Long> {
    User findByEmail(String email);
}