package com.didipark.pojo;

import java.io.Serializable;

public class Cid implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String clientid;

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Cid(String clientid,int id) {
		this.clientid = clientid;
		this.id=id;
	}

	public String getClientid() {
		return clientid;
	}

	public void setClientid(String clientid) {
		this.clientid = clientid;
	}

	public Cid() {
		super();
	}

}
