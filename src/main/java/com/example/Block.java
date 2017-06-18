package com.example;

import java.security.MessageDigest;
import java.util.ArrayList;

public class Block  {

	 private String hash;
	 private int index = 0; //Index of operation
	 public long nonce = 0; // добавка для генерации
	 private String previousHash; 
	 private long timestamp; //Date and time of operation
	 private String voteHash = ""; // Голоса в блоке
	 public ArrayList<Vote> votes;
	 public String goal;
	 
    public Block() {
		
	}
    
    // Конструктор для первого блока (genesis block)
    public Block(ArrayList<Vote> votes,String goal){
        this.votes = votes;
        this.previousHash = "0";
        index = 0;
        this.hash = hashcode();
        this.goal = goal;
        timestamp = System.currentTimeMillis();
    }

    // Конструктор для остальных блоков
    public Block(ArrayList<Vote> votes, Block previousBlock, String goal){
        this.votes = votes;
        this.previousHash = previousBlock.hash;
        this.index = previousBlock.index + 1;
        this.hash = hashcode();
        this.goal = goal;
        timestamp = System.currentTimeMillis();
        for (int i = 0; i < votes.size(); i++){
            voteHash += votes.get(i).hashcode();
        }
    }

    // hash function
    public String hashcode(){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (Exception e) {
        	e.printStackTrace();
        };
        String futureHash = voteHash+index+timestamp+nonce+previousHash+goal;
        md.update(futureHash.getBytes());
        byte byteData[] = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static boolean blockValidity(Block a, Block lastBlock, String goal){
        if (a.hash.compareTo(goal) < 0 && a.index == lastBlock.index + 1 && a.previousHash.equals(lastBlock.hash)) return true;
        else return false;
    }

	public int getIndex() {
		return index;
	}

	public long getTimestamp() {       
		return timestamp;			
	}									

	public String getVoteHash() {
		return voteHash;
	}

	public String getHash() {
		return hash;
	}

	public String getPreviousHash() {
		return previousHash;
	}

}