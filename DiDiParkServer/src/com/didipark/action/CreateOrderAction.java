package com.didipark.action;



import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.interceptor.ServletResponseAware;
import com.alibaba.fastjson.JSONObject;
import com.didipark.dao.CarportDao;
import com.didipark.dao.CidDao;
import com.didipark.dao.OrderDao;
import com.didipark.dao.UserDao;
import com.didipark.pojo.Carport;
import com.didipark.pojo.Order;
import com.didipark.pojo.User;
import com.didipark.push.PushResult;
import com.didipark.utils.MyConstant;
import com.opensymphony.xwork2.ActionSupport;

public class CreateOrderAction extends ActionSupport implements
		ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private String orderId;
	private int carport_id, user_id;
	private OrderDao orderDao;
	private String time;
	private CarportDao carportDao;
	private CidDao cidDao;
	private String type;
	private double money;
	private UserDao userDao;

	
	public void setType(String type) {
		this.type = type;
	}

	public void setMoney(double money) {
		this.money = money;
	}

	public void setUserDao(UserDao userDao) {
		this.userDao = userDao;
	}

	public void setCidDao(CidDao cidDao) {
		this.cidDao = cidDao;
	}

	public void setCarportDao(CarportDao carportDao) {
		this.carportDao = carportDao;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void setOrderId(String orderId) {
		this.orderId = orderId;
	}

	public void setCarport_id(int carport_id) {
		this.carport_id = carport_id;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public void setOrderDao(OrderDao orderDao) {
		this.orderDao = orderDao;
	}

	public void createOrder() {
		JSONObject json = new JSONObject();
		if (orderId != null && user_id != 0 && carport_id != 0 && time != null) {
			Order order = new Order(orderId,carport_id,user_id,time,type,money);
			orderDao.saveOrder(order);
			Carport carport = carportDao.findCarportByID(carport_id);
			User user = userDao.findUserById(carport.getUser_id());
			cidDao.findCidById(user.getId());
			String phone = user.getPhone().substring(
					user.getPhone().toString().length() - 4,
					user.getPhone().toString().length());
			try {
				PushResult.pushResultToCarportOwner(
						cidDao.findCidById(carport.getUser_id()), phone,carport.getId()+"");
	            carport.setNum(carport.getNum()-1);
	            carportDao.updateCarport(carport);
			} catch (Exception e) {
				e.printStackTrace();
			}

			json.put("message", "success");
		} else {
			json.put("message", "failed");
		}
		try {
			byte[] jsonBytes = json.toString().getBytes("utf-8");
			System.out.println(json.toString());
			response.setContentLength(jsonBytes.length);
			response.getOutputStream().write(jsonBytes);
			response.getOutputStream().flush();
			response.getOutputStream().close();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}