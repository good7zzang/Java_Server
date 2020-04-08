package Server;

import java.net.Socket;

import Communication.Matching_With_Customer;

public class ServerListening implements Runnable{
	private Boolean Listening_Flag;
	private Server ServerObj;
	
	/**
	 * ServerListening �ν��Ͻ� ������
	 * 
	 * @param ServerLogic : Server ��ü
	 */
	public  ServerListening(Server ServerLogic) {
		ServerObj = ServerLogic;	//Server ��ü ���� ����
		
		Listening_Flag = true;      //Server Listen ������ ���� Flag
	}

	@Override
	public void run() {
		System.out.println("Server Listening....");
		
		while(Listening_Flag) {
			try {
				Socket ClientSocket = ServerObj.Server.accept(); 
				ClientSocket.setSoTimeout(10000);				  									//Read Timeout 10��
				
				ServerObj.ClientList.add(ClientSocket);           									//Client Socket Descriptor �߰�
				
				Matching_With_Customer Join = new Matching_With_Customer(ServerObj, ClientSocket);  //Client�� Thread ��Ī �ν��Ͻ� ����
				
				ServerObj.ThreadPool.submit(Join);													//Client Communication Start
			} catch (Exception e) {
				System.out.println("Client Not Accept");
			}
		}
	}
}
