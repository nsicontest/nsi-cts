package net.geant.nsi.contest.platform.config;

import java.util.Properties;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import net.geant.nsi.contest.platform.persistence.JobDAO;
import net.geant.nsi.contest.platform.persistence.LogEntryDAO;
import net.geant.nsi.contest.platform.persistence.ProjectDAO;
import net.geant.nsi.contest.platform.persistence.RoleDAO;
import net.geant.nsi.contest.platform.persistence.TestCaseDAO;
import net.geant.nsi.contest.platform.persistence.TestCaseTemplateDAO;
import net.geant.nsi.contest.platform.persistence.TestDAO;
import net.geant.nsi.contest.platform.persistence.UserDAO;

import org.apache.tomcat.dbcp.dbcp.BasicDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseBuilder;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;
import org.springframework.orm.hibernate4.HibernateExceptionTranslator;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories("net.geant.nsi.contest.platform.persistence")
@EnableJpaAuditing
//@EnableAspectJAutoProxy
@PropertySource("classpath:db.properties")
@ComponentScan("net.geant.nsi.contest.platform.persistence")
public class PersistenceConfig {
	
	private final static Logger log = LoggerFactory.getLogger(PersistenceConfig.class);
	
	public final static String DRIVER="database.driver";
	public final static String URL="database.url";
	public final static String USERNAME="database.username";
	public final static String PASSWORD="database.password";
	
	
	// supported hibernate properties
	public final static String[] HIBERNATE_PROPS = {
		"hibernate.hbm2ddl.auto",
		"hibernate.dialect",
		"hibernate.show_sql",
		"hibernate.bytecode.use_reflection_optimizer"	//for testing purposes only
	};
	
	
	@Autowired
	private Environment env;
	
	@Bean
	public DataSource dataSource() {
		if(env.getProperty(DRIVER) != null) {
			log.info("Using database on " + env.getProperty(URL));
			BasicDataSource dataSource = new BasicDataSource();
			dataSource.setDriverClassName(env.getProperty(DRIVER));
			dataSource.setUrl(env.getProperty(URL));
			dataSource.setUsername(env.getProperty(USERNAME));
			dataSource.setPassword(env.getProperty(PASSWORD));
			return dataSource;
		} else {
			log.info("Database is not provided. Using embedded DB instead of...");
			EmbeddedDatabaseBuilder builder = new EmbeddedDatabaseBuilder();
			return builder.setType(EmbeddedDatabaseType.H2).build();
		}
		
	}
	
	/*
	 * For sessions only
	 * 

	@Bean
	public LocalSessionFactoryBean sessionFactory() {
		LocalSessionFactoryBean sessionFactory = new LocalSessionFactoryBean();
		sessionFactory.setDataSource(dataSource());
		sessionFactory.setPackagesToScan(new String[] { "net.geant.nsi.contest.platform" });
		sessionFactory.setHibernateProperties(getHibernateProperties());
		return sessionFactory;
	}
	
	@Bean
	@Autowired
	public HibernateTransactionManager transactionManager(SessionFactory sessionFactory) {
		HibernateTransactionManager txManager = new HibernateTransactionManager(sessionFactory);
		
		return txManager;
	}
	*/
	
	@Bean
	@Autowired
	public PlatformTransactionManager transactionManager(EntityManagerFactory emf) {
		JpaTransactionManager txManager = new JpaTransactionManager(emf);
		return txManager;
	}
	
	@Bean
	public HibernateExceptionTranslator hibernateExceptionTranslator() {
		return new HibernateExceptionTranslator();
	}
	
	private Properties getHibernateProperties() {
		Properties props = new Properties();
		
		for(String prop : HIBERNATE_PROPS) {
			if(env.getProperty(prop) != null)
				props.setProperty(prop, env.getProperty(prop));
		}
		
		return props;
	}
	
	@Bean
	public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
		LocalContainerEntityManagerFactoryBean emf = new LocalContainerEntityManagerFactoryBean();
		emf.setDataSource(dataSource());
		emf.setPackagesToScan(new String[] { "net.geant.nsi.contest.platform.data"});
		
		JpaVendorAdapter vendorAdapter = new HibernateJpaVendorAdapter();
		emf.setJpaVendorAdapter(vendorAdapter);
		emf.setJpaProperties(getHibernateProperties());
		
		return emf;
	}
	
	@Bean
	@Autowired
	public EntityManager entityManager(EntityManagerFactory emf) {
		return emf.createEntityManager();
	}
	
	
	@Bean
	public UserDAO userDAO() {
		return new UserDAO();
	}
	
	@Bean
	public RoleDAO roleDAO() {
		return new RoleDAO();
	}
	
	@Bean
	public ProjectDAO projectDAO() {
		return new ProjectDAO();
	}
	
	@Bean
	public JobDAO jobDAO() {
		return new JobDAO();
	}
	
	@Bean
	public LogEntryDAO logEntryDAO() {
		return new LogEntryDAO();
	}
	
	@Bean
	public TestCaseDAO testCaseDAO() {
		return new TestCaseDAO();
	}
	
	@Bean
	public TestCaseTemplateDAO testCaseTemplateDAO() {
		return new TestCaseTemplateDAO();
	}
	
	@Bean
	public TestDAO testDAO() {
		return new TestDAO();
	}
}
