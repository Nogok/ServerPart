package com.example;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.Gson;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

@RestController
public class GetDataConroller {
	
	ApplicationContext ctx = new AnnotationConfigApplicationContext(SpringConfig.class);
	DB db = ctx.getBean(DB.class);
	DBCollection collect = null;
	String DBforInitiatives = "INITIATIVES", DBforVotes = "VOTES", DBforBlocks = "BLOCKS";
	Gson gson = new Gson();
	
	public static Vote v; //Голос, с которым идёт работа
	public static Block b = new Block(null); //Блок, с которым идёт работа
	public static List<Initiative> initiatives=new ArrayList<>(); //Список инициатив
    public static List<Vote> votes = new ArrayList<>(); //Список нераспределённых в блок голосов
    public static List<Block> chain = new ArrayList<>(); //Цепочка блоков
    public static String goal = "00007ffaff3939fbca4eb074249dc7d39b1d1ee4fed2da3f87430703cac5d250a"; //Число для условия blockhash < goal
			
    
    /*
     * Блок отправки данных пользователю 
     * */
    
	//Запрос на получение числа goal
	@RequestMapping(path = "/getgoal")
	public String getGoal(@RequestParam(value="name",
			required=false, defaultValue="World") String name){
		return goal;
	}
	
	//Запрос на получение последнего блока из цепочки
	@RequestMapping(value = "/getBlock", method={RequestMethod.POST,RequestMethod.GET})
	public Block getBlock(){
		collect = db.getCollection(DBforBlocks);
		if (collect.count() == 0) addNewBlock(b);
		DBCursor cursor = collect.find();
		Gson gson = new Gson();
		while(cursor.hasNext()){
			b = gson.fromJson(gson.toJson(cursor.next()), Block.class);
		}
		return b;
	}
    
    //Запрос на получение инициативы, содержащую описание descrption
    @RequestMapping( path = "/getinitiativebydescriprion/{description}", method = RequestMethod.GET)
    public Initiative getInitiativeByDescription(@PathVariable("description") String description){
    	collect = db.getCollection(DBforInitiatives);
    	BasicDBObject query = new BasicDBObject();
    	query.put("description", description);
    	DBCursor cursor = collect.find(query);
    	return gson.fromJson(gson.toJsonTree(cursor.next()),Initiative.class);
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
	 //Запрос на получение списка инициатив
    @RequestMapping(value="/getinitiatives",method={RequestMethod.POST,RequestMethod.GET})
    public List<Initiative> getAllInitives(){
    	initiatives = new ArrayList<>();
    	collect = db.getCollection(DBforInitiatives);
    	System.err.println("Collection size "+collect.count());
    	DBCursor cursor = collect.find();
    	while (cursor.hasNext()){
    		initiatives.add(gson.fromJson(gson.toJson(cursor.next()), Initiative.class));
    	}
    	
    	return initiatives;
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
    		boolean isTaken = true;
    		Vote vote = listOfVotes.get(i);
    		
    		for(int j = 0; j < chain.size(); j++){
    			Block b = chain.get(j);
    			if(!b.votes.contains(vote)) isTaken = false;
    			
    		}
    		if (!isTaken) votesNotInBlock.add(listOfVotes.get(i)); 
    	}
    	
    	return votesNotInBlock;
    }
    
    /**
     * Блок получения и обработки данных
     * 
     * */
    
    
	//Добавление инициативы, пришедшей от пользователя.
	@RequestMapping(value="/addinitiative",method=RequestMethod.POST)
    public void addNewInitive(@RequestBody Initiative initiative){
    	
    	collect = db.getCollection(DBforInitiatives);
    	BasicDBObject object = new BasicDBObject();
    	object.put("description", initiative.description);
    	object.put("variants", initiative.variants);
    	BasicDBObject whereQuery = new BasicDBObject();
    	whereQuery.put("description", initiative.description);
    	DBCursor cursor = collect.find(whereQuery);
    	if (!cursor.hasNext()){
    		collect.insert(object);
    	}
    	   	
    }
	
	//Создание голоса, пришедшего от пользователя. Проверка на достоверность, добавление в список нераспределённых голосов
		@RequestMapping( path ="/addvote", method=RequestMethod.POST)
		public void voteCreator(@RequestBody Vote vote){
			boolean voteIsFirst = true;
			collect = db.getCollection(DBforVotes);
			BasicDBObject query = new BasicDBObject();
			List<BasicDBObject> listForQuery = new ArrayList<>();
			listForQuery.add(new BasicDBObject("dsaSign", vote.dsaSign));
			listForQuery.add(new BasicDBObject("initiative", gson.toJson(vote.initiative)));
			query.put("$and", listForQuery);
			System.err.println("previous votes count is: "+collect.find(query).count());
						voteIsFirst = collect.find(query)==null || collect.find(query).count()==0 ; 
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
					GetDataConroller.votes.add(vote);
					BasicDBObject obj = new BasicDBObject();
					BasicDBObject object = new BasicDBObject();
			    	object.put("description", vote.initiative.description);
			    	object.put("variants", vote.initiative.variants);
					obj.put("dsaSign", vote.dsaSign);
					obj.put("initiative", object);
					obj.put("publicKey",vote.publicKey);
					obj.put("variant",vote.variant);
					collect.insert(obj);
				}  
			}	
		}
		
		@RequestMapping(value="/addblock",method=RequestMethod.POST)
		public void addNewBlock(@RequestBody Block block){
			collect = db.getCollection(DBforBlocks);
			DBCursor cursor = collect.find();
			Block lastBlock = new Block();
			while(cursor.hasNext()){
				lastBlock = gson.fromJson(gson.toJson(cursor.next()), Block.class);
			}
			if(Block.blockValidity(block, lastBlock, GetDataConroller.goal))
					GetDataConroller.b = block;
												
				BasicDBObject object = new BasicDBObject();
				object.put("index", b.getIndex());
				object.put("timestamp",b.getTimestamp());
				object.put("voteHash",b.getVoteHash());
				object.put("votes",b.votes);
				object.put("hash",b.getHash());
				object.put("previousHash",b.getPreviousHash());
				object.put("previousBlock",b.getPreviousBlock());
				object.put("nonce",b.nonce);
				collect.insert(object);
				
		}
	
}
