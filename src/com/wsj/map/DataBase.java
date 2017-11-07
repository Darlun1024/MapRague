package com.wsj.map;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @author 
 *数据库连接工具辅助类
 */

public class DataBase {
	private Connection connection = null;
	private Statement statement;
	private ResultSet rs;
	private static DataBase singleDataBase;
	public static DataBase getInstance(String dbPath){
		if(singleDataBase ==null){
			singleDataBase = new DataBase(dbPath);
		}
		return singleDataBase;
	}
	
	private DataBase(String dbpath){
		 // create a database connection
        try {
			connection = DriverManager.getConnection("jdbc:sqlite:" + dbpath);
	        statement = connection.createStatement();
	        statement.setQueryTimeout(30);  // set timeout to 30 sec.
	        statement.executeUpdate("create table if not exists metadata (name string, value string)");
	        
	        statement.executeUpdate("create table if not exists tab_about (type string, data blob)");
	        
	        statement.executeUpdate("create table if not exists tiles (zoom_level integer, tile_column integer, "
	        		+ "tile_row integer, tile_data blob)");
	        
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
	public Connection getConnection() {
		return connection;
	}
	
	public void closeAll(){
			try {
				if(connection != null)
				connection.close();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	public ResultSet query(String sql){
		 try {
			 rs = statement.executeQuery(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		 return rs;
	}
	
	public void update(String sql){
		 try {
			 statement.executeUpdate(sql);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
