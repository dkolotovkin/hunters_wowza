package app.room.minigamebets;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import app.ServerApplication;
import app.room.Room;
import app.user.UserClient;
import app.utils.amfwobjectbuilder.AMFWObjectBuilder;
import app.utils.changeinfo.ChangeInfoParams;
import app.utils.gameaction.GameActionType;
import app.utils.gameaction.bets.MiniGameActionBetsContent;

public class MiniGameBetsRoom extends Room {	
	public Timer timer;
	public int waittime = 60;
	public int currenttime = 0;
	
	public List<MiniGameBetInfo> bets = new ArrayList<MiniGameBetInfo>();
	
	public MiniGameBetsRoom(int id, String title){
		super(id, title);
		
		setTimerToStart();
	}
	
	public void setTimerToStart(){
		timer = new Timer();
		timer.schedule(new TimerToStart(), 1000, 1000);
	}
	
	class TimerToStart extends TimerTask{
        public void run(){ 
        	currenttime++;
        	if (currenttime >= waittime){
        		endGame();
        	}
         }  
     }
	
	public int bet(UserClient u, int betSection, int bet){
		u.user.updateMoney(u.user.money - bet);
		ServerApplication.application.commonroom.changeUserInfoByID(u.user.id, ChangeInfoParams.USER_MONEY, u.user.money, 0);
		
		for(int i = 0; i < bets.size(); i++){
			if(bets.get(i).userid == u.user.id && bets.get(i).betsection == betSection){					
				bets.get(i).bet += bet;
				return MiniGameBetResult.OK;
			}
		}
		
		MiniGameBetInfo bi = new MiniGameBetInfo(u.user.id, betSection, bet);
		bets.add(bi);
		return MiniGameBetResult.OK;
	}	
	
	public void endGame(){
		if (timer != null){
			timer.cancel();
		}
		timer = null;		
		waittime = 60;
		currenttime = 0;
		setTimerToStart();
		
		//возвращенные ставки
		Map<Integer, Integer> returnbets = new HashMap<Integer, Integer>();
		//выйгравшие ставки
		Map<Integer, Integer> winnerbets = new HashMap<Integer, Integer>();
		
		int winnerSection = (int) Math.floor((float)Math.random() * 11) + 1;
		
		int maxwinbet = 0;
		int winbets = 0;
		for(int i = 0; i < bets.size(); i++){
			if(bets.get(i).betsection == winnerSection){					
				winbets += bets.get(i).bet;
				if(bets.get(i).bet > maxwinbet){
					maxwinbet = bets.get(i).bet;
				}
			}			
		}
		
		int allbets = 0;
		for(int i = 0; i < bets.size(); i++){
			if(bets.get(i).betsection != winnerSection){				
				if(bets.get(i).bet > maxwinbet){
					int delta = Math.max(0, (bets.get(i).bet - maxwinbet));
					
					if(returnbets.get(bets.get(i).userid) != null){
						int lastvalue = returnbets.get(bets.get(i).userid);
						returnbets.put(bets.get(i).userid, lastvalue + delta);						
					}else{
						returnbets.put(bets.get(i).userid, delta);
					}
					
					UserClient user = ServerApplication.application.commonroom.users.get(Integer.toString(bets.get(i).userid));
					if(user == null) user = users.get(Integer.toString(bets.get(i).userid));
					
					ServerApplication.application.userinfomanager.addMoney(bets.get(i).userid, delta, user);
					user = null;
					
					bets.get(i).bet = Math.min(bets.get(i).bet, maxwinbet);
				}
			}
			allbets += bets.get(i).bet;
		}
		
		int nalog = (int) Math.ceil((float) Math.max(0, (allbets - winbets)) * 0.15);
		allbets = allbets - nalog;			//налог
		
		int prize = 0;
		float percent;
		for(int i = 0; i < bets.size(); i++){
			if(bets.get(i).betsection == winnerSection){
				percent = (float) bets.get(i).bet / winbets;
				prize = (int) Math.floor((float) allbets * percent);
				
				if(winnerbets.get(bets.get(i).userid) != null){
					int lastvalue = winnerbets.get(bets.get(i).userid);
					winnerbets.put(bets.get(i).userid, lastvalue + prize);						
				}else{
					winnerbets.put(bets.get(i).userid, prize);
				}					
				
				UserClient user = ServerApplication.application.commonroom.users.get(Integer.toString(bets.get(i).userid));
				ServerApplication.application.userinfomanager.addMoney(bets.get(i).userid, prize, user);					
				user = null;
			}
		}
					
		//рассылаем всем сообщения
		Set<Entry<String, UserClient>> set = users.entrySet();
		for (Map.Entry<String, UserClient> user:set){
			if (user.getValue() != null && user.getValue().client != null && user.getValue().client.isConnected()) {					
				int returnmoney = 0;
				if(returnbets.get(user.getValue().user.id) != null){
					returnmoney = returnbets.get(user.getValue().user.id);
				}
				
				int prizemoney = 0;
				if(winnerbets.get(user.getValue().user.id) != null){
					prizemoney = winnerbets.get(user.getValue().user.id);
				}
				
				user.getValue().client.call("processGameAction", null, AMFWObjectBuilder.createObjMiniGameActionFinishBets(GameActionType.FINISH_BETS, this.id, returnmoney, prizemoney, winnerSection));
	    	}
		}
		set = null;
		
		returnbets.clear();
		winnerbets.clear();
		
		bets = new ArrayList<MiniGameBetInfo>();
		
		MiniGameActionBetsContent action = new MiniGameActionBetsContent(GameActionType.BETS_CONTENT, this.id);
		action.betsinfo = ServerApplication.application.gamemanager.minigamegetBetsInfo();
		action.time = waittime - currenttime;
		
		set = users.entrySet();			
		for (Map.Entry<String, UserClient> gu:set){
			if(gu.getValue().client.isConnected()){
				gu.getValue().client.call("processGameAction", null, AMFWObjectBuilder.createObjMiniGameActionBetsContent(action));
			}					
		}
		set = null;
		
		int minute = (int) Math.floor((float) waittime / 60);
		int second = waittime - minute * 60;
		
		sendSystemMessage("Призовая секция: " + winnerSection + ". Следующий розыгрыш состоится через " + minute + " минут(у) " + second + " секунд(ы).");
	}
}
