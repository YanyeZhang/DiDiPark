package com.didipark.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.didipark.dao.CarportDao;
import com.didipark.pojo.Carport;
import com.didipark.pojo.User;
import com.didipark.utils.DistanceUtil;

public class CarportDaoImpl implements CarportDao {
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

	public Carport saveCarport(String describe, String addr, int perHoursMoney,
			int num, double latitude, double longitude, int id) {
		User user = getHibernateTemplate().get(User.class, id);
		Carport carport = new Carport(addr, describe, num, latitude, longitude,
				perHoursMoney, user.getId(), "运营");
		getHibernateTemplate().save(carport);
		getHibernateTemplate().flush();
		return carport;
	}

	public Carport findCarportByID(int id) {
		Carport carport = getHibernateTemplate().get(Carport.class, id);
		return carport;
	}

	public void updateCarport(Carport carport) {
		getHibernateTemplate().update(carport);

	}

	public void removeCarportByID(int id) {
		Carport carport = getHibernateTemplate().get(Carport.class, id);
		getHibernateTemplate().delete(carport);
	}

	public List<Carport> findByPoints(double latitude, double longitude) {
		@SuppressWarnings("unchecked")
		List<Carport> carports = getHibernateTemplate().find("from Carport c where c.state=? ","运营");
		List<Carport> result = new ArrayList<Carport>();
		for (Carport temp : carports) {
			if (DistanceUtil.GetDistance(longitude, latitude,
					temp.getLongitude(), temp.getLatitude()) <= 5000
					&& temp.getNum() > 0) {
				result.add(temp);
			}
		}
		return result;
	}


	public List<Carport> findByUserIdAll(int id) {
		List<Carport> carports = getHibernateTemplate().find(
				"from Carport c where c.user_id=?",id);
		return carports;
	}

	public List<Carport> findByUserIdAvilable(int id) {
		List<Carport> carports = getHibernateTemplate().find(
				"from Carport c where c.user_id=? and c.state <> ?",id,"下架");
		return carports;
	}

}
