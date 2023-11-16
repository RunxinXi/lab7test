package edu.toronto.dbservice.types;

import java.io.Serializable;
import org.web3j.crypto.Credentials;
public final class EtherAccount implements Serializable {
	private static final long serialVersionUID = 7526472295622776148L;
	
	private String privateKey;
	
	public EtherAccount(String aPrivateKey) {
		privateKey = aPrivateKey;
	}
	
	public Credentials getCredentials(){
		return Credentials.create(privateKey);
	}
}
