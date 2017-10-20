package com.wsj.map;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;


public class TileDownloader {
	private static String  ROOT_URL = "http://t2.tianditu.com/DataServer?T=cva_w&x={x}&y={y}&l={z}";
	ExecutorService mExecutor = Executors.newFixedThreadPool(10);
	
	public void download(String mapType,int minZoom,int maxZoom,LatLng topLeft,LatLng bottomRight){
		for(int zoom = minZoom;zoom <= maxZoom; zoom++){
			Point minXY = ProjMercator.deg2num(topLeft.lat, topLeft.lon, zoom);
			Point maxXY = ProjMercator.deg2num(bottomRight.lat, bottomRight.lon, zoom);
			for(int x=minXY.x;x<=maxXY.x;x++){
				for(int y=minXY.y;y<=maxXY.y;y++){
					Tile tile = new Tile("img_w",x,y,zoom);
					DownloadRunnable runnable = new DownloadRunnable(tile);
					mExecutor.execute(runnable);
				}
			}
			
			if(minXY.x > 0){
				int minX = minXY.x-1;
				int maxX = maxXY.x+1;
				int minY = minXY.y-1;
				int maxY = maxXY.y+1;
				for(int x= minX;x<=maxX;x++){
					generateEmptyImage(new Tile("img_w",x,minY,zoom));
					generateEmptyImage(new Tile("img_w",x,maxY,zoom));
				}
				for(int y= minY;y<=maxY;y++){
					generateEmptyImage(new Tile("img_w",minX,y,zoom));
					generateEmptyImage(new Tile("img_w",maxX,y,zoom));
				}
			}
		}
	}
	
	public void startDownload(){
		
	}
	public void download(Tile tile){
	
		
	}
	
	
	
	class DownloadRunnable implements Runnable{
		public Tile mTile;
		public DownloadRunnable(Tile tile) {
			// TODO Auto-generated constructor stub
			this.mTile = tile;
		}
		@Override
		public void run() {
			URL url;
			try {
				url = new URL(String.format("http://t2.tianditu.com/DataServer?T=%s&x=%d&y=%d&l=%d",mTile.mapType,mTile.x,mTile.y,mTile.z));
				String path = String.format("/Users/gxsn/Work/tdt/%s_%d_%d_%d.png", mTile.mapType, mTile.z,mTile.x,mTile.y);
				File file = new File(path);
				if(!file.exists())file.createNewFile();
				FileOutputStream fos = new FileOutputStream(file);
				URLConnection connection = url.openConnection();
				InputStream is = connection.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				byte[] b = new byte[4096];
				int length;
				while((length=bis.read(b))!=-1){
					fos.write(b,0,length);
					fos.flush();
				}
				fos.close();
				bis.close();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
		
	}
	
	private void generateEmptyImage(Tile tile){
		int width = 256;
		int height = 256;
		// 创建BufferedImage对象
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
		// 获取Graphics2D
		Graphics2D g2d = image.createGraphics();
		// ----------  增加下面的代码使得背景透明  -----------------
		image = g2d.getDeviceConfiguration().createCompatibleImage(width, height, Transparency.TRANSLUCENT);
		g2d.dispose();
		g2d = image.createGraphics();
		// ----------  背景透明代码结束  -----------------
		// 保存文件    
		try {
			 String path = String.format("/Users/gxsn/Work/tdt/%s_%d_%d_%d.png", tile.mapType, tile.z,tile.x,tile.y);
			ImageIO.write(image, "png", new File(path));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
