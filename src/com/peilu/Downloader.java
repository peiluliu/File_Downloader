package com.peilu;
import java.io.ByteArrayOutputStream;  
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
public class Downloader {
	
	public static void main(String[] args) throws ParseException {
		Options options = new Options();
		options.addOption("c", false, "Concurrent Downloading");
		CommandLineParser parser = new DefaultParser();
		CommandLine cmd = parser.parse(options,  args);
		String home = System.getProperty("user.home");
		if (cmd.hasOption("c")) {
			String str = downLoadFromUrl("http://www.cs.sjsu.edu/~austin/cs252-fall19/labs/lab08/doit.hs", home + "/Downloads/");
		}
		String str = downLoadFromUrl("http://www.cs.sjsu.edu/~austin/cs252-fall19/labs/lab08/doit.hs", home + "/Downloads/");
		System.out.println("hi");
		

//		String str = downLoadFromUrl("http://www.cs.sjsu.edu/~austin/cs252-fall19/labs/lab08/doit.hs", 
//				home + "/Downloads/");
	}
	/**
	 * 从网络Url中下载文件
	 * 
	 * @param urlStr
	 * @param fileName
	 * @param savePath
	 * @throws IOException
	 */
	public static String downLoadFromUrl(String urlStr, String savePath) {
		try {
 
			URL url = new URL(urlStr);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			//time out - 3 seconds
			conn.setConnectTimeout(3 * 1000);
			// 防止屏蔽程序抓取而返回403错误
			//conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
 
			InputStream inputStream = conn.getInputStream();
			byte[] getData = readInputStream(inputStream);
 
			// Save file
			File saveDir = new File(savePath);
			if (!saveDir.exists()) {
				saveDir.mkdir();
			}
			String fileName =  urlStr.substring(urlStr.lastIndexOf("/") + 1);
			File file = new File(saveDir + File.separator + fileName);
			FileOutputStream fos = new FileOutputStream(file);
			fos.write(getData);
			if (fos != null) {
				fos.close();
			}
			if (inputStream != null) {
				inputStream.close();
			}
			System.out.println("info:"+url+" download success");
			return saveDir + File.separator + fileName;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return "";
 
	}

	public static byte[] readInputStream(InputStream inputStream) throws IOException {
		byte[] buffer = new byte[1024];
		int len = 0;
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		while ((len = inputStream.read(buffer)) != -1) {
			bos.write(buffer, 0, len);
		}
		bos.close();
		return bos.toByteArray();
	}

}
