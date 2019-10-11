package com.deloitte.exceltojson.pojo;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;

import com.deloitte.exceltojson.processor.Constants;

public class ExcelReader {

	final static Logger log = Logger.getLogger(ExcelReader.class);
	
	public ArrayList<Sheet> readExcel (InputStream is) throws InvalidFormatException{
		
		Workbook workbook;
		
		log.info("Processing Mapping File...");
		
		try {
			ArrayList<Sheet> al = new ArrayList<Sheet>();
			//workbook = WorkbookFactory.create(new File(mappingFilePath));
			workbook = WorkbookFactory.create(is);
			
	        for(Sheet sheet: workbook) {
	        	if (sheet.getSheetName() != null && (sheet.getSheetName().trim().equalsIgnoreCase(Constants.DATA_SHEET_NAME ) ||
	        			sheet.getSheetName().trim().equalsIgnoreCase(Constants.META_DATA_SHEET_NAME)))
	        	{
	        		al.add(sheet);
	        	}
	        }
	        //Sheet sheet = workbook.getSheetAt(0); // Getting the Sheet at index zero
	        workbook.close();
	        return al;
		} catch (EncryptedDocumentException | IOException e) {
			e.printStackTrace();
			log.error("Error while reading the file");
		}
		return null;
	}
	
}
