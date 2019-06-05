package com.weather.application.service;

import static com.jayway.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoMoreInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringRunner;

import com.weather.application.model.User;
import com.weather.application.model.WeatherHistory;
import com.weather.application.repository.WeatherHistoryRepository;

@RunWith(SpringRunner.class)
public class WeatherHistoryServiceTest {
	@Mock
	private WeatherHistoryRepository mockWeatherHistoryRepository;

	private WeatherHistoryService weatherHistoryServiceUnderTest;
	private static WeatherHistory weatherHistory;
	private static WeatherHistory weatherHistory1;
	private static List<WeatherHistory> weatherHistoryList;

	private static final String BASE_API_URL = "http://api.openweathermap.org/data/2.5/weather?APPID=";

	private static final String API_KEY = "988a11e3cf43cc97c3c4638fea47070e";
	private static final String APPEND_UNIT = "&units=metric";
	private String cityName = "London";
	final String endpointURL = BASE_API_URL + API_KEY + "&q=" + cityName + APPEND_UNIT;

	@Before
	public void setUp() {
		initMocks(this);
		weatherHistoryServiceUnderTest = new WeatherHistoryService(mockWeatherHistoryRepository);
		User user = new User();
		user.setId(5);
		user.setEmail("test@test.com");
		user.setDateOfBirth(new Date());

		weatherHistory = new WeatherHistory();
		weatherHistory.setId(100);
		weatherHistory.setCityName("London");
		weatherHistory.setWeatherDescription("Cloudy");
		weatherHistory.setCurrentTemperature(13.00);
		weatherHistory.setMinTemperature(10.00);
		weatherHistory.setMaxTemperature(30.00);
		weatherHistory.setSunrise("06:00");
		weatherHistory.setSunset("18:00");
		weatherHistory.setUser(user);

		weatherHistory1 = new WeatherHistory();
		weatherHistory1.setId(200);
		weatherHistory1.setCityName("Berlin");
		weatherHistory1.setWeatherDescription("Rain");
		weatherHistory1.setCurrentTemperature(10.00);
		weatherHistory1.setMinTemperature(05.00);
		weatherHistory1.setMaxTemperature(20.00);
		weatherHistory1.setSunrise("07:00");
		weatherHistory1.setSunset("17:00");
		weatherHistory1.setUser(user);

		Mockito.when(mockWeatherHistoryRepository.save(any())).thenReturn(weatherHistory);

	}

	@Test
	public void testSaveWeatherHistory() {
		// Setup
		final int id = 100;

		// Run the test
		final WeatherHistory result = weatherHistoryServiceUnderTest.saveWeatherHistory(weatherHistory);

		// Verify the results
		assertEquals(id, result.getId());
	}

	@Test
	public void testFindByCityNameAndUserId() {

		// Setup
		when(weatherHistoryServiceUnderTest.findByCityNameAndUserId(anyString(), anyLong())).thenReturn(getWeather());
		final String cityName = "London";
		final long userId = 5;
		final Double temp = 13.00;

		// Run the test
		final WeatherHistory fetchedresult = weatherHistoryServiceUnderTest.findByCityNameAndUserId(cityName, userId);

		// Verify the results
		assertEquals(temp, fetchedresult.getCurrentTemperature());
	}

	@Test
	public void testFindHistoryByUserId() {

		// Setup
		final long userId = 5;
		final int size = 2;
		when(weatherHistoryServiceUnderTest.findHistoryByUserId(anyLong())).thenReturn(createUserList());

		// Run the test
		final List<WeatherHistory> fetchedresult = weatherHistoryServiceUnderTest.findHistoryByUserId(userId);

		// Verify the results
		assertNotNull(fetchedresult);
		assertEquals(size, fetchedresult.size());
	}

	@Test
	public void testDeleteWeatherHistory() {

		doNothing().when(mockWeatherHistoryRepository).delete(any(WeatherHistory.class));

		weatherHistoryServiceUnderTest.deleteWeatherHistory(weatherHistory);

		verify(mockWeatherHistoryRepository, times(1)).delete((weatherHistory));

		verifyNoMoreInteractions(mockWeatherHistoryRepository);

	}

	@Test
	public void status200SearchByCityName() {

		given().param("q", cityName).when().get(endpointURL).then().assertThat().statusCode(200);
	}

	public static List<WeatherHistory> createUserList() {
		weatherHistoryList = new ArrayList<>();
		weatherHistoryList.add(weatherHistory);
		weatherHistoryList.add(weatherHistory1);
		return weatherHistoryList;
	}

	public static WeatherHistory getWeather() {
		return weatherHistory;
	}

}