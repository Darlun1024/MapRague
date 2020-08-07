package com.wsj.map;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.util.TextUtils;

import com.wsj.map.projection.ProjMercator;
import com.wsj.map.storage.DatabaseStorage;
import com.wsj.map.storage.FileStorage;

public class MapRouge {

	public static void main(String[] args) {
		/** download tdt image tiles */
		// TileDownloader downloader = new TileDownloader.Builder()
		// .setURLTemplet("http://t1.tianditu.com/DataServer?T=cva_w&x={x}&y={y}&l={z}")
		// .setMaxLevel(18)
		// .setMinLevel(1)
		// .setStorage(new DatabaseStorage("/Users/gxsn/Work/tdt/cva_wudaokou.db"))
		// .setProjection(new ProjMercator())
		// .createBlankTile(true)
		// .setBounds(new LatLngBounds(116.30, 40.2, 116.40, 39.95))
		//// .setBounds(new LatLngBounds(116.5, 39.1, 116.6, 39.2))
		// .create();
		// downloader.startDownload();
	}

	static class DownloadUrl {
		public String url;
		public String path;

		public DownloadUrl(String u, String p) {
			this.url = u;
			this.path = p;
		}
	}
	

}
