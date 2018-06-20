package com.wsj.map;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import com.sun.jna.platform.FileUtils;
import com.wsj.map.projection.IProjection;
import com.wsj.map.projection.ProjMercator;
import com.wsj.map.storage.DatabaseStorage;
import com.wsj.map.storage.IStorage;

import org.apache.http.HttpEntity;
import org.apache.http.client.*;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;

public class TileDownloader {
	private static final int TILE_SIZE = 256;
	ExecutorService mExecutor = Executors.newFixedThreadPool(10);
	private IStorage mStorage;
	private String mURLTemplete;
	private LatLngBounds mBounds;
	private int mMaxZoom, mMinZoom;
	private boolean isCreateBlankTile = false; // 是否在周围生成透明瓦片，mapbox需要
	private DownloadCallback mCallback;
	private IProjection mProjection = new ProjMercator();

	public static interface DownloadCallback{
		public void downloadStart();
		public void downloadFinished();
		public void downloadError();
	}
	
	public static class Builder {
		private TileDownloader downloader;

		public Builder() {
			downloader = new TileDownloader();
		}

		public Builder setURLTemplet(String templete) {
			downloader.mURLTemplete = templete;
			return this;
		}

		public Builder setStorage(IStorage storage) {
			downloader.mStorage = storage;
			return this;
		}

		public Builder setBounds(LatLngBounds bounds) {
			downloader.mBounds = bounds;
			return this;
		}

		public Builder setMaxLevel(int max) {
			downloader.mMaxZoom = max;
			return this;
		}

		public Builder setMinLevel(int min) {
			downloader.mMinZoom = min;
			return this;
		}

		public Builder createBlankTile(boolean isCreate) {
			downloader.isCreateBlankTile = isCreate;
			return this;
		}

		public Builder setProjection(IProjection projection) {
			downloader.mProjection = projection;
			return this;
		}
		
		public Builder setCallback(DownloadCallback callback) {
			downloader.mCallback = callback;
			return this;
		}

		public TileDownloader create() {
			return downloader;
		}

	}

	public void setURLTemplete(String templete) {
		this.mURLTemplete = templete;
	}

	private TileDownloader() {
		
	}

	public void download(String mapType, int minZoom, int maxZoom, LatLng topLeft, LatLng bottomRight) {

	}

	public void startDownload() {
		if (mStorage instanceof DatabaseStorage) {
			((DatabaseStorage) mStorage).saveMapInfo(mBounds, mMinZoom, mMaxZoom);
		}
		for (int zoom = mMinZoom; zoom <= mMaxZoom; zoom++) {
			Point minXY = mProjection.deg2num(mBounds.top, mBounds.left, zoom);
			Point maxXY = mProjection.deg2num(mBounds.bottom, mBounds.right, zoom);
			int minX = minXY.x;
			int minY = minXY.y;
			int maxX = maxXY.x;
			int maxY = maxXY.y;
			for (int x = minX; x <= maxX; x++) {
				for (int y = minY; y <= maxY; y++) {
					Tile tile = new Tile(x, y, zoom);
					DownloadRunnable runnable = new DownloadRunnable(tile);
					mExecutor.execute(runnable);
				}
			}
			if (isCreateBlankTile) {
				minX -= 1;
				maxX += 1;
				minY -= 1;
				maxY += 1;
				for (int x = minX; x <= maxX; x++) {
					generateEmptyImage(new Tile(x, minY, zoom));
					generateEmptyImage(new Tile(x, maxY, zoom));
				}
				for (int y = minY; y <= maxY; y++) {
					generateEmptyImage(new Tile(minX, y, zoom));
					generateEmptyImage(new Tile(maxX, y, zoom));
				}
			}
			if (mStorage instanceof DatabaseStorage) {
				((DatabaseStorage) mStorage).saveLevelInfo(zoom, minX, maxX, minY, maxY);
			}
		}
		mExecutor.shutdown();
		try {
			mExecutor.awaitTermination(10, TimeUnit.SECONDS);
			Boolean isShutDown = false;
			while(!isShutDown) {
				isShutDown = mExecutor.isShutdown();
				if(isShutDown) {
					if(mCallback!=null) {
						System.out.println("finish download"+mURLTemplete);
						mCallback.downloadFinished();
					}
					
					break;
				}
				Thread.sleep(1000);
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	class DownloadRunnable implements Runnable {
		public Tile mTile;

		public DownloadRunnable(Tile tile) {
			// TODO Auto-generated constructor stub
			this.mTile = tile;
		}

		@Override
		public void run() {
			URL url;
			try {
				String urlString = mURLTemplete.replace("{x}", mTile.x + "").replace("{y}", mTile.y + "").replace("{z}",
						mTile.z + "");
				url = new URL(urlString);
//				HttpURLConnection connection = (HttpURLConnection) url.openConnection();

				// connection.setRequestMethod("GET");
				// connection.setRequestProperty("connection", "Keep-Alive");
				// connection.setRequestProperty("user-agent", "Mozilla/5.0
				// (Macintosh; Intel Mac OS X 10_13_1) AppleWebKit/537.36
				// (KHTML, like Gecko) Chrome/62.0.3202.94 Safari/537.36");
				// if (connection.getResponseCode() == 200) {
				// System.out.println(connection.getContentLength());
				//
				//// System.out.println(url);
				//// long length = connection.getContentLengthLong();
				//// System.out.println("length:"+length);
				//// InputStream is = connection.getInputStream();
				//// mStorage.save(mTile, is);
				// }
				

			    CloseableHttpClient httpClient = HttpClients.createDefault();
			    HttpGet httpGet = new HttpGet(urlString);
			    RequestConfig config = RequestConfig.custom().setSocketTimeout(100000).setConnectTimeout(100000).build();
			    httpGet.setConfig(config);
			    httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:34.0) Gecko/20100101 Firefox/34.0");
			    try {
			        CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
			        HttpEntity fileEntity = httpResponse.getEntity();
			        int code = httpResponse.getStatusLine().getStatusCode();
			        if(code!=200) {
			        	 System.out.println("miss:"+urlString);
			        		return;
			        }
			        System.out.println("download:"+urlString);
			        if (fileEntity != null) {
						mStorage.save(mTile, fileEntity.getContent());
			        }
			    } catch (IOException e) {
			    	
			    }

			    httpGet.releaseConnection();


			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}

	/**
	 * 生成周边透明瓦片
	 * 
	 * @param tile
	 */
	private void generateEmptyImage(Tile tile) {
		// 创建BufferedImage对象
		BufferedImage image = new BufferedImage(TILE_SIZE, TILE_SIZE, BufferedImage.TYPE_INT_RGB);
		// 获取Graphics2D
		Graphics2D g2d = image.createGraphics();
		// ---------- 增加下面的代码使得背景透明 -----------------
		image = g2d.getDeviceConfiguration().createCompatibleImage(TILE_SIZE, TILE_SIZE, Transparency.TRANSLUCENT);
		g2d.dispose();
		g2d = image.createGraphics();
		// ---------- 背景透明代码结束 -----------------
		mStorage.save(tile, image);
	}

}
