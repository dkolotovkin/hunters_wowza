package app.room.auction;

import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;

import app.ServerApplication;
import app.message.MessageType;
import app.message.auctionstate.AuctionStateMessage;
import app.room.Room;
import app.user.UserClient;
import app.utils.amfwobjectbuilder.AMFWObjectBuilder;
import app.utils.changeinfo.ChangeInfoParams;

public class AuctionRoom extends Room {
	public Timer timer;
	
	public int minbet = 6;
	public int winnerid = 0;
	public int bank = 5;
	public int waittime = 30;
	public int currenttime = 0;
	public byte betcounts = 0;
	
	public AuctionRoom(int id, String title){
		super(id, title);		
		bank = 5;
		minbet = bank + 1;
		
		setTimerToStart();
	}
	
	public void setTimerToStart(){
		timer = new Timer();
		timer.schedule(new TimerToStart(), 1000, 1000);
	}
	
	public void endAuction(){
		currenttime = 0;
		
		if (timer != null){
			timer.cancel();
		}
		timer = null;
		setTimerToStart();
		
		if(betcounts > 2 || bank > 50){
			UserClient user = ServerApplication.application.commonroom.users.get(Integer.toString(winnerid));
			if(user != null){
				sendSystemMessage("Победитель аукциона: " + user.user.title + ". Победитель выйграл: " + bank + " монет.");				
				if(bank > 3000){
					ServerApplication.application.commonroom.sendSystemMessage("Победитель аукциона: " + user.user.title + ". Победитель выйграл: " + bank + " монет.");
				}
				
				if(user.client.isConnected()){
					user.client.call("processMassage", null, AMFWObjectBuilder.createObjMessageAuctionPrize(MessageType.AUCTION_PRIZE, this.id, bank));
				}
			}else{
				sendSystemMessage("Аукцион окончен. Победитель выйграл: " + bank + " монет.");
			}
			
			ServerApplication.application.userinfomanager.addMoney(winnerid, bank, user);
			user = null;
			
			bank = 5;
			minbet = bank + 1;			
			winnerid = 0;
			betcounts = 0;			
		}else{
			sendSystemMessage("Аукцион продолжается. Банк будет разыгран когда будет сделано минимум 3 ставки или банк составит более 50 монет.");
		}
		
		AuctionStateMessage msg = new AuctionStateMessage(MessageType.AUCTION_STATE, this.id, bank, (waittime - currenttime), winnerid, minbet);
		
		Set<Entry<String, UserClient>> set = users.entrySet();
		for (Map.Entry<String, UserClient> user:set){
			if(user.getValue().client.isConnected()){
				user.getValue().client.call("processMassage", null, AMFWObjectBuilder.createObjMessageAuctionState(msg));
			}
		}
		set = null;
		msg = null;
	}
	
	public int bet(UserClient u, int b){
		if(currenttime < waittime - 1){
			if(u != null){
				if(b >= minbet){
					if(u.user.money >= b){
						u.user.updateMoney(u.user.money - b);
						changeUserInfoByID(u.user.id, ChangeInfoParams.USER_MONEY, u.user.money, 0);
						
						int komission = (int) Math.ceil((float) b / 10);
						bank = Math.max(0, bank + b - komission);
						
						betcounts++;
						winnerid = u.user.id;
						minbet = b + 1;
						currenttime = 0;
						
						AuctionStateMessage message = new AuctionStateMessage(MessageType.AUCTION_STATE, this.id, bank, (waittime - currenttime), winnerid, minbet);		
						 
						Set<Entry<String, UserClient>> set = users.entrySet();
						for (Map.Entry<String, UserClient> user:set){					
							if(user.getValue().client.isConnected()){
								user.getValue().client.call("processMassage", null, AMFWObjectBuilder.createObjMessageAuctionState(message));
							}					
						}			
						set = null;
						
						return BetResult.OK;
					}else{
						return BetResult.NO_MONEY;
					}
				}else{
					return BetResult.OTHER; 
				}
			}else{
				return BetResult.NO_USER;  
			}
		}
		return BetResult.OTHER;  
	}
	
	public void addUser(UserClient u){		
		super.addUser(u);
		
		AuctionStateMessage message = new AuctionStateMessage(MessageType.AUCTION_STATE, this.id, bank, (waittime - currenttime), winnerid, minbet);		
		
		if(u.client.isConnected()){
			u.client.call("processMassage", null, AMFWObjectBuilder.createObjMessageAuctionState(message));
		}
		message = null;
	}
	
	class TimerToStart extends TimerTask{
        public void run(){ 
        	currenttime++;
        	if (currenttime >= waittime){
        		endAuction();
        	}
         }  
     }
}
