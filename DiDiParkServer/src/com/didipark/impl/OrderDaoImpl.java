package com.didipark.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.didipark.dao.CarportDao;
import com.didipark.dao.CidDao;
import com.didipark.dao.OrderDao;
import com.didipark.pojo.Carport;
import com.didipark.pojo.Cid;
import com.didipark.pojo.Order;
import com.didipark.pojo.User;
import com.didipark.utils.MyConstant;

public class OrderDaoImpl implements OrderDao {
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

	public void saveOrder(Order order) {
		getHibernateTemplate().save(order);
	}

	public List<Order> findByCarportId(int carportId) {
		List<Order> orders=getHibernateTemplate().find(
				"from Order o where o.carport_id=? order by o.time desc",carportId);
		return orders;
	}

	public List<Order> findByUserId(int userId) {
		List<Order> orders=getHibernateTemplate().find(
				"from Order o where o.user_id=?  order by o.time desc",userId);
		return orders;
	}

	public void updateById(String orderId,int commentId) {
		List<Order> orders=getHibernateTemplate().find(
				"from Order o where o.id=?",orderId);
		orders.get(0).setCommentId(commentId);
		getHibernateTemplate().update(orders.get(0));
	}

}
