package com.vtd.server;

import java.util.ArrayList;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Handles requests for the application home page.
 */
@Controller
public class HomeController {
	
	private static final Logger logger = LoggerFactory.getLogger(HomeController.class);
	private MorphAnalyzer morphAnalyzer = new MorphAnalyzer();
	/**
	 * Simply selects the home view to render by returning its name.
	 */
	
	@RequestMapping(value = "/", method = RequestMethod.GET)
	public void home() {
	}
	
	@RequestMapping(value = "/morph")
	@ResponseBody
	public Map<String, ArrayList<String>> morph(HttpServletRequest request) {
		String message = request.getParameter("message");
		Map<String, ArrayList<String>> commands;
		
		commands = morphAnalyzer.analyze(message);
		
		return commands;
	}
}
