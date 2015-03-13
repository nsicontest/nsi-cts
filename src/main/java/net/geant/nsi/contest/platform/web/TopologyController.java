package net.geant.nsi.contest.platform.web;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.UUID;

import net.geant.nsi.contest.platform.core.ProjectService;
import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;
import net.geant.nsi.contest.platform.data.helper.StringParser;
import net.geant.nsi.contest.platform.web.data.Alert;
import net.geant.nsi.contest.platform.web.data.Alert.AlertType;
import net.geant.nsicontest.topology.DemoTopology;
import net.geant.nsicontest.topology.Nsa;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.FileSystemResource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping(value="/projects/{key}/topology")
@PreAuthorize("hasPermission(#key, 'isAcceptedProjectMember')")
public class TopologyController extends AbstractBaseController {
	private final static Logger log = Logger.getLogger(TopologyController.class);
	
	@Autowired
	ProjectService projects;
	
	@RequestMapping(value="/upload", method=RequestMethod.POST)
	@Transactional
	public String upload(Model model, RedirectAttributes attr, @PathVariable("key") UUID key, @RequestParam("file") MultipartFile file) {
		List<Alert> alerts = prepareAlerts(model, attr);
		
		net.geant.nsi.contest.platform.data.Project project = null;
		try {
			project = projects.findBy(key);
		} catch (ResourceNotFoundException ex) {
			log.debug("Project " + key + " not found.", ex);
			alerts.add(Alert.failure("Unable to find project " + key));
			return "redirect:/projects";
		}
		
		if(project.getNetworkId() == null) {
			alerts.add(Alert.failure("Please provide NSA ID before uploading topology."));
			return "redirect:/projects/"+key;			
		} else if (!existAgentTopologies(key)) {
			alerts.add(Alert.failure("Please generate Agents topologies before uploading your own."));
			return "redirect:/projects/"+key;
		}
		
		MultipartFile f = file;// uploadTopologyForm.getFile();
		
		log.debug("File: " + f);
		
		if(f != null && !f.isEmpty()) {
			//TODO: check uploaded file size
			
			String path = getProjectTopologiesLocationDir(key);
			String filename = path + getUserAgentFilename();
			
			log.debug("If needed trying to create directory " + path);
			File dir = new File(path);
			if(!dir.exists() && !dir.mkdirs()) {
				log.error("Upload path (" +  path +") does not exists.");
				alerts.add(Alert.failure("Unable to save file. Please contact administrator."));
				return "redirect:/projects/" + key;
			}
			
			
			log.info("Saving file " + filename);
			File saveFile = new File(filename);
			try {
				saveFile.createNewFile();
				OutputStream os = new FileOutputStream(saveFile);
				
				IOUtils.copy(f.getInputStream(), os);
			
				os.close();
				f.getInputStream().close();
			} catch (IOException ex) {
				log.error("Unable to save file " + filename, ex);
				alerts.add(Alert.failure("Unable to save file. Please contact administrator."));
				return "redirect:/projects/" + key;
			}
			
			//TODO: add uploaded topology file validation
			Nsa agentA;
			Nsa agentB;
			Nsa userAgent;
			
			try {
				
				agentA = new Nsa(path + "agentA.xml");
				agentB = new Nsa(path + "agentB.xml");
				userAgent = new Nsa(filename);				
			} catch (Exception ex) {
				log.error("Unable to update topologies", ex);
				alerts.add(Alert.failure("Unable to update topologies. Contact administrator."));
				return "redirect:/projects/"+key;
			}
			
			if(!agentA.hasPeering(userAgent)) {
				alerts.add(Alert.failure("User agent topology has wrong peering with AgentA"));
				return "redirect:/projects/"+key;
			}
			if(!agentB.hasPeering(userAgent)) {
				alerts.add(Alert.failure("User agent topology has wrong peering with AgentB"));
				return "redirect:/projects/"+key;
			}
			
			log.debug("Updating topology with new file");
			
			project.getTopology().setTopologyFile(new net.geant.nsi.contest.platform.data.File(filename, f.getOriginalFilename()));
			projects.update(project);
			
			alerts.add(Alert.success("Topology successfully uploaded!"));
		} else
			alerts.add(Alert.failure("File is empty."));
		
		return "redirect:/projects/"+key;
	}
	
	@RequestMapping(value="/download", params={"id"}, method=RequestMethod.GET)
	@ResponseBody
	public FileSystemResource download(@PathVariable("key") UUID key, @RequestParam("id") String id) {
		log.info("Downloading file id=" + id + " for project " + key);
		String path = getProjectTopologiesLocationDir(key);
		
		if("userAgent".equals(id)) {
			String filename = path + getUserAgentFilename();
			
			return new FileSystemResource(filename);
		} else if(id != null && (id.equals("agentA") || id.equals("agentB") || id.equals("agentC"))) {
			return new FileSystemResource(path + id+".xml");
		}
		
		return null;
	}
	
	@RequestMapping(value="/generate", method=RequestMethod.GET)
	public String generate(RedirectAttributes attr, @PathVariable("key") UUID key) {
		List<Alert> alerts = prepareAlerts(null, attr);
		
		net.geant.nsi.contest.platform.data.Project project = null;
		try {
			project = projects.findBy(key);
		} catch (ResourceNotFoundException ex) {
			log.debug("Project " + key + " not found.", ex);
			alerts.add(Alert.failure("Unable to find project " + key));
			return "redirect:/projects";
		}
		
		if(project.getNetworkId() == null) {
			alerts.add(Alert.failure("Unable to generate topologies for project " + key + ". Please update NSA ID"));
			return "redirect:/projects/" + key;
		}
		
		
		//TODO: check user networkId and use it ...
		
		String path = getProjectTopologiesLocationDir(key);
		
		log.debug("If needed trying to create directory " + path);
		File dir = new File(path);
		if(!dir.exists() && !dir.mkdirs()) {
			log.error("Upload path (" +  path +") does not exists.");
			alerts.add(Alert.failure("Unable to save file. Please contact administrator."));
			return "redirect:/projects/" + key;
		}
		
		
		//TODO: ... here
		DemoTopology demoTopology = new DemoTopology(project.getNetworkId());
		
		try {
			demoTopology.getAgentA().save(path + "agentA.xml");
			demoTopology.getAgentB().save(path + "agentB.xml");
			demoTopology.getAgentC().save(path + "agentC.xml");
			
			log.info("Topologies generated for project " + key);
			alerts.add(Alert.success("Topology generated."));
		} catch (Exception ex) {
			log.error("Unable to save topologies", ex);
			alerts.add(Alert.failure("Unable to generate topologies. Contact administrator."));
		}
		return "redirect:/projects/"+key;
	}

	
	
}
