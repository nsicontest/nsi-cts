package net.geant.nsi.contest.platform.web.data.runner;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

@XmlRootElement(name="operation")
@XmlAccessorType(XmlAccessType.FIELD)
public class Operation {
    
	@XmlAttribute
    private String id;

	@XmlAttribute
    private String name;

    @XmlAttribute(required=false)
    private int interval;

    @XmlAttribute(required=false)
    private boolean continueOnError = false;

    @XmlAttribute(required=false)
    private boolean failOperation = false;
    
    @XmlAttribute(required=true)
    private String expected;
    
    @XmlTransient
    private Map<String, String> params = new HashMap<String, String>();
    
    @XmlTransient
    private Map<String, String> matches = new HashMap<String, String>();
    
    @XmlAttribute(required=false)
    private String expectedRequestedNSA;

    @XmlElement(required=false)
    private Result result;
    
	public final String getId() {
		return id;
	}

	public final void setId(String id) {
		this.id = id;
	}

	public final String getName() {
		return name;
	}

	public final void setName(String name) {
		this.name = name;
	}

	public final int getInterval() {
		return interval;
	}

	public final void setInterval(int interval) {
		this.interval = interval;
	}

	public final boolean isContinueOnError() {
		return continueOnError;
	}

	public final void setContinueOnError(boolean continueOnError) {
		this.continueOnError = continueOnError;
	}

	public final boolean isFailOperation() {
		return failOperation;
	}

	public final void setFailOperation(boolean failOperation) {
		this.failOperation = failOperation;
	}

	public final String getExpected() {
		return expected;
	}

	public final void setExpected(String expected) {
		this.expected = expected;
	}

	public final Map<String, String> getParams() {
		return params;
	}

	public final void setParams(Map<String, String> params) {
		this.params = params;
	}

	public final Map<String, String> getMatches() {
		return matches;
	}

	public final void setMatches(Map<String, String> matches) {
		this.matches = matches;
	}

	public final String getExpectedRequestedNSA() {
		return expectedRequestedNSA;
	}

	public final void setExpectedRequestedNSA(String expectedRequestedNSA) {
		this.expectedRequestedNSA = expectedRequestedNSA;
	}

	public final Result getResult() {
		return result;
	}

	public final void setResult(Result result) {
		this.result = result;
	}
	
	
}
