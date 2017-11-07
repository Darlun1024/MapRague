package com.wsj.map;

import com.wsj.map.storage.DatabaseStorage;

public class MapRouge {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TileDownloader downloader = new TileDownloader.Builder()
				.setURLTemplet("http://t2.tianditu.com/DataServer?T=cva_w&x={x}&y={y}&l={z}")
				.setMaxLevel(18)
				.setMinLevel(7)
				.setStorage(new DatabaseStorage("/Users/gxsn/Work/tdt/cva_w.db"))
				.createBlankTile(true)
				.setBounds(new LatLngBounds(116.590756, 39.687959, 116.720739, 39.587319))
				.create();
		downloader.startDownload();
	}
	
}
