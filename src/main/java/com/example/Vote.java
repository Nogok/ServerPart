package com.example;

import java.security.MessageDigest;

public class Vote {
	public String dsaSign;
	public Initiative initiative;
	public String publicKey;
	public int variant;
	
	
	
	public Vote(){
		
	}
	public Vote(Initiative initiative,int variant,String publicKey){
		this.initiative=initiative;
		this.variant=variant;
		this.publicKey=publicKey;
	}
	  public String hashcode(){

	        MessageDigest md = null;
	        try {
	            md = MessageDigest.getInstance("SHA-256");
	        } catch (Exception e) {

	        };
	        String futureHash = dsaSign+initiative.toString()+publicKey+variant;
	        md.update(futureHash.getBytes());
	        byte byteData[] = md.digest();
	        StringBuffer sb = new StringBuffer();
	        for (int i = 0; i < byteData.length; i++) {
	            sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
	        }

	        return sb.toString();
	    }
	  
	  @Override
	public boolean equals(Object obj) {
		  if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (this.getClass() != obj.getClass())
				return false;
		Vote vote = (Vote)obj;
		if (this.dsaSign.equals(vote.dsaSign) && this.initiative.equals(vote.initiative) && this.variant == vote.variant) return true;
		return false;
	}
}
