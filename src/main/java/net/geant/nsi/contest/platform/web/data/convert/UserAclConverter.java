package net.geant.nsi.contest.platform.web.data.convert;

import net.geant.nsi.contest.platform.data.UserAcl;

public class UserAclConverter extends AbstractConvertType<UserAcl, net.geant.nsi.contest.platform.web.data.UserAcl> {
	
	public UserAclConverter(Class<UserAcl> type) {
		super(type);
	}

	@Override
	public net.geant.nsi.contest.platform.web.data.UserAcl convert(
			UserAcl acl) {
		return new net.geant.nsi.contest.platform.web.data.UserAcl(acl.getProject().getKey(), acl.getUser().getUserId(), acl.getStatus());
	}

	@Override
	public net.geant.nsi.contest.platform.web.data.UserAcl convertDetailed(
			UserAcl acl) {
		return convert(acl);
	}

	
	
}
