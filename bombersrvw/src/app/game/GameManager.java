package app.game;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import app.Config;
import app.ServerApplication;
import app.room.Room;
import app.room.game.GameRoom;
import app.room.minigamebets.MiniGameBetResult;
import app.room.minigamebets.MiniGameBetsInfo;
import app.user.UserClient;
import app.utils.amfwobjectbuilder.AMFWObjectBuilder;
import app.utils.gameaction.GameActionType;
import app.utils.gameaction.bets.MiniGameActionBetsContent;
import app.utils.random.RoomRandom;

import com.wowza.wms.amf.AMFDataList;
import com.wowza.wms.client.IClient;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.request.RequestFunction;

public class GameManager extends ModuleBase {
	public static Map<String, String> maps;
	public static Map<String, Integer> mapssrcs;
	public static Map<String, Integer> mapsseats;
	public static Map<String, String> mapscreators;
	public static Map<String, GameRoom> gamerooms;
	public static GameRoom simplegame;
	
	public static File[] locations;
	
	public String pathtoxml = "locations/";
	
	public GameManager(){
		gamerooms = new ConcurrentHashMap<String, GameRoom>();
		maps = new HashMap<String, String>();	
		mapssrcs = new HashMap<String, Integer>();
		mapsseats = new HashMap<String, Integer>();
		mapscreators = new HashMap<String, String>();
		
		File directory = null;
		
		directory = new File(pathtoxml);
		if (directory.isDirectory()){			
			locations = directory.listFiles();			
		}
		directory = null;
	}
	
	//войти в игру(забег)
	public void gameStartRequest(IClient client, RequestFunction function, AMFDataList params){
		
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		boolean added = false;
		
		if(user != null && !checkUserInGame(user.user.id)){
			if(simplegame == null){
				simplegame = new GameRoom(RoomRandom.getRoomID(), "Игра", user.user.level);
				gamerooms.put(Integer.toString(simplegame.id), simplegame);
				added = simplegame.addwaituser(user);
			}else{
				added = simplegame.addwaituser(user);
			}
		}	
		user = null;
		
		//return GameActionWaitStart
		sendResult(client, params, AMFWObjectBuilder.createObjGameActionWaitStart(GameActionType.WAIT_START, 0, Config.waitTimeToStart(), added));
		return;
	}
	
	public boolean checkUserInGame(int userid){
		Set<Entry<String, GameRoom>> set = gamerooms.entrySet();
		for (Map.Entry<String, GameRoom> room:set){
			if(room.getValue().waitusers.get(Integer.toString(userid)) != null) return true;
		}
		return false;
	}
	
	public void clearNewGameRoomByLevel(int level){
		simplegame = null;
	}	
	
	public void removeGameRoom(Room room){
		gamerooms.remove(Integer.toString(room.id));	
    }
	
	public void gameGotoleft(IClient client, RequestFunction function, AMFDataList params){
    	int roomID = getParamInt(params, PARAM1);
    	boolean down = getParamBoolean(params, PARAM2);
    	double userx = getParamDouble(params, PARAM3);
    	double usery = getParamDouble(params, PARAM4);
    	double lvx = getParamDouble(params, PARAM5);
    	double lvy = getParamDouble(params, PARAM6);
		GameRoom gameroom = gamerooms.get(Integer.toString(roomID));
		if (gameroom != null){
			gameroom.gotoleft(client, down, userx, usery, lvx, lvy);
		}
		gameroom = null;		
		
		//return void
		sendResult(client, params, true);   
	}
	
	public void gameGotoright(IClient client, RequestFunction function, AMFDataList params){
    	int roomID = getParamInt(params, PARAM1);
    	boolean down = getParamBoolean(params, PARAM2);
    	double userx = getParamDouble(params, PARAM3);
    	double usery = getParamDouble(params, PARAM4);
    	double lvx = getParamDouble(params, PARAM5);
    	double lvy = getParamDouble(params, PARAM6);
    	
		GameRoom gameroom = gamerooms.get(Integer.toString(roomID));
		if (gameroom != null){
			gameroom.gotoright(client, down, userx, usery, lvx, lvy);
		}
		gameroom = null;
		
		//return void
		sendResult(client, params, true);  
	}	
	
	public void gameJump(IClient client, RequestFunction function, AMFDataList params){
    	int roomID = getParamInt(params, PARAM1);
    	boolean down = getParamBoolean(params, PARAM2);
    	double userx = getParamDouble(params, PARAM3);
    	double usery = getParamDouble(params, PARAM4);
    	double lvx = getParamDouble(params, PARAM5);
    	double lvy = getParamDouble(params, PARAM6);
    	
		GameRoom gameroom = gamerooms.get(Integer.toString(roomID));
		if (gameroom != null){
			gameroom.jump(client, down, userx, usery, lvx, lvy);
		}
		gameroom = null;
		
		//return void
		sendResult(client, params, true);  
	}
	
	public void moveTarget(IClient client, RequestFunction function, AMFDataList params){
    	int roomID = getParamInt(params, PARAM1);
    	double tx = getParamDouble(params, PARAM2);
    	double ty = getParamDouble(params, PARAM3);
    	
		GameRoom gameroom = gamerooms.get(Integer.toString(roomID));
		if (gameroom != null){
			gameroom.moveTarget(client, tx, ty);
		}
		gameroom = null;
		
		//return void
		sendResult(client, params, true);  
	}
	
	public void shot(IClient client, RequestFunction function, AMFDataList params){
    	int roomID = getParamInt(params, PARAM1);
    	double sx = getParamDouble(params, PARAM2);
    	double sy = getParamDouble(params, PARAM3);
    	
		GameRoom gameroom = gamerooms.get(Integer.toString(roomID));
		if (gameroom != null){
			gameroom.shot(client, sx, sy);
		}
		gameroom = null;
		
		//return void
		sendResult(client, params, true);  
	}
	
	public void dodge(IClient client, RequestFunction function, AMFDataList params){
    	int roomID = getParamInt(params, PARAM1);
    	double sx = getParamDouble(params, PARAM2);
    	double sy = getParamDouble(params, PARAM3);
    	
		GameRoom gameroom = gamerooms.get(Integer.toString(roomID));
		if (gameroom != null){
			gameroom.dodge(client, sx, sy);
		}
		gameroom = null;
		
		//return void
		sendResult(client, params, true);  
	}
	
	public void wound(IClient client, RequestFunction function, AMFDataList params){
    	int roomID = getParamInt(params, PARAM1);
    	
		GameRoom gameroom = gamerooms.get(Integer.toString(roomID));
		if (gameroom != null){
			gameroom.wound(client);
		}
		gameroom = null;
		
		//return void
		sendResult(client, params, true);  
	}
	
	public void gameGetBullet(IClient client, RequestFunction function, AMFDataList params){
    	int roomID = getParamInt(params, PARAM1);    	
    	int sourceId = getParamInt(params, PARAM2);
    	
		GameRoom gameroom = gamerooms.get(Integer.toString(roomID));
		if (gameroom != null){
			gameroom.getBullet(client, sourceId);
		}
		gameroom = null;
		
		//return void
		sendResult(client, params, true);  
	}
	
	public void gameGetSource(IClient client, RequestFunction function, AMFDataList params){
    	int roomID = getParamInt(params, PARAM1);    	
    	int sourceId = getParamInt(params, PARAM2);
    	
		GameRoom gameroom = gamerooms.get(Integer.toString(roomID));;
		if (gameroom != null){
			gameroom.getSource(client, sourceId);
		}
		gameroom = null;
		
		//return void
		sendResult(client, params, true);  
	}
	
	public void gameUserout(IClient client, RequestFunction function, AMFDataList params){
    	int roomID = getParamInt(params, PARAM1);
    	
		GameRoom gameroom = gamerooms.get(Integer.toString(roomID));;
		if (gameroom != null){
			gameroom.userout(client);
		}
		gameroom = null;
		
		//return void
		sendResult(client, params, true);  
	}
	
	public void gameUserexit(IClient client, RequestFunction function, AMFDataList params){
    	int roomID = getParamInt(params, PARAM1);
    	
		GameRoom gameroom = gamerooms.get(Integer.toString(roomID));;
		if (gameroom != null){
			gameroom.userexit(client);
		}
		gameroom = null;
		
		//return void
		sendResult(client, params, true);  
	}
	
	public void selectGun(IClient client, RequestFunction function, AMFDataList params){
		int roomID = getParamInt(params, PARAM1);
		int gunID = getParamInt(params, PARAM2);
		GameRoom gameroom = gamerooms.get(Integer.toString(roomID));		
		if (gameroom != null){
			gameroom.selectGun(client, gunID);
		}
		gameroom = null;
	}
	
	
	
	
	
	
	/*******************************************КОМАНДЫ КОМНАТЫ ИГРЫ НА СТАВКИ*****************************************/
	//войти в комнату игры на ставки
	public void minigameAddToBetsGame(IClient client, RequestFunction function, AMFDataList params){    	
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));		
		MiniGameActionBetsContent action = new MiniGameActionBetsContent(GameActionType.BETS_CONTENT, ServerApplication.application.betsroom.id);
		
		ServerApplication.application.betsroom.addUser(user);
		action.betsinfo = minigamegetBetsInfo();
		action.time = ServerApplication.application.betsroom.waittime - ServerApplication.application.betsroom.currenttime;
		
		user = null;
		
		//return GameActionBetsContent
		sendResult(client, params, AMFWObjectBuilder.createObjMiniGameActionBetsContent(action));
		return;
	}
	
	//получить информацию (участники и ставки) о игре на ставки
	public MiniGameBetsInfo minigamegetBetsInfo(){
		MiniGameBetsInfo binfo = new MiniGameBetsInfo();
		
		Set<Entry<String, UserClient>> set = ServerApplication.application.betsroom.users.entrySet();
		for (Map.Entry<String, UserClient> user:set){
			binfo.users.add(user.getValue().user.id);
		}	
		set = null;
		
		binfo.bets = ServerApplication.application.betsroom.bets;		
		return binfo;
	}
	public void minigameGetBetsInfo(IClient client, RequestFunction function, AMFDataList params){		
		//return BetsInfo
		sendResult(client, params, AMFWObjectBuilder.createObjMiniGameBetsInfo(minigamegetBetsInfo()));
		return;
	}
	//сделать ставку
	public void minigameBet(IClient client, RequestFunction function, AMFDataList params){
		int betSection = getParamInt(params, PARAM1);
		int bet = getParamInt(params, PARAM2);
		
		MiniGameActionBetsContent action = new MiniGameActionBetsContent(GameActionType.BETS_CONTENT, ServerApplication.application.betsroom.id);
		
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		
		if(user != null && betSection > 0 && betSection < 13){
			if(user.user.money >= bet && bet > 0){					
				action.result = (byte) ServerApplication.application.betsroom.bet(user, betSection, bet);
				action.betsinfo = minigamegetBetsInfo();
				action.time = ServerApplication.application.betsroom.waittime - ServerApplication.application.betsroom.currenttime;
				
				Set<Entry<String, UserClient>> set = ServerApplication.application.betsroom.users.entrySet();			
				for (Map.Entry<String, UserClient> gu:set){
					if(gu.getValue().client.isConnected()){
						gu.getValue().client.call("processGameAction", null, AMFWObjectBuilder.createObjMiniGameActionBetsContent(action));
					}					
				}				
			}else{
				action.result = MiniGameBetResult.NO_MONEY;
			}
		}else{
			action.result = MiniGameBetResult.OTHER;
		}
		
		user = null;
		
		//return byte
		sendResult(client, params, action.result);
		return;
	}
	//выйти из игры на ставки
	public void minigameExitBetsGame(IClient client, RequestFunction function, AMFDataList params){
		
		boolean result = false;
		UserClient u = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		
		ServerApplication.application.betsroom.removeUserByClientID(Integer.toString(u.client.getClientId()));
		u = null;
		
		//return boolean
		sendResult(client, params, result);
		return;
	}
}
