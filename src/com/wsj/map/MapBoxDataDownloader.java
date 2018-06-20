package com.wsj.map;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.http.HttpEntity;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sun.xml.internal.ws.util.StringUtils;
import com.wsj.map.projection.ProjMercator;
import com.wsj.map.storage.DatabaseStorage;

public class MapBoxDataDownloader implements TileDownloader.DownloadCallback {

	private static class Source {
		String id;
		String url;
	}

	private LatLngBounds mBounds;
	private int mMaxZoom, mMinZoom;
	private String mStyleUrl; // 样式文件地址
	private String mLocalPath, mSpritePath, mGlyphPath, mDBDirPath;
	private List<Source> mSourceUrlList;
	private String mSpriteUrl, mGlyphsUrl;
	private int mCurDownloadIndex = -1;

	public static class Builder {
		private MapBoxDataDownloader downloader;

		public Builder() {
			downloader = new MapBoxDataDownloader();
		}

		public Builder setBounds(LatLngBounds bounds) {
			downloader.mBounds = bounds;
			return this;
		}

		public Builder setMaxLevel(int max) {
			downloader.mMaxZoom = max;
			return this;
		}

		public Builder setStyleUrl(String url) {
			downloader.mStyleUrl = url;
			return this;
		}

		public Builder setMinLevel(int min) {
			downloader.mMinZoom = min;
			return this;
		}

		public Builder setLocalPath(String path) {
			if (!path.endsWith("/"))
				;
			path = path + "/";
			downloader.mLocalPath = path;
			return this;
		}

		public MapBoxDataDownloader create() {
			return downloader;
		}
	}

	public void startDownLoad() {
		// 初始化文件夹
		mSpritePath = mLocalPath + "sprite/";
		mGlyphPath = mLocalPath + "glyph/";
		mDBDirPath = mLocalPath + "db/";
		File f = new File(mSpritePath);
		if (!f.exists())
			f.mkdirs();
		f = new File(mGlyphPath);
		if (!f.exists())
			f.mkdirs();
		f = new File(mDBDirPath);
		if (!f.exists())
			f.mkdirs();
		downloadStyle();
	}

	private MapBoxDataDownloader() {
	}

	/**
	 * 下载样式文件
	 */
	private void downloadStyle() {
		File dir = new File(mLocalPath);
		if (!dir.exists())
			dir.mkdirs();
		String stylePath = dir.getAbsolutePath() + "/Style.json";
		downloadFile(mStyleUrl, stylePath);
		parseStyleJSON(stylePath);

		for (Source source : mSourceUrlList) {
			System.out.println(source.id + ":" + source.url);
		}

		downloadSprites();
		downloadGlyphs();
		downloadSourceJson();

	}

	/**
	 * 解析样式文件
	 * 
	 * @param stylePath
	 */
	private void parseStyleJSON(String stylePath) {
		try {
			String styleJSON = org.apache.commons.io.FileUtils.readFileToString(new File(stylePath), "utf-8");
			JSONObject style = JSON.parseObject(styleJSON);
			JSONObject sourcesObj = style.getJSONObject("sources");
			Set<String> sourceSet = sourcesObj.keySet();
			mSourceUrlList = new ArrayList<>();
			for (String key : sourceSet) {
				Source source = new Source();
				source.id = key;
				JSONObject sourceObj = sourcesObj.getJSONObject(key);
				source.url = sourceObj.getString("url");
				mSourceUrlList.add(source);
			}
			mSpriteUrl = style.getString("sprite");
			mGlyphsUrl = style.getString("glyphs");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * 下载资源文件
	 */
	private void downloadSourceJson() {
		for (Source source : mSourceUrlList) {
			String url = source.url;
			String localPath = mLocalPath + source.id + ".json";
			downloadFile(url, localPath);
		}

		downloadTiles();
	}

	/**
	 * 下载瓦片 根据范围和资源文件配置下载
	 */
	private void downloadTiles() {
		downloadNextSource();
	}

	/**
	 * 逐个下载
	 */
	private void downloadNextSource() {
		mCurDownloadIndex++;
		if (mCurDownloadIndex > mSourceUrlList.size() - 1)
			return;
		Source source = mSourceUrlList.get(mCurDownloadIndex);
		String localPath = mLocalPath + source.id + ".json";
		String template = parseSourceJSON(localPath);
		if (template != null) {
			downloadTile(source.id, template);
		} else {
			downloadNextSource();
		}
	}

	/**
	 * 下载瓦片
	 * 
	 * @param sourceId
	 * @param template
	 */
	private void downloadTile(String sourceId, String template) {
		String dbPath = mDBDirPath + sourceId + ".db";
		TileDownloader downloader = new TileDownloader.Builder().setURLTemplet(template).setMaxLevel(mMaxZoom)
				.setMinLevel(mMinZoom).setStorage(new DatabaseStorage(dbPath)).setProjection(new ProjMercator())
				.createBlankTile(false).setBounds(mBounds).setCallback(this).create();
		downloader.startDownload();
	}

	/**
	 * 解析数据源JSON
	 */
	private String parseSourceJSON(String path) {
		try {
			String source = org.apache.commons.io.FileUtils.readFileToString(new File(path), "utf-8");
			JSONObject object = JSON.parseObject(source);
			JSONArray array = object.getJSONArray("tiles");
			String url = array.getString(0);
			return url;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 下载符号 固定文件
	 */
	public void downloadSprites() {
		String spriteImgUrl = mSpriteUrl + ".png";
		String spriteJsonUrl = mSpriteUrl + ".json";
		String spriteImgUrl2x = mSpriteUrl + "@2x.png";
		String spriteJsonUrl2x = mSpriteUrl + "@2x.json";
		downloadFile(spriteJsonUrl, mSpritePath + "sprite.json");
		downloadFile(spriteImgUrl, mSpritePath + "sprite.png");
		downloadFile(spriteJsonUrl2x, mSpritePath + "sprite@2x.json");
		downloadFile(spriteImgUrl2x, mSpritePath + "sprite@2x.png");
	}

	/**
	 * 下载字体 根据样式文件中的字体设置，下载全部字体
	 */
	private void downloadGlyphs() {
		String font = "SimHei Regular";
		String fontDir = mGlyphPath+font+"/";
		File f = new File(fontDir);
		if(!f.exists())f.mkdirs();
		try {
			font = URLEncoder.encode(font, "unicode");
			String url = mGlyphsUrl.replace("{fontstack}", font);
			url = url.replaceAll("\\+", "%20");
			int n = 65536 / 256;
			for (int i = 0; i < n; i++) {
				int start = i * 256;
				int end = start + 255;
				String range = start + "-" + end;
				String downloadUrl = url.replace("{range}", range);
				downloadFile(downloadUrl, fontDir+ range + ".pbf");
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	private void downloadFile(String urlString, String localPath) {
		try {
			URL url = new URL(urlString);

			CloseableHttpClient httpClient = HttpClients.createDefault();
			HttpGet httpGet = new HttpGet(urlString);
			RequestConfig config = RequestConfig.custom().setSocketTimeout(10000).setConnectTimeout(10000).build();
			httpGet.setConfig(config);
			// httpGet.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:34.0)
			// Gecko/20100101 Firefox/34.0");
			try {
				CloseableHttpResponse httpResponse = httpClient.execute(httpGet);
				HttpEntity fileEntity = httpResponse.getEntity();
				int code = httpResponse.getStatusLine().getStatusCode();
				if (code != 200)
					return;
				if (fileEntity != null) {
					File file = new File(localPath);
					if (!file.exists())
						file.createNewFile();
					FileOutputStream fos = new FileOutputStream(file);
					BufferedInputStream bis = new BufferedInputStream(fileEntity.getContent());
					byte[] b = new byte[4096];
					int length;
					while ((length = bis.read(b)) != -1) {
						fos.write(b, 0, length);
						fos.flush();
					}
					fos.close();
					bis.close();

				}
			} catch (IOException e) {

			}

			httpGet.releaseConnection();

		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	@Override
	public void downloadStart() {
		// TODO Auto-generated method stub

	}

	@Override
	public void downloadFinished() {
		downloadNextSource();
	}

	@Override
	public void downloadError() {
		// TODO Auto-generated method stub

	}

}
