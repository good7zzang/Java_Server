package Communication;

public class ServerData_Storage {
	public static final int SETDATA = 114;
	public static final int ACTDATA = 800;
	
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
	
	public byte[] Request_Data(int StartAddress, int Length, int Client) {
		byte[] CopyData = new byte[Length];
		byte[] SendData = new byte[Length];
		
		System.arraycopy(ServerData[Client], StartAddress, CopyData, 0, Length);

		for(int Pos=0; Pos<CopyData.length; Pos++) {
			SendData[Pos] = (byte) Integer.parseInt(Integer.toHexString(CopyData[Pos]));
		}
		
		return SendData;
	}
}
