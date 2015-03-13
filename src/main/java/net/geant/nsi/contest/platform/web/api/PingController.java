package net.geant.nsi.contest.platform.web.api;

import java.util.Calendar;

import net.geant.nsi.contest.platform.web.data.Pong;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


@RestController("apiPing")
@RequestMapping(value="/api", produces="application/json")
public class PingController {

	@RequestMapping(value="/ping", method=RequestMethod.GET)
	public Pong ping() {
		return new Pong("CTS", Calendar.getInstance().getTime());
	}
	
}
