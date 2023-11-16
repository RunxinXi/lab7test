package edu.toronto.dbservice.config;

public class DBConstants {
	
	public static final String DB_DRIVER = "org.h2.Driver";

//	public static final String DB_NAME = "jdbc:h2:file:mie354db;FILE_LOCK=SOCKET";
//	public static final String DB_NAME = "jdbc:h2:tcp://localhost:9092/mie354db;FILE_LOCK=SOCKET";
//	public static final String DB_NAME = "jdbc:h2:mem:mie354db;DB_CLOSE_DELAY=-1";
//	public static final String DB_NAME = "jdbc:h2:mie354db;FILE_LOCK=SOCKET";
//	public static final String DB_NAME = "jdbc:h2:tcp://localhost:9092/mie354db";
//	public static final String DB_NAME = "jdbc:h2:mie354db"; // IFEXISTS=TRUE
//	public static final String DB_NAME = "jdbc:h2:mie354";
	public static final String DB_NAME = "jdbc:h2:mem:flowable;IFEXISTS=TRUE"; // works
//	public static final String DB_NAME = "jdbc:h2:mem:activiti"; // testing 
//	public static final String DB_NAME = "jdbc:h2:activiti;IFEXISTS=TRUE";
//	public static final String DB_NAME = "jdbc:h2:tcp://localhost/mie354;IFEXISTS=TRUE";
	
	public static final String DB_LOGIN = "sa";
	public static final String DB_PASSWD = "";

}
