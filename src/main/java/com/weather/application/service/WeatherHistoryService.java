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
@Service("weatherHostoryService")
public class WeatherHistoryService {

	private static final Logger logger = LoggerFactory.getLogger(WeatherHistoryService.class);
	@Value("${spring.app.key}")
	private String appKey;

	private WeatherHistoryRepository weatherHistoryRepository;

	public WeatherHistory findByCityNameAndUserId(String cityName, long userId) {
		return weatherHistoryRepository.findByCityNameAndUserId(cityName, userId);
	}

	@Autowired
	public WeatherHistoryService(WeatherHistoryRepository weatherHistoryRepository) {
		this.weatherHistoryRepository = weatherHistoryRepository;
	}

	public List<WeatherHistory> findHistoryByUserId(long userId) {
		return weatherHistoryRepository.findByUserId(userId);
	}

	public WeatherHistory findById(int id) {
		return weatherHistoryRepository.findById(id);
	}

	public List<WeatherHistory> findAllById(long[] ids) {
		return weatherHistoryRepository.findAllById(ids);
	}

	public WeatherHistory editWeatherHistory(WeatherHistory weatherHistory) {
		return weatherHistoryRepository.save(weatherHistory);
	}

	public WeatherHistory saveWeatherHistory(WeatherHistory weatherHistory) {
		return weatherHistoryRepository.save(weatherHistory);
	}

	public void deleteWeatherHistory(WeatherHistory weatherHistory) {
		weatherHistoryRepository.delete(weatherHistory);
	}

	public void deleteById(long[] ids) {
		for (long id : ids) {
			weatherHistoryRepository.deleteById(id);
		}
		logger.debug("--Application deleteById load --");
	}

	public WeatherHistory getWeather(String cityName, WeatherHistory weatherHistory) {
		String result = "";
		String urlStr = "http://api.openweathermap.org/data/2.5/weather?APPID=" + appKey + "&q=" + cityName
				+ "&units=metric";
		try {
			URL url_weather = new URL(urlStr);

			HttpURLConnection httpURLConnection = (HttpURLConnection) url_weather.openConnection();

			if (httpURLConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {

				InputStreamReader inputStreamReader = new InputStreamReader(httpURLConnection.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(inputStreamReader, 8192);
				String line = null;
				while ((line = bufferedReader.readLine()) != null) {
					result += line;
				}

				bufferedReader.close();

				ParseResult(result, weatherHistory);

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
		logger.debug("--Application getWeather load --");
		return weatherHistory;
	}

	static private WeatherHistory ParseResult(String json, WeatherHistory weatherResult) throws JSONException {

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

	public static String formatTime(Date dateObject) {
		SimpleDateFormat timeFormat = new SimpleDateFormat("h:mm a");
		return timeFormat.format(dateObject);
	}

}