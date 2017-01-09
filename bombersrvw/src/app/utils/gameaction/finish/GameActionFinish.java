package app.utils.gameaction.finish;

import app.utils.extraction.ExtractionData;
import app.utils.gameaction.GameAction;

public class GameActionFinish extends GameAction {
	public ExtractionData extraction;
	public byte position;
	
	public GameActionFinish(byte type, int roomID, ExtractionData extraction, byte position){
		super(type, roomID);
		this.extraction = extraction;
		this.position = position;
	}
}
