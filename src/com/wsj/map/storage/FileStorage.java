package com.wsj.map.storage;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.wsj.map.Tile;

public class FileStorage implements IStorage{
	private String mSavePath;
	private String mExt = "png";
	public FileStorage(String savePath){
		mSavePath = savePath;
	}
	
	public FileStorage(String savePath,String ext){
		mSavePath = savePath;
		mExt = ext;
	}
	
	@Override
	public void save(Tile tile,InputStream in){
		String path = String.format(mSavePath+"%d_%d_%d.%s", tile.z,tile.x,tile.y,mExt);
		File file = new File(path);
		if(!file.exists())
			try {
				file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				BufferedInputStream bis = new BufferedInputStream(in);
				byte[] b = new byte[4096];
				int length;
				while((length=bis.read(b))!=-1){
					fos.write(b,0,length);
					fos.flush();
				}
				fos.close();
				bis.close();
				in.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
	
	@Override
	public void save(Tile tile,BufferedImage image){
		 String path = String.format(mSavePath+"%d_%d_%d.png", tile.z,tile.x,tile.y);
		 File f = new File(path);
		 if(!f.exists())
			try {
				f.createNewFile();
				ImageIO.write(image, "png", f); 
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}
}
