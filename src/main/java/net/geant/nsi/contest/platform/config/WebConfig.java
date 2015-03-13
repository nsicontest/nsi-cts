package net.geant.nsi.contest.platform.config;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.MultipartConfigElement;

import net.geant.nsi.contest.platform.data.Project;
import net.geant.nsi.contest.platform.data.Test;
import net.geant.nsi.contest.platform.data.TestCase;
import net.geant.nsi.contest.platform.data.TestCaseTemplate;
import net.geant.nsi.contest.platform.data.User;
import net.geant.nsi.contest.platform.data.UserAcl;
import net.geant.nsi.contest.platform.data.helper.StringParser;
import net.geant.nsi.contest.platform.data.helper.TimeMarker;
import net.geant.nsi.contest.platform.web.data.convert.Converter;
import net.geant.nsi.contest.platform.web.data.convert.ConverterImpl;
import net.geant.nsi.contest.platform.web.data.convert.ProjectConverter;
import net.geant.nsi.contest.platform.web.data.convert.TemplateConverter;
import net.geant.nsi.contest.platform.web.data.convert.TestCaseConverter;
import net.geant.nsi.contest.platform.web.data.convert.TestConverter;
import net.geant.nsi.contest.platform.web.data.convert.UserAclConverter;
import net.geant.nsi.contest.platform.web.data.convert.UserConverter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.MultipartConfigFactory;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.multipart.support.StandardServletMultipartResolver;
import org.springframework.web.servlet.LocaleResolver;
import org.springframework.web.servlet.ViewResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;
import org.springframework.web.servlet.i18n.LocaleChangeInterceptor;
import org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;

@Configuration
@EnableWebMvc
@EnableGlobalMethodSecurity(prePostEnabled=true)				//TODO: why this has to be here?
@ComponentScan(basePackages = {"net.geant.nsi.contest.platform.web"})
public class WebConfig extends WebMvcConfigurerAdapter {

	@Autowired
	Environment env;
	
	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/resources/**").addResourceLocations("/resources/");
	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		LocaleChangeInterceptor localeChangeInterceptor = new LocaleChangeInterceptor();
		localeChangeInterceptor.setParamName("lang");
		registry.addInterceptor(localeChangeInterceptor);
		//registry.addInterceptor(new ErrorHandlerInterceptor());
	}
		
	@Bean
	public LocaleResolver localeResolver() {
		CookieLocaleResolver cookieLocaleResolver = new CookieLocaleResolver();
		cookieLocaleResolver.setDefaultLocale(StringUtils.parseLocaleString("en"));
		return cookieLocaleResolver;
	}
	
	public SpringTemplateEngine templateEngine() {
		SpringTemplateEngine engine = new SpringTemplateEngine();
		engine.setTemplateResolver(templateResolver());
		engine.addDialect(new SpringSecurityDialect());
		return engine;
	}
	
	@Bean
	public ServletContextTemplateResolver templateResolver() {
		ServletContextTemplateResolver resolver = new ServletContextTemplateResolver();
		resolver.setPrefix("/WEB-INF/views/");
		resolver.setSuffix(".html");
		resolver.setTemplateMode("HTML5");
		resolver.setCacheable(false);
		return resolver;
	}
	
	@Bean
	public ViewResolver createViewResolver() {
		ThymeleafViewResolver viewResolver = new ThymeleafViewResolver();
		viewResolver.setTemplateEngine(templateEngine());
		viewResolver.setOrder(1);
		viewResolver.setViewNames(new String[] {"*"});
		viewResolver.setCache(false);
		return viewResolver;
	}
	
	@Bean
	public MessageSource messageSource() {
		ReloadableResourceBundleMessageSource msgSource = new ReloadableResourceBundleMessageSource();
		msgSource.setBasenames("classpath:messages/messages", "classpath:messages/validation");
		msgSource.setUseCodeAsDefaultMessage(false);
		msgSource.setDefaultEncoding("utf-8");
		msgSource.setCacheSeconds(60);
		return msgSource;
	}
	
//	@Bean
//	public MultipartConfigElement multipartConfigElement() {
//		MultipartConfigFactory factory = new MultipartConfigFactory();
//		factory.setLocation(env.getProperty("topology.location"));
//		factory.setMaxFileSize(env.getProperty("topology.maxFileSize", ""));
//		factory.setMaxRequestSize(env.getProperty("topology.maxFileSize", ""));
//		factory.setFileSizeThreshold(env.getProperty("topology.maxFileSize", ""));
//		return factory.createMultipartConfig();
//	}
	
	@Bean
	public MultipartResolver multipartResolver() {
		return new StandardServletMultipartResolver();
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Bean
	public Converter converter() {
		Converter converter = new ConverterImpl();
		
		converter.register(new TemplateConverter(TestCaseTemplate.class));
		converter.register(new ProjectConverter(Project.class));
		converter.register(new TestCaseConverter(TestCase.class));
		converter.register(new TestConverter(Test.class));
		converter.register(new UserConverter(User.class));
		converter.register(new UserAclConverter(UserAcl.class));
		return converter;
	}
	
	@Bean
	public StringParser stringParser() {
		StringParser parser = new StringParser();
		parser.register(new TimeMarker("time"));
		return parser;
	}
}
