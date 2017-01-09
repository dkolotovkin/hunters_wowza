package app.room.minigamebets;

public class MiniGameBetInfo {
	public int userid;
	public int bet;
	public int betsection;
	
	public MiniGameBetInfo(int userid, int betuserid, int bet){
		this.userid = userid;
		this.bet = bet;
		this.betsection = betuserid;
	}
}