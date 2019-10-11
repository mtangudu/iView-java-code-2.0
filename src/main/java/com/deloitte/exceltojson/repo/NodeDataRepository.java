package com.deloitte.exceltojson.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Component;

import com.deloitte.exceltojson.pojo.SheetData;

@Component
public interface NodeDataRepository extends MongoRepository<SheetData, String> {
	
	

}
