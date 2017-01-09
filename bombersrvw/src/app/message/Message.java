package app.message;
public class Message {
	public byte type;
	public int roomId;	
	
	public Message(byte type, int roomId){
		this.type = type;
		this.roomId = roomId;
	}
}
