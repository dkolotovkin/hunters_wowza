package app.utils.gameaction.energy;
import app.utils.gameaction.GameAction;

public class GameActionEnoughEnergy extends GameAction {
	public int needEnergy;	
	
	public GameActionEnoughEnergy(byte type, int roomID, int needEnergy){
		super(type, roomID);
		this.needEnergy = needEnergy;				
	}
}
