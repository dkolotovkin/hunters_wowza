package app.message.changeinfo;

import app.message.Message;

public class MessageChangeInfo3 extends Message{
	public byte param;
	public int value1;
	public int value2;
	public int value3;
	
	public MessageChangeInfo3(byte type, int roomID, byte param, int value1, int value2, int value3){
		super(type, roomID);
		this.param = param;
		this.value1 = value1;
		this.value2 = value2;
		this.value3 = value3;
	}
}
