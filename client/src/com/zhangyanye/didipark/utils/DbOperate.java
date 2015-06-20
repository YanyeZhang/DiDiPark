package com.zhangyanye.didipark.utils;

import com.lidroid.xutils.DbUtils;
import com.lidroid.xutils.DbUtils.DaoConfig;
import com.zhangyanye.didipark.application.MyApplication;

public class DbOperate {
	private static DbUtils db=null;

	public static DbUtils getInstance() {
		if (db == null) {
			DaoConfig config = new DaoConfig(MyApplication.getContext());
			config.setDbName("didiDB");
			config.setDbVersion(1);
			db = DbUtils.create(config);
		}
		return db;
	}

}
