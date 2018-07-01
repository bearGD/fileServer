package com.newer.fileserver;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * 服务端
 * 
 * @author 孙智雄
 *
 */
public class App {
	/**
	 * 端口
	 */
	int port = 9000;
	/**
	 * 服务器套接字
	 */
	ServerSocket serverSocket;
	/**
	 * 线程池
	 */
	ExecutorService pool;

	/**
	 * 接收到文件存放于本地的地址
	 */
	String filePath = "";

	public void start() {

		// 获取存放文件的本地地址
		Scanner sc = new Scanner(System.in);
		System.out.println("请输入您要存放文件的本地地址:");
		filePath = sc.next();
		sc.close();

		try {
			serverSocket = new ServerSocket(port);

			while (true) {
				// 获取套接字
				Socket socket = serverSocket.accept();
				pool = Executors.newCachedThreadPool();

				pool.execute(new Runnable() {

					// 缓存,可变数组
					ByteArrayOutputStream data = new ByteArrayOutputStream();

					@Override
					public void run() {
						try (InputStream in = socket.getInputStream()) {
							byte[] buf = new byte[1024 * 4];
							int size = 0;
							while (-1 != (size = in.read(buf))) {
								// 数据写入可变数组缓存
								data.write(buf, 0, size);
							}
						} catch (Exception e) {
							e.printStackTrace();
						}

						// 接收到的数据,字节数组存放
						byte[] info = data.toByteArray();
						String file = "";

						try {
							byte[] hash = MessageDigest.getInstance("SHA-256").digest(info);
							file = new BigInteger(1, hash).toString(16);
						} catch (NoSuchAlgorithmException e) {
							e.printStackTrace();
						}

						try (FileOutputStream out = new FileOutputStream(new File(filePath,file))) {
							out.write(info);
							System.out.println("上传完成!");
						} catch (Exception e) {
							System.out.println("上传失败!");
						}

					}

				});
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public static void main(String[] args) {
		App server = new App();
		server.start();
	}

}
