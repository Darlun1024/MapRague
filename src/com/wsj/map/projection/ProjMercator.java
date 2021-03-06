package com.wsj.map.projection;

import java.awt.Point;

import com.wsj.map.LatLng;

public class ProjMercator implements IProjection {
	
	static class PointD{
		public double x;
		public double y;
		public PointD(double x,double y){
			this.x = x;
			this.y = y;
		}
	}
	
	/**
	 * 经纬度 转 行列号
	 * 
	 * @param lat
	 * @param lon
	 * @param zoom
	 * @return
	 */
	public Point deg2num(double lat, double lon, float zoom) {
		double lat_rad = Math.toRadians(lat);
		double n = (int) Math.pow(2, (int) zoom);
		int xtile = (int) ((lon + 180.0) / 360.0 * n);
		int ytile = (int) ((1.0 - Math.log(Math.tan(lat_rad) + (1 / Math.cos(lat_rad))) / Math.PI) / 2.0 * n);
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
		double lat_rad = Math.atan(Math.sinh(Math.PI * (1 - 2 * ytile / n)));
		double lat_deg = Math.toDegrees(lat_rad);
		return new LatLng(lat_deg, lon_deg);
	}
	
	public PointD lonLat2WebMercator(double lat,double lon)
	{
		;
	    double x = lon *20037508.34/180;
	    double y = Math.log(Math.tan((90+lat)*Math.PI/360))/(Math.PI/180);
	    y = y *20037508.34/180;
	    PointD  mercator = new PointD(x, y);
	    return mercator ;
	}
	
	//Web墨卡托转经纬度
	LatLng webMercator2lonLat( PointD  mercator )
	{
	    double x = mercator.x/20037508.34*180;
	    double y = mercator.y/20037508.34*180;
	    y= 180/Math.PI*(2*Math.atan(Math.exp(y*Math.PI/180))-Math.PI/2);
	    LatLng lonLat = new LatLng(y,x);
	    return lonLat;
	}

}
