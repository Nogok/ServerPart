package com.example;

import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Block  {

    // Variables
    private int index = 0; //Index of operation
    private final Date timestamp; //Date and time of operation
    private String voteHash;
    public ArrayList<Vote> votes = new ArrayList<>();
    private String hash, previousHash;
    private Block previousBlock = null;
    public long nonce = 0;

    public Block() {
		timestamp = new Date();
	}
    
    // Constructor for the FIRST Block
    public Block(ArrayList<Vote> votes){
        this.votes = votes;
        this.previousHash = "0";
        index = 0;
        this.hash = hashcode();
        timestamp = new Date();
    }

    // Конструктор для остальных блоков
    public Block(ArrayList<Vote> votes, Block previousBlock){
        this.votes = votes;
        this.previousHash = previousBlock.hash;
        this.index = previousBlock.index + 1;
        this.hash = hashcode();
        timestamp = new Date();
        this.previousBlock = previousBlock;
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

        };
        String futureHash = voteHash+index+timestamp+nonce;
        md.update(futureHash.getBytes());
        byte byteData[] = md.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < byteData.length; i++) {
            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
        }

        return sb.toString();
    }

    public static boolean blockValidity(Block a, List<Block> chain, String goal){
        Block b = chain.get(chain.size()-1);
        if (a.hash.compareTo(goal) < 0) return true;
        else return false;
    }

    public int getIndex() {
        return index;
    }

    public String getHash() {
        return hash;
    }

    public String getPreviousHash() {
        return previousHash;
    }
    public Block getPreviousBlock() {
        return previousBlock;
    }
    
    
}