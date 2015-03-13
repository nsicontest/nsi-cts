package net.geant.nsi.contest.platform.web;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.geant.nsi.contest.platform.web.exceptions.RedirectException;

import org.apache.log4j.Logger;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.FlashMap;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.support.RequestContextUtils;
import org.springframework.web.servlet.view.RedirectView;
/**
 * 
 * Intercept all unhandled exceptions thrown in controllers.
 * 
 * @author mikus
 *
 */
@ControllerAdvice(annotations={Controller.class}, basePackages={"net.geant.nsi.contest.platform.web"})
public class WebErrorHandler {
	private final static Logger log = Logger.getLogger(WebErrorHandler.class);
	
	@ExceptionHandler(value=RedirectException.class) 
	public ModelAndView handleRedirection(HttpServletRequest request, HttpServletResponse response, RedirectException ex) {
		log.error("Error reported. ", ex);
		
		RedirectView redirect = new RedirectView(ex.getRedirect());
		FlashMap flashMap = RequestContextUtils.getOutputFlashMap(request);
		if(flashMap != null)
			flashMap.put("alerts", ex.getAlerts());
		
		ModelAndView mv = new ModelAndView(redirect);
		
		return mv;
	}
	
	@ExceptionHandler(value=AccessDeniedException.class)
	public ModelAndView accessDenied(HttpServletRequest request, HttpServletResponse response, Exception ex) {
		log.warn("Access denied: " + request.getRequestURI() + " by " + request.getUserPrincipal());
		ModelAndView model = new ModelAndView("error/custom");
		
		model.addObject("title", "Access denied.");
		model.addObject("message", "It looks like you should not be here.");
		
		return model;
	}
	
	@ExceptionHandler(value=Exception.class)
	public ModelAndView defaultErrorHandler(HttpServletRequest request, HttpServletResponse response, Exception ex) {
		String code = "#" + System.currentTimeMillis();
		log.error("Unhandled exception for " + code, ex);
		ModelAndView model = new ModelAndView("error/exception");
		model.addObject("code", code);
		
		return model;
	}
}
