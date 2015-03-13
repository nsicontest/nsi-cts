package net.geant.nsi.contest.platform.config;

import javax.servlet.Filter;
import javax.servlet.MultipartConfigElement;
import javax.servlet.ServletRegistration.Dynamic;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.core.annotation.Order;
import org.springframework.core.env.Environment;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.filter.HiddenHttpMethodFilter;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.support.AbstractAnnotationConfigDispatcherServletInitializer;

@Configuration
@EnableAutoConfiguration
@ComponentScan(basePackages="net.geant.nsi.contest.platform.config")
@PropertySource("classpath:cts.properties")
@Order(2)
public class WebAppInit extends AbstractAnnotationConfigDispatcherServletInitializer {
		
	@Override
	protected void customizeRegistration(Dynamic registration) {
		MultipartConfigFactory factory = new MultipartConfigFactory();
		
		registration.setMultipartConfig(factory.createMultipartConfig());
	}

	@Override
	protected Class<?>[] getRootConfigClasses() {
		return new Class<?>[] { PersistenceConfig.class, CoreConfig.class, WebSecurityConfig.class, WebHelperConfig.class};
	}
	
	@Override
	protected Class<?>[] getServletConfigClasses() {
		return new Class<?>[] { WebConfig.class };
	}
	
	@Override
	protected String[] getServletMappings() {
		return new String[] { "/" };
	}

	@Override
	protected Filter[] getServletFilters() {
		CharacterEncodingFilter characterEncodingFilter = new CharacterEncodingFilter();
		characterEncodingFilter.setEncoding("UTF-8");
		return new Filter[] { characterEncodingFilter, new MultipartFilter(), new HiddenHttpMethodFilter()};
	}
	
	public static void main(String[] args) {
		SpringApplication.run(WebAppInit.class, args);
	}
}
