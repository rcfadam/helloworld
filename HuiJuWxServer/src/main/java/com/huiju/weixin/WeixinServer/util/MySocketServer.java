package com.huiju.weixin.WeixinServer.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

public class MySocketServer extends Thread {
   
	private Socket client;
	
	private BufferedReader bins;
	
	private PrintWriter out;


	public MySocketServer(Socket client) {
		super();
		this.client = client;
		try {
			this.bins = new BufferedReader(new InputStreamReader(client.getInputStream()));
			this.out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(client.getOutputStream())),true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		start();
	}

	@Override
	public void run() {
		try{
		  while(true){
			String str = this.bins.readLine();
			if("exit".equalsIgnoreCase(str)){
				break;
			}
		    System.out.println("In Server reveived the info: " + str);
            out.println(str);
		  }
		}catch(Exception e){
			
		}finally{
			try {
				client.close();
				
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
	}
	
	
	
}
