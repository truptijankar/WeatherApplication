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

@Controller
public class WeatherController {
	
	private static final Logger logger = LoggerFactory.getLogger(WeatherController.class);

	@Autowired
	private WeatherHistoryService weatherHistoryService;

	@Autowired
	private UserService userService;

	@RequestMapping(value = "/search", method = RequestMethod.GET)
	public String search(@RequestParam(value = "search", required = false) String q, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		WeatherHistory weatherHistory = new WeatherHistory();
		if (q != null && !q.isEmpty()) {
			logger.debug("--Application /index load :: Get weather data--");
			
			weatherHistory = weatherHistoryService.findByCityNameAndUserId(q, user.getId());
			if(weatherHistory==null) {
				weatherHistory = new WeatherHistory();
			}
			weatherHistory.setCreatedAt(new Date());
			weatherHistory.setUser(user);
			weatherHistory = weatherHistoryService.getWeather(q, weatherHistory);
			if(weatherHistory.getCityName() == null) {
				model.addAttribute("errorMessage", "Invalid city name" +q);
				throw new RuntimeException("Invalid city name  :" +q);
			}
			model.addAttribute("search", weatherHistory);
			weatherHistoryService.saveWeatherHistory(weatherHistory);
		}
		List<WeatherHistory> weatherHistoryList = weatherHistoryService.findHistoryByUserId(weatherHistory.getUser().getId());	
		model.addAttribute("userName", "Welcome " +user.getEmail());
		model.addAttribute("adminMessage","Content Available Only for Users with Admin Role");
		model.addAttribute("weatherHistoryList", weatherHistoryList);
		logger.debug("--Application /index load--");
		return "admin/home";

	}

	@RequestMapping(value = "/delete/{id}", method = RequestMethod.GET)
	public ModelAndView deleteUser(@PathVariable("id") int id) {
		ModelAndView modelAndView = new ModelAndView();

		WeatherHistory weatherHistory = weatherHistoryService.findById(id);
		long userId = weatherHistory.getUser().getId();
		weatherHistoryService.deleteWeatherHistory(weatherHistory);

		List<WeatherHistory> weatherHistoryList = weatherHistoryService.findHistoryByUserId(userId);
		modelAndView.addObject("weatherHistoryList", weatherHistoryList);
		modelAndView.setViewName("redirect:/admin/home");
		logger.debug("--Application /delete load--");
		return modelAndView;
	}

	@RequestMapping(value = "/bulkDelete", method = RequestMethod.GET)
	public ModelAndView deleteBulkUser(@PathVariable("ids") long[] ids) {
		ModelAndView modelAndView = new ModelAndView();
		
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		User user = userService.findUserByEmail(auth.getName());
		weatherHistoryService.deleteById(ids);
		List<WeatherHistory> searchResults = weatherHistoryService.findHistoryByUserId(user.getId());
		modelAndView.addObject("weatherHistoryList", searchResults);
		modelAndView.setViewName("redirect:/admin/home");
		logger.debug("--Application /bulkDelete load--");
		return modelAndView;
	}

	@RequestMapping(value ="/edit/{id}", method = RequestMethod.GET)
	public String showUpdateForm(@PathVariable("id") int id, Model model) {
		WeatherHistory weatherHistory = weatherHistoryService.findById(id);
		model.addAttribute("weatherHistory", weatherHistory);
		model.addAttribute("adminMessage","Record updated successfully.");
		logger.debug("--Application /edit form load--");
		return "edit";
	}

	@RequestMapping(value ="/edit/{id}", method = RequestMethod.POST)
	public String updateUser(@PathVariable("id") int id, @Valid WeatherHistory weatherHistory, 
			BindingResult result,
			Model model) {
		if (result.hasErrors()) {
			weatherHistory.setId(id);
			return "edit";
		}
		WeatherHistory editWeatherHistory = (WeatherHistory) model.asMap().get("weatherHistory");
		weatherHistoryService.editWeatherHistory(editWeatherHistory);
		List<WeatherHistory> weatherHistoryList = weatherHistoryService.findHistoryByUserId(editWeatherHistory.getUser().getId());	
		model.addAttribute("weatherHistoryList", weatherHistoryList);
		logger.debug("--Application /edit post load--");
		return "redirect:/admin/home";
	}
}
