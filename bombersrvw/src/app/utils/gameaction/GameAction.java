package app.utils.gameaction;

public class GameAction {
	public byte type;
	public int roomId;
	
	public GameAction(byte type, int roomID){
		this.type = type;
		this.roomId = roomID;
	}
}
