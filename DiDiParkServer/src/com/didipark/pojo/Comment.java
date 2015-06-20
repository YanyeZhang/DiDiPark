package com.didipark.pojo;

import java.io.Serializable;

public class Comment implements Serializable{

	private static final long serialVersionUID = 1L;
    private int id;
    private String content;
    private int user_id;
    private int carport_id;
    private int level;
    private String time;
    
    public Comment(){}
    
	public Comment(String content, int user_id, int carport_id, int level,String time) {
		super();
		this.content = content;
		this.user_id = user_id;
		this.carport_id = carport_id;
		this.level = level;
		this.time=time;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getContent() {
		return content;
	}
	public void setContent(String content) {
		this.content = content;
	}
	public int getUser_id() {
		return user_id;
	}
	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}
	public int getCarport_id() {
		return carport_id;
	}
	public void setCarport_id(int carport_id) {
		this.carport_id = carport_id;
	}
	public int getLevel() {
		return level;
	}
	public void setLevel(int level) {
		this.level = level;
	}
}
