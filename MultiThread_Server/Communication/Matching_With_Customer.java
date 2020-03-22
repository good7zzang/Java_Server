package Communication;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import Server.Server;

public class Matching_With_Customer implements Runnable {
	private static final int FunctionCode = 7;
	private static final int StartAddress = 8;
	private static final int Length = 11;
	private static final int READ_MODE = 0x03;
	private static final int WRITE_MODE = 0x06;

	private Boolean Communication_Stop;
	private Server ServerObj;
	private Socket JoinClient;
	private byte[] RequestPacket;

	public Matching_With_Customer(Server ServerLogic, Socket ClientSocket) {
		ServerObj = ServerLogic;
		JoinClient = ClientSocket;
		
		RequestPacket = new byte[12];
		Communication_Stop = true;
	}
	
	@Override
	public void run() {
		try {
			InputStream RecvMsg = JoinClient.getInputStream();
			BufferedInputStream RequestMsg = new BufferedInputStream(RecvMsg);
			
			OutputStream SendMsg = JoinClient.getOutputStream();
			BufferedOutputStream ResponseMsg = new BufferedOutputStream(SendMsg);

			while(Communication_Stop) {
				if(RequestMsg.read(RequestPacket) != -1) {
					switch(Integer.parseInt(Integer.toString(RequestPacket[FunctionCode]))) {
					case READ_MODE:
						System.out.println("Read Mode");
						
						byte[] SendData = new byte[RequestPacket[Length]];
						
						SendData = ServerObj.ServerData.Request_Data(114, 2, 0);

						break;
					case WRITE_MODE:
						System.out.println("Write Mode");
						break;
					default:
						System.out.println(Integer.toHexString(RequestPacket[FunctionCode]));
					}
				}
			}
			
			RecvMsg.close();
			ResponseMsg.close();
		} catch (IOException e) {
			System.out.println("Matching Disconnect");
			Communication_Stop = false;
			ServerObj.ClinetSocketClose(JoinClient);
		} 
	}
}
