package com.deloitte.exceltojson.processor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Constants {

	public final static String PATH_SLASH ="\\";
	public final static String CURRENT_FOLDER_PATH =  System.getProperty("user.dir");
	public final static String DATA_SHEET_NAME = "RawData";// +"remove this"; 
	public final static String META_DATA_SHEET_NAME = "MetaData"; 
	public final static List<String> META_DATA_IGNORE_COLk_VALUE =  new ArrayList<String>(Arrays.asList("IntegrationPatternCode", "Integration Patterns - Connection Type","Description","Name ","IntegrationPatternName"));
	public final static List<String> metadataIgnoreColBValue =  new ArrayList<String>(Arrays.asList("CategoryCode","Description","Name","CategoryName"));
	public final static List<String> metadataIgnoreColSValue =  new ArrayList<String>(Arrays.asList("MetricsStatusDescription"));
	public final static String GENERATING_FILE_NAME = "generatedDataJSON.json";
	public final static List<String> fileExtn =  new ArrayList<String>(Arrays.asList(".xlsm", ".xlsx"));
	
	
	public final static String ERR_FILE_FORMAT_NOT_FOUND = "File format not found. Please execute again with an .xlsx  or .xlsm file";
	public final static String ERR_INVALID_ARG = "Input Arguments are null or empty";
	public final static String ERR_UNKNOWN_ERROR = "Unknown error";
	public final static String ERR_SHEET_NOT_FOUND = "Sheets name are not matching...";
	
	
	public final static String EMPTY_STRING = "";
	
	public final static String SERIAL_NO_INDEX_STR = "serialNoIndex";
	public final static String ETA_STATUS_STR = "etaStatus";
	public final static String CHILDERN_STR ="children";
	public final static String NAME_STR = "name";
	public final static String DESCRIPTION_STR="description";
	public final static String CATEGORY_STR ="category";
	public final static String CONNECTION_TYPE_STR ="connectionType";
	public final static String LINK_INDEX_STR="linkIndex";
	public final static String DETAILS_STR ="details";
	public final static String GROUP_STR ="Group";
	public final static String OWNER_STR ="Owner";
	public final static String DB_CALLS_STR="DB Calls (ms)";
	public final static String HOSTED_ON_STR ="Hosted On";
	public final static String CONFIDENCE_LEVEL_STR ="Confidence Level";
	public final static String TIME_OF_CALL_STR ="Time of Function/Call";
	public final static String TOTAL_PROCESS_PATH_LEN_NRF_STR ="Total Process Path Length NFR";
	public final static String PROCESS_NFR_STR ="Process NFR";
	public final static String TOTAL_PROCESS_PATH_LEN_STR ="Total Process Path Length";
	public final static String PROVIDER_STR ="Provider";
	public final static String RULES_STR="Rules (ms)";
	public final static String TOTAL_PROCESS_TIME_STR ="Total Process Time";
	public final static String CONSUMER_STR ="Consumer(s)";
	public final static String NFR_TARGET_STR ="NFR Target";
	public final static String LOGIC_STR ="Logic (ms)";
	public final static String LATENCY_STR ="Latency (ms)";
	public final static String NO_OF_INVOCATIONS ="# of Invocations";
	public final static String DATA_STR = "data";
	public final static String META_STR ="meta";
	public final static String ITEMS_STR ="items";
	public final static String COLOR_STR ="color";
	
	public final static String ON_TARGET_STR="OnTarget";
	public final static String OFF_TARGET_STR="OffTarget";
	public final static String DEFAULT_CODE_STR = "000";
	public static final String ID = "_id";
}
