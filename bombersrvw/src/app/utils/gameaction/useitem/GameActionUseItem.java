package app.utils.gameaction.useitem;

import app.utils.gameaction.GameAction;

public class GameActionUseItem extends GameAction {		
	public int initiatorID;
	public int itemID;
	public int gwitemID;			//game world
	public int itemtype;
	public Float itemx;
	public Float itemy;
	
	public GameActionUseItem(byte type, int roomID, int itemID, int gwitemID, int itemtype, int initiatorID, Float itemx, Float itemy){
		super(type, roomID);		
		this.initiatorID = initiatorID;
		this.itemID = itemID;
		this.gwitemID = gwitemID;
		this.itemtype = itemtype;
		this.itemx = itemx;
		this.itemy = itemy;
	}
}
