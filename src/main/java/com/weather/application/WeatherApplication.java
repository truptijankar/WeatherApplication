package com.weather.application;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author trupti.jankar
 *	This class contains the main method of the Application
 */
@SpringBootApplication
public class WeatherApplication {

	private static final Logger logger = LoggerFactory.getLogger(WeatherApplication.class);
    public static void main(String[] args) {
        SpringApplication.run(WeatherApplication.class, args);
        logger.debug("--WeatherApplication Started--");
    }
}
