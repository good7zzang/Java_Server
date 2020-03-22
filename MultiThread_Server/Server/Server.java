package Server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Communication.ServerData_Storage;

public class Server {

	private String ServerIP;
	private int ServerPort;
	
	public ServerListening ClientListner;
	public ExecutorService ThreadPool;
	public List<Socket> ClientList;
	public ServerData_Storage ServerData;
	
	public Server() {
		Init();
	}
	
	private void Init() {
		ClientList = new ArrayList<Socket>();
		
		CreateServerData();
		CreateThreadPool();
		ServerInfo();
		ServerBinding();
	}
	
	private void CreateServerData() {
		ServerData = new ServerData_Storage();
	}

	private void CreateThreadPool() {
		ThreadPool = Executors.newFixedThreadPool(50);
	}
	
	private void ServerInfo() {
		ServerIP = "127.0.0.1";
		ServerPort = 502;
	}
	
	private void ServerBinding() {
		try {
			ServerSocket Server = new ServerSocket();
			
			Server.bind(new InetSocketAddress(ServerIP, ServerPort));
			
			ClientListner = new ServerListening(this, Server);
			
			ThreadPool.submit(ClientListner);
		} catch (Exception e) {
			System.out.println("Server Not Binding...");
		}	
	}
	
	public void ClinetSocketClose(Socket RemoveSocket) {
		try {
			RemoveSocket.close();
		} catch (IOException e) {
			System.out.println("Socket Remove Fail");
		}
		
		ClientList.remove(RemoveSocket);
	}
	
	public static void main(String[] args) {
		System.out.println("Multi Server Start!!!!");
		
		Server MutiServer = new Server();
	}

}
