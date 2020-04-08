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

	/** Server Connect Info **/
	private String ServerIP;
	private int ServerPort;
	
	public ServerListening ClientListner;   //client connect Listener ��ü
	public ExecutorService ThreadPool;      //Thread Pool ��ü
	public List<Socket> ClientList;         //client socket ���� ��ü
	public ServerSocket Server;				//Server Socket ��ü
	public ServerData_Storage ServerData;
	
	/**
	 * Server �ν��Ͻ� ������
	 */
	public Server() {
		Init();
	}
	
	/**
	 * Server�� ������Ű�� ���� �ʱ�ȭ �޼ҵ�
	 * 
	 * CreateThreadPool : ������ ���� �޼ҵ�
	 * ServerInfo 		: Server �������� ���� �޼ҵ�
	 * ServerBinding 	: Server Socket Binding �޼ҵ�
	 */
	private void Init() {
		ClientList = new ArrayList<Socket>();   //client socket ���� List ����
		
		CreateServerData();
		CreateThreadPool();
		ServerInfo();
		ServerBinding();
	}
	
	private void CreateServerData() {
		ServerData = new ServerData_Storage();
	}

	/**
	 * Server�� ������ Client Request ������ ���� ó���ϱ� ���� ������ ����
	 */
	private void CreateThreadPool() {
		ThreadPool = Executors.newFixedThreadPool(50);
	}
	
	/**
	 * Server ���� ���� IP Address, Port ���� ���� �޼ҵ�
	 */
	private void ServerInfo() {
		ServerIP = "127.0.0.1";
		ServerPort = 502;
	}
	
	/**
	 * Server Socket Binding �ϱ� ���� �޼ҵ�
	 * 
	 * @exception Binding ���н�
	 */
	private void ServerBinding() {
		try {
			Server = new ServerSocket();
			
			Server.bind(new InetSocketAddress(ServerIP, ServerPort));
			
			ClientListner = new ServerListening(this);
			
			ThreadPool.submit(ClientListner);
		} catch (Exception e) {
			System.out.println("Server Not Binding...");
		}	
	}
	
	/**
	 * Server�� ���ӵ� Client Socket Disconnect�� �ش� Client Socket Descriptor ���� �޼ҵ�
	 * 
	 * @param RemoveSocket
	 * @exception List���� Socket ���� ������
	 */
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
		
		/** Server �ν��Ͻ� ���� **/
		Server MutiServer = new Server();
	}
}
