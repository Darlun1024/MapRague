package com.wsj.map;

public class MapRouge {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		TileDownloader downloader = new TileDownloader();
		downloader.download("img_w", 1, 18, new LatLng(39.929626,116.391585), new LatLng(39.918809,116.415013));
	}
	
}
