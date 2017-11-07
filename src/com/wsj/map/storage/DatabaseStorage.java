package com.wsj.map.storage;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.concurrent.locks.ReentrantLock;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import com.wsj.map.DataBase;
import com.wsj.map.Tile;

public class DatabaseStorage implements IStorage{
	DataBase mDataBase;
	private static ReentrantLock mLock = new ReentrantLock();

	public DatabaseStorage(String dbPath) {
		mDataBase = DataBase.getInstance(dbPath);
	}

	public interface StatusLinsenter {
		void onSuccess();
		void onError();
	}


	
	
	@Override
	public void save(Tile tile, InputStream in) {
		storageInTable(tile, bufferInputStream(in));
	}
	
	private void storageInTable(Tile tile,InputStream in){
		String strSQL = "INSERT INTO tiles(zoom_level,tile_column,tile_row,tile_data) VALUES(?,?,?,?)";
		PreparedStatement pstmt = null;
		mLock.lock();
		try {
			pstmt = mDataBase.getConnection().prepareStatement(strSQL);
			mDataBase.getConnection().setAutoCommit(false);
			pstmt.setInt(1, tile.z);
			pstmt.setInt(2, tile.x);
			pstmt.setInt(3, tile.y);
			pstmt.setBinaryStream(4, in, in.available());
			pstmt.addBatch();
			pstmt.executeBatch();
			mDataBase.getConnection().commit();
		} catch (SQLException e) {
			try {
				mDataBase.getConnection().rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  finally {
			try {
				if(pstmt!=null)
					pstmt.close();
			} catch (Exception ex) {

			}
		}
		mLock.unlock();
	}
	
	/**
	 * 由于从网络不能一次性的返回所有数据，我们不能使用available，一次性的获取InputStream的长度。
	 * 所以，需要先将网络的返回的数据写入一个临时的InputStream
	 * @return
	 */
	private InputStream bufferInputStream(InputStream in){
		BufferedInputStream bis   = new BufferedInputStream(in);
		ByteArrayOutputStream byos= new ByteArrayOutputStream();
		byte[] b = new byte[4096];
		int length;
		try{
		while((length=bis.read(b))!=-1){
			byos.write(b,0,length);
			byos.flush();
			}
		in.close();
		}catch(IOException e){
			
		}
		ByteArrayInputStream bais = new ByteArrayInputStream(byos.toByteArray());
		return bais;
	}

	@Override
	public void save(Tile tile, BufferedImage image) {
		// TODO Auto-generated method stub
		 InputStream is;
		try {
			ByteArrayOutputStream bs =new ByteArrayOutputStream(); 
			 ImageOutputStream imOut = ImageIO.createImageOutputStream(bs);
			 ImageIO.write(image,"png",imOut); 
			 is = new ByteArrayInputStream(bs.toByteArray());
			 storageInTable(tile, is);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		
	}


}
