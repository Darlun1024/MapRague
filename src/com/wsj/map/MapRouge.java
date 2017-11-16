package com.wsj.map;

import com.wsj.map.projection.ProjMercator;
import com.wsj.map.storage.DatabaseStorage;

public class MapRouge {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TileDownloader downloader = new TileDownloader.Builder()
				.setURLTemplet("http://t2.tianditu.com/DataServer?T=img_w&x={x}&y={y}&l={z}")
				.setMaxLevel(16)
				.setMinLevel(1)
				.setStorage(new DatabaseStorage("/Users/gxsn/Work/tdt/img_beijing.db"))
				.setProjection(new ProjMercator())
				.createBlankTile(true)
				.setBounds(new LatLngBounds(115, 41, 117, 39))
//				.setBounds(new LatLngBounds(116.5, 39.1, 116.6, 39.2))
				.create();
		downloader.startDownload();
	}
	
}
