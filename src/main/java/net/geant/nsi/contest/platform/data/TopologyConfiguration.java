package net.geant.nsi.contest.platform.data;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.CascadeType;
import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.MapKeyColumn;
import javax.persistence.OneToOne;

@Entity
public class TopologyConfiguration {

		@Id
		@GeneratedValue(strategy=GenerationType.AUTO)
		Long id;
		
		@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
		Project project;
		
//		@ElementCollection(fetch=FetchType.EAGER)
//		@MapKeyColumn(name="id")
//		@Column(name="topologyFile")
//		@CollectionTable(joinColumns=@JoinColumn(name="topologyConfiguration_id"))
//		private Map<String, File> topologyFiles = new HashMap<String, File>();

		@OneToOne(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
		File topologyFile;
		
		public TopologyConfiguration() {
		}
		
		public Long getId() {
			return id;
		}

		public void setId(Long id) {
			this.id = id;
		}

		public Project getProject() {
			return project;
		}

		public void setProject(Project project) {
			this.project = project;
		}

		public File getTopologyFile() {
			return topologyFile;
		}

		public void setTopologyFile(File topologyFile) {
			this.topologyFile = topologyFile;
		}

//		public Map<String, File> getTopologyFiles() {
//			return topologyFiles;
//		}
//
//		public void setTopologyFiles(Map<String, File> topologyFiles) {
//			this.topologyFiles = topologyFiles;
//		}		
		
		
}
