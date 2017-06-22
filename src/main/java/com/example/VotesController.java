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
import com.mongodb.util.JSON;

@RestController
public class VotesController {

	ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class); //Получение контекста
	DB db = ctx.getBean(DB.class);//Получение экземпляра Базы Данных
	DBCollection collect = null; // Создание объекта коллекции (NoSQL)
	String DBforInitiatives = "INITIATIVES", DBforVotes = "VOTES", DBforBlocks = "BLOCKS"; //Названия коллекций
	Gson gson = new Gson(); //Json converter для записи в БД
		

	@RequestMapping(value = "/cleanVotesDB", method={RequestMethod.POST,RequestMethod.GET})
	public void cleanDBInit(){
		collect = db.getCollection(DBforVotes);
		collect.drop();
	}
	 //Запрос на получение списка голосов за иниициативу
    @RequestMapping( path = "/getlistofvotes", method={RequestMethod.POST,RequestMethod.GET})
    public List<Vote> getVotesForInitiative(@RequestBody Initiative initiative){
    	collect = db.getCollection(DBforVotes);
    	ArrayList<Vote> votesForInitiative = new ArrayList<>();
    	BasicDBObject query = new BasicDBObject();
    	query.put("initiative.description", initiative.description);
    	System.err.println(gson.toJson(initiative));
    	DBCursor cursor = collect.find(query);
    	System.err.println("Selected votes:"+cursor.count());
    	while (cursor.hasNext()){
    		votesForInitiative.add(gson.fromJson(gson.toJson(cursor.next()), Vote.class));
    	}
    	
    	return votesForInitiative;
    }
    
    @RequestMapping(value="/getallvotes",method={RequestMethod.POST,RequestMethod.GET})
    public List<Vote> getAllVotesAll(){
    	ArrayList<Vote> votes = new ArrayList<>();
    	collect = db.getCollection(DBforVotes);
    	System.err.println("All votes count is: "+collect.count());
    	DBCursor cursor = collect.find();
    	while (cursor.hasNext()){
    		votes.add(gson.fromJson(gson.toJson(cursor.next()), Vote.class));
    	}
    	return votes;	
    }
    
  //Запрос на получение списка голосов для генерации блока
    @RequestMapping(value="/getvotes",method={RequestMethod.POST,RequestMethod.GET})
    public List<Vote> getAllVotes(){
    	collect = db.getCollection(DBforVotes);
    	DBCursor cursor = collect.find();
    	List<Vote> listOfVotes = new ArrayList<>();
    	while(cursor.hasNext()){
    		listOfVotes.add(gson.fromJson(gson.toJson(cursor.next()), Vote.class));
    	}
    	collect = db.getCollection(DBforBlocks);
    	cursor = collect.find();
    	List<Block> chain = new ArrayList<>();
    	while(cursor.hasNext()){
    		chain.add(gson.fromJson(gson.toJson(cursor.next()), Block.class));
    	}
    	
    	List<Vote> votesNotInBlock = new ArrayList<>();
    	for(int i = 0; i < listOfVotes.size(); i++){
    		boolean isTaken = false;
    		Vote vote = listOfVotes.get(i);
    		for(int j = 0; j < chain.size(); j++){
    			if (chain.size() == 1){
    				break;
    			}
    			Block b = chain.get(j);
    			if(b.votes.contains(vote)) isTaken = true;
    			
    		}
    		if (!isTaken) votesNotInBlock.add(listOfVotes.get(i)); 
    	}
    	
    	return votesNotInBlock;
    }
    
  //Создание голоса, пришедшего от пользователя. Проверка на достоверность, добавление в список нераспределённых голосов
  		@RequestMapping( path ="/addvote", method=RequestMethod.POST)
  		public void voteCreator(@RequestBody Vote vote){
  			boolean voteIsFirst = true;
  			collect = db.getCollection(DBforVotes);
  			BasicDBObject query = new BasicDBObject();
  			List<BasicDBObject> listForQuery = new ArrayList<>();
  			listForQuery.add(new BasicDBObject("publicKey", vote.publicKey));
  			listForQuery.add(new BasicDBObject("initiative", JSON.parse(gson.toJson(vote.initiative))));
  			query.put("$and", listForQuery);
  			System.err.println(query.toString());
  			System.err.println("previous votes count is: "+collect.find(query).count());
  			voteIsFirst = !collect.find(query).hasNext() || collect.find(query).count()==0 ; 
  			if (voteIsFirst){
  				String tmpDsaSign=vote.dsaSign;
  				byte[] sign=Base64.getMimeDecoder().decode(vote.dsaSign);
  				byte[] pubKey=Base64.getMimeDecoder().decode(vote.publicKey);
  				vote.dsaSign=null;
  				boolean valid = false;
  				try {
  					valid = DigitalSign.verifySig(gson.toJson(vote).getBytes(), DigitalSign.convertKey(pubKey) , sign);
  				} catch (Exception e) {
  					e.printStackTrace();
  				}
  				System.err.println(valid);
  				if(valid){
  					vote.dsaSign=tmpDsaSign;
  					collect.insert((DBObject)JSON.parse(gson.toJson(vote)));
  				}  
  			}	
  		}
		
}
