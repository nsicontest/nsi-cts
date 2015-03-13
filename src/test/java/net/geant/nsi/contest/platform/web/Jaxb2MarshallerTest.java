package net.geant.nsi.contest.platform.web;

import net.geant.nsi.contest.platform.config.CoreConfig;
import net.geant.nsi.contest.platform.config.PersistenceConfig;
import net.geant.nsi.contest.platform.config.WebConfig;
import net.geant.nsi.contest.platform.config.WebHelperConfig;
import net.geant.nsi.contest.platform.config.WebSecurityConfig;
import net.geant.nsi.contest.platform.web.data.runner.Scenario;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.xml.transform.StringSource;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes={WebHelperConfig.class}/* loader = AnnotationConfigContextLoader.class, */ )
public class Jaxb2MarshallerTest {

	@Autowired
	Jaxb2Marshaller marshaller;
	
	String xml = "<scenario id=\"1\">\n   <section id=\"sect1\" interval=\"500\">\n      <operation interval=\"0\" continueOnError=\"false\" failOperation=\"false\" expected=\"reserve\" name=\"listen\" id=\"oper1\">\n         <result>\n            <success>true</success>\n            <receivedEvent>request.reserve</receivedEvent>\n         </result>\n      </operation>\n      <operation interval=\"0\" continueOnError=\"false\" failOperation=\"false\" expected=\"reserveCommit\" name=\"listen\" id=\"oper1\">\n         <result>\n            <success>true</success>\n            <receivedEvent>request.reserveCommit</receivedEvent>\n         </result>\n      </operation>\n      <operation interval=\"0\" continueOnError=\"false\" failOperation=\"false\" expected=\"provision\" name=\"listen\" id=\"oper1\">\n         <result>\n            <success>true</success>\n            <receivedEvent>request.provision</receivedEvent>\n         </result>\n      </operation>\n      <operation interval=\"0\" continueOnError=\"false\" failOperation=\"false\" expected=\"release\" name=\"listen\" id=\"oper1\">\n         <result>\n            <success>true</success>\n            <receivedEvent>request.release</receivedEvent>\n         </result>\n      </operation>\n      <operation interval=\"0\" continueOnError=\"false\" failOperation=\"false\" expected=\"terminate\" name=\"listen\" id=\"oper1\">\n         <result>\n            <success>true</success>\n            <receivedEvent>request.terminate</receivedEvent>\n         </result>\n      </operation>\n   </section>\n   <configuration>\n      <option name=\"nsi_listen_port\" value=\"9090\"/>\n      <option name=\"reply_to_endpoint\" value=\"http://127.0.0.1:9090/nsicontest/ConnectionRequester\"/>\n      <option name=\"remote_endpoint\" value=\"http://127.0.0.1:9091/nsicontest/ConnectionProvider\"/>\n      <option name=\"provider_nsa\" value=\"test.provider\"/>\n      <option name=\"requester_nsa\" value=\"test.requester\"/>\n   </configuration>\n</scenario>";
	
	@Test
	public void testUnmarshal() {
		
		Scenario scenario = (Scenario) marshaller.unmarshal(new StringSource(xml));
		
		assertNotNull(scenario);
		assertEquals(5, scenario.getSection().getOperations().size());
		assertNotNull(scenario.getSection().getOperations().get(0).getResult());
		assertTrue(scenario.getSection().getOperations().get(0).getResult().isSuccess());
	}
}
