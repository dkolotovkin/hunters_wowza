package app.utils.gameaction.start;

import app.utils.gameaction.GameAction;

public class GameActionWaitStart extends GameAction {
	public int waittime;
	public Boolean added = false;
	
	public GameActionWaitStart(byte type, int roomID, int waittime){
		super(type, roomID);
		this.waittime = waittime;		
	}
}
