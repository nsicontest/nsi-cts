package net.geant.nsi.contest.platform.config;

import java.net.MalformedURLException;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

import net.geant.nsi.contest.platform.core.ProjectService;
import net.geant.nsi.contest.platform.core.AdminService;
import net.geant.nsi.contest.platform.core.TestCaseServiceImpl;
import net.geant.nsi.contest.platform.core.TestCaseService;
import net.geant.nsi.contest.platform.core.TestCaseTemplateService;
import net.geant.nsi.contest.platform.core.UserService;
import net.geant.nsi.contest.platform.core.runner.TestRunner;
import net.geant.nsi.contest.platform.core.runner.simple.SimpleTestRunner;
import net.geant.nsi.contest.platform.core.tasks.TaskManagerImpl;
import net.geant.nsi.contest.platform.core.tasks.TaskManager;
import net.geant.nsi.contest.platform.data.TestCaseTemplate;

import org.springframework.amqp.rabbit.connection.CachingConnectionFactory;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.env.Environment;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ConcurrentTaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

@Configuration
@ComponentScan({"net.geant.nsi.contest.platform.core", "net.geant.nsi.contest.platform.core.tasks"})
@PropertySource("classpath:cts.properties")
public class CoreConfig {
	
	@Autowired
	Environment env;
	
	@Bean
	public ThreadFactory threadFactory() {
		ThreadFactory tf = Executors.defaultThreadFactory();
		return tf;
	}
	
	@Bean
	public ThreadPoolTaskExecutor executor() {
		ThreadPoolTaskExecutor tp = new ThreadPoolTaskExecutor();
		
		int maxPoolSize = Integer.parseInt(env.getProperty("executor.pool.size", "10"));
		int startPoolSize = Integer.parseInt(env.getProperty("executor.pool.start", "1"));
		int queueSize = Integer.parseInt(env.getProperty("executor.queue.size", "100"));
		
		tp.setCorePoolSize(startPoolSize);
		tp.setMaxPoolSize(maxPoolSize);
		tp.setQueueCapacity(queueSize);
		
		tp.initialize();
		
		return tp;
	}

	
	@Bean
	public ThreadPoolTaskScheduler scheduler() {
		ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();
		
		int maxPoolSize = Integer.parseInt(env.getProperty("scheduler.pool.size", "1"));
		
		scheduler.setPoolSize(maxPoolSize);
		
		scheduler.initialize();
		
		return scheduler;
	}
	
	@Bean
	@Autowired
	public TaskManager taskManager(ThreadPoolTaskScheduler scheduler, ThreadPoolTaskExecutor executor) {
		return new TaskManagerImpl(scheduler, executor, null);
	}
	
	@Bean
	public ConnectionFactory connectionFactory() {
		CachingConnectionFactory cf = new CachingConnectionFactory();
		
		return cf;
	}
	
	@Bean
	public AdminService adminService() {
		return new AdminService();
	}
	
	@Bean
	public UserService userService() {
		return new UserService();
	}

	@Bean
	public ProjectService projectService() {
		return new ProjectService();
	}

	@Bean
	public TestCaseTemplateService templateService() {
		return new TestCaseTemplateService();
	}
	
	@Bean
	public TestCaseService testCaseServiceImpl() {
		return new TestCaseServiceImpl();
	}
//	
//	@Bean
//	public TestCaseService testCaseService() {
//		String period = env.getProperty("tests.pull.period", "60");
//		String timeout = env.getProperty("tests.timeout", "0");
//		
//		TestCaseService testCaseService = new TestCaseService();
//		testCaseService.setPullPeriod(Integer.parseInt(period));
//		testCaseService.setTestTimeout(Integer.parseInt(timeout));
//		return testCaseService;
//		//return new TestCaseService(Integer.parseInt(period), Integer.parseInt(timeout));
//	}
	
	@Bean
	public TestRunner testRunner() throws MalformedURLException {
		String url = env.getProperty("testRunner.url");
		
		return new SimpleTestRunner(url);
	}
	
	@Bean
	public StartupInit startupInit() {
		return new StartupInit();
	}
}
