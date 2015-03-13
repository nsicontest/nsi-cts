package net.geant.nsi.contest.platform.web;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;
import net.geant.nsi.contest.platform.data.helper.StringParser;
import net.geant.nsi.contest.platform.web.data.Alert;
import net.geant.nsi.contest.platform.web.data.convert.Converter;
import net.geant.nsicontest.topology.Nsa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.ui.Model;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

public class AbstractBaseController {
	
	@Autowired
	protected Environment env;
	
	@Autowired
	protected Converter converter;
	
	@Autowired
	StringParser parser;
	
	@SuppressWarnings("unchecked")
	protected List<Alert> prepareAlerts(Model model, RedirectAttributes attr) {
		final String ALERTS = "alerts";
		
		List<Alert> alerts = new ArrayList<Alert>();
		
		if(model != null) {
			if(!model.containsAttribute(ALERTS))
				model.addAttribute(ALERTS, alerts);
			else
				alerts = (List<Alert>) model.asMap().get(ALERTS);
		}
		
		if(attr != null) {
			if(!attr.containsAttribute(ALERTS)) 
				attr.addFlashAttribute(ALERTS, alerts);
			else
				alerts = (List<Alert>)attr.getFlashAttributes().get(ALERTS);
		}
		
		return alerts;
	}
	
	
	protected String getTopologiesLocationDir() {
		return env.getProperty("topology.location", ".");
	}
	
	protected String getProjectTopologiesLocationDir(UUID key) {
		if(key == null)
			throw new IllegalArgumentException("key is null");
		
		return getTopologiesLocationDir() + File.separator + "projects"+File.separator + key + File.separator;
	}
	
	protected boolean existAgentTopologies(UUID key) {
		String path = getProjectTopologiesLocationDir(key);
		
		for(String i : new String[] {"A", "B", "C"}) {
			File f = new File(path + "agent" + i + ".xml");
			if(!(f.exists() && f.isFile()))
				return false;
		}
		return true;
	}
	
	protected String getUserAgentFilename() {
		String filename = env.getProperty("topology.filename", "userAgent.xml");
		return parser.parse(filename);
	}
	
	protected boolean existUserAgentTopology(UUID key) {
		String path = getProjectTopologiesLocationDir(key);
		File f = new File(path + getUserAgentFilename());
		return (f.exists() && f.isFile());
	}
	
	protected Map<String, Nsa> getProjectAgents(UUID key) throws ResourceNotFoundException {
		if(key == null)
			throw new IllegalArgumentException("key is null");
		
		String path = getProjectTopologiesLocationDir(key);
		
		Map<String, Nsa> agents = new HashMap<String, Nsa>();
		
		for(String i : new String[] {"A", "B", "C"}) {
			try {
				agents.put("Agent" + i, new Nsa(path + "agent" + i + ".xml"));
			} catch (Exception ex) {
				throw new ResourceNotFoundException("Unable to get Agent" + i + " topology.", ex);
			}
		}
		
		return agents;
	}
	
	protected Map<String, Nsa> getAllProjectAgents(UUID key) throws ResourceNotFoundException {
		String path = getProjectTopologiesLocationDir(key);
		
		Map<String, Nsa> agents = getProjectAgents(key);
		
		try {
			agents.put("UserAgent", new Nsa(path + getUserAgentFilename()));
		} catch (Exception ex) {
			throw new ResourceNotFoundException("Unable to get User Agent topology from "+path + getUserAgentFilename() + " file.", ex);
		}
		
		return agents;
	}
	
	
	
}
