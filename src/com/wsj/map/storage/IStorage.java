package com.wsj.map.storage;

import java.awt.image.BufferedImage;
import java.io.InputStream;

import com.wsj.map.Tile;

public interface IStorage {
	void save(Tile tile,InputStream in);
	void save(Tile tile,BufferedImage image);
}
