package com.weather.application.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.weather.application.model.WeatherHistory;

/**
 * @author trupti.jankar
 *	This is Repository to access CRUD methods on Weather History table using in built JpaRepository
 */
@Repository("WeatherHistoryRepository")
public interface WeatherHistoryRepository extends JpaRepository<WeatherHistory, Long> {

	List<WeatherHistory> findByUserId(long userId);

	WeatherHistory findById(long userId);
	
	List<WeatherHistory> findAllById(long[] ids);

	WeatherHistory findByCityNameAndUserId(String cityName, long userId);
	
}