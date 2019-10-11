package com.deloitte.exceltojson.processor;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.util.CellReference;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.bson.Document;
import org.apache.poi.ss.usermodel.CellType;
import org.springframework.beans.factory.annotation.Autowired;

import com.deloitte.exceltojson.controller.ExcelToJsonController;
import com.deloitte.exceltojson.pojo.NodeData;
import com.deloitte.exceltojson.pojo.NodeDetails;
import com.deloitte.exceltojson.repo.NodeDataRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mongodb.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.model.Filters;
import com.deloitte.exceltojson.util.Calculate;

@SuppressWarnings("unchecked")
public class MessageProcessor {

	final static Logger log = Logger.getLogger(MessageProcessor.class);

	@Autowired
	NodeDataRepository nodeDataRepository;
	
	
	
	public static LinkedHashMap<String, Object> updateNFR (Properties appProperties, LinkedHashMap<String, Object> nodeData,Map<String, Integer> countMap ){
		ArrayList<LinkedHashMap<String, Object>> children = (ArrayList<LinkedHashMap<String, Object>>) nodeData.get("children");
		countMap.put("count",countMap.get("count") + 1);
		
		for (LinkedHashMap<String, Object> c : children)
		{
			int cb = isChildAvailable(c);
			if (cb == 1){
				updateNFR(appProperties, c, countMap );
			}
			else{
				LinkedHashMap<String, Object> nd = (LinkedHashMap<String, Object>) c.get(appProperties.get("text.data.details"));
				int timeOfFun = Calculate.getTimeOfFunction(appProperties, c);
				int totalProcessTime = timeOfFun *Calculate.convertStringToInt(nd.get(appProperties.get("text.data.details.countOfInvocations"))) ;
				int totalProcessNFR = Calculate.convertStringToInt(nd.get(appProperties.get("text.data.details.countOfInvocations")))
						* Calculate.convertStringToInt(nd.get(appProperties.get("text.data.details.ProcessNFR")));
				nd.put((String) appProperties.get("text.data.details.timeOfFuncation"), String.valueOf(timeOfFun));
				nd.put((String) appProperties.get("text.data.details.totalProcessTime"), String.valueOf(totalProcessTime));
				nd.put((String) appProperties.get("text.data.details.totalProcessPathLength"), String.valueOf(totalProcessTime));
				nd.put((String) appProperties.get("text.data.details.totalProcessNFR"), String.valueOf(totalProcessNFR));
				nd.put((String) appProperties.get("text.data.details.totalProcessPathLengthNFR"), String.valueOf(totalProcessNFR));				
				c.put((String) appProperties.get("text.data.details"), nd);
			}
		}
		Map<String, Integer> cm = new LinkedHashMap<String, Integer>();
		cm.put("count", 1);
		return updateNFRAtLocation(appProperties, nodeData, countMap.get("count")-1,cm );
	}
	public static LinkedHashMap<String, Object> updateNFRAtLocation (Properties appProperties, LinkedHashMap<String, Object> nodeData, int currentLocation,Map<String,Integer> countMap)
	{		//GOT LOCATION AS 2 GOT 2ND OBJECT TOO..
		
		for (int i = currentLocation; i>0; i--)
		{
			if (currentLocation == countMap.get("count"))
			{
				ArrayList<LinkedHashMap<String, Object>> children = (ArrayList<LinkedHashMap<String, Object>>) nodeData.get("children");
				LinkedHashMap<String, Object> nd = (LinkedHashMap<String, Object>) nodeData.get(appProperties.get("text.data.details"));
				int timeOfFun = Calculate.getTimeOfFunction(appProperties, nodeData);
				int totalProcessTime = Calculate.convertStringToInt(nd.get(appProperties.get("text.data.details.countOfInvocations"))) * timeOfFun;
				int totalProcessNFR = Calculate.convertStringToInt(nd.get(appProperties.get("text.data.details.countOfInvocations"))) 
							* Calculate.convertStringToInt(nd.get(appProperties.get("text.data.details.ProcessNFR")));
				
				nd.put((String) appProperties.get("text.data.details.timeOfFuncation"), String.valueOf(timeOfFun));
				nd.put((String) appProperties.get("text.data.details.totalProcessTime"), String.valueOf(totalProcessTime));
				nd.put((String) appProperties.get("text.data.details.totalProcessPathLength"), Calculate.getTotalProcessPathLenghtForParent(appProperties, totalProcessTime, children));
				nd.put((String) appProperties.get("text.data.details.totalProcessNFR"), String.valueOf(totalProcessNFR));
				nd.put((String) appProperties.get("text.data.details.totalProcessPathLengthNFR"),Calculate.getTotalProcessPathLenghtNFRForParent(appProperties, totalProcessNFR,children));
				nodeData.put((String) appProperties.get("text.data.details"), nd);
			}
			else
			{
				countMap.put("count", countMap.get("count")+1);
				updateNFRAtLocation(appProperties, nodeData, currentLocation,countMap);
			}
		}
		return nodeData;
	}
	
	
	
	public static int isChildAvailable (Map<String, Object> nodeData)
	{
		ArrayList<Map<String, Object>> children = (ArrayList<Map<String, Object>>) nodeData.get("children");
		if (children != null && children.size()>0)
			return 1;
		else
			return 0;
	}
	
	public static NodeData getUpdatedData (NodeData nd, Properties fieldDetails, NodeData updatedData)
	{
		ArrayList<HashMap<String, String>> al = getAsMap(nd, fieldDetails, updatedData);
		NodeData nodeData = prepareObject(al, fieldDetails, "1", true);
		return nodeData;
	}
	
	public static NodeData getDataBySerialNo (NodeData nd, Properties fieldDetails, String serialNo)
	{
		NodeData updatedData = new NodeData();
		ArrayList<HashMap<String, String>> al = getAsMap(nd, fieldDetails, updatedData);
		NodeData nodeData = prepareObject(al, fieldDetails, serialNo, true);
		return nodeData;
	}
	public static boolean isCurrentData(NodeData currentData, NodeData updatedData)
	{
		if (currentData.getSerialNoIndex().equals(updatedData.getSerialNoIndex()))
			return true;
		//return updatedData;
		return false;
	}
	
	public static ArrayList <HashMap<String, String>> getAsMap(NodeData nd, Properties fieldDetails, NodeData updatedData)
	{
		
		ArrayList <HashMap<String, String>> al = new ArrayList <HashMap<String, String>>(); 
		if (isCurrentData(nd, updatedData))
		{
			HashMap<String, String> hm = new HashMap<String, String>(); 
			hm.put(fieldDetails.getProperty("data.serialNoIndex"),updatedData.getSerialNoIndex());
			hm.put(fieldDetails.getProperty("data.etaStatus"),updatedData.getEtaStatus());
			hm.put(fieldDetails.getProperty("data.name"),updatedData.getName());
			hm.put(fieldDetails.getProperty("data.description"),updatedData.getDescription());
			hm.put(fieldDetails.getProperty("data.category"),updatedData.getCategory());
			hm.put(fieldDetails.getProperty("data.connectionType"),updatedData.getConnectionType());
			hm.put(fieldDetails.getProperty("data.linkIndex"),updatedData.getLinkIndex());
			NodeDetails updatedDataetails = updatedData.getDetails();
			hm.put(fieldDetails.getProperty("data.details.group"),updatedDataetails.getGroup());
			hm.put(fieldDetails.getProperty("data.details.owner"),updatedDataetails.getOwner());
			hm.put(fieldDetails.getProperty("data.details.dbCalls"),updatedDataetails.getDbCalls());
			hm.put(fieldDetails.getProperty("data.details.hostedOn"),updatedDataetails.getHostedOn());
			hm.put(fieldDetails.getProperty("data.details.confidenceLevel"),updatedDataetails.getConfidenceLevel());
			hm.put(fieldDetails.getProperty("data.details.timeOfFunction"),updatedDataetails.getTimeOfFunction());
			hm.put(fieldDetails.getProperty("data.details.totalProcessPathLengthNFR"),updatedDataetails.getTotalProcessPathLengthNFR());
			hm.put(fieldDetails.getProperty("data.details.processNFR"),updatedDataetails.getProcessNFR());
			hm.put(fieldDetails.getProperty("data.details.totalProcessPathLength"),updatedDataetails.getTotalProcessPathLength());
			hm.put(fieldDetails.getProperty("data.details.provider"),updatedDataetails.getProvider());
			hm.put(fieldDetails.getProperty("data.details.rules"),updatedDataetails.getRules());
			hm.put(fieldDetails.getProperty("data.details.totalProcessTime"),updatedDataetails.getTotalProcessTime());
			hm.put(fieldDetails.getProperty("data.details.consumer"),updatedDataetails.getConsumers());
			hm.put(fieldDetails.getProperty("data.details.logic"),updatedDataetails.getLogic());
			hm.put(fieldDetails.getProperty("data.details.latency"),updatedDataetails.getLatency());
			
			hm.put(fieldDetails.getProperty("data.details.integrationPattern"),updatedDataetails.getIntegrationPattern());
			hm.put(fieldDetails.getProperty("data.details.totalProcessNFR"),updatedDataetails.getTotalProcessNFR());
			hm.put(fieldDetails.getProperty("data.details.noOfInvocations"),updatedDataetails.getNoOfInvocations());
			hm.put(fieldDetails.getProperty("data.details.name"),updatedDataetails.getName());
			al.add(hm);
			if ((nd.getChildren()!= null) && nd.getChildren().size()!=0)
			{
				ArrayList <HashMap<String, String>> childAL = new ArrayList <HashMap<String, String>>();
				for (NodeData cnd : nd.getChildren())
				{
					childAL.addAll(getAsMap(cnd, fieldDetails,updatedData));
				}
				al.addAll(childAL);
			}
			
			
		}
		else{
			HashMap<String, String> hm = new HashMap<String, String>(); 
			hm.put(fieldDetails.getProperty("data.serialNoIndex"),nd.getSerialNoIndex());
			hm.put(fieldDetails.getProperty("data.etaStatus"),nd.getEtaStatus());
			hm.put(fieldDetails.getProperty("data.name"),nd.getName());
			hm.put(fieldDetails.getProperty("data.description"),nd.getDescription());
			hm.put(fieldDetails.getProperty("data.category"),nd.getCategory());
			hm.put(fieldDetails.getProperty("data.connectionType"),nd.getConnectionType());
			hm.put(fieldDetails.getProperty("data.linkIndex"),nd.getLinkIndex());
			
			NodeDetails nDetails = nd.getDetails();
			hm.put(fieldDetails.getProperty("data.details.group"),nDetails.getGroup());
			hm.put(fieldDetails.getProperty("data.details.owner"),nDetails.getOwner());
			hm.put(fieldDetails.getProperty("data.details.dbCalls"),nDetails.getDbCalls());
			hm.put(fieldDetails.getProperty("data.details.hostedOn"),nDetails.getHostedOn());
			hm.put(fieldDetails.getProperty("data.details.confidenceLevel"),nDetails.getConfidenceLevel());
			hm.put(fieldDetails.getProperty("data.details.timeOfFunction"),nDetails.getTimeOfFunction());
			hm.put(fieldDetails.getProperty("data.details.totalProcessPathLengthNFR"),nDetails.getTotalProcessPathLengthNFR());
			hm.put(fieldDetails.getProperty("data.details.processNFR"),nDetails.getProcessNFR());
			hm.put(fieldDetails.getProperty("data.details.totalProcessPathLength"),nDetails.getTotalProcessPathLength());
			hm.put(fieldDetails.getProperty("data.details.provider"),nDetails.getProvider());
			hm.put(fieldDetails.getProperty("data.details.rules"),nDetails.getRules());
			hm.put(fieldDetails.getProperty("data.details.totalProcessTime"),nDetails.getTotalProcessTime());
			hm.put(fieldDetails.getProperty("data.details.consumer"),nDetails.getConsumers());
			hm.put(fieldDetails.getProperty("data.details.logic"),nDetails.getLogic());
			hm.put(fieldDetails.getProperty("data.details.latency"),nDetails.getLatency());
			hm.put(fieldDetails.getProperty("data.details.integrationPattern"),nDetails.getIntegrationPattern());
			hm.put(fieldDetails.getProperty("data.details.totalProcessNFR"),nDetails.getTotalProcessNFR());
			hm.put(fieldDetails.getProperty("data.details.noOfInvocations"),nDetails.getNoOfInvocations());
			hm.put(fieldDetails.getProperty("data.details.name"),nDetails.getName());
			al.add(hm);
			if ((nd.getChildren()!= null) && nd.getChildren().size()!=0)
			{
				ArrayList <HashMap<String, String>> chal = new ArrayList <HashMap<String, String>>();
				for (NodeData cnd : nd.getChildren())
				{
					chal.addAll(getAsMap(cnd, fieldDetails,updatedData));
				}
				al.addAll(chal);
			}
		}
		return al;
	}
	
	/*public static NodeData updateData(NodeData currentData, NodeData updatedData)
	{
		currentData.setCategory(updatedData.getCategory());
		currentData.setChildren(updatedData.getChildren());
		currentData.setConnectionType(updatedData.getConnectionType());
		currentData.setDescription(updatedData.getDescription());
		currentData.setDetails(updatedData.getDetails());
		currentData.setEtaStatus(updatedData.getEtaStatus());
		currentData.setLinkIndex(updatedData.getLinkIndex());
		currentData.setName(updatedData.getName());
		currentData.setSerialNoIndex(updatedData.getSerialNoIndex());
		
		NodeDetails updatedNodeDetails = updatedData.getDetails();
		NodeDetails currentNodeDetails = currentData.getDetails();
		
		currentNodeDetails.setConfidenceLevel(updatedNodeDetails.getConfidenceLevel());
		currentNodeDetails.setConsumers(updatedNodeDetails.getConsumers());
		currentNodeDetails.setDbCalls(updatedNodeDetails.getDbCalls());
		currentNodeDetails.setGroup(updatedNodeDetails.getGroup());
		currentNodeDetails.setHostedOn(updatedNodeDetails.getHostedOn());
		currentNodeDetails.setIntegrationPattern(updatedNodeDetails.getIntegrationPattern());
		currentNodeDetails.setLatency(updatedNodeDetails.getLatency());
		currentNodeDetails.setLogic(updatedNodeDetails.getLogic());
		currentNodeDetails.setName(updatedNodeDetails.getName());
		currentNodeDetails.setNoOfInvocations(updatedNodeDetails.getNoOfInvocations());
		currentNodeDetails.setOwner(updatedNodeDetails.getOwner());
		currentNodeDetails.setProcessNFR(updatedNodeDetails.getProcessNFR());
		currentNodeDetails.setProvider(updatedNodeDetails.getProvider());
		currentNodeDetails.setRules(updatedNodeDetails.getRules());
		currentNodeDetails.setTimeOfFunction(updatedNodeDetails.getTimeOfFunction());
		currentNodeDetails.setTotalProcessNFR(updatedNodeDetails.getTotalProcessNFR());
		currentNodeDetails.setTotalProcessPathLength(updatedNodeDetails.getTotalProcessPathLength());
		currentNodeDetails.setTotalProcessPathLengthNFR(updatedNodeDetails.getTotalProcessPathLengthNFR());
		currentNodeDetails.setTotalProcessTime(updatedNodeDetails.getTotalProcessTime());
		currentData.setDetails(currentNodeDetails);
		return currentData;
	}*/
	
	public static HashMap<String, Object> getConnectionTypeDetails(Properties fieldDetails,
			ArrayList<HashMap<String, String>> sheetDataList) {
		HashMap<String, Object> connectionCodes = new HashMap<String, Object>();
		connectionCodes = buildConnectionItems(sheetDataList, fieldDetails);
		HashMap<String, Object> connectionDetails = new HashMap<String, Object>();
		connectionDetails.put("name", "Integration Patterns");
		connectionDetails.put("items", connectionCodes);
		return connectionDetails;
	}

	public static HashMap<String, Object> getCategoryDetails(Properties fieldDetails,
			ArrayList<HashMap<String, String>> sheetDataList) {
		HashMap<String, Object> categoryCodes = new HashMap<String, Object>();
		categoryCodes = buildCategoryItems(sheetDataList, fieldDetails);
		HashMap<String, Object> categoryDetails = new HashMap<String, Object>();
		categoryDetails.put("name", "Hosted Platform / Owner");
		categoryDetails.put("items", categoryCodes);
		return categoryDetails;
	}

	public static HashMap<String, Object> getETADetails(Properties fieldDetails,
			ArrayList<HashMap<String, String>> sheetDataList) {
		HashMap<String, Object> etaCodes = new HashMap<String, Object>();
		etaCodes = buildETAItems(sheetDataList, fieldDetails);
		HashMap<String, Object> etaDetails = new HashMap<String, Object>();
		etaDetails.put("name", "On Target or off Target");
		etaDetails.put("items", etaCodes);
		return etaDetails;
	}

	public static HashMap<String, Object> getMetaData(Properties fieldDetails,
			ArrayList<HashMap<String, String>> sheetDataList) {
		HashMap<String, Object> metaData = new HashMap<String, Object>();
		metaData.put("category", getCategoryDetails(fieldDetails, sheetDataList));
		metaData.put("connectionType", getConnectionTypeDetails(fieldDetails, sheetDataList));
		metaData.put("etaStatus", getETADetails(fieldDetails, sheetDataList));

		return metaData;
	}

	public void processSheetAndGetData(Properties fieldDetails, ArrayList<Sheet> sheets, String serviceLine, String description) {
		NodeData nodeData = new NodeData();
		ObjectMapper mapper = new ObjectMapper();
		JsonNode rootNode = mapper.createObjectNode();

		HashMap<String, Object> metaData = new HashMap<String, Object>();

		for (Sheet sheet : sheets) {
			if (sheet.getSheetName().equalsIgnoreCase(Constants.META_DATA_SHEET_NAME)) {
				ArrayList<HashMap<String, String>> sheetDataList = getSheetDataList(
						fieldDetails.getProperty("metadata.header.column.no"), sheet);
				metaData = getMetaData(fieldDetails, sheetDataList);
			}
		}

		for (Sheet sheet : sheets) {
			if (sheet.getSheetName().equalsIgnoreCase(Constants.DATA_SHEET_NAME)) {
				ArrayList<HashMap<String, String>> sheetDataList = getSheetDataList(
						fieldDetails.getProperty("data.header.column.no"), sheet);
				nodeData = prepareObject(sheetDataList, fieldDetails, "1", true);
				((ObjectNode) rootNode).putPOJO("data", nodeData);
				((ObjectNode) rootNode).putPOJO(Constants.META_STR, metaData);

				InputStream propertiesInput = ExcelToJsonController.class.getClassLoader()
						.getResourceAsStream("application.properties");
				Properties appProperties = new Properties();
				try { // insert service line 
					appProperties.load(propertiesInput);

					MongoClient mongoClient = new MongoClient(appProperties.getProperty("mongodb.host"),
							new Integer(appProperties.getProperty("mongodb.port")));
					MongoDatabase db = mongoClient.getDatabase(appProperties.getProperty("mongodb.db"));
					//collection name 
					MongoCollection<Document> coll = db.getCollection("service_line");

					coll.deleteOne(Filters.eq("_id", serviceLine));

					Document doc = new Document();
					doc.append("_id", serviceLine);
					doc.append("description", description);

					coll.insertOne(doc);

					mongoClient.close();

				} catch (IOException e) {
					log.error("Sorry, unable to find application.properties");
				}
				
				try {
					appProperties.load(propertiesInput);

					MongoClient mongoClient = new MongoClient(appProperties.getProperty("mongodb.host"),
							new Integer(appProperties.getProperty("mongodb.port")));
					MongoDatabase db = mongoClient.getDatabase(appProperties.getProperty("mongodb.db"));
					MongoCollection<Document> coll = db.getCollection(appProperties.getProperty("mongodb.collection"));

					coll.deleteOne(Filters.eq("_id", serviceLine));
					Map<String, Object> addDetails = new HashMap<String, Object>();
					addDetails.put("insertedTimeStamp", new Timestamp(System.currentTimeMillis()).toString());
					addDetails.put("isLocked", false);
					addDetails.put("updatedTimeStamp", null);
					Document doc = new Document();
					doc.append("_id", serviceLine);
					doc.append("meta", metaData);
					doc.append("data", mapper.convertValue(nodeData, Map.class));
					doc.append("details", mapper.convertValue(addDetails, Map.class));

					coll.insertOne(doc);

					mongoClient.close();

				} catch (IOException e) {
					log.error("Sorry, unable to find application.properties");
				}
			}
		}
	}

	public static NodeData prepareObject(ArrayList<HashMap<String, String>> sheetDataList, Properties fieldDetails,
			String serialNoIndex, boolean isParent) {
		NodeData nd = new NodeData();
		int sheetDataLen = sheetDataList.size();
		for (int i = 0; i < sheetDataLen; i++) {
			
			HashMap<String, String> singleNodeData = sheetDataList.get(i);
			if (serialNoIndex.equalsIgnoreCase(singleNodeData.get(fieldDetails.getProperty("data.serialNoIndex")))) {
				nd.setSerialNoIndex(singleNodeData.get(fieldDetails.getProperty("data.serialNoIndex")));
				nd.setEtaStatus(singleNodeData.get(fieldDetails.getProperty("data.etaStatus")));
				nd.setName(singleNodeData.get(fieldDetails.getProperty("data.name")));
				nd.setDescription(singleNodeData.get(fieldDetails.getProperty("data.description")));
				nd.setCategory(singleNodeData.get(fieldDetails.getProperty("data.category")));
				nd.setConnectionType(singleNodeData.get(fieldDetails.getProperty("data.connectionType")));
				nd.setLinkIndex(singleNodeData.get(fieldDetails.getProperty("data.linkIndex")));

				NodeDetails nDetails = new NodeDetails();
				nDetails.setGroup(singleNodeData.get(fieldDetails.getProperty("data.details.group")));
				nDetails.setOwner(singleNodeData.get(fieldDetails.getProperty("data.details.owner")));
				nDetails.setDbCalls(singleNodeData.get(fieldDetails.getProperty("data.details.dbCalls")));
				nDetails.setHostedOn(singleNodeData.get(fieldDetails.getProperty("data.details.hostedOn")));
				nDetails.setConfidenceLevel(
						singleNodeData.get(fieldDetails.getProperty("data.details.confidenceLevel")));
				nDetails.setTimeOfFunction(singleNodeData.get(fieldDetails.getProperty("data.details.timeOfFunction")));
				nDetails.setTotalProcessPathLengthNFR(
						singleNodeData.get(fieldDetails.getProperty("data.details.totalProcessPathLengthNFR")));
				nDetails.setProcessNFR(singleNodeData.get(fieldDetails.getProperty("data.details.processNFR")));
				nDetails.setTotalProcessPathLength(
						singleNodeData.get(fieldDetails.getProperty("data.details.totalProcessPathLength")));
				nDetails.setProvider(singleNodeData.get(fieldDetails.getProperty("data.details.provider")));
				nDetails.setRules(singleNodeData.get(fieldDetails.getProperty("data.details.rules")));
				nDetails.setTotalProcessTime(
						singleNodeData.get(fieldDetails.getProperty("data.details.totalProcessTime")));
				nDetails.setConsumers(singleNodeData.get(fieldDetails.getProperty("data.details.consumer")));
				// nDetails.setNfrTarget(singleNodeData.get(fieldDetails.getProperty("nfrTarget")));
				nDetails.setLogic(singleNodeData.get(fieldDetails.getProperty("data.details.logic")));
				nDetails.setLatency(singleNodeData.get(fieldDetails.getProperty("data.details.latency")));
				nDetails.setIntegrationPattern(
						singleNodeData.get(fieldDetails.getProperty("data.details.integrationPattern")));
				nDetails.setTotalProcessNFR(
						singleNodeData.get(fieldDetails.getProperty("data.details.totalProcessNFR")));
				nDetails.setNoOfInvocations(
						singleNodeData.get(fieldDetails.getProperty("data.details.noOfInvocations")));
				nDetails.setName(singleNodeData.get(fieldDetails.getProperty("data.details.name")));
				nd.setDetails(nDetails);
				ArrayList<NodeData> child = getChildObject(sheetDataList, fieldDetails, serialNoIndex);
				if (child.size() > 0)
					nd.setChildren(child);
				if (isParent)
					break;
			}
		}
		return nd;
	}

	public static ArrayList<NodeData> getChildObject(ArrayList<HashMap<String, String>> sheetDataList,
			Properties fieldDetails, String serialNoIndex) {
		int sheetDataLen = sheetDataList.size();
		ArrayList<NodeData> child = new ArrayList<NodeData>();
		for (int i = 0; i < sheetDataLen; i++) {
			HashMap<String, String> singleNodeData = sheetDataList.get(i);

			if (serialNoIndex.equalsIgnoreCase(singleNodeData.get(fieldDetails.getProperty("data.linkIndex")))) {
				child.add(prepareObject(sheetDataList, fieldDetails,
						singleNodeData.get(fieldDetails.getProperty("data.serialNoIndex")), false));
			}
		}
		return child;
	}

	public static ArrayList<HashMap<String, String>> getSheetDataList(String headerNo, Sheet sheet) {
		ArrayList<HashMap<String, String>> sheetData = new ArrayList<HashMap<String, String>>();

		// Get the first and last sheet row number.
		int firstRowNum = sheet.getFirstRowNum();
		int lastRowNum = sheet.getLastRowNum();
		if (lastRowNum > 0) {
			// Loop in sheet rows.
			for (int i = firstRowNum; i < lastRowNum + 1; i++) {
				if (i > Integer.parseInt(headerNo) - 1) {
					// Get current row object.
					Row row = sheet.getRow(i);

					// Get first and last cell number.
					int firstCellNum = row.getFirstCellNum();
					int lastCellNum = row.getLastCellNum();
					// Loop in the row cells.
					HashMap<String, String> currentRow = new HashMap<String, String>();
					for (int j = firstCellNum; j < lastCellNum; j++) {
						// Get current cell.

						Cell cell = row.getCell(j);
						if (cell != null) {
							CellReference.convertNumToColString(cell.getColumnIndex());
							CellReference.convertNumToColString(cell.getColumnIndex());
							String cellValue = getCellValue(cell);
							currentRow.put(CellReference.convertNumToColString(cell.getColumnIndex()), cellValue);
						}
					}
					sheetData.add(currentRow);
				}
			}
		}
		return sheetData;
	}

	public static String getCellValue(Cell cell) {
		String cellValue = "";
		if (cell == null)
			return cellValue;
		if (cell.getCellTypeEnum() == CellType.BOOLEAN)
			cellValue = Boolean.toString(cell.getBooleanCellValue());
		else if (cell.getCellTypeEnum() == CellType.NUMERIC)
			cellValue = String.valueOf((int) cell.getNumericCellValue());
		else if (cell.getCellTypeEnum() == CellType.FORMULA) {
			if (CellType.NUMERIC == cell.getCachedFormulaResultTypeEnum())
				cellValue = String.valueOf((int) cell.getNumericCellValue());
			else if (CellType.STRING == cell.getCachedFormulaResultTypeEnum())
				cellValue = cell.getStringCellValue();
			else
				System.out.println("UnIdentified Formula result data type found. ");
		} else if (cell.getCellTypeEnum() == CellType.STRING)
			cellValue = cell.getStringCellValue();
		else if (cell.getCellTypeEnum() == CellType.BLANK) {
			// ignored as we are setting value at the ini level
		} else {
			System.out.println("Unidentitifed cellType Found");
		}
		return cellValue;
	}

	public static HashMap<String, Object> buildCategoryItems(ArrayList<HashMap<String, String>> sheetDataList,
			Properties fieldDetails) {
		int sheetDataListSize = sheetDataList.size();
		ArrayList<HashMap<String, Object>> categoryCodeList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> categoryCode = new HashMap<String, Object>();
		for (int i = 0; i < sheetDataListSize; i++) {
			HashMap<String, String> row = sheetDataList.get(i);
			HashMap<String, String> categoryObj = new HashMap<String, String>();
			if (row.get(fieldDetails.getProperty("metadata.categoryDesc")) != null) {
				categoryObj.put("color", row.get(fieldDetails.getProperty("metadata.categoryColor")));
				categoryObj.put("description", row.get(fieldDetails.getProperty("metadata.categoryDesc")));

				categoryCode.put(row.get(fieldDetails.getProperty("metadata.categoryCode")), categoryObj);
				categoryCodeList.add(categoryCode);
			}
		}
		return categoryCode;
	}

	public static HashMap<String, Object> buildETAItems(ArrayList<HashMap<String, String>> sheetDataList,
			Properties fieldDetails) {
		int sheetDataListSize = sheetDataList.size();
		ArrayList<HashMap<String, Object>> etaCodeList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> categoryCode = new HashMap<String, Object>();
		for (int i = 0; i < sheetDataListSize; i++) {
			HashMap<String, String> row = sheetDataList.get(i);
			HashMap<String, String> etaObj = new HashMap<String, String>();
			if (row.get(fieldDetails.getProperty("metadata.etaDesc")) != null) {
				etaObj.put("color", row.get(fieldDetails.getProperty("metadata.etaColor")));
				etaObj.put("description", row.get(fieldDetails.getProperty("metadata.etaDesc")));

				categoryCode.put(row.get(fieldDetails.getProperty("metadata.etaCode")), etaObj);
				etaCodeList.add(categoryCode);
			}
		}
		return categoryCode;
	}

	public static HashMap<String, Object> buildConnectionItems(ArrayList<HashMap<String, String>> sheetDataList,
			Properties fieldDetails) {
		int sheetDataListSize = sheetDataList.size();
		ArrayList<HashMap<String, Object>> connectionCodeList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> categoryCode = new HashMap<String, Object>();
		for (int i = 0; i < sheetDataListSize; i++) {
			HashMap<String, String> row = sheetDataList.get(i);
			HashMap<String, String> connectionObj = new HashMap<String, String>();
			if (row.get(fieldDetails.getProperty("metadata.connectionDesc")) != null) {
				connectionObj.put("color", row.get(fieldDetails.getProperty("metadata.connectionColor")));
				connectionObj.put("description", row.get(fieldDetails.getProperty("metadata.connectionDesc")));
				// HashMap<String, Object> categoryCode = new HashMap<String, Object>();
				categoryCode.put(row.get(fieldDetails.getProperty("metadata.connectionCode")), connectionObj);
				connectionCodeList.add(categoryCode);
			}
		}
		return categoryCode;
	}

}
