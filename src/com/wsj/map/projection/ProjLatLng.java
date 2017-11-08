package com.wsj.map.projection;

import java.awt.Point;

import com.wsj.map.LatLng;

public class ProjLatLng implements IProjection {

	/**
	 * 经纬度 转 行列号
	 * 
	 * @param lat
	 * @param lon
	 * @param zoom
	 * @return
	 */
	public Point deg2num(double lat, double lon, float zoom) {
		double n = (int) Math.pow(2, (int) zoom);
		int xtile = (int) ((lon + 180.0) / 360.0 * n);
		int ytile = (int) ((90-lat)*n/360);
		return new Point(xtile, ytile);
	}

	/**
	 * 瓦片行列号转经纬度
	 * 
	 * @param xtile
	 * @param ytile
	 * @param zoom
	 * @return 瓦片左上角的坐标
	 */
	public LatLng num2deg(int xtile, int ytile, float zoom) {
		double n = Math.pow(2.0, (int) zoom);
		double lon_deg = xtile / n * 360.0 - 180.0;
		double lat_deg = 90- (ytile / n * 360);
		return new LatLng(lat_deg, lon_deg);
	}

}
