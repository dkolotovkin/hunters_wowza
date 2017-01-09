package app.message.status;

import java.util.List;

import app.message.Message;
import app.room.Room;
import app.user.ChatInfo;

public class MessageStatus extends Message {
	public ChatInfo initiator;
	public String roomTitle;
	public List<Message> messages;
	public List<ChatInfo> users;
	
	public MessageStatus(byte type, Room room, ChatInfo initiator){
		super(type, room.id);
		this.roomTitle = room.title;
		this.initiator = initiator;
	}
}
