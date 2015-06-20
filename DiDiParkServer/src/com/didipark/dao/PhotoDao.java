package com.didipark.dao;

import java.util.List;

import org.hibernate.SessionFactory;

import com.didipark.pojo.Carport;
import com.didipark.pojo.Photo;

public interface PhotoDao {
	public void setSessionFactory(SessionFactory sessionFactory);
	public Photo savePhoto(String url,Carport id,String url2);
	public void deletPhotoByCarport_id(int id);
	public List<Photo> findByCarport(List<Carport> carports);
	public Photo findByCarportId(int id);
}
