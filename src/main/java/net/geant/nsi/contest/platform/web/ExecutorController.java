package net.geant.nsi.contest.platform.web;

import java.util.Collection;

import net.geant.nsi.contest.platform.core.tasks.QueuedTask;
import net.geant.nsi.contest.platform.core.tasks.TaskManager;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@Controller
@RequestMapping("/executor")
public class ExecutorController extends AbstractBaseController {
	private final static Logger log = Logger.getLogger(ExecutorController.class);
	
	@Autowired
	TaskManager taskManager;
	
	@RequestMapping(method=RequestMethod.GET)
	public String show(Model model) {
		//model.addAttribute("scheduled", taskManager.countScheduledTasks());
		model.addAttribute("scheduledTasks", taskManager.getScheduledTasks());
		
		//model.addAttribute("running", taskManager.countRunningTasks());
		model.addAttribute("runningTasks", taskManager.getRunningTasks());
		
		//model.addAttribute("queued", taskManager.countQueuedTasks());
		model.addAttribute("queuedTasks", taskManager.getQueuedTasks());
		
		return "executor/show :: stats";
		
	}
	
	@RequestMapping(value="/{taskId}", method=RequestMethod.DELETE)
	public void cancel(@PathVariable("taskId") Long taskId) {
		log.info("Cancelling task " + taskId);
		
		if(taskId != null)
			taskManager.cancel(taskId);
	}
	
	@RequestMapping(value="/queued/group/{groupId}")
	public String showQueuedGroup(Model model, @PathVariable(value="groupId") String groupId) {
		Collection<QueuedTask> tasks = taskManager.getQueuedTasks(groupId);
		
		model.addAttribute("tasks", tasks);
		
		return "executor/show :: queuedTasksGroup";
	}
	
	
}
