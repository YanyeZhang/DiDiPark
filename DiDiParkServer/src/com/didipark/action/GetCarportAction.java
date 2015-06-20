package com.didipark.action;

import java.util.List;

import javax.servlet.http.HttpServletResponse;
import net.sf.json.JSONObject;
import org.apache.struts2.interceptor.ServletResponseAware;

import com.didipark.dao.CarportDao;
import com.didipark.dao.PhotoDao;
import com.didipark.pojo.Carport;
import com.didipark.pojo.Photo;
import com.opensymphony.xwork2.ActionSupport;

public class GetCarportAction extends ActionSupport implements
		ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private double longitude, latitude;
	private CarportDao carportDao;
	private PhotoDao photoDao;

	public void setPhotoDao(PhotoDao photoDao) {
		this.photoDao = photoDao;
	}
	public void setCarportDao(CarportDao carportDao) {
		this.carportDao = carportDao;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void getCarport() {
		JSONObject json = new JSONObject();
		List<Carport> carports = carportDao.findByPoints(latitude, longitude);
		List<Photo> photos=photoDao.findByCarport(carports);
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