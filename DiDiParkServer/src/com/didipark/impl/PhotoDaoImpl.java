package com.didipark.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.orm.hibernate3.HibernateTemplate;

import sun.security.action.GetBooleanAction;

import com.didipark.dao.PhotoDao;
import com.didipark.pojo.Carport;
import com.didipark.pojo.Cid;
import com.didipark.pojo.Photo;
import com.didipark.pojo.User;

public class PhotoDaoImpl implements PhotoDao {
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

	public Photo savePhoto(String url, Carport carport, String url2) {
		Photo photo = new Photo(carport.getId(), url, url2);
		getHibernateTemplate().save(photo);
		getHibernateTemplate().flush();
		return photo;
	}



	public List<Photo> findByCarport(List<Carport> carports) {
		List<Photo> photos = new ArrayList<Photo>();
		for (Carport temp : carports) {
			List<Photo> photo =getHibernateTemplate().find(
					"from Photo p where p.carport_id=?", temp.getId());
			if (photo != null)
				photos.add(photo.get(0));
		}
		return photos;
	}

	public void deletPhotoByCarport_id(int id) {
		List<Photo> photo = getHibernateTemplate().find(
				"from Photo p where p.carport_id=?", id);
		getHibernateTemplate().deleteAll(photo);
		
	}

	public Photo findByCarportId(int id) {
		List<Photo> photo = getHibernateTemplate().find(
				"from Photo p where p.carport_id=?", id);
		return photo.get(0);
	}

}
