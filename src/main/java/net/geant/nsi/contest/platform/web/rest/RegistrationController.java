package net.geant.nsi.contest.platform.web.rest;

import java.util.UUID;

import net.geant.nsi.contest.platform.data.Project;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api")
public class RegistrationController {
	
	@RequestMapping(value="/project", method=RequestMethod.POST)
	public Project registerProject(String projectName) {
		//TODO:
		return null;
	}
	
}
