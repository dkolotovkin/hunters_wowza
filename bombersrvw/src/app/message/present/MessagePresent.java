package app.message.present;

import app.message.Message;

public class MessagePresent extends Message{
	public int prototypeid;	
	public int fromId;
	public int toId;
	
	public MessagePresent(byte type, int roomId, int prototypeid, int fromId, int toId){
		super(type, roomId);
		this.prototypeid = prototypeid;
		this.fromId = fromId;
		this.toId = toId;
	}
}
