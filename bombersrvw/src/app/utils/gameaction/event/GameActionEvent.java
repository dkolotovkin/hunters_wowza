package app.utils.gameaction.event;

import app.utils.gameaction.GameAction;

public class GameActionEvent extends GameAction {
	public byte subtype;	
	public int initiatorID;
	public String initiatorTitle;
	public byte position;
	public Float userx;
	public Float usery;
	
	public GameActionEvent(byte type, int roomID, byte subtype, int initiatorID, String initiatorTitle, byte position, Float userx, Float usery){
		super(type, roomID);
		this.subtype = subtype;
		this.initiatorID = initiatorID;
		this.initiatorTitle = initiatorTitle;
		this.position = position;
		this.userx = userx;
		this.usery = usery;
	}
}
