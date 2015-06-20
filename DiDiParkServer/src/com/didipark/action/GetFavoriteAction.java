package com.didipark.action;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.didipark.dao.CarportDao;
import com.didipark.dao.FavoriteDao;
import com.didipark.dao.PhotoDao;
import com.didipark.pojo.Carport;
import com.didipark.pojo.Favorite;
import com.didipark.pojo.Photo;
import com.opensymphony.xwork2.ActionSupport;

public class GetFavoriteAction extends ActionSupport implements
		ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private CarportDao carportDao;
	private PhotoDao photoDao;
	private FavoriteDao favoriteDao;
	private int userId;

	public void setUserId(int userId) {
		this.userId = userId;
	}

	public void setFavoriteDao(FavoriteDao favoriteDao) {
		this.favoriteDao = favoriteDao;
	}

	public void setPhotoDao(PhotoDao photoDao) {
		this.photoDao = photoDao;
	}

	public void setCarportDao(CarportDao carportDao) {
		this.carportDao = carportDao;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void getFavorite() {
		JSONObject json = new JSONObject();
		List<Carport> carports = new ArrayList<Carport>();
		List<Photo> photos = new ArrayList<Photo>();
		List<Favorite> favorites = favoriteDao.findFavoriteByUserId(userId);
		for (Favorite temp : favorites) {
			if (!carportDao.findCarportByID(temp.getCarportId()).getState()
					.equals("下架")) {
				carports.add(carportDao.findCarportByID(temp.getCarportId()));
				photos.add(photoDao.findByCarportId(temp.getCarportId()));
			}
		}
		json.put("carports", carports);
		json.put("photos", photos);
		try {
			System.out.println(json.toString());
			byte[] jsonBytes = json.toString().getBytes("utf-8");
			response.setContentLength(jsonBytes.length);
			response.getOutputStream().write(jsonBytes);
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
}