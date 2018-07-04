package com.newer.client;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
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

	/**
	 * map存放文件散列值和文件名
	 */
	Map<String, String> map = new HashMap<>();

	public void start(String file) {

		// 建立链接
		try {
			// 实例化套接字
			socket = new Socket(serverAddress, serverPort);
			out = socket.getOutputStream();
			ByteArrayOutputStream byteArrayOut = new ByteArrayOutputStream();
			in = new FileInputStream(file);
			// 缓存buf,4kB
			byte[] buf = new byte[1024 * 4];
			int size = 0;
			while (-1 != (size = in.read(buf))) {
				byteArrayOut.write(buf, 0, size);
			}
			byte[] info = byteArrayOut.toByteArray();
			try {
				// 将文件信息用SHA-256编码
				byte[] hash = MessageDigest.getInstance("SHA-256").digest(info);
				String hashStr = new BigInteger(1, hash).toString(16);
				for (String mapKey : map.keySet()) {
					if(hashStr.equals(mapKey)) {
						System.err.println("文件已存在,不可重复上传!");
						return;
					}
				}
				map.put(hashStr, file.substring(file.lastIndexOf("\\") + 1));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			out.write(info);
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
		System.out.println("客户端启动!");
		// 获取需要上传的文件
		Scanner sc = new Scanner(System.in);
		System.out.println("输入上传文件数量:");
		int number = sc.nextInt();
		for (int i = 0; i < number; i++) {
			System.out.println("请输入需要上传的文件:");
			String file = sc.next();
			client.start(file);
		}
		// 用完扫描器,关闭
		sc.close();
		// 上传记录写入文件
		FileOutputStream fileOut = null;
		try {
			fileOut = new FileOutputStream(new File("E:\\MyServer", "服务器接收记录.txt"));
			String str = "";
			for (String mapKey : client.map.keySet()) {
				str = mapKey + " : " + client.map.get(mapKey) + "\n";
				try {
					fileOut.write(str.getBytes());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				fileOut.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
