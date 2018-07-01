package com.newer.client;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

/**
 * 客户端
 * 
 * @author 孙智雄
 *
 */
public class App {

	/**
	 * 套接字
	 */
	Socket socket;
	/**
	 * 读取文件内容
	 */
	FileInputStream in;
	/**
	 * 端口号
	 */
	int serverPort = 9000;
	/**
	 * 服务器地址
	 */
	String serverAddress = "";
	/**
	 * 输出内容
	 */
	OutputStream out;

	public void start() {
		// 获取需要上传的文件
		Scanner sc = new Scanner(System.in);
		System.out.println("请输入需要上传的文件:");
		String file = sc.next();
		// 用完扫描器,关闭
		sc.close();

		// 建立链接

		try {
			// 实例化套接字
			socket = new Socket(serverAddress, serverPort);
			out = socket.getOutputStream();
			in = new FileInputStream(file);
			// 缓存buf,4kB
			byte[] buf = new byte[1024 * 4];
			int size = 0;
			while (-1 != (size = in.read(buf))) {
				out.write(buf, 0, size);
				// 刷新
				out.flush();
			}
			System.out.println("上传成功!");
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			// 关闭资源
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				socket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void main(String[] args) {
		App client = new App();
		client.start();
	}
}
