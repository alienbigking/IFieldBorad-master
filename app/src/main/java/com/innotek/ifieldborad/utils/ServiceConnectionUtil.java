package com.innotek.ifieldborad.utils;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ServiceConnectionUtil {
	public static final String SERVER_ADDRESS = "http://192.168.0.126:8080";
	public static final int port = 8088;
	public static final String SERVER_NAMESPACE = "http://service.InfoManage.com/";
		
	
	public static boolean isOnline(Context context) throws UnknownHostException{
	    ConnectivityManager connMgr = (ConnectivityManager)context
	    		.getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
	    
			return networkInfo != null && networkInfo.isConnected();// &&
//					isResolvable(SERVER_ADDRESS) && 
//					isConnect(InetAddress.getByName(SERVER_ADDRESS), 8088);

	} 
	
	
	private static boolean isResolvable(String hostname){
		try{
			InetAddress.getAllByName(hostname);
			return true;
		}catch(UnknownHostException e){
			return false;
		}
	}
	
	private static boolean isConnect(InetAddress address, int port) throws UnknownHostException{
		Socket socket = new Socket();
		SocketAddress socketAddress = new InetSocketAddress(address ,port);
		try{
			socket.connect(socketAddress, 2000);
		}catch (IOException e) {
			e.printStackTrace();
		}
		finally{
			if(socket.isConnected()){
				try{
					socket.close();
				}catch(IOException e){
					e.printStackTrace();
				}
			}
		}
		return true;
	}
	
}
