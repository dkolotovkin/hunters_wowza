package app.message.betgame;

import app.message.Message;

public class BetGameRequestMessage extends Message {
	public int userid;
	
	public BetGameRequestMessage(byte type, int roomId, int userid){
		super(type, roomId);
	
		this.userid = userid;
	}
}
