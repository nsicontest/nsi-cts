package net.geant.nsi.contest.platform.web.data.convert;

import net.geant.nsi.contest.platform.data.User;

public class UserConverter extends AbstractConvertType<User, net.geant.nsi.contest.platform.web.data.User> {

	
	public UserConverter(Class<User> type) {
		super(type);
	}

	@Override
	public net.geant.nsi.contest.platform.web.data.User convert(User user) {
		return new net.geant.nsi.contest.platform.web.data.User(user.getUserId(), user.getEmail(), user.getUsername());
	}

	@Override
	public net.geant.nsi.contest.platform.web.data.User convertDetailed(User user) {
		return convert(user);
	}

}
