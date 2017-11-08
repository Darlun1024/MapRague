package com.wsj.map;

import com.wsj.map.projection.ProjLatLng;
import com.wsj.map.storage.DatabaseStorage;
import com.wsj.map.storage.FileStorage;

public class MapRouge {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TileDownloader downloader = new TileDownloader.Builder()
				.setURLTemplet("http://t2.tianditu.com/DataServer?T=img_c&x={x}&y={y}&l={z}")
				.setMaxLevel(15)
				.setMinLevel(1)
				.setStorage(new FileStorage("/Users/gxsn/Work/tdt/"))
				.setProjection(new ProjLatLng())
				.createBlankTile(false)
				.setBounds(new LatLngBounds(116.590756, 39.687959, 116.720739, 39.587319))
				.create();
		downloader.startDownload();
	}
	
}
