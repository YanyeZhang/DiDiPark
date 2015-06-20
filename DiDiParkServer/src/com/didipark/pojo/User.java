package com.didipark.pojo;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

public class User implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private String password;
	private String nickName;
	private String imageUrl;
	private String phone;
	private String carCard;


	public User() {

	}

	public User(String nickName, String password, String imageUrl,
			String phone, String carCard) {
		super();
		this.password = password;
		this.nickName = nickName;
		this.imageUrl = imageUrl;
		this.phone = phone;
	}

	public User(int id, String nickName, String password, String imageUrl,
			String phone, String carCard) {
		super();
		this.id = id;
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

	public String getCarCard() {
		return carCard;
	}

	public void setCarCard(String carCard) {
		this.carCard = carCard;
	}

}
