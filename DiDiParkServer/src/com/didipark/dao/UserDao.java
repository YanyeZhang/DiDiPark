package com.didipark.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import com.didipark.pojo.User;

public abstract interface UserDao {
	public void setSessionFactory(SessionFactory sessionFactory);

	public List<User> login(String phone, String password);

	public boolean register(String nickName, String password, String phone,
			String imageUrl);

	public boolean checkPhone(String phone);

	public void updateFigure(String url, int id);

	public User findUserById(int id);

	public void updateUser(User user);

	public List<User> loginByQQ(String password);
}
