//package edu.toronto.dbservice.config;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileReader;
//import java.sql.Connection;
//import java.sql.DriverManager;
//import java.sql.ResultSet;
//import java.sql.ResultSetMetaData;
//import java.sql.SQLException;
//
//import edu.toronto.dbservice.exceptions.SQLExceptionHandler;
//
//public class MIE354DBHelper {
//
//	public static void main(String[] args) throws Exception {
//		// connect to the DB
//
//		Connection dbCon = getDBConnection();
//
//		// retrieve schema of tables
//
//		File ddldir = new File("ddl");
//		File[] list = ddldir.listFiles();
//
//		for (File datafile : list) {
//			System.out.println("executing ddl file:" + datafile.getName());
//			BufferedReader bis;
//			bis = new BufferedReader(new FileReader(datafile));
//			StringBuffer sb = new StringBuffer();
//			String line = null;
//			while ((line = bis.readLine()) != null) {
//				sb.append(line);
//			}
//
//			bis.close();
//
//			dbCon.createStatement().execute(sb.toString());
//		}
//
//		ResultSet resultSet = dbCon.createStatement().executeQuery("SHOW TABLES");
//		// ResultSetMetaData rsMetaData = resultSet.getMetaData();
//		// int numberOfColumns = rsMetaData.getColumnCount();
//
//		System.out.println("Tables:");
//		while (resultSet.next()) {
//			String tableName = resultSet.getString(1);
//			if (tableName.startsWith("ACT_")) {
//				continue;
//			}
//			System.out.println(resultSet.getString(1));
//
//			// select all
//
//			ResultSet resultSet2 = dbCon.createStatement().executeQuery("Select * from " + tableName);
//			ResultSetMetaData rsMetaData2 = resultSet2.getMetaData();
//			int numberOfColumns2 = rsMetaData2.getColumnCount();
//
//			while (resultSet2.next()) {
//				for (int i = 1; i < numberOfColumns2; i++) {
//					System.out.print(resultSet2.getString(i) + "\t");
//				}
//				System.out.println();
//			}
//
//		}
//
//		dbCon.close();
//
//		System.out.println();
//
//	}
//
//	public static Connection getDBConnection() {
//		Connection dbCon = null;
//		// if (dbCon == null) {
//
//		// connect to the database
//		try {
//			System.out.println("loading driver..");
//			Class.forName(DBConstants.DB_DRIVER);
//			System.out.println("driver loading complete.");
//		} catch (ClassNotFoundException cfe) {
//			System.out.println("The JDBC drive loading failed.");
//			cfe.printStackTrace();
//		}
//		try {
//			System.out.println("connecting to db...");
//			dbCon = DriverManager.getConnection(DBConstants.DB_NAME, DBConstants.DB_LOGIN, DBConstants.DB_PASSWD);
//		} catch (SQLException se) {
//			SQLExceptionHandler.handleException(se);
//			System.exit(1);
//		}
//		// }
//		return dbCon;
//	}
//
//}

package edu.toronto.dbservice.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

import edu.toronto.dbservice.exceptions.SQLExceptionHandler;

public class MIE354DBHelper {

	public static void main(String[] args) throws Exception {
		// connect to the DB

		Connection dbCon = getDBConnection();

		// retrieve schema of tables

		File ddldir = new File("ddl");
		File[] list = ddldir.listFiles();

		for (File datafile : list) {
			System.out.println("executing ddl file:" + datafile.getName());
			BufferedReader bis;
			bis = new BufferedReader(new FileReader(datafile));
			StringBuffer sb = new StringBuffer();
			String line = null;
			while ((line = bis.readLine()) != null) {
				sb.append(line);
			}

			bis.close();

			dbCon.createStatement().execute(sb.toString());
		}

		ResultSet resultSet = dbCon.createStatement().executeQuery("SHOW TABLES");
		// ResultSetMetaData rsMetaData = resultSet.getMetaData();
		// int numberOfColumns = rsMetaData.getColumnCount();

		System.out.println("Tables:");
		while (resultSet.next()) {
			String tableName = resultSet.getString(1);
			if (tableName.startsWith("ACT_")) {
				continue;
			}
			System.out.println(resultSet.getString(1));

			// select all

			ResultSet resultSet2 = dbCon.createStatement().executeQuery("Select * from " + tableName);
			ResultSetMetaData rsMetaData2 = resultSet2.getMetaData();
			int numberOfColumns2 = rsMetaData2.getColumnCount();

			while (resultSet2.next()) {
				for (int i = 1; i < numberOfColumns2; i++) {
					System.out.print(resultSet2.getString(i) + "\t");
				}
				System.out.println();
			}

		}

		dbCon.close();

		System.out.println();

	}

	public static Connection getDBConnection() {
		Connection dbCon = null;
		// if (dbCon == null) {

		// connect to the database
		try {
			System.out.println("loading driver..");
			Class.forName(DBConstants.DB_DRIVER);
			System.out.println("driver loading complete.");
		} catch (ClassNotFoundException cfe) {
			System.out.println("The JDBC drive loading failed.");
			cfe.printStackTrace();
		}
		try {
			System.out.println("connecting to db...");
			dbCon = DriverManager.getConnection(DBConstants.DB_NAME, DBConstants.DB_LOGIN, DBConstants.DB_PASSWD);
		} catch (SQLException se) {
			SQLExceptionHandler.handleException(se);
			System.exit(1);
		}
		// }
		return dbCon;
	}

}

