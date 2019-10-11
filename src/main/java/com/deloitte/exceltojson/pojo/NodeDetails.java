package com.deloitte.exceltojson.pojo;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({"Process", "Group","Owner", "Hosted Name","Provider","Consumer","Integration Pattern",
	"Confidence Factor","Count of Invocations","DB Calls (ms)","Logic (ms)","Rules (ms)","Latency (ms)",
	"Time of Function/Call",
	"Total Process Time","Total Process Path Length","Process NFR",
	"Total Process NFR","Total Process Path Length",
	"Total Process Path Length NFR" })
public class NodeDetails {
	
	
	
	public NodeDetails(String name, String group, String owner, String hostedOn, String provider, String consumers,
			String integrationPattern, String confidenceLevel, String noOfInvocations, String dbCalls, String logic,
			String rules, String latency, String timeOfFunction, String totalProcessTime, String totalProcessPathLength,
			String processNFR, String totalProcessNFR, String totalProcessPathLengthNFR) {
		super();
		this.name = name;
		this.group = group;
		this.owner = owner;
		this.hostedOn = hostedOn;
		this.provider = provider;
		this.consumers = consumers;
		this.integrationPattern = integrationPattern;
		this.confidenceLevel = confidenceLevel;
		this.noOfInvocations = noOfInvocations;
		this.dbCalls = dbCalls;
		this.logic = logic;
		this.rules = rules;
		this.latency = latency;
		this.timeOfFunction = timeOfFunction;
		this.totalProcessTime = totalProcessTime;
		this.totalProcessPathLength = totalProcessPathLength;
		this.processNFR = processNFR;
		this.totalProcessNFR = totalProcessNFR;
		this.totalProcessPathLengthNFR = totalProcessPathLengthNFR;
	}
	
	

	public NodeDetails() {
		super();
		// TODO Auto-generated constructor stub
	}



	@JsonProperty(value="Process")
	public String name;
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	@JsonProperty(value="Group")
	public String group;
	@JsonProperty(value="Owner")
	public String owner;
	@JsonProperty(value="Hosted Name") @JsonInclude(JsonInclude.Include.NON_NULL)
	public String hostedOn;
	@JsonProperty(value="Provider")
	public String provider;
	@JsonProperty(value="Consumer")
	public String consumers;
	
	@JsonProperty(value="Integration Pattern")
	public String integrationPattern;
	
	@JsonProperty(value="Confidence Factor")
	public String confidenceLevel;
	@JsonProperty(value="Count of Invocations")
	public String noOfInvocations;
	@JsonProperty(value="DB Calls (ms)" )
	public String dbCalls;
	@JsonProperty(value="Logic (ms)")
	public String logic;
	@JsonProperty(value="Rules (ms)")
	public String rules;
	@JsonProperty(value="Latency (ms)")
	public String latency;
	@JsonProperty(value="Time of Function/Call")
	public String timeOfFunction;
	@JsonProperty(value="Total Process Time")
	public String totalProcessTime;
	
	
	
	@JsonProperty(value="Total Process Path Length")
	public String totalProcessPathLength;
	@JsonProperty(value="Process NFR")
	public String processNFR;
	@JsonProperty(value="Total Process NFR")
	public String totalProcessNFR;
	
	@JsonProperty(value="Total Process Path Length NFR")
	public String totalProcessPathLengthNFR;

	/*@JsonProperty(value="NFR Target")
	public String nfrTarget;*/
	
	public String getGroup() {
		return group;
	}
	public void setGroup(String group) {
		this.group = group;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getTotalProcessNFR() {
		return totalProcessNFR;
	}
	public void setTotalProcessNFR(String totalProcessNFR) {
		this.totalProcessNFR = totalProcessNFR;
	}
	public String getIntegrationPattern() {
		return integrationPattern;
	}
	public void setIntegrationPattern(String integrationPattern) {
		this.integrationPattern = integrationPattern;
	}
	public String getDbCalls() {
		return dbCalls;
	}
	public void setDbCalls(String dbCalls) {
		this.dbCalls = dbCalls;
	}
	public String getHostedOn() {
		return hostedOn;
	}
	public void setHostedOn(String hostedOn) {
		this.hostedOn = hostedOn;
	}
	public String getConfidenceLevel() {
		return confidenceLevel;
	}
	public void setConfidenceLevel(String confidenceLevel) {
		this.confidenceLevel = confidenceLevel;
	}
	public String getTimeOfFunction() {
		return timeOfFunction;
	}
	public void setTimeOfFunction(String timeOfFunction) {
		this.timeOfFunction = timeOfFunction;
	}
	public String getTotalProcessPathLengthNFR() {
		return totalProcessPathLengthNFR;
	}
	public void setTotalProcessPathLengthNFR(String totalProcessPathLengthNFR) {
		this.totalProcessPathLengthNFR = totalProcessPathLengthNFR;
	}
	public String getProcessNFR() {
		return processNFR;
	}
	public void setProcessNFR(String processNFR) {
		this.processNFR = processNFR;
	}
	public String getTotalProcessPathLength() {
		return totalProcessPathLength;
	}
	public void setTotalProcessPathLength(String totalProcessPathLength) {
		this.totalProcessPathLength = totalProcessPathLength;
	}
	public String getProvider() {
		return provider;
	}
	public void setProvider(String provider) {
		this.provider = provider;
	}
	public String getRules() {
		return rules;
	}
	public void setRules(String rules) {
		this.rules = rules;
	}
	public String getTotalProcessTime() {
		return totalProcessTime;
	}
	public void setTotalProcessTime(String totalProcessTime) {
		this.totalProcessTime = totalProcessTime;
	}
	public String getConsumers() {
		return consumers;
	}
	public void setConsumers(String consumers) {
		this.consumers = consumers;
	}
	/*public String getNfrTarget() {
		return nfrTarget;
	}
	public void setNfrTarget(String nfrTarget) {
		this.nfrTarget = nfrTarget;
	}*/
	public String getLogic() {
		return logic;
	}
	public void setLogic(String logic) {
		this.logic = logic;
	}
	public String getLatency() {
		return latency;
	}
	public void setLatency(String latency) {
		this.latency = latency;
	}
	public String getNoOfInvocations() {
		return noOfInvocations;
	}
	public void setNoOfInvocations(String noOfInvocations) {
		this.noOfInvocations = noOfInvocations;
	}
	
	public String toString()
	{
		return " group : " +group;
	}
}
