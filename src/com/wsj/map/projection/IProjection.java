package com.wsj.map.projection;

import java.awt.Point;

import com.wsj.map.LatLng;

public interface IProjection {
	  Point deg2num(double lat, double lon, float zoom);
	  LatLng num2deg(int xtile, int ytile, float zoom);
}
