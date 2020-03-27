package Communication;

public class ServerData_Storage {
	public static final int SETDATA = 114;
	public static final int ACTDATA = 800;

	private static final int PDU_READ_DATA_BYTE_POS = 1;
	private static final int READ_INFO_BYTE = 2; //Function-Code + Read Data Size
	
	private static final int PDU_WRITE_DATA_FIRST_POS = 13;
	private static final int WRITE_FIRST_POS = 0;

	private static final int FUNCTION_CODE_BYTE = 1;
	private static final int DOUBLE_BYTE = 2;
	
	public byte [][] ServerData;

	public ServerData_Storage() {
		Init();
	}
	
	private void Init() {
		Create_ServerData();
	}
	
	private void Create_ServerData() {
		ServerData = new byte[2][1000];
		
		for(int Client=0; Client<2; Client++) {
			for(int DataPos=0; DataPos<1000; DataPos++) {
				if(Client == 0) {
					if(DataPos >= SETDATA && DataPos < ACTDATA) {
						if(100 + (Math.random() * 10) <= 0)
							ServerData[Client][DataPos] = (byte) 100;
						else
							ServerData[Client][DataPos] = (byte) (100 + (Math.random() * 10));
					} else if(DataPos >= ACTDATA) {
						if(200 + (Math.random() * 10) <= 0)
							ServerData[Client][DataPos] = (byte) 200;
						else
							ServerData[Client][DataPos] = (byte) (200 + (Math.random() * 10));
					} else {
						ServerData[Client][DataPos] = (byte) 10;
					}
				} else {
					if(DataPos >= SETDATA && DataPos < ACTDATA) {
						if(300 + (Math.random() * 10) <= 0)
							ServerData[Client][DataPos] = (byte) 300;
						else
							ServerData[Client][DataPos] = (byte) (300 + (Math.random() * 10));
					} else if(DataPos >= ACTDATA) {
						if(400 + (Math.random() * 10) <= 0)
							ServerData[Client][DataPos] = (byte) 400;
						else
							ServerData[Client][DataPos] = (byte) (400 + (Math.random() * 10));
					} else {
						ServerData[Client][DataPos] = (byte) 20;
					}
				}
			}
		}
	}
	
	
	public byte[] Request_Read_Processing(int StartAddress, int Length, int Mode) {
		byte[] CopyData = new byte[READ_INFO_BYTE+Length*DOUBLE_BYTE];
		
		System.arraycopy(ServerData[Mode], StartAddress, CopyData, READ_INFO_BYTE, Length*DOUBLE_BYTE);
		
		CopyData[PDU_READ_DATA_BYTE_POS] = (byte) (Length*DOUBLE_BYTE);
		
		return CopyData;
	}
	
	public byte[] Request_Write_Processing(int StartAddress, int Length, byte[] Request_WriteData, int Mode) {
		byte[] WriteData = new byte[Length+DOUBLE_BYTE];
		byte[] WritePDU = new byte[FUNCTION_CODE_BYTE+DOUBLE_BYTE+DOUBLE_BYTE];
		
		System.arraycopy(Request_WriteData, PDU_WRITE_DATA_FIRST_POS, WriteData, WRITE_FIRST_POS, Length+DOUBLE_BYTE);
		
		return WritePDU;
	}
}
