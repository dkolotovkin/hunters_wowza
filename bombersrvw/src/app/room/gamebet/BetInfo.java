package app.room.gamebet;


public class BetInfo {
	public int userid;
	public int bet;
	public int betuserid;
	
	public BetInfo(int userid, int betuserid, int bet){
		this.userid = userid;
		this.bet = bet;
		this.betuserid = betuserid;
	}
}
