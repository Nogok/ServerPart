package com.example;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@RestController
public class AddDataController {

	
	ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
	DB db = ctx.getBean(DB.class);
	DBCollection collect = null;
	String DBforInitiatives = "INITIATIVES", DBforVotes = "VOTES", DBforBlocks = "BLOCKS";
	Gson gson = new Gson();
	

		
}
