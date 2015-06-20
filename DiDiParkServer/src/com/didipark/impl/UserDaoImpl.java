package com.didipark.impl;

import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;
import com.didipark.dao.UserDao;
import com.didipark.pojo.User;

public class UserDaoImpl implements UserDao {

	private HibernateTemplate ht = null;
	private SessionFactory sessionFactory;

	public void setSessionFactory(SessionFactory sessionFactory) {
		this.sessionFactory = sessionFactory;
	}

	private HibernateTemplate getHibernateTemplate() {
		if (ht == null) {
			ht = new HibernateTemplate(sessionFactory);
		}
		return ht;
	}

	/*
	 * 用户注册
	 * 
	 * @param 昵称，密码，电话号码，头像
	 */
	public boolean register(String nickName, String password, String phone,
			String imageUrl) {
		boolean flag = false;
		User user = new User(nickName, password, imageUrl, phone, null);
		getHibernateTemplate().save(user);
		flag = !getHibernateTemplate().contains(user);
		return flag;
	}

	/*
	 * 检查电话号码
	 * 
	 * @param 电话号码
	 */
	@SuppressWarnings("unchecked")
	public boolean checkPhone(String phone) {
		List<User> user = null;
		user = getHibernateTemplate()
				.find("from User u where u.phone=?", phone);
		if (user.size() == 0) {
			return true;
		} else {
			return false;
		}
	}

	@SuppressWarnings("unchecked")
	public User findUserByPhone(String phone) {
		List<User> user = null;
		user = getHibernateTemplate()
				.find("from User u where u.phone=?", phone);
		return user.get(0);
	}

	@SuppressWarnings("unchecked")
	public List<User> login(String phone, String password) {
		List<User> user = null;
		user = getHibernateTemplate()
				.find("from User u where u.password=? and u.phone=?", password,
						phone);
		return user;
	}

	public void updateFigure(String url, int id) {
		User user = getHibernateTemplate().get(User.class, id);
		user.setImageUrl(url);
		getHibernateTemplate().update(user);
	}

	public User findUserById(int id) {
		List<User> user = getHibernateTemplate().find(
				"from User u where u.id=?", id);
		return user.get(0);
	}

	public List<User> loginByQQ(String password) {
		List<User> users = getHibernateTemplate().find(
				"from User u where u.password=?", password);
		return users;
	}

	public void updateUser(User user) {
		getHibernateTemplate().update(user);
	}

}
