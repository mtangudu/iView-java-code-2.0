package com.deloitte.exceltojson.util;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;

@SuppressWarnings("unchecked")
public class  Calculate {
	
	public static int getTimeOfFunction (Properties appProperties, LinkedHashMap<String, Object> nodeData)
	{
		LinkedHashMap<String, Object> nodeDetails = (LinkedHashMap<String, Object>) nodeData.get("details");
		int dbCall = convertStringToInt(nodeDetails.get(appProperties.get("text.data.details.dbCalls")));
		int logic = convertStringToInt(nodeDetails.get(appProperties.get("text.data.details.logic")));
		int rules =convertStringToInt(nodeDetails.get(appProperties.get("text.data.details.rules")));
		int latency =convertStringToInt(nodeDetails.get(appProperties.get("text.data.details.latency")));
		return dbCall + logic+ rules+ latency;
	}
	public static String getTotalProcessPathLenghtNFRForParent (Properties appProperties, int totalProcessNFR, ArrayList<LinkedHashMap<String, Object>> children)
	{
		int nfr=0;
		for (Map<String, Object> c: children){
			Map<String, Object> nd = (Map<String, Object>) c.get("details");
			if (nd.get(appProperties.get("text.data.details.totalProcessPathLengthNFR")) != null)
				nfr = nfr + Integer.valueOf(nd.get(appProperties.get("text.data.details.totalProcessPathLengthNFR")).toString());
		}
		return String.valueOf(nfr + totalProcessNFR);
	}
	
	public static String getTotalProcessPathLenghtForParent (Properties appProperties, int totalProcessPathLength, ArrayList<LinkedHashMap<String, Object>> children)
	{
		int nfr=0;
		for (Map<String, Object> c: children){
			Map<String, Object> nd = (Map<String, Object>) c.get("details");
			if (nd.get(appProperties.get("text.data.details.totalProcessPathLength")) != null)
				nfr = nfr + Integer.valueOf(nd.get(appProperties.get("text.data.details.totalProcessPathLength")).toString());
		}
		return String.valueOf(nfr + totalProcessPathLength);
	}
	public static int convertStringToInt(Object valueToConvert)
	{
		if (valueToConvert == null)
			return 0;
		else
		return Integer.valueOf(valueToConvert.toString());
	}
}
