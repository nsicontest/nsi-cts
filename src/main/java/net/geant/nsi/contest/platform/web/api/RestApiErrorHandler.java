package net.geant.nsi.contest.platform.web.api;

import javax.servlet.http.HttpServletRequest;

import net.geant.nsi.contest.platform.auth.PermissionException;
import net.geant.nsi.contest.platform.core.exceptions.CTSException;
import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;
import net.geant.nsi.contest.platform.web.data.ErrorInfo;
import net.geant.nsi.contest.platform.web.exceptions.BadRequestException;
import net.geant.nsi.contest.platform.web.exceptions.UnsupportedAuthException;

import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;


@ControllerAdvice(annotations={RestController.class}, basePackages={"net.geant.nsi.contest.platform.web.api"})
public class RestApiErrorHandler {
	private final static Logger log = Logger.getLogger(RestApiErrorHandler.class);
	
	@ExceptionHandler(value=BadRequestException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	@ResponseBody
	public ErrorInfo handleBadRequest(HttpServletRequest req, Exception ex) {
		return processException("Bad request.", req, ex);
	}

	@ExceptionHandler(value=PermissionException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ResponseBody
	public ErrorInfo handlePermission(HttpServletRequest req, Exception ex) {
		return processException("No permission.", req, ex);
	}

	
	@ExceptionHandler(value=UnsupportedAuthException.class)
	@ResponseStatus(HttpStatus.UNAUTHORIZED)
	@ResponseBody
	public ErrorInfo handleUnsupportedAuth(HttpServletRequest req, Exception ex) {
		return processException("Unsupported auth method. Internal error.", req, ex);
	}
	
	
	@ExceptionHandler(value=ResourceNotFoundException.class)
	@ResponseStatus(HttpStatus.NOT_FOUND)
	@ResponseBody
	public ErrorInfo handleNotFound(HttpServletRequest req, Exception ex) {
		return processException("Resource not found.", req, ex);
	}
	
	@ExceptionHandler(value=CTSException.class)
	@ResponseStatus(HttpStatus.PRECONDITION_FAILED)
	@ResponseBody
	public ErrorInfo handleCTSException(HttpServletRequest req, Exception ex) {
		return processException("Unhandled CTS exception " + ex.getClass().getSimpleName() + ".", req, ex);
	}
	
	
	@ExceptionHandler(value=Exception.class)
	@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
	@ResponseBody
	public ErrorInfo handleException(HttpServletRequest req, Exception ex) {
		return processException("Unhandled exception. Internal error.", req, ex);
	}
	
	private ErrorInfo processException(String message, HttpServletRequest req, Exception ex) {
		ErrorInfo errInfo = new ErrorInfo("#"+System.currentTimeMillis(), ex.getMessage());
		log.error("Request [" + req.getRequestURI() + "]. Message " + message + " Code: " + errInfo.getCode(), ex);
		return errInfo;		
	}
	
}
