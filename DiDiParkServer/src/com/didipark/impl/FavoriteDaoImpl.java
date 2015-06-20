package com.didipark.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.didipark.dao.CarportDao;
import com.didipark.dao.CidDao;
import com.didipark.dao.FavoriteDao;
import com.didipark.pojo.Carport;
import com.didipark.pojo.Cid;
import com.didipark.pojo.Favorite;
import com.didipark.pojo.User;
import com.didipark.utils.MyConstant;

public class FavoriteDaoImpl implements FavoriteDao {
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

	public void saveFavorite(int user_id, int carport_id) {
		Favorite favorite = new Favorite(user_id, carport_id);
		getHibernateTemplate().save(favorite);
	}

	public List<Favorite> findFavoriteByUserId(int user_id) {
		List<Favorite> favorite = getHibernateTemplate().find(
				"from Favorite f where f.userId=?", user_id);
		return favorite;
	}

	public boolean hasSaved(int user_id, int carport_id) {
		getHibernateTemplate().flush();
		List<Favorite> favorite = getHibernateTemplate().find(
				"from Favorite f where f.userId=?  and  f.carportId=?",
				user_id, carport_id);
		if (favorite.size() == 0)
			return true;
		else
			return false;
	}

	public void deletFavorite(int user_id, int carport_id) {
		List<Favorite> favorite = getHibernateTemplate().find(
				"from Favorite f where f.userId=?  and  f.carportId=?",
				user_id, carport_id);
		getHibernateTemplate().deleteAll(favorite);	
	}

}
