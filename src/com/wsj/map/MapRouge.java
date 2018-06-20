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
		/** download tdt vector tiles */
		// DownloadUrl[] downloadArray = {
		// new
		// DownloadUrl("http://vectortile.geo-compass.com/api/v1/tilesets/superadmin/china_POI_2/{z}/{x}/{y}.pbf","/Users/gxsn/Work/tdt/china_poi_2.db"),
		// new
		// DownloadUrl("http://vectortile.geo-compass.com/api/v1/tilesets/superadmin/china_other/{z}/{x}/{y}.pbf","/Users/gxsn/Work/tdt/china_other.db"),
		// new
		// DownloadUrl("http://vectortile.geo-compass.com/api/v1/tilesets/superadmin/china_LRDL/{z}/{x}/{y}.pbf","/Users/gxsn/Work/tdt/china_lrdl.db"),
		// new
		// DownloadUrl("http://vectortile.geo-compass.com/api/v1/tilesets/superadmin/china_JIEDAO/{z}/{x}/{y}.pbf","/Users/gxsn/Work/tdt/china_jiedao.db"),
		// new
		// DownloadUrl("http://vectortile.geo-compass.com/api/v1/tilesets/superadmin/china_SUBL/{z}/{x}/{y}.pbf","/Users/gxsn/Work/tdt/china_sbul.db"),
		// new
		// DownloadUrl("http://vectortile.geo-compass.com/api/v1/tilesets/superadmin/china_1-10/{z}/{x}/{y}.pbf","/Users/gxsn/Work/tdt/china_1_10.db"),
		// };
		// int downloadIndex = 5;
		// TileDownloader downloader = new TileDownloader.Builder()
		// .setURLTemplet(downloadArray[downloadIndex].url)
		// .setMaxLevel(14)
		// .setMinLevel(1)
		// .setStorage(new DatabaseStorage(downloadArray[downloadIndex].path))
		// .setProjection(new ProjMercator())
		// .createBlankTile(false)
		// .setBounds(new LatLngBounds(115, 41, 117, 39))
		//// .setBounds(new LatLngBounds(116.5, 39.1, 116.6, 39.2))
		// .create();
		// downloader.startDownload();

		/** download mapbox data */

//		MapBoxDataDownloader downloadler = new MapBoxDataDownloader.Builder()
//				.setBounds(new LatLngBounds(118.13, 32.04, 118.89, 31.97)).setLocalPath("/Users/gxsn/Work/tdt/nj/")
//				.setMaxLevel(16).setMinLevel(1)
//				.setStyleUrl(
//						"http://vectortile.geo-compass.com/api/v1/styles/xjl/BJe4UiYajf/publish?access_key=7c611870843304ad94ce4df5afed4d5f")
//				.create();
//		downloadler.startDownLoad();
		// MapBoxDataDownloader downloadler = new MapBoxDataDownloader.Builder()
		// .setBounds(new LatLngBounds(118.13, 32.04, 118.89, 31.97))
		// .setLocalPath("/Users/gxsn/Work/tdt/qhq/")
		// .setMaxLevel(14)
		// .setMinLevel(1)
		// .setStyleUrl("http://vectortile.geo-compass.com/api/v1/styles/xjl/BJl28rSP5z/publish?access_key=7c611870843304ad94ce4df5afed4d5f")
		// .create();
		// downloadler.startDownLoad();
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
