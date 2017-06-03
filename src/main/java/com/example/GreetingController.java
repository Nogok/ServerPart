package com.example;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.google.gson.Gson;

@RestController
public class GreetingController {	
	
//	/**
//	 * Контроллер запросов
//	 * */
//	
//	public Vote v; //Голос, с которым идёт работа
//	public Block b = new Block(null); //Блок, с которым идёт работа
//	public List<Initiative> initiatives=new ArrayList<>(); //Список инициатив
//    public List<Vote> votes = new ArrayList<>(); //Список нераспределённых в блок голосов
//    public List<Block> chain = new ArrayList<>(); //Цепочка блоков
//    public String goal = "00007ffaff3939fbca4eb074249dc7d39b1d1ee4fed2da3f87430703cac5d250a"; //Число для условия blockhash < goal
//
//    //POST запрос -- создание инициативы, внесение в список инициатив (Инициатива получается от пользователя)
//    
//    
//    @RequestMapping(value="/getallvotes",method={RequestMethod.POST,RequestMethod.GET})
//    public List<Vote> getAllVotesAll(){
//    	return votes;	
//    }
//    
//    //Запрос на получение списка инициатив
//    @RequestMapping(value="/getinitiatives",method={RequestMethod.POST,RequestMethod.GET})
//    public List<Initiative> getAllInitives(){
//    	return initiatives;
//    }
//	//Запрос на получение списка голосов
//    @RequestMapping(value="/getvotes",method={RequestMethod.POST,RequestMethod.GET})
//    public List<Vote> getAllVotes(){
//    	
//    	List<Vote> votesNotInBlock = new ArrayList<>();
//    	for(int i = 0; i < votes.size(); i++){
//    		boolean isTaken = true;
//    		Vote vote = votes.get(i);
//    		for(int j = 0; j < chain.size(); j++){
//    			Block b = chain.get(j);
//    			if(!b.votes.contains(vote)) isTaken = false;
//    			
//    		}
//    		if (!isTaken) votesNotInBlock.add(votes.get(i)); 
//    	}
//    	
//    	return votesNotInBlock;
//    }
//    
//    
//    @RequestMapping( path = "/getlistofvotes", method={RequestMethod.POST,RequestMethod.GET})
//    public List<Vote> getVotesForInitiative(@RequestBody Initiative initiative){
//    	ArrayList<Vote> votesForInitiative = new ArrayList<>();
//    	
//    	for (int i = 0; i < votes.size(); i++){
//    		Vote v = votes.get(i);
//    		if (v.initiative.equals(initiative)){
//    			votesForInitiative.add(v);
//    			System.err.println("SOMETHING ADDED");
//    	
//    		}
//    	}
//    	return votesForInitiative;
//    }
//    
//    @RequestMapping( path = "/getinitiativebydescriprion/{description}", method = RequestMethod.GET)
//    public Initiative getInitiativeByDescription(@PathVariable("description") String description){
//    	
//    	for (int i = 0; i < initiatives.size(); i++){
//    		if (initiatives.get(i).description.equals(description)) return initiatives.get(i);
//    	}
//    	return null;
//    }
//    
//    
//    
//	
//	//Запрос на получение числа goal
//	@RequestMapping(path = "/getgoal")
//	public String getGoal(@RequestParam(value="name",
//			required=false, defaultValue="World") String name){
//		return goal;
//	}
//	
//	//Запрос на получение последнего блока из цепочки
//	@RequestMapping("/getBlock")
//	public Block getBlock(@RequestParam(value="name",
//			required=false, defaultValue="World") String name){
//		return b;
//	}
//	
	
}
