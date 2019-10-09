package com.peilu.multiThread;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;
//新开启一个线程，用于完成下载任务
class DownloadThread extends Thread{
 
	int thredId;
	int startIndex;
	int endIndex;
	
	public DownloadThread(int thredId, int startIndex, int endIndex) {
		super();
		this.thredId = thredId;
		this.startIndex = startIndex;
		this.endIndex = endIndex;
	}
	
	public void run() {
		try {
		//下载进度文件保存的路径和文件名
			String home = System.getProperty("user.home");
			String str =  home + "/Downloads/";
		File progressFile = new File(str,(thredId + ".hs"));
		//判断保存下载进度的临时文件是否存在，以便确定下载的开始位置
		if (progressFile.exists()) {
			FileInputStream fis = new FileInputStream(progressFile);
			BufferedReader bReader = new BufferedReader(new InputStreamReader(fis));
			//拿到临时文件中保存的数据，并把此数据设置为新的开始位置
			int text = Integer.parseInt(bReader.readLine());
			startIndex = text;
			fis.close();
			}
			System.out.println("线程"+thredId+"的最终开始下载位置是："+startIndex);
			
			URL url = new URL(MultiDownload2.path);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
//			conn.setRequestMethod("GET");
//			conn.setConnectTimeout(5000);
			conn.setReadTimeout(5000);
			//设置请求数据的范围
			conn.setRequestProperty("Range", "bytes="+startIndex+"-"+endIndex);
			//建立连接，状态码206表示请求部分数据成功，此时开始下载任务
			//if (conn.getResponseCode()==206) {
				InputStream is = conn.getInputStream();
				//指定文件名和文件路径
				File file = new File(MultiDownload2.getFileName(MultiDownload2.path) );
				int len = 0;
				byte [] b = new byte[1024];
				//三个线程各自创建自己的随机存储文件
				RandomAccessFile raf = new RandomAccessFile(file, "rwd");
				//设置数据从哪个位置开始写入数据到临时文件
				raf.seek(startIndex);
				//设置当前线程下载的总字节数
				int total = 0;
				long start = System.currentTimeMillis();
				
				//当下载意外停止时，记录当前下载进度
				int currentPosition = startIndex;
				
				while ((len=is.read(b))!=-1) {
					raf.write(b,0,len);
					//打印当前线程下载的总字节数
					total += len;
					/**
					* 实现断点续传的功能
					*/
					//RandomAccessFile主要用来存放下载的临时文件，可以用FileOutputStream代替
					RandomAccessFile rafProgress = new RandomAccessFile(progressFile, "rwd");
					//再次下载时的开始位置
					currentPosition = startIndex + total;
					//把下载进度写进rafProgress临时文件，下一次下载时，就以这个值作为新的startIndex
					rafProgress.write((currentPosition + "").getBytes());
					//关流
					rafProgress.close();
					System.out.println("线程"+thredId+"已经下载了"+total);
				}
				raf.close();
				long end = System.currentTimeMillis();
				//打印线程下载文件用时
				System.out.println("线程"+thredId+"下载文件用时"+(end-start)+"ms");
				//打印线程的结束
				System.out.println("线程："+thredId+" 下载结束了 !!!");
				//下载结束后，删除所有的临时文件
				MultiDownload2.threadFinished ++;
				//使用同步语句块，保证线程的安全性
				synchronized (MultiDownload2.path) {
				//如果这个条件成立，说明所有的线程下载结束
				if (MultiDownload2.threadFinished == MultiDownload2.threadCount) {
					for (int i = 0; i < MultiDownload2.threadCount; i++) {
						//删除三个线程产生的临时文件
						File temp = new File(str, i + ".hs");
						temp.delete();
					}
					//保证三个线程的临时文件同时被删除
					MultiDownload2.threadFinished = 0;
					}
				}
			//}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}

