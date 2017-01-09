package app.message.status;

import app.message.Message;
import app.room.Room;

public class MessageOutStatus extends Message{
	public int initiatorId;	
	
	public MessageOutStatus(byte type, Room room, int initiatorId){
		super(type, room.id);
		this.initiatorId = initiatorId;
	}
}
