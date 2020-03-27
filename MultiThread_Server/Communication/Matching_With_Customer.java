package Communication;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import Server.Server;

public class Matching_With_Customer implements Runnable {
	private static final int NON_PACKETSIZE = -1;
	/*Packet Position*/
	private static final int FUNCTION_CODE = 7;
	private static final int REQUEST_START_ADDRESS_UP = 8;
	private static final int REQUEST_START_ADDRESS_DOWN = 9;
	private static final int REQUEST_SIZE_UP = 10;
	private static final int REQUEST_SIZE_DOWN = 11;
	
	private static final int REQUEST_WRITE_BYTE = 12;
	
	private static final int ERROR_INFO_SIZE = 2;

	private static final int MAX_BYTE_SIZE = 65535;
	private static final byte MAX_READ_SIZE = 0x7D;
	private static final byte MAX_WRITE_SIZE = 0x7B;
	
	private static final byte EXCEPTION_NUMBER_1 = 0x01;
	private static final byte EXCEPTION_NUMBER_2 = 0x02;
	private static final byte EXCEPTION_NUMBER_3 = 0x03;
	
	private static final byte ERROR_RESPONSE_REGESTERS = 0x00;
	private static final byte READ_HOLDING_REGESTERS = 0x03;
	private static final byte WRITE_MULTIFUL_REGESTERS = 0x10;

	private Boolean Communication_Stop;
	private Server ServerObj;
	private Socket JoinClient;
	private ResponsePacket Rsp_Packet;
	private DataInputStream RequestMsg;
	private DataOutputStream ResponseMsg;

	public byte[] RequestPacket;

	public Matching_With_Customer(Server ServerLogic, Socket ClientSocket) {
		ServerObj = ServerLogic;
		JoinClient = ClientSocket;
		
		RequestPacket = new byte[261]; 
		Communication_Stop = true;
	}
	
	@Override
	public void run() {
		try {
			Rsp_Packet = new ResponsePacket(this);
			
			InputStream RecvMsg = JoinClient.getInputStream();
			RequestMsg = new DataInputStream(RecvMsg);
			
			OutputStream SendMsg = JoinClient.getOutputStream();
			ResponseMsg = new DataOutputStream(SendMsg);

			while(Communication_Stop) {
				if(RequestMsg.read(RequestPacket) != NON_PACKETSIZE) {
					if(ErrorPacketCheck()) {	
						byte[] ResponsePacket = null;
						
						switch(RequestPacket[FUNCTION_CODE]&0xff) {
							case READ_HOLDING_REGESTERS:
								ResponsePacket = ServerObj.ServerData.Request_Read_Processing(Get_ConvertAddress(), Get_ConvertSize(), 0);
								break;
							case WRITE_MULTIFUL_REGESTERS:
								ResponsePacket = ServerObj.ServerData.Request_Write_Processing(Get_ConvertAddress(), Get_ConvertSize(), RequestPacket, 0);
								break;
						}
						
						if(ResponsePacket != null) {
							Response_To_Client(Rsp_Packet.Create_ResponsePacket((byte)(RequestPacket[FUNCTION_CODE]&0xff), ResponsePacket));
						}
					}
				}
			}
		} catch (IOException e) {
			System.out.println("Matching Disconnect");
			
			Communication_Stop = false;
			ServerObj.ClinetSocketClose(JoinClient);
		} finally{
			Disconnect_Match();
		}
	}
	
	private void Response_To_Client(byte[] ResponsePacket) throws IOException {	
		ResponseMsg.write(ResponsePacket);
	}
	
	private Boolean ErrorPacketCheck() throws IOException {
		Boolean ErrorCheck = false;
		byte RequestFunctionCode = 0;
		byte[] ErrorCode = new byte[ERROR_INFO_SIZE];
		
		RequestFunctionCode = (byte) (RequestPacket[FUNCTION_CODE]&0xFF);
			
		if(RequestFunctionCode != READ_HOLDING_REGESTERS && RequestFunctionCode != WRITE_MULTIFUL_REGESTERS) {
			ErrorCheck = true;
			ErrorCode[1] = EXCEPTION_NUMBER_1;
		} else {
			int Request_StratAddress = Get_ConvertAddress();
			int Request_Read_Size = Get_ConvertSize();
			
			switch(RequestFunctionCode) {
				case READ_HOLDING_REGESTERS:
					if(Request_Read_Size < 0 || Request_Read_Size > MAX_READ_SIZE) {
						ErrorCheck = true;
						ErrorCode[1] = EXCEPTION_NUMBER_3;
					}
					break;
				case WRITE_MULTIFUL_REGESTERS:
					if((Request_Read_Size < 0 || Request_Read_Size > MAX_WRITE_SIZE) || (RequestPacket[REQUEST_WRITE_BYTE] != Request_Read_Size*2)) {
						ErrorCheck = true;
						ErrorCode[1] = EXCEPTION_NUMBER_3;
					}
					break;
			}
			
			if(!ErrorCheck && (Request_StratAddress < 0 || Request_StratAddress > MAX_BYTE_SIZE) && (Request_StratAddress + Request_Read_Size > MAX_BYTE_SIZE)) {
				ErrorCheck = true;
				ErrorCode[1] = EXCEPTION_NUMBER_2;
			}
		}
		
		if(ErrorCheck) {
			byte[] ErrorResponsePacket;
			
			ErrorCode[0] = (byte) (0x80 + RequestPacket[FUNCTION_CODE]);
			
			ErrorResponsePacket = Rsp_Packet.Create_ResponsePacket(ERROR_RESPONSE_REGESTERS, ErrorCode);
			
			Response_To_Client(ErrorResponsePacket);
			
			return false;
		}
		
		return true;
	}
	
	private int Get_ConvertAddress() {
		return RequestPacket[REQUEST_START_ADDRESS_UP]<<8 | RequestPacket[REQUEST_START_ADDRESS_DOWN]&0xFF;
	}
	
	private int Get_ConvertSize() {
		return RequestPacket[REQUEST_SIZE_UP]<<8 | RequestPacket[REQUEST_SIZE_DOWN] & 0xFF;
	}
	
	private void Disconnect_Match(){
		try {
			RequestMsg.close();
			ResponseMsg.close();
		} catch (IOException e) {
			System.out.println("Stream Not Closed");
		}
	}
}
