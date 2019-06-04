package com.weather.application.configuration;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * @author trupti.jankar
 *	This class provides encryption while storing the password
 */
@Configuration
public class WebMvcConfig implements WebMvcConfigurer {
	
	private static final Logger logger = LoggerFactory.getLogger(WebMvcConfig.class);
	/**
	 * This method will encrypt the password
	 * @return
	 */
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        logger.debug("--Application passwordEncoder configure--");
        return bCryptPasswordEncoder;
        
       
    }

}