package net.geant.nsi.contest.platform.web.rest;

import java.util.UUID;

import net.geant.nsi.contest.platform.core.TestCaseServiceImpl;
import net.geant.nsi.contest.platform.data.TestCase;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/testcase")
public class TestCaseController {
	
	private static Logger log = Logger.getLogger(TestCaseController.class);
	
//	@Autowired
//	TestCaseService testcaseService;
	
	@RequestMapping(method = RequestMethod.POST)
	public @ResponseBody TestCase create() {
		return null;
	}
	
	@RequestMapping(method = RequestMethod.DELETE, value = "/{id}")
	@ResponseStatus(HttpStatus.OK)
	public void delete(@PathVariable UUID id) {
		
	}
	
}
