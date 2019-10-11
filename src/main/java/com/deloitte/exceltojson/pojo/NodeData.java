package com.deloitte.exceltojson.pojo;

import java.util.ArrayList;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({ "serialNoIndex","linkIndex", "name","description","etaStatus",   "category","connectionType","details","children" })
public class NodeData {

	public String serialNoIndex;
	public String etaStatus;
	public String name;
	public String description;
	public NodeDetails details;
	public String category;
	public String connectionType;
	public String linkIndex;
	ArrayList<NodeData> children;
	
	public String toString()
	{
		return "serialNoIndex : "+ serialNoIndex + "etaStatus : "+  etaStatus + "name : "+  name + "description : "+  description + "details : "+ details + "category : "+ category +
				 "connectionType : "+ connectionType + "linkIndex : "+linkIndex +"children : "+ children; 
	}
	
	public NodeData(String template) {
		super();
		this.serialNoIndex = "1";
		this.etaStatus = "On Target";
		this.name = "Root node";
		this.description = "Root node description";
		this.details = new NodeDetails("Root node", "Group", "owner", "hostedOn", "provider",
				"consumers", "PCF Internal (C-to-C)", "Group", "0", "0", "0",
				"0", "0", "0", "0", "0", "0", 
				"0", "0") ;
		this.category = "CC000";
		this.connectionType = "IP000";
		this.linkIndex = "0";
		
	}
	
	

	public String getSerialNoIndex() {
		return serialNoIndex;
	}
	
	public NodeData() {
		super();
		// TODO Auto-generated constructor stub
	}

	public void setSerialNoIndex(String serialNoIndex) {
		this.serialNoIndex = serialNoIndex;
	}
	public String getEtaStatus() {
		return etaStatus;
	}
	public void setEtaStatus(String etaStatus) {
		this.etaStatus = etaStatus;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public NodeDetails getDetails() {
		return details;
	}
	public void setDetails(NodeDetails details) {
		this.details = details;
	}
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	public String getConnectionType() {
		return connectionType;
	}
	public void setConnectionType(String connectionType) {
		this.connectionType = connectionType;
	}
	public String getLinkIndex() {
		return linkIndex;
	}
	public void setLinkIndex(String linkIndex) {
		this.linkIndex = linkIndex;
	}
	public ArrayList<NodeData> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<NodeData> children) {
		this.children = children;
	}
	
	
}
