package com.weather.application.service;

import java.util.Arrays;
import java.util.HashSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.weather.application.model.Role;
import com.weather.application.model.User;
import com.weather.application.repository.RoleRepository;
import com.weather.application.repository.UserRepository;


/**
 * @author trupti.jankar
 *	This is service class to access data layer for User profile
 */
@Service("UserService")
public class UserService {
	
	private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private UserRepository userRepository;
    private RoleRepository roleRepository;
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    public UserService(UserRepository userRepository,
                       RoleRepository roleRepository,
                       BCryptPasswordEncoder bCryptPasswordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.bCryptPasswordEncoder = bCryptPasswordEncoder; 
    }

    /**
     * This method will find the user by email if it already exists
     * @author trupti.jankar
     * @param email
     * @return User entity if it exists
     */
    public User findUserByEmail(String email) {
    	logger.debug("-- findUserByEmail --");
        return userRepository.findByEmail(email);
    }

    /**
     * This method will save the user entity (create a new User)
     * @param user
     * @return user entity
     */
    public User saveUser(User user) {
    	logger.debug("-- saveUser --");
        user.setPassword(bCryptPasswordEncoder.encode(user.getPassword()));
        user.setActive(1);
        Role userRole = roleRepository.findByRole("ADMIN");
        user.setRoles(new HashSet<Role>(Arrays.asList(userRole)));
        return userRepository.save(user);
    }

}