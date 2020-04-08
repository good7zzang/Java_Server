package Server;

import java.net.Socket;

import Communication.Matching_With_Customer;

public class ServerListening implements Runnable{
	private Boolean Listening_Flag;
	private Server ServerObj;
	
	/**
	 * ServerListening 인스턴스 생성자
	 * 
	 * @param ServerLogic : Server 객체
	 */
	public  ServerListening(Server ServerLogic) {
		ServerObj = ServerLogic;	//Server 객체 변수 저장
		
		Listening_Flag = true;      //Server Listen 쓰레드 종료 Flag
	}

	@Override
	public void run() {
		System.out.println("Server Listening....");
		
		while(Listening_Flag) {
			try {
				Socket ClientSocket = ServerObj.Server.accept(); 
				ClientSocket.setSoTimeout(10000);				  									//Read Timeout 10초
				
				ServerObj.ClientList.add(ClientSocket);           									//Client Socket Descriptor 추가
				
				Matching_With_Customer Join = new Matching_With_Customer(ServerObj, ClientSocket);  //Client와 Thread 매칭 인스턴스 생성
				
				ServerObj.ThreadPool.submit(Join);													//Client Communication Start
			} catch (Exception e) {
				System.out.println("Client Not Accept");
			}
		}
	}
}
