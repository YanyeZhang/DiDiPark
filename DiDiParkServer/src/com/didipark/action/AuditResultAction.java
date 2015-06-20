package com.didipark.action;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import org.apache.struts2.interceptor.ServletResponseAware;
import com.alibaba.fastjson.JSONObject;
import com.didipark.dao.CarportDao;
import com.didipark.dao.CidDao;
import com.didipark.dao.OrderDao;
import com.didipark.dao.PhotoDao;
import com.didipark.pojo.Carport;
import com.didipark.pojo.Order;
import com.didipark.push.PushResult;
import com.opensymphony.xwork2.ActionSupport;

public class AuditResultAction extends ActionSupport implements
		ServletResponseAware {
	private static final long serialVersionUID = 1L;
	private HttpServletResponse response;
	private String result;
	private int carport_id;
	private int user_id;
	private int customer_id;
	private CidDao cidDao;

	public void setCidDao(CidDao cidDao) {
		this.cidDao = cidDao;
	}

	public void setUser_id(int user_id) {
		this.user_id = user_id;
	}

	public void setResult(String result) {
		this.result = result;
	}

	public void setCarport_id(int carport_id) {
		this.carport_id = carport_id;
	}

	public void setCustomer_id(int customer_id) {
		this.customer_id = customer_id;
	}

	public void setServletResponse(HttpServletResponse response) {
		this.response = response;
	}

	public void auditResult() {
		JSONObject json = new JSONObject();
		System.out.println("zyy");
		if (result.equals("agree")) {
			json.put("message", "success");
			try {
				PushResult.pushResultToCustom(cidDao.findCidById(customer_id),carport_id,
						"审核通过，请预支付一小时停车费用");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else if (result.equals("disagree")) {
			try {
				PushResult.pushResultToCustom(cidDao.findCidById(customer_id),carport_id,
						"申请被拒绝，请换一个车位试试吧");
			} catch (Exception e) {
				e.printStackTrace();
			}
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
}