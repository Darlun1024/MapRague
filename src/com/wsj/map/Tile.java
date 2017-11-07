package com.wsj.map;

import com.sun.istack.internal.NotNull;

public class Tile {
	public int x;
	public int y;
	public int z;
	
	public Tile(int x,int y,int z){
		this.x = x;
		this.y = y;
		this.z = z;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof Tile)){
			return false;
		}else{
			Tile tile = (Tile) obj;
			return this.x==tile.x&&this.y==tile.y&&this.z==tile.z;
		}
	}
}
