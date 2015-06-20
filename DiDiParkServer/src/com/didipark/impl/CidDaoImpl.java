package com.didipark.impl;

import java.util.List;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import com.didipark.dao.CarportDao;
import com.didipark.dao.CidDao;
import com.didipark.pojo.Carport;
import com.didipark.pojo.Cid;
import com.didipark.pojo.User;
import com.didipark.utils.MyConstant;

public class CidDaoImpl implements CidDao {
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

	public void saveCid(String clientid, int id) {
		// getHibernateTemplate().delete("delete Cid c where c.user=?",user);
		Cid cid = new Cid(clientid, id);
		List<Cid> cids = getHibernateTemplate().find("from Cid c where c.id=?",
				id);
		List<Cid> cids2=getHibernateTemplate().find("from Cid c where c.clientid=?",
				clientid);
		if (cids.size() < 1&&cids2.size()<1) {
			getHibernateTemplate().save(cid);
		}else
		{
			getHibernateTemplate().deleteAll(cids);
			getHibernateTemplate().deleteAll(cids2);
			getHibernateTemplate().save(cid);
		}
	}

	public String findCidById(int id) {
		List<Cid> cids = getHibernateTemplate().find("from Cid c where c.id=?",
				id);
		return cids.get(0).getClientid();
	}

}
