package app.utils.random;

import app.ServerApplication;
import app.game.GameManager;
import app.room.Room;

public class RoomRandom {
	public static int maxRoomID = 100000;
	
	public static int getRoomID(){
		int rID = 0;
		Room r;
		Boolean good = false;
		do{
			rID = Math.round((float)Math.random() * maxRoomID);			
			r = ServerApplication.application.rooms.get(Integer.toString(rID));
			if (r != null) good = false;
			else good = true;
			r = GameManager.gamerooms.get(Integer.toString(rID));
			if (r != null) good = false;
			else good = true;			
		}while(!good);
		
		return rID;
	}
	
	public static int getRandomFromTo(int from, int to){
		int random = 0;
		do{
			random = Math.round((float)Math.random() * to);			
						
		}while(random < from);
		
		return random;
	}
}
