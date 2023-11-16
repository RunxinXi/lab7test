package edu.toronto.dbservice.types;

import java.io.Serializable;

public class ClientRequest implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer clientNum;
	private String item;
	
	public ClientRequest(Integer pClientNum, String pItem) {
		clientNum = pClientNum;
		item = pItem;
	}
	
	public Integer getClientNum() {
		return clientNum;
	}
	
	public String getItem() {
		return item;
	}

}