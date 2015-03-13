package net.geant.nsi.contest.platform.web;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.validation.Valid;

import net.geant.nsi.contest.platform.core.UserService;
import net.geant.nsi.contest.platform.core.exceptions.CTSException;
import net.geant.nsi.contest.platform.core.exceptions.UserNotFoundException;
import net.geant.nsi.contest.platform.web.data.Alert;
import net.geant.nsi.contest.platform.web.data.User;
import net.geant.nsi.contest.platform.web.data.Alert.AlertType;
import net.geant.nsi.contest.platform.web.data.forms.ProfileForm;
import net.geant.nsi.contest.platform.web.exceptions.RedirectException;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
public class ProfileController extends AbstractBaseController {
	private final static Logger log = Logger.getLogger(ProfileController.class);
	
	@Autowired
	UserService userService;
	
	
	@RequestMapping(value="/profile", method=RequestMethod.GET) 
	public String showProfile(Model model, @ModelAttribute User user) {
		
		ProfileForm form = new ProfileForm();
		form.setUserId(user.getUserId());
		form.setEmail(user.getEmail());
		form.setUsername(user.getUsername());
		model.addAttribute("profileForm", form);
		
		return "profile";
	}
	
	@RequestMapping(value="/profile", method=RequestMethod.POST) 
	public String saveProfile(RedirectAttributes attr, Model model, @ModelAttribute User user, @Valid ProfileForm profileForm, BindingResult bindingResult) {
		List<Alert> alerts = prepareAlerts(model, attr);
		
		log.debug("Profile userId: '" + profileForm.getUserId() + "' User userId: '" + user.getUserId() + "'  equals="+user.getUserId().equals(profileForm.getUserId()));
		
		// Extra validation
		if(profileForm.getPassword() != null 
				&& profileForm.getPassword().trim().length() <6
				&& profileForm.getPassword().trim().length() >0)
			bindingResult.rejectValue("password", null);
		if(!user.getUserId().equals(profileForm.getUserId()))
			bindingResult.rejectValue("userId", null);
		if(bindingResult.hasErrors()) {
			alerts.add(Alert.failure("Profile form has some errors."));			
			return "profile";
		}
		
		net.geant.nsi.contest.platform.data.User u = getMe(user.getUserId(), alerts);
		
		u.setEmail(profileForm.getEmail());
		u.setUsername(profileForm.getUsername());
		
		//TODO: use secure password method
		if(profileForm.getPassword() != null && profileForm.getPassword().trim().length() >= 6)
			u.setPassword(profileForm.getPassword().trim());

		try {
			userService.update(u);
		} catch (CTSException ex) {
			log.error("Unable to update user " + user.getUserId(), ex);
			alerts.add(Alert.failure("Unable to update your profile "));
			return "redirect:/profile";
		}
		
		user.setEmail(profileForm.getEmail());
		user.setUsername(profileForm.getUsername());		
		
		alerts.add(Alert.success("Profile successfully saved."));
		
		return "redirect:/profile";
	}

	@RequestMapping(value="/profile", method=RequestMethod.DELETE) 
	public String deleteProfile(RedirectAttributes attr, @ModelAttribute User user) {
		//TODO: extra checking
		List<Alert> alerts = prepareAlerts(null, attr);
		
		try {
			userService.delete(user.getUserId());
			alerts.add(Alert.success("Your account has been deleted!"));
			return "redirect:/logout";
		} catch (CTSException e) {
			log.error("Unable to delete user " + user.getUserId());
			alerts.add(Alert.failure("Unable to delete your account!"));
			return "redirect:/profile";
		}
	}
	
	public net.geant.nsi.contest.platform.data.User getMe(UUID userId, List<Alert> alerts) {
		try {
			return userService.findBy(userId);
		} catch (UserNotFoundException e) {
			alerts.add(Alert.failure("Unable to find user " + userId));
			throw new RedirectException("/logout", alerts);
		}
	}
	
}
