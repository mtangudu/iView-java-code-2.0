package com.deloitte.exceltojson.pojo;

import java.util.HashMap;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;


@Document(collection = "node_data")
public class SheetData {

	@Override
	public String toString() {
		return "Users [id=" + id + ", meta=" + meta + ", data=" + data +  "]";
	}

	@Id
	private String id;
	public HashMap<String,Object> meta;
	public NodeData data;
	
	public HashMap<String, Object> getMeta() {
		return meta;
	}
	public void setMeta(HashMap<String, Object> meta) {
		this.meta = meta;
	}
	public NodeData getData() {
		return data;
	}
	public void setData(NodeData data) {
		this.data = data;
	}

	
	
	/*
	 * public SheetData(String meta, String data) { this.meta = meta; this.data =
	 * data; }
	 */
}
