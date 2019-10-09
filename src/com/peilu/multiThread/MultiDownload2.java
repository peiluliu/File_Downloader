package com.peilu.multiThread;

import java.io.File; 
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class MultiDownload2 {
	static String path = "http://www.cs.sjsu.edu/~austin/cs252-fall19/labs/lab08/doit.hs";
	//开启线程的数量
	static int threadCount = 6;
	//下载结束的线程数
	static int threadFinished = 0;
	public static void main(String[] args){
//		Options options = new Options();
//		options.addOption("c", false, "Concurrent Downloading");
//		CommandLineParser parser = new DefaultParser();
//		CommandLine cmd = parser.parse(options,  args);
//		String home = System.getProperty("user.home");
//		if (cmd.hasOption("c")) {
			startDownload();
//		}
		
	}
	public static void startDownload() {
		try {
			URL url = new URL("http://www.cs.sjsu.edu/~austin/cs252-fall19/labs/lab08/doit.hs");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setRequestMethod("GET");
//			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			//此时只是确定和服务器建立了连接，但并没有开始下载任务
			//if (conn.getResponseCode()==200) {
				//拿到文件的长度
				int length = conn.getContentLength();
				//指定文件路径和文件名
				String home = System.getProperty("user.home");
				String str =  home + "/Downloads/";
				File file = new File(str, getFileName(path));
				//创建随机存储文件大小，为了建立一个和源文件大小相同的存储区间
				RandomAccessFile raf = new RandomAccessFile(file, "rwd");
				//设置临时文件的大小，和服务器文件一模一样
				raf.setLength(length);
				//计算每个线程下载的字节数
				int size = length / threadCount;
				//计算三个线程下载的开始位置和结束位置
				for (int i = 0; i < threadCount; i++) {
					int startIndex = i * size;
					int endIndex = (i + 1) * size-1;
					//如果是最后一个线程，要把结尾读完
					if (i == threadCount-1) {
						//length从0开始读，所以length-1表示最后一个字节
						endIndex = length-1;
					}
					//打印三个线程的开始与结束位置
					System.out.println("线程"+i+"的开始和结束位置："+startIndex+"----"+endIndex);
					//开启线程，传入线程ID，下载的开始位置和下载的结束位置
					new DownloadThread(i, startIndex, endIndex).start();;
					}
				//}
			} catch (Exception e) {
				e.printStackTrace();
			}
	}
	
	/*
	* 获取文件名
	*/
	public static String getFileName(String path){
		int index=path.lastIndexOf("/");
		return path.substring(index + 1);
	}
}

