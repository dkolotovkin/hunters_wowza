package app.message.simple;

import app.message.Message;

public class MessageSimple extends Message {
	public String text;	
	public int fromId;
	public int toId;
	
	public MessageSimple(byte type, int roomId, String text, int fromId, int toId){
		super(type, roomId);
		this.text = text;
		this.fromId = fromId;
		this.toId = toId;
	}
}
