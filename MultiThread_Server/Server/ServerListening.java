package Server;

import java.net.ServerSocket;
import java.net.Socket;

import Communication.Matching_With_Customer;

public class ServerListening implements Runnable{
	private ServerSocket Server;
	private Boolean Listening_Flag;
	private Server ServerObj;
	
	public  ServerListening(Server ServerLogic, ServerSocket MainServer) {
		ServerObj = ServerLogic;
		Server = MainServer;
		
		Init();
	}

	private void Init() {
		Listening_Flag = true;
	}

	@Override
	public void run() {
		System.out.println("Server Listening....");
		
		while(Listening_Flag) {
			try {
				Socket ClientSocket = Server.accept();
				ClientSocket.setSoTimeout(10000);			//Read Timeout 10√ 
				
				ServerObj.ClientList.add(ClientSocket);
				
				Matching_With_Customer Join = new Matching_With_Customer(ServerObj, ClientSocket);
				
				ServerObj.ThreadPool.submit(Join);
			} catch (Exception e) {
				System.out.println("Client Not Accept");
			}
		}
	}
}
