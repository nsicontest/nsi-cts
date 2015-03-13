package net.geant.nsi.contest.platform.data;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.apache.log4j.Logger;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import net.geant.nsi.contest.platform.core.exceptions.ResourceNotFoundException;
import net.geant.nsi.contest.platform.data.UserAcl.Status;

@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(uniqueConstraints = {@UniqueConstraint(columnNames={"key"})})
public class Project {
	private final static Logger log = Logger.getLogger(Project.class);
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	
	@NotNull
	private UUID key;
	
	@Size(min=3, max=30)
	private String name;

	@CreatedDate
	private Date createdAt;
	
	@OneToMany(cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	private Set<UserAcl> userAcls = new HashSet<UserAcl>();
	
	String networkId;
	
	/*
	private String url;

	private AgentType type;
	*/
	
	@OneToMany(mappedBy="project", cascade = { CascadeType.ALL }, fetch = FetchType.EAGER)
	private List<TestCase> testCases = new ArrayList<TestCase>();
	
	@OneToOne(cascade = {CascadeType.ALL}, fetch=FetchType.LAZY)
	ProjectConfiguration confguration = new ProjectConfiguration();
	
	@OneToOne(cascade = CascadeType.ALL, fetch=FetchType.EAGER)
	TopologyConfiguration topology = new TopologyConfiguration();
	
	public Project() {
		key = UUID.randomUUID();
		confguration = new ProjectConfiguration();
	}
	
	public Project(String name) {
		this();
		this.name=name;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public UUID getKey() {
		return key;
	}

	public void setKey(UUID key) {
		this.key = key;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Set<UserAcl> getUserAcls() {
		return userAcls;
	}

	public void setUserAcls(Set<UserAcl> userAcls) {
		this.userAcls = userAcls;
	}

//	public String getUrl() {
//		return url;
//	}
//
//	public void setUrl(String url) {
//		this.url = url;
//	}
//	
//	public AgentType getType() {
//		return type;
//	}
//
//	public void setType(AgentType type) {
//		this.type = type;
//	}

	public final String getNetworkId() {
		return networkId;
	}

	public final void setNetworkId(String networkId) {
		this.networkId = networkId;
	}

	public Date getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Date createdAt) {
		this.createdAt = createdAt;
	}
	
	public ProjectConfiguration getConfguration() {
		return confguration;
	}

	public void setConfguration(ProjectConfiguration confguration) {
		this.confguration = confguration;
	}

	public TopologyConfiguration getTopology() {
		return topology;
	}

	public void setTopology(TopologyConfiguration topology) {
		this.topology = topology;
	}

	@Transient
	public Set<UserAcl> getUserAclsBy(UserAcl.Status status) {
		Set<UserAcl> filtered = new HashSet<UserAcl>();
		for(UserAcl acl : userAcls) {
			if(acl.getStatus() == status)
				filtered.add(acl);
		}
		return filtered;
	}
	
	@Transient
	public UserAcl getUserAcl(User user) {
		for(UserAcl acl : userAcls)
			if(acl.getUser().equals(user))
				return acl;
		return null;
	}
	
	@Transient
	public long getUsersCount() {
		return (userAcls != null ? userAcls.size() : 0);
	}
	
	/**
	 * 
	 * Add awaiting user to the project
	 * 
	 * @param user
	 * @return
	 */
	@Transient
	public boolean addUser(User user) {
		return addUser(user, UserAcl.Status.AWAITING);
	}
	
	@Transient
	public boolean addUser(User user, UserAcl.Status status) {
		UserAcl acl = new UserAcl(this, user, status);
		if(!userAcls.contains(acl)) {
			userAcls.add(acl);
			return true;
		}
		return false;
	}
	
	@Transient
	public void removeUser(User user) {
		UserAcl acl = getUserAcl(user);
		userAcls.remove(acl);
	}
	
	@Transient
	public boolean hasUserAccess(User user) {
		if(user == null)
			return false;
		
		for(UserAcl acl : userAcls) {
			if(acl.getUser().equals(user) && acl.getStatus() == Status.ACCEPTED)
				return true;
		}
		return false;
	}
	
	@Transient
	public boolean hasUserAccess(UUID userId) {
		if(userId == null)
			return false;
		
		for(UserAcl acl : userAcls) {
			log.debug("Checking acl for user='" + userId + "' against acl=" + acl);
			if(acl.getUser().getUserId().equals(userId) && (acl.getStatus() == Status.ACCEPTED))
				return true;
		}
		return false;
	}
	
	@Transient
	public void addTestCase(TestCase testCase) {
		if(testCase != null) {
			testCase.setProject(this);
			testCases.add(testCase);
		}
	}
	
	@Transient
	public void removeTestCase(TestCase testCase) {
		if(testCases.contains(testCase)) {
			testCases.remove(testCase);
			testCase.setProject(null);
		}
	}
	
	@Transient
	public TestCase getTestCase(UUID testCaseId) throws ResourceNotFoundException {
		for(TestCase tc : testCases)
			if(tc.getTestCaseId() == testCaseId)
				return tc;
		throw new ResourceNotFoundException("Project does not contain test case " + testCaseId);
	}
	
	@Transient
	public boolean hasTestCase(UUID testCaseId) {
		try {
			return (getTestCase(testCaseId) != null);
		} catch (ResourceNotFoundException e) {
			return false;
		}
	}
	
	public final List<TestCase> getTestCases() {
		return testCases;
	}

	protected void setTestCases(List<TestCase> testCases) {
		this.testCases = testCases;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((key == null) ? 0 : key.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Project other = (Project) obj;
		if (key == null) {
			if (other.key != null)
				return false;
		} else if (!key.equals(other.key))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		return "Project [id=" + id + ", key=" + key + ", name=" + name
				+ ", createdAt=" + createdAt + ", networkId=" + networkId + "acls#=" + userAcls.size() + "]";
	}


	
}
