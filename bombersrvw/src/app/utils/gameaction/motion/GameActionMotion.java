package app.utils.gameaction.motion;

import app.utils.gameaction.GameAction;

public class GameActionMotion extends GameAction {
	public byte subtype;
	public Boolean down;
	public int initiatorID;
	public Float userx;
	public Float usery;
	public Float lvx;
	public Float lvy;
	
	public GameActionMotion(byte type, int roomID, byte subtype, int initiatorID, Boolean down, Float userx, Float usery, Float lvx, Float lvy){
		super(type, roomID);
		this.subtype = subtype;
		this.initiatorID = initiatorID;
		this.down = down;
		this.userx = userx;
		this.usery = usery;
		this.lvx = lvx;
		this.lvy = lvy;
	}
}
