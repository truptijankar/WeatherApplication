package com.weather.application.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * @author trupti.jankar
 * This class handles the error messages
 */
@Controller
public class CustomErrorController implements ErrorController {

	/**
	 * This method will formulate the status_cdoe and error message
	 * @param request
	 */
    @RequestMapping("/error")
    @ResponseBody
    public ModelAndView handleError(HttpServletRequest request) {
    	ModelAndView modelView = new ModelAndView();
    	Integer statusCode = (Integer) request.getAttribute("javax.servlet.error.status_code");
        Exception exception = (Exception) request.getAttribute("javax.servlet.error.exception");
        
        modelView.addObject("statusCode", "Error Code : " + statusCode);
        modelView.addObject("exception", "Error details : "+ exception==null? "N/A": exception.getMessage());
        modelView.setViewName("error");
        return modelView;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }
}