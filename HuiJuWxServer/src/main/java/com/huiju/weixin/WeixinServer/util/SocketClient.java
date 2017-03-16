package com.huiju.weixin.WeixinServer.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient extends Thread {

	private String host;
	
	private int port;
	
	private BufferedReader bins;
	
	private PrintWriter out;
	
	private Socket client;
	
	
	public SocketClient(String host, int port) {
		super();
		this.host = host;
		this.port = port;
		try {
			this.client = new Socket(this.host, this.port);
			this.bins = new BufferedReader(new InputStreamReader(this.client.getInputStream()));
			this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(this.client.getOutputStream())),true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		start();
	}

	public void run() {
		 for(;;){
			try {
				String str = bins.readLine();
				if("exit".equalsIgnoreCase(str)) break;
				System.out.println(str);
				out.println("byebye");
			} catch (IOException e) {
				e.printStackTrace();
			}

		 }
	}

	
	
}
