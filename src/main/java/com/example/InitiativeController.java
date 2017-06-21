package com.example;

import java.util.ArrayList;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
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
import com.mongodb.util.JSON;

@RestController
public class InitiativeController {

	ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class); //Получение контекста
	DB db = ctx.getBean(DB.class);//Получение экземпляра Базы Данных
	DBCollection collect = null; // Создание объекта коллекции (NoSQL)
	String DBforInitiatives = "INITIATIVES", DBforVotes = "VOTES", DBforBlocks = "BLOCKS"; //Названия коллекций
	Gson gson = new Gson(); //Json converter для записи в БД
	
	
	//Запрос на получение итнициативы, содержащую описание descrption
    @RequestMapping( path = "/getinitiativebydescriprion/{description}", method = RequestMethod.GET)
    public Initiative getInitiativeByDescription(@PathVariable("description") String description){
    	collect = db.getCollection(DBforInitiatives);
    	BasicDBObject query = new BasicDBObject();
    	query.put("description", description);
    	DBCursor cursor = collect.find(query);
    	return gson.fromJson(gson.toJsonTree(cursor.next()),Initiative.class);
    }
    
   //Запрос на получение списка инициатив
    @RequestMapping(value="/getinitiatives",method={RequestMethod.POST,RequestMethod.GET})
    public List<Initiative> getAllInitives(){
    	ArrayList<Initiative> initiatives = new ArrayList<>();
    	collect = db.getCollection(DBforInitiatives);
    	System.err.println("Collection size "+collect.count());
    	DBCursor cursor = collect.find();
    	while (cursor.hasNext()){
    		initiatives.add(gson.fromJson(gson.toJson(cursor.next()), Initiative.class));
    	}
    	return initiatives;
    }
    
    //Добавление инициативы, пришедшей от пользователя.
  	@RequestMapping(value="/addinitiative",method=RequestMethod.POST)
      public void addNewInitive(@RequestBody Initiative initiative){
      	collect = db.getCollection(DBforInitiatives);
//      	BasicDBObject object = new BasicDBObject();
//      	object.put("description", initiative.description);
//      	object.put("variants", initiative.variants);
      	BasicDBObject whereQuery = new BasicDBObject();
      	whereQuery.put("description", initiative.description);
      	DBCursor cursor = collect.find(whereQuery);
      	if (!cursor.hasNext()){
      		collect.insert((DBObject)JSON.parse(gson.toJson(initiative)));
      	}
      	   	
      }
  	
  	@RequestMapping(value = "/cleanInitiativeDB", method={RequestMethod.POST,RequestMethod.GET})
	public void cleanDBInit(){
		collect = db.getCollection(DBforInitiatives);
		collect.drop();
	}
}
