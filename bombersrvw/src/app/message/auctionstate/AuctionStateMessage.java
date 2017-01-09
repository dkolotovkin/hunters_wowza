package app.message.auctionstate;

import app.message.Message;

public class AuctionStateMessage extends Message {
	public int passtime;
	public int winnerid;
	public int minbet;
	public int bank;
	
	public AuctionStateMessage(byte type, int roomId, int bank, int passtime, int winnerid, int minbet){
		super(type, roomId);
	
		this.passtime = passtime;
		this.winnerid = winnerid;
		this.minbet = minbet;
		this.bank = bank;
	}
}
