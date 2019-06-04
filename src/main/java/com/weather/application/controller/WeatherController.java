package com.weather.application.controller;

import java.util.Date;
import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import com.weather.application.model.User;
import com.weather.application.model.WeatherHistory;
import com.weather.application.service.UserService;
import com.weather.application.service.WeatherHistoryService;

/**
 * 
 * @author trupti.jankar 
 * This Weather Controller displays weather data and perform edit/delete operations
 *
 */
@Controller
public class WeatherController {

	private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);

	@Autowired
	private WeatherHistoryService weatherHistoryService;

	@Autowired
	private UserService userService;

	/**
	 * This method will provide the weather data for searched city name and provide
	 * history records
	 * 
	 * @param city name, Model
	 * @return display the weather for city and history records
	 */
	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String searchWeatherData(@RequestParam(value = "search", required = false) String q, Model model) {
		logger.debug("--Application searchWeatherData load--");
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		WeatherHistory weatherHistory = new WeatherHistory();
		if (q != null && !q.isEmpty()) {
			logger.debug("--Application /index load :: Get weather data--");

			weatherHistory = weatherHistoryService.findByCityNameAndUserId(q, user.getId());
			if (weatherHistory == null) {
				weatherHistory = new WeatherHistory();
			}
			weatherHistory.setCreatedAt(new Date());
			weatherHistory.setUser(user);
			weatherHistory = weatherHistoryService.getWeather(q, weatherHistory);
			if (weatherHistory.getCityName() == null) {
				model.addAttribute("errorMessage", "Invalid city name" + q);
				throw new RuntimeException("Invalid city name  :" + q);
			}
			model.addAttribute("search", weatherHistory);
			weatherHistoryService.saveWeatherHistory(weatherHistory);
		}
		List<WeatherHistory> weatherHistoryList = weatherHistoryService
				.findHistoryByUserId(weatherHistory.getUser().getId());
		model.addAttribute("userName", "Welcome " + user.getEmail());
		model.addAttribute("adminMessage", "Content Available Only for Users with Admin Role");
		model.addAttribute("weatherHistoryList", weatherHistoryList);
		return "weather";

	}

	/**
	 * This method will delete the single selected record
	 * 
	 * @param id
	 */
	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public ModelAndView deleteWeatherData(@PathVariable("id") int id) {
		logger.debug("--Application deleteWeatherData load--");
		ModelAndView modelAndView = new ModelAndView();

		WeatherHistory weatherHistory = weatherHistoryService.findById(id);
		long userId = weatherHistory.getUser().getId();
		weatherHistoryService.deleteWeatherHistory(weatherHistory);

		List<WeatherHistory> weatherHistoryList = weatherHistoryService.findHistoryByUserId(userId);
		modelAndView.addObject("weatherHistoryList", weatherHistoryList);
		modelAndView.setViewName("weather");
		return modelAndView;
	}

	/**
	 * This method will delete multiple selected records
	 * 
	 * @param selected record ids
	 *
	 */
	@RequestMapping(value = "/bulkDelete", method = RequestMethod.GET)
	public ModelAndView deleteBulkUser(@PathVariable("ids") long[] ids, Model model) {
		logger.debug("--Application /deleteBulkUser load--");
		ModelAndView modelAndView = new ModelAndView();

		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		weatherHistoryService.deleteById(ids);
		List<WeatherHistory> searchResults = weatherHistoryService.findHistoryByUserId(user.getId());
		modelAndView.addObject("weatherHistoryList", searchResults);
		modelAndView.setViewName("redirect:/admin/home");

		return modelAndView;
	}

	@RequestMapping(value = "/edit/{id}", method = RequestMethod.GET)
	public String showUpdateForm(@PathVariable("id") int id, Model model) {
		logger.debug("--Application showUpdateForm form load--");
		WeatherHistory weatherHistory = weatherHistoryService.findById(id);
		model.addAttribute("weatherHistory", weatherHistory);
		model.addAttribute("adminMessage", "Record updated successfully.");

		return "edit";
	}

	@RequestMapping(value = "/weather", method = RequestMethod.GET)
	public String displayWeatherData(Model model) {
		logger.debug("--Application displayWeatherData form load--");
		WeatherHistory weatherHistory = (WeatherHistory) model.asMap().get("weatherHistory");
		model.addAttribute("userName", "Welcome " + weatherHistory.getUser().getEmail());
		model.addAttribute("search", weatherHistory);
		List<WeatherHistory> weatherHistoryList = weatherHistoryService
				.findHistoryByUserId(weatherHistory.getUser().getId());
		model.addAttribute("weatherHistoryList", weatherHistoryList);
		return "/admin/home";
	}

	/**
	 * This POST method will update the selected weather history record
	 * 
	 * @param id
	 * @param weatherHistory
	 * @param BindingResult
	 * @param Model
	 * @return this will return to the home page
	 */
	@RequestMapping(value = "/edit/{id}", method = RequestMethod.POST)
	public String editWeatherData(@PathVariable("id") int id, @Valid WeatherHistory weatherHistory,
			BindingResult result, Model model) {
		logger.debug("--Application editWeatherData load--");
		if (result.hasErrors()) {
			logger.debug("--Application editWeatherData error--");
			weatherHistory.setId(id);
			return "edit";
		}
		WeatherHistory editWeatherHistory = (WeatherHistory) model.asMap().get("weatherHistory");
		weatherHistoryService.saveWeatherHistory(editWeatherHistory);
		model.addAttribute("search", editWeatherHistory);
		List<WeatherHistory> weatherHistoryList = weatherHistoryService
				.findHistoryByUserId(editWeatherHistory.getUser().getId());
		model.addAttribute("weatherHistoryList", weatherHistoryList);
		return "weather";
	}
}
