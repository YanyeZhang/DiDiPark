package com.didipark.action;

import java.util.List;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import org.apache.struts2.interceptor.ServletResponseAware;

import com.didipark.dao.CarportDao;
import com.didipark.dao.CidDao;
import com.didipark.dao.PhotoDao;
import com.didipark.dao.UserDao;
import com.didipark.pojo.Carport;
import com.didipark.pojo.Photo;
import com.didipark.pojo.User;
import com.opensymphony.xwork2.ActionSupport;

public class LoginAction extends ActionSupport implements ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private String password;
	// 其实是电话号码。。懒得改
	private String nickname;
	private UserDao userDao;
	private CidDao cidDao;
	private CarportDao carportDao;
	private PhotoDao photoDao;
	private String cid;
    private JSONObject json = new JSONObject();
	
    public void setCarportDao(CarportDao carportDao) {
		this.carportDao = carportDao;
	}

	public void setPhotoDao(PhotoDao photoDao) {
		this.photoDao = photoDao;
	}

	public void setCid(String cid) {
		this.cid = cid;
	}

	public void setCidDao(CidDao cidDao) {
		this.cidDao = cidDao;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public void setNickname(String nickname) {
		this.nickname = nickname;
	}

	public void login() {
		
		if (cid != null) {
			if (nickname != null) {
				List<User> user = userDao.login(nickname, password);
				if (user.size() >= 1) {
					json.put("message", "success");
					cidDao.saveCid(cid, user.get(0).getId());
					json.put("user", user.get(0));
					List<Carport> carports=carportDao.findByUserIdAvilable(user.get(0).getId());
					List<Photo> photos=photoDao.findByCarport(carports);
					json.put("carports", carports);
					json.put("photos", photos);
				} else {
					json.put("message", "failed");
				}
			} else {
				List<User> user = userDao.loginByQQ(password);
				if (user.size() >= 1) {
					System.out.println();
					json.put("message", "success");
					cidDao.saveCid(cid, user.get(0).getId());
					json.put("user", user.get(0));
					List<Carport> carports=carportDao.findByUserIdAvilable(user.get(0).getId());
					List<Photo> photos=photoDao.findByCarport(carports);
					json.put("carports", carports);
					json.put("photos", photos);
				} else {
					json.put("message", "failed");
				}
			}
		} else {
			json.put("message", "failed");
		}
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
	private void getCarport(int userId){
		
	}

}