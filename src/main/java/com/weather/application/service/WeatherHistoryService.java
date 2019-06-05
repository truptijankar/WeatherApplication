package com.weather.application.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.weather.application.model.WeatherHistory;
import com.weather.application.repository.WeatherHistoryRepository;

/**
 * @author trupti.jankar This is service class to access data layer for Weather
 *         History
 */
@Service("WeatherHistoryService")
public class WeatherHistoryService {

	private static final Logger logger = LoggerFactory.getLogger(WeatherHistoryService.class);
	private static final String APPEND_UNIT = "&units=metric";
	private static final String TIME_FORMAT = "h:mm a";
	private int INPUT_BUFFER_SIZE = 8192;

	/**
	 * Read values from Application.properties for App URL and App Key.
	 */
	@Value("${spring.app.key}")
	private String appKey;

	@Value("${spring.app.api.url}")
	private String appUrl;

	private WeatherHistoryRepository weatherHistoryRepository;

	@Autowired
	public WeatherHistoryService(WeatherHistoryRepository weatherHistoryRepository) {
		this.weatherHistoryRepository = weatherHistoryRepository;
	}

	/**
	 * This method will find if user has already has search history for the city
	 * name
	 * 
	 * @param cityName and userId
	 * @return It will return the history record if cityName has already been search
	 */
	public WeatherHistory findByCityNameAndUserId(String cityName, long userId) {
		return weatherHistoryRepository.findByCityNameAndUserId(cityName, userId);
	}

	/**
	 * This method will provide the history of the cities user has searched.
	 * 
	 * @param userId
	 * @return It will return list of history records
	 */
	public List<WeatherHistory> findHistoryByUserId(long userId) {
		return weatherHistoryRepository.findByUserId(userId);
	}

	/**
	 * This method will provide the single record history for Edit/Delete operation.
	 * 
	 * @param weather_id
	 * @return It will return history record based on weather_id
	 */
	public WeatherHistory findById(long id) {
		return weatherHistoryRepository.findById(id);
	}

	/**
	 * This method insert/update the weather history record.
	 * 
	 * @param weatherHistory
	 * @return Inserted/updated weather history entity
	 */
	public WeatherHistory saveWeatherHistory(WeatherHistory weatherHistory) {
		return weatherHistoryRepository.save(weatherHistory);
	}

	/**
	 * This method remove the weather history record.
	 * 
	 * @param weatherHistory
	 */
	public void deleteWeatherHistory(WeatherHistory weatherHistory) {
		weatherHistoryRepository.delete(weatherHistory);
	}

	/**
	 * This method remove/s the weather history record for selected weather Ids.
	 * 
	 * @param weather_ids
	 */
	public void deleteById(long[] ids) {
		logger.debug("--Application deleteById load --");
		for (long id : ids) {
			weatherHistoryRepository.deleteById(id);
		}
	}

	/**
	 * This method will fetch the weather JSON response from the OpenWeatherMap API
	 * call
	 * 
	 * @param cityName
	 * @return Weather Data
	 */
	public WeatherHistory getWeather(String cityName, WeatherHistory weatherHistory) {
		logger.debug("-- getWeather --");
		String result = "";
		StringBuilder urlStr = new StringBuilder();
		try {
			urlStr.append(appUrl);
			urlStr.append(appKey);
			urlStr.append("&q=");
			urlStr.append(cityName);
			urlStr.append(APPEND_UNIT);

			URL url_weather = new URL(urlStr.toString());

			HttpURLConnection httpURLConnection = (HttpURLConnection) url_weather.openConnection();

			if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

				InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader, INPUT_BUFFER_SIZE);
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					result += line;
				}
				ParseResult(result, weatherHistory);
				bufferedReader.close();

			} else {
				logger.error("Error in httpURLConnection.getResponseCode()!!!");
			}

		} catch (MalformedURLException ex) {
			logger.error("MalformedURLException!!!", ex);
			throw new RuntimeException("MalformedURLException :" + ex.getMessage());
		} catch (IOException ex) {
			logger.error("IOException!!!", ex);
			throw new RuntimeException("IOException :" + ex.getMessage());
		} catch (JSONException ex) {
			logger.error("JSONException!!!", ex);
			throw new RuntimeException("JSONException :" + ex.getMessage());
		}
		return weatherHistory;
	}

	/**
	 * This method will parse the JSON result into Weather entity
	 * 
	 * @param weather response json, weatherResult
	 * @return weather entity with parsed values
	 * @throws JSONException
	 */
	static private WeatherHistory ParseResult(String json, WeatherHistory weatherResult) throws JSONException {
		logger.debug("-- ParseResult --");
		JSONObject jsonObject = new JSONObject(json);
		String result_weather;

		logger.debug("Number of object = " + jsonObject.length() + "\n\n");
		// "sys"
		JSONObject JSONObject_sys = jsonObject.getJSONObject("sys");
		String result_country = JSONObject_sys.getString("country");
		int result_sunrise = JSONObject_sys.getInt("sunrise");
		weatherResult.setSunrise(formatTime(new Date(result_sunrise * 1000)));
		int result_sunset = JSONObject_sys.getInt("sunset");
		weatherResult.setSunset(formatTime(new Date(result_sunset * 1000)));

		// "weather"
		JSONArray JSONArray_weather = jsonObject.getJSONArray("weather");
		if (JSONArray_weather.length() > 0) {
			JSONObject JSONObject_weather = JSONArray_weather.getJSONObject(0);
			result_weather = JSONObject_weather.getString("description");
		} else {
			result_weather = "weather not specified!";
			logger.error("weather not specified!");
		}
		weatherResult.setWeatherDescription(result_weather);

		// "main"
		JSONObject JSONObject_main = jsonObject.getJSONObject("main");
		Double result_temp = JSONObject_main.getDouble("temp");
		weatherResult.setCurrentTemperature(result_temp);
		Double result_temp_min = JSONObject_main.getDouble("temp_min");
		weatherResult.setMinTemperature(result_temp_min);
		Double result_temp_max = JSONObject_main.getDouble("temp_max");
		weatherResult.setMaxTemperature(result_temp_max);

		// "name"
		String result_name = jsonObject.getString("name");
		weatherResult.setCityName(result_name);

		logger.debug("sys\tcountry: " + result_country + "\tsunrise: " + result_sunrise + "\tsunset: " + result_sunset
				+ "\n" + result_weather + "\n" + "main\ttemp: " + result_temp + "\ttemp_min: " + result_temp_min
				+ "\ttemp_max: " + result_temp_min + "\n" + "City name: " + result_name + "\n" + "\n");
		return weatherResult;
	}

	/**
	 * This method will format the date object into time format
	 * 
	 * @param Date object
	 * @return object in time format
	 */
	private static String formatTime(Date dateObject) {
		logger.debug("-- formatTime --");
		SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT);
		return timeFormat.format(dateObject);
	}

}