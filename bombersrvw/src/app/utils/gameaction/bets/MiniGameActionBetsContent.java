package app.utils.gameaction.bets;
import app.room.minigamebets.MiniGameBetResult;
import app.room.minigamebets.MiniGameBetsInfo;
import app.utils.gameaction.GameAction;

public class MiniGameActionBetsContent extends GameAction {
	public MiniGameBetsInfo betsinfo;
	public byte result = MiniGameBetResult.OTHER;
	public int time;
	
	public MiniGameActionBetsContent(byte type, int roomID){
		super(type, roomID);
	}
}