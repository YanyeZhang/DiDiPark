package com.didipark.utils;

import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

public class ExportDB {
   /**
    * �Զ������ݿ�
    * @param args
    */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
          Configuration configuration=new Configuration().configure();
          SchemaExport schema=new SchemaExport(configuration);
          schema.create(true, true);
          System.out.println("ok");
	}

}
