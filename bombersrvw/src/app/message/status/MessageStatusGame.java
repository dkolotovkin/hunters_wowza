package app.message.status;

import java.util.List;

import app.message.Message;
import app.room.Room;
import app.user.GameInfo;

public class MessageStatusGame extends Message{
	public GameInfo initiator;
	public String roomTitle;
	public List<Message> messages;
	public List<GameInfo> users;
	
	public MessageStatusGame(byte type, Room room, GameInfo initiator){
		super(type, room.id);
		this.roomTitle = room.title;
		this.initiator = initiator;
	}
}
