package app.utils.gameaction.updateitems;

import java.util.ArrayList;

import app.utils.gameaction.GameAction;

public class GameActionUpdateItems extends GameAction {
	public ArrayList<Object> items;
	
	public GameActionUpdateItems(byte type, int roomID, ArrayList<Object> items){
		super(type, roomID);
		this.items = items;
	}
}
