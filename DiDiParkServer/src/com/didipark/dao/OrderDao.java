package com.didipark.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.didipark.pojo.Carport;
import com.didipark.pojo.Order;
import com.didipark.pojo.User;

public interface OrderDao {
	public void setSessionFactory(SessionFactory sessionFactory);


	public void saveOrder(Order order);
	public void updateById(String orderId,int commentId);
    public List<Order> findByCarportId(int carportId);
    public List<Order> findByUserId(int userId);
}
