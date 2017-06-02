package com.example;

import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.Signature;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

public class DigitalSign {


	
	public static PublicKey convertKey(byte[] data) throws InvalidKeySpecException, NoSuchAlgorithmException{
		return KeyFactory.getInstance("DSA").generatePublic(new X509EncodedKeySpec(data));
	}
  
  public static byte[] signData(byte[] data, PrivateKey key) throws Exception {
    Signature signer = Signature.getInstance("SHA1withDSA");
    signer.initSign(key);
    signer.update(data);
    return (signer.sign());
  }
  

  public static boolean verifySig(byte[] data, PublicKey key, byte[] sig) throws Exception {
    Signature signer = Signature.getInstance("SHA1withDSA");
    signer.initVerify(key);
    signer.update(data);
    return (signer.verify(sig));

  }

  public static KeyPair generateKeyPair(long seed) throws Exception {
    KeyPairGenerator keyGenerator = KeyPairGenerator.getInstance("DSA");
    SecureRandom rng = SecureRandom.getInstance("SHA1PRNG", "SUN");
    rng.setSeed(seed);
    keyGenerator.initialize(1024, rng);
    return (keyGenerator.generateKeyPair());
  }


}
