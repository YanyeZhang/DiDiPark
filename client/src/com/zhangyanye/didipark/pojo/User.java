package com.zhangyanye.didipark.pojo;

import com.lidroid.xutils.db.annotation.Id;



public class User {
	
	@Id
	private int db_id;
	private int id;
	private String password;
	private String nickName;
	private String imageUrl;
	private String phone;

	public User() {

	}
	
	public User(int id,String nickName,String password, String imageUrl, String phone,
			String carCard) {
		super();
		this.id=id;
		this.password = password;
		this.nickName = nickName;
		this.imageUrl = imageUrl;
		this.phone = phone;
	}


	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String image) {
		this.imageUrl = image;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}


}
