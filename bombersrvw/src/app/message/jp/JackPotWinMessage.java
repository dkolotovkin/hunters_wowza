package app.message.jp;

import app.message.Message;

public class JackPotWinMessage extends Message {
	public int money = 0;
	public String winner;
	
	public JackPotWinMessage(byte type, int roomId, int money, String winner){
		super(type, roomId);
	
		this.money = money;
		this.winner = winner;
	}
}
