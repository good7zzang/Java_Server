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
	
	public ServerListening ClientListner;   //client connect Listener 객체
	public ExecutorService ThreadPool;      //Thread Pool 객체
	public List<Socket> ClientList;         //client socket 관리 객체
	public ServerSocket Server;				//Server Socket 객체
	public ServerData_Storage ServerData;
	
	/**
	 * Server 인스턴스 생성자
	 */
	public Server() {
		Init();
	}
	
	/**
	 * Server를 구동시키기 위한 초기화 메소드
	 * 
	 * CreateThreadPool : 쓰레드 생성 메소드
	 * ServerInfo 		: Server 접속정보 설정 메소드
	 * ServerBinding 	: Server Socket Binding 메소드
	 */
	private void Init() {
		ClientList = new ArrayList<Socket>();   //client socket 관리 List 생성
		
		CreateServerData();
		CreateThreadPool();
		ServerInfo();
		ServerBinding();
	}
	
	private void CreateServerData() {
		ServerData = new ServerData_Storage();
	}

	/**
	 * Server에 접속한 Client Request 응답을 병렬 처리하기 위한 쓰레드 생성
	 */
	private void CreateThreadPool() {
		ThreadPool = Executors.newFixedThreadPool(50);
	}
	
	/**
	 * Server 접속 관련 IP Address, Port 정보 설정 메소드
	 */
	private void ServerInfo() {
		ServerIP = "127.0.0.1";
		ServerPort = 502;
	}
	
	/**
	 * Server Socket Binding 하기 위한 메소드
	 * 
	 * @exception Binding 실패시
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
	 * Server에 접속된 Client Socket Disconnect시 해당 Client Socket Descriptor 삭제 메소드
	 * 
	 * @param RemoveSocket
	 * @exception List에서 Socket 삭제 오류시
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
		
		/** Server 인스턴스 생성 **/
		Server MutiServer = new Server();
	}
}
