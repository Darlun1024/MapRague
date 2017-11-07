package com.wsj.map;

import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Transparency;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageOutputStream;

import com.wsj.map.storage.FileStorage;
import com.wsj.map.storage.IStorage;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;
import com.wsj.map.storage.DatabaseStorage;

public class TileDownloader {
	private static final int TILE_SIZE = 256;
	private static String ROOT_URL = "http://t2.tianditu.com/DataServer?T=cva_w&x={x}&y={y}&l={z}";
	ExecutorService mExecutor = Executors.newFixedThreadPool(10);
	private IStorage mStorage;
	private String mURLTemplete;
	private LatLngBounds mBounds;
	private int mMaxLevel, mMinLevel;
	private boolean isCreateBlankTile = false; // 是否在周围生成透明瓦片，mapbox需要

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
			downloader.mMaxLevel = max;
			return this;
		}

		public Builder setMinLevel(int min) {
			downloader.mMinLevel = min;
			return this;
		}

		public Builder createBlankTile(boolean isCreate) {
			downloader.isCreateBlankTile = isCreate;
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
		for (int zoom = mMinLevel; zoom <= mMaxLevel; zoom++) {
			Point minXY = ProjMercator.deg2num(mBounds.top, mBounds.left, zoom);
			Point maxXY = ProjMercator.deg2num(mBounds.bottom, mBounds.right, zoom);
			for (int x = minXY.x; x <= maxXY.x; x++) {
				for (int y = minXY.y; y <= maxXY.y; y++) {
					Tile tile = new Tile(x, y, zoom);
					DownloadRunnable runnable = new DownloadRunnable(tile);
					mExecutor.execute(runnable);
				}
			}
			if (isCreateBlankTile) {
				if (minXY.x > 0) {
					int minX = minXY.x - 1;
					int maxX = maxXY.x + 1;
					int minY = minXY.y - 1;
					int maxY = maxXY.y + 1;
					for (int x = minX; x <= maxX; x++) {
						generateEmptyImage(new Tile(x, minY, zoom));
						generateEmptyImage(new Tile(x, maxY, zoom));
					}
					for (int y = minY; y <= maxY; y++) {
						generateEmptyImage(new Tile(minX, y, zoom));
						generateEmptyImage(new Tile(maxX, y, zoom));
					}
				}
			}
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
				URLConnection connection = url.openConnection();
				InputStream is = connection.getInputStream();
				mStorage.save(mTile, is);
				System.out.println(url);
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
