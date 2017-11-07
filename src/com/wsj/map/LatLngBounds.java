package com.wsj.map;

public class LatLngBounds {
	public double left;
	public double top;
	public double right;
	public double bottom;
	public LatLngBounds(double l,double t,double r,double b){
		this.left = l;
		this.right = r;
		this.top = t;
		this.bottom = b;
	}
}
