package app.room.game;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import app.Config;
import app.ServerApplication;
import app.clan.ClanDeposit;
import app.clan.ClanRole;
import app.game.GameManager;
import app.message.MessageType;
import app.room.Room;
import app.shop.category.ShopCategory;
import app.shop.item.Item;
import app.user.GameInfo;
import app.user.User;
import app.user.UserClient;
import app.user.game.UserGame;
import app.user.game.UserGameAim;
import app.user.game.UserGameHunter;
import app.utils.amfwobjectbuilder.AMFWObjectBuilder;
import app.utils.changeinfo.ChangeInfoParams;
import app.utils.extraction.ExtractionData;
import app.utils.gameaction.GameActionSubType;
import app.utils.gameaction.GameActionType;
import app.utils.gameaction.start.GameActionStart;
import app.utils.gameaction.start.GameActionStartType;
import app.utils.random.RoomRandom;
import app.utils.xmltostring.XmlToStringBuilder;

import com.wowza.wms.amf.AMFDataObj;
import com.wowza.wms.client.IClient;

public class GameRoom extends Room {
	public Map<String, UserClient> waitusers;
	public Map<String, UserClient> outusers;				//те кто упали и след€т за происход€щим
	public Map<String, UserClient> exitusers;				//те кто сами вышли из игры и не след€т за происход€щим
	public Map<String, UserGameAim> aimusers;				//список игроков на которых охотимс€
	public Map<Integer, Integer> getsourceids;				//список id ресурсов, вз€тых игроками
	public Map<Integer, Integer> getbulletids;				//список id патронов, вз€тых игроками
	public UserGameHunter hunter;							//охотник
	
	public Timer timer;
	public Timer updateSoursesTimer;
	public int startCount = 0;
	public int srcsCount = 0;
	public int srcCollected = 0;
	public boolean gameisover = false;
	public GameRoom link;
	public Item gun;
	
	public int durationtime;
	public int passedtime = 0;
	public int creatorlevel = 0;
	
	//количество попаданий по белкам
	int damagePoints = 0;
	
	public GameRoom(int id, String title, int creatorlevel){
		super(id, title);
		
		this.creatorlevel = creatorlevel;
		link = this;
		outusers = new ConcurrentHashMap<String, UserClient>();
		exitusers = new ConcurrentHashMap<String, UserClient>();
		waitusers = new ConcurrentHashMap<String, UserClient>();
		aimusers = new ConcurrentHashMap<String, UserGameAim>();
		getbulletids = new ConcurrentHashMap<Integer, Integer>();
		getsourceids = new ConcurrentHashMap<Integer, Integer>();
		
		durationtime = Config.waitTimeToStart();
		setTimerToStart();
	}
	
	public void setTimerToStart(){
		timer = new Timer();
		timer.schedule(new TimerToStart(), 1000, 1000);
	}
	
	public Boolean addwaituser(UserClient u){	
		if(waitusers.size() < Config.maxUsersInGame()){
			if (waitusers.get(Integer.toString(u.user.id)) == null){
				waitusers.remove(Integer.toString(u.user.id));
			}
			
			waitusers.put(Integer.toString(u.user.id), u);	
			if (waitusers.size() == Config.maxUsersInGame()){
				startGame();
			}
		}else{
			return false;
		}
		return true;
	}
	
	public void startGame(){
		timer.cancel();
		
		damagePoints = 0;
		
		updateSoursesTimer = new Timer();
		updateSoursesTimer.schedule(new TimerUpdateSourses(), 2000, 2000);
		
		ServerApplication.application.gamemanager.clearNewGameRoomByLevel(creatorlevel);
		
		if (waitusers.size() >= Config.minUsersInGame()){			
			int hunterId = 0;
			int hunterCounter = 0;
			int hunterIndex = (int)Math.round((double) Math.random() * (waitusers.size() - 1));
			Set<Entry<String, UserClient>> setWait = waitusers.entrySet();
			for (Map.Entry<String, UserClient> user:setWait){
				//ищем охотника
				if(hunterCounter == hunterIndex) hunterId = user.getValue().user.id;
				hunterCounter++;
				
				addUser(user.getValue());				
			}
			setWait = null;			
			
			waitusers.clear();
			startCount = users.size();
			
			GameActionStart action = createGameActionStart();
			action.gametype = GameActionStartType.SIMPLE;
			action.hunterId = hunterId;
			AMFDataObj amfAction = AMFWObjectBuilder.createObjGameActionStart(action);
			setTimerToEnd(action.time);
			 
			UserGameAim aimuser;
			Set<Entry<String, UserClient>> set = users.entrySet();
			for (Map.Entry<String, UserClient> user:set){
				if(user.getValue().client.isConnected()){
					//заполн€ем роли игроков (цели и охотник)
					if(user.getValue().user.id != hunterId){
						aimuser = new UserGameAim(user.getValue(), 3);
						aimusers.put(new Integer(aimuser.user.user.id).toString(), aimuser);
					}else{
						hunter = new UserGameHunter(user.getValue());
						hunter.user.user.bullets = Math.max(0, hunter.user.user.bullets - 1);
					}
					
					user.getValue().client.call("processGameAction", null, amfAction);
				}
			}
			set = null;
			action = null;
			amfAction = null;
			aimuser = null;
		}else{
			AMFDataObj a = AMFWObjectBuilder.createObjGameAction(GameActionType.NOT_ENOUGH_USERS, this.id);
			Set<Entry<String, UserClient>> setWait = waitusers.entrySet();
			for (Map.Entry<String, UserClient> user:setWait){
				if(user.getValue().client.isConnected()){
					user.getValue().client.call("processGameAction", null, a);
				}
			}
			setWait = null;
			a = null;
			
			roomClear();
			ServerApplication.application.gamemanager.removeGameRoom(this);			
		}
	}	
	
	public void updateSourses(){
		double percent = (double) getsourceids.size() / srcsCount;
		double rnd = Math.random();
		if(rnd <= percent){			
			int sourceId = -1;
			int index = (int)Math.floor(Math.random() * getsourceids.size());
			int counter = 0;
			Set<Entry<Integer, Integer>> setSource = getsourceids.entrySet();
			for (Map.Entry<Integer, Integer> source:setSource){
				if(counter == index){
					sourceId = source.getValue();
					break;
				}
				counter++;
			}
			setSource = null;
			
			if(sourceId != -1){
				getsourceids.remove(sourceId);
				
				AMFDataObj amfAction = AMFWObjectBuilder.createObjGameActionEvent(GameActionType.ACTION, this.id, GameActionSubType.RESTORE_SOURCE, 0, sourceId);
				
				Set<Entry<String, UserClient>> set = users.entrySet();
				for (Map.Entry<String, UserClient> user:set){
					if(user.getValue().client != null && user.getValue().client.isConnected()){
						user.getValue().client.call("processGameAction", null, amfAction);
					}
				}
				set = null;
				amfAction = null;
			}
		}
	}
	
	
	public void setTimerToEnd(int time){
		timer = new Timer();
		timer.schedule(new TimerToEnd(), time * 1000, time * 1000);
	}
	
	public void endGame(){
		if (timer != null){
			timer.cancel();
		}
		timer = null;
		if (updateSoursesTimer != null){
			updateSoursesTimer.cancel();
		}
		updateSoursesTimer = null;
		gameisover = true;
		
		Map<Integer, ClanDeposit> clandeposits = new HashMap<Integer, ClanDeposit>();

		double koef = (double) startCount / Config.maxUsersInGame();
		int maxSorces = Math.max(srcCollected, srcsCount);
		
		//раздаем награды игрокам-цел€м
		UserGameAim aimuser;
		Set<Entry<String, UserGameAim>> set = aimusers.entrySet();
		for (Map.Entry<String, UserGameAim> user:set){
			aimuser = user.getValue();
			ExtractionData extraction = new ExtractionData();
			
			int experiencebonus = 0;
			int cexperiencebonus = 0;			
			for(int i = 0; i < aimuser.user.user.itemsArr.size(); i++){
				if(aimuser.user.user.itemsArr.get(i).onuser == 1 && aimuser.user.user.itemsArr.get(i).param1 == ShopCategory.THINGS){
					if(aimuser.user.user.itemsArr.get(i).quality > 0){
						experiencebonus += aimuser.user.user.itemsArr.get(i).param3;
						cexperiencebonus += aimuser.user.user.itemsArr.get(i).param4;
					}
					
					aimuser.user.user.itemsArr.get(i).quality = Math.max(aimuser.user.user.itemsArr.get(i).quality - 1, 0);
					aimuser.user.user.itemsArr.get(i).change = true;
				}
			}
			
			if(aimuser.countLife > 0){
				double percentCollected = (double) aimuser.countSource / maxSorces;
				
				extraction.money = (int) Math.round((double)((double) Config.moneyPrize() * percentCollected) * koef);
				extraction.bullets = aimuser.countBullet;
				extraction.experience = Config.experiencePrizeAim();
				extraction.experiencebonus = experiencebonus;
				extraction.cexperiencebonus = cexperiencebonus;
				
				if(aimuser.user.user.claninfo.clanid > 0 && aimuser.user.user.claninfo.clanrole > ClanRole.INVITED){
					extraction.cexperience = Config.experienceClanPrize();
					if (clandeposits.get(aimuser.user.user.claninfo.clanid) == null){
						ClanDeposit deposit = new ClanDeposit();
						deposit.depositm += 10;
						deposit.deposite += (extraction.cexperience + extraction.cexperiencebonus);
						clandeposits.put(aimuser.user.user.claninfo.clanid, deposit);
						deposit = null;
					}else{
						clandeposits.get(aimuser.user.user.claninfo.clanid).depositm += 1;
						clandeposits.get(aimuser.user.user.claninfo.clanid).deposite += (extraction.cexperience + extraction.cexperiencebonus);
					}
					aimuser.user.user.updateMoney(aimuser.user.user.money + extraction.money, 1, (extraction.cexperience + extraction.cexperiencebonus));										
				}else{
					aimuser.user.user.updateMoney(aimuser.user.user.money + extraction.money);
				}
				aimuser.user.user.updateExperience(aimuser.user.user.experience + extraction.experience + extraction.experiencebonus);
				aimuser.user.user.updateBullets(aimuser.user.user.bullets + extraction.bullets);
				
				changeUserInfoByID(aimuser.user.user.id, ChangeInfoParams.USER_MONEY_EXPERIENCE_BULLETS, aimuser.user.user.money, aimuser.user.user.experience, aimuser.user.user.bullets);
			}else{
				if(aimuser.exitFlag)
					extraction.experience = -4;
				else
					extraction.experience = -1;
				
				aimuser.user.user.updateExperience(aimuser.user.user.experience + extraction.experience + extraction.experiencebonus);
				changeUserInfoByID(aimuser.user.user.id, ChangeInfoParams.USER_EXPERIENCE, aimuser.user.user.experience, 0);
			}
			
			if (aimuser.user.client.isConnected()){
				aimuser.user.client.call("processGameAction", null, AMFWObjectBuilder.createObjGameActionFinish(GameActionType.FINISH, this.id, extraction));
				removeUserByClientID(Integer.toString(aimuser.user.client.getClientId()));
	    	}
			extraction = null;
			aimuser = null;
		}
		set = null;
		
		//отдаем награду охотнику	
		if(hunter != null){
			ExtractionData extraction = new ExtractionData();				
			
			for(int i = 0; i < hunter.user.user.itemsArr.size(); i++){
				if(hunter.user.user.itemsArr.get(i).onuser == 1 && hunter.user.user.itemsArr.get(i).param1 == ShopCategory.ARSENAL){						
					hunter.user.user.itemsArr.get(i).quality = Math.max(hunter.user.user.itemsArr.get(i).quality - 1, 0);
					hunter.user.user.itemsArr.get(i).change = true;
				}
			}
			
			if(hunter.countKilled > 0 || damagePoints > 0){
				int notCollectedCount = Math.max(0, srcsCount - srcCollected);
				double percentNotCollected = (double) notCollectedCount / maxSorces;
				
				if(hunter.countKilled > 0){
					extraction.money = (int) Math.round((double)((double) Config.moneyPrize() * percentNotCollected) * koef);
				}
				extraction.experience = hunter.countKilled * Config.experiencePrize() + damagePoints;
				
				if(hunter.countKilled > 0){
					if(hunter.user.user.claninfo.clanid > 0 && hunter.user.user.claninfo.clanrole > ClanRole.INVITED){
						extraction.cexperience = Config.experienceClanPrize();
						if (clandeposits.get(hunter.user.user.claninfo.clanid) == null){
							ClanDeposit deposit = new ClanDeposit();
							deposit.depositm += 1;
							deposit.deposite += (extraction.cexperience + extraction.cexperiencebonus);
							clandeposits.put(hunter.user.user.claninfo.clanid, deposit);
							deposit = null;
						}else{
							clandeposits.get(hunter.user.user.claninfo.clanid).depositm += 1;
							clandeposits.get(hunter.user.user.claninfo.clanid).deposite += (extraction.cexperience + extraction.cexperiencebonus);
						}
						hunter.user.user.updateMoney(hunter.user.user.money + extraction.money, 1, (extraction.cexperience + extraction.cexperiencebonus));										
					}else{
						hunter.user.user.updateMoney(hunter.user.user.money + extraction.money);
					}
				}
				hunter.user.user.updateExperience(hunter.user.user.experience + extraction.experience + extraction.experiencebonus);
				changeUserInfoByID(hunter.user.user.id, ChangeInfoParams.USER_MONEY_EXPERIENCE_BULLETS, hunter.user.user.money, hunter.user.user.experience, hunter.user.user.bullets);
			}
			
			if (hunter.user.client.isConnected()){
				hunter.user.client.call("processGameAction", null, AMFWObjectBuilder.createObjGameActionFinish(GameActionType.FINISH, this.id, extraction));
				removeUserByClientID(Integer.toString(hunter.user.client.getClientId()));
	    	}
			extraction = null;
		}
		
		ServerApplication.application.clanmanager.updateClanDeposits(clandeposits);
		clandeposits.clear();
		clandeposits = null;
		
		roomClear();
		ServerApplication.application.gamemanager.removeGameRoom(this);
	}	
	
	public GameActionStart createGameActionStart(){
		int time = 0;
		String creatorName = "";
		String locationXML = "";
		
		File locationFile = getLocationFile();		
		
		if(GameManager.maps.get(locationFile.toString()) == null){			
			Document document = null;
	        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	        factory.setValidating(false);
	        factory.setNamespaceAware(true);
	        try
	        {
	            DocumentBuilder builder = factory.newDocumentBuilder();
	            document = builder.parse(locationFile);
	            document.getDocumentElement().normalize();
	            NodeList nodelist = document.getDocumentElement().getElementsByTagName("src");
	            
	            GameManager.maps.put(locationFile.toString(), XmlToStringBuilder.getStringFromNode(document));
	            GameManager.mapssrcs.put(locationFile.toString(), nodelist.getLength());
	            
	            nodelist = document.getDocumentElement().getElementsByTagName("hero");
	            GameManager.mapsseats.put(locationFile.toString(), nodelist.getLength());
	            GameManager.mapscreators.put(locationFile.toString(), "");
	            
	            if(XmlToStringBuilder.getStringFromNode(document).indexOf("creator") > 0) {
	            	NodeList nodelistCreator = document.getDocumentElement().getElementsByTagName("creator");
		            Element fstNmElmntCreator = (Element) nodelistCreator.item(0);
		            int creatorId = new Integer(fstNmElmntCreator.getAttribute("id"));
		            if(creatorId > 0){
		    			Connection _sqlconnection = null;
		    	    	PreparedStatement find = null;
		    	    	ResultSet findRes = null;
		    	    	try {
		    		    	_sqlconnection = ServerApplication.application.sqlpool.getConnection();    		    		
		    	    		find = _sqlconnection.prepareStatement("SELECT title FROM user WHERE id=?");
		    	    		find.setInt(1, creatorId);
		    	    		findRes = find.executeQuery();    		
		    	    		if (findRes.next()){
		    	    			creatorName = findRes.getString("title");
		    	    			GameManager.mapscreators.put(locationFile.toString(), creatorName);
		    	    		}
		    	    	}catch (SQLException e) {			
		    				logger.error("Game room select creator error. creatorId: " + creatorId +  e.toString());
		    			} 
		    			finally
		    			{
		    			    try{
		    			    	if (_sqlconnection != null) _sqlconnection.close(); 
		    			    	if (find != null) find.close(); 
		    			    	if (findRes != null) findRes.close();		    	
		    			    	_sqlconnection = null;
		    			    	find = null;
		    			    	findRes = null;		    	
		    			    }
		    			    catch (SQLException sqlx) {     
		    			    }
		    			}
		    		}
				}
	         
	            builder = null;
	        }
	        catch (Exception e){
	        	logger.log("Error Parse XML From Loading LocationXML");
	        	return null;
	        }
	        factory = null;
	        document = null;
		}
		
		locationXML = GameManager.maps.get(locationFile.toString());
		srcsCount = GameManager.mapssrcs.get(locationFile.toString());
		creatorName = GameManager.mapscreators.get(locationFile.toString());
		int seatsCount = GameManager.mapsseats.get(locationFile.toString());
		
		//врем€ на забег от 40 секунд до 80
		int random = (int) Math.round(((float) Math.random() * 40));
		time = 40 + random;
        
		UserGame gameuser;
		List<Integer> pids;
		
        List<UserGame> gameusers = new ArrayList<UserGame>();
		Set<Entry<String, UserClient>> set = users.entrySet();
		for (Map.Entry<String, UserClient> user:set){
			gameuser = new UserGame();
			gameuser.id = user.getValue().user.id;
			gameuser.seatIndex = (int) Math.round(((float) Math.random() * (seatsCount - 1)));
			
			pids = new ArrayList<Integer>();
			for(int i = 0; i < user.getValue().user.itemsArr.size(); i++){
				if(user.getValue().user.itemsArr.get(i).onuser == 1 && user.getValue().user.itemsArr.get(i).quality > 0 && user.getValue().user.itemsArr.get(i).param1 == ShopCategory.THINGS){
					pids.add(user.getValue().user.itemsArr.get(i).pid);
				}
			}
			gameuser.pids = pids;
			
			gameusers.add(gameuser);
		}
		
        GameActionStart action = new GameActionStart(GameActionType.START, this.id, creatorName, gameusers, time, locationFile.toString(), locationXML);
        
        locationFile = null;
        gameusers = null;
        gameuser = null;
        pids = null;
        
        return action;
	}
	
	public File getLocationFile(){
		File locationFile = null;
		do{			
			locationFile = GameManager.locations[RoomRandom.getRandomFromTo(0, GameManager.locations.length - 1)];				
		}while(!locationFile.isFile());
		return locationFile;
	}
	
	@Override
	public void removeUserByClientID(String connID){
		if(!gameisover){
			UserClient user = getUserByClientID(connID);			
			if (user != null){
				if (waitusers.size() > 0){									//игра еще не началась
					waitusers.remove(Integer.toString(user.user.id));
				}else{
					if (exitusers.get(Integer.toString(user.user.id)) == null){
						exitusers.put(Integer.toString(user.user.id), user);
					}
					// + 1 = ситуаци€ когда охотник остаетс€ один
					if (outusers.size() + exitusers.size() + 1 == users.size()){
						endGame();
					}
				}
			}
			user = null;
		}else{
			super.removeUserByClientID(connID);
		}	
    }	
	
	@Override
	public UserClient getUserByClientID(String cID){
		String userID = userClientIDtoID.get(cID);
		if (userID != null && userID != "null"){
			return users.get(userID);
		}else{
			Set<Entry<String, UserClient>> setWait = waitusers.entrySet();
			for (Map.Entry<String, UserClient> user:setWait){				
				if(new Integer(user.getValue().client.getClientId()).toString().equals(cID)){
					return user.getValue();
				}				
			}
			setWait = null;
		}
		return null;
	}
	
	@Override
	public void roomClear(){
		waitusers.clear();
		aimusers.clear();
		outusers.clear();
		exitusers.clear();
		getsourceids.clear();
		getbulletids.clear();
		hunter = null;
		link = null;
		if(timer != null){
			timer.cancel();
		}
		timer = null;
		if(updateSoursesTimer != null){
			updateSoursesTimer.cancel();
		}
		updateSoursesTimer = null;
		
		super.roomClear();
    }
	
	@Override
	public void addUser(UserClient u){
		users.put(Integer.toString(u.user.id), u);
		if (u.client != null && u.client.isConnected()){
			userClientIDtoID.put(new Integer(u.client.getClientId()).toString(), Integer.toString(u.user.id));
		}
		userSocialIDtoID.put(u.user.idSocial, Integer.toString(u.user.id));
		
		AMFDataObj amfMessage = AMFWObjectBuilder.createObjMessageStatusGame(MessageType.USER_IN, this, GameInfo.createFromUser(u.user));
		List<GameInfo> usersinroom = new ArrayList<GameInfo>(); 
		
		Set<Entry<String, UserClient>> set = users.entrySet();
		for (Map.Entry<String, UserClient> user:set){
			usersinroom.add(GameInfo.createFromUser(user.getValue().user));
			if(user.getValue().user.id != u.user.id){
				if(user.getValue().client.isConnected()){
					user.getValue().client.call("processMassage", null, amfMessage);
				}
			}
		}
		
		amfMessage = null;
		amfMessage = AMFWObjectBuilder.createObjMessageStatusGame(MessageType.USER_IN, this, GameInfo.createFromUser(u.user), messages, usersinroom);
		
		if(u.client != null && u.client.isConnected()){
			u.client.call("processMassage", null, amfMessage);
		}
		
		usersinroom = null;
		amfMessage = null;
	}
	
	class TimerToStart extends TimerTask{
        public void run(){        	
        	passedtime++;
        	if (passedtime >= durationtime){
        		startGame();
        	}
         }  
     }
	class TimerToEnd extends TimerTask{
        public void run(){  
        	endGame();
        }
    }
	class TimerUpdateSourses extends TimerTask{
        public void run(){        	
        	updateSourses();
         }  
     }
	
	//ќЅ–јЅќ“„» » ѕќЋ№«ќ¬ј“я≈Ћ№— »’ —ќЅџ“»…
	public void gotoleft(IClient client, Boolean down, double userx, double usery, double lvx, double lvy){
		UserClient initiator = getUserByClientID(Integer.toString(client.getClientId()));
		if(initiator != null){
			
			AMFDataObj amfAction = AMFWObjectBuilder.createObjGameActionMotion(GameActionType.ACTION, this.id, GameActionSubType.GOTOLEFT, initiator.user.id, down, userx, usery, lvx, lvy);
		
			Set<Entry<String, UserClient>> set = users.entrySet();
			for (Map.Entry<String, UserClient> user:set){
				if(user.getValue().client != null && user.getValue().client.isConnected() && user.getValue().user.id != initiator.user.id){
					user.getValue().client.call("processGameAction", null, amfAction);
				}
			}
			set = null;
			initiator = null;
			amfAction = null;
		}
	}
	
	public void gotoright(IClient client, Boolean down, double userx, double usery, double lvx, double lvy){
		UserClient initiator = getUserByClientID(Integer.toString(client.getClientId()));
		if(initiator != null){			
			AMFDataObj amfAction = AMFWObjectBuilder.createObjGameActionMotion(GameActionType.ACTION, this.id, GameActionSubType.GOTORIGHT, initiator.user.id, down, userx, usery, lvx, lvy);		
			
			Set<Entry<String, UserClient>> set = users.entrySet();
			for (Map.Entry<String, UserClient> user:set){
				if(user.getValue().client != null && user.getValue().client.isConnected() && user.getValue().user.id != initiator.user.id){
					user.getValue().client.call("processGameAction", null, amfAction);
				}
			}
			set = null;
			initiator = null;
			amfAction = null;
		}
	}	
	public void jump(IClient client, Boolean down, double userx, double usery, double lvx, double lvy){
		UserClient initiator = getUserByClientID(Integer.toString(client.getClientId()));
		if(initiator != null){			
			AMFDataObj amfAction = AMFWObjectBuilder.createObjGameActionMotion(GameActionType.ACTION, this.id, GameActionSubType.JUMP, initiator.user.id, down, userx, usery, lvx, lvy);		
			
			Set<Entry<String, UserClient>> set = users.entrySet();
			for (Map.Entry<String, UserClient> user:set){
				if(user.getValue().client != null && user.getValue().client.isConnected() && user.getValue().user.id != initiator.user.id){
					user.getValue().client.call("processGameAction", null, amfAction);
				}
			}
			set = null;			
			initiator = null;
			amfAction = null;
		}
	}
	
	public void moveTarget(IClient client, double tx, double ty){
		UserClient initiator = getUserByClientID(Integer.toString(client.getClientId()));
		if(initiator != null && hunter != null && initiator.user.id == hunter.user.user.id){		
			AMFDataObj amfAction = AMFWObjectBuilder.createObjGameActionMotion(GameActionType.ACTION, this.id, GameActionSubType.MOVE_TARGET, initiator.user.id, tx, ty);		
			
			Set<Entry<String, UserClient>> set = users.entrySet();
			for (Map.Entry<String, UserClient> user:set){
				if(user.getValue().client != null && user.getValue().client.isConnected() && user.getValue().user.id != initiator.user.id){
					user.getValue().client.call("processGameAction", null, amfAction);
				}
			}
			set = null;
			initiator = null;
			amfAction = null;
		}
	}
	
	public void shot(IClient client, double sx, double sy){
		UserClient initiator = getUserByClientID(Integer.toString(client.getClientId()));
		if(initiator != null && hunter != null && initiator.user.id == hunter.user.user.id){
			int needBullets = 0;
			if(gun != null){
				needBullets = 1;
			}
			if(initiator.user.bullets >= needBullets){
				initiator.user.bullets = Math.max(0, initiator.user.bullets - needBullets);
				
				AMFDataObj amfAction = AMFWObjectBuilder.createObjGameActionMotion(GameActionType.ACTION, this.id, GameActionSubType.SHOT, initiator.user.id, sx, sy);		
				
				Set<Entry<String, UserClient>> set = users.entrySet();
				for (Map.Entry<String, UserClient> user:set){
					if(user.getValue().client != null && user.getValue().client.isConnected() && user.getValue().user.id != hunter.user.user.id){
						user.getValue().client.call("processGameAction", null, amfAction);
					}
				}
				set = null;
				
				amfAction = AMFWObjectBuilder.createObjGameActionHunterShot(GameActionType.ACTION, this.id, GameActionSubType.HUNTER_SHOT, initiator.user.id, sx, sy, initiator.user.bullets);
				if(initiator.client != null && initiator.client.isConnected()){
					initiator.client.call("processGameAction", null, amfAction);
				}
				
				amfAction = null;
			}			
			
			initiator = null;
		}
	}
	
	public void dodge(IClient client, double sx, double sy){
		UserClient initiator = getUserByClientID(Integer.toString(client.getClientId()));
		if(initiator != null && hunter != null){		
			AMFDataObj amfAction = AMFWObjectBuilder.createObjGameActionMotion(GameActionType.ACTION, this.id, GameActionSubType.DODGE, initiator.user.id, sx, sy);		
			
			Set<Entry<String, UserClient>> set = users.entrySet();
			for (Map.Entry<String, UserClient> user:set){
				if(user.getValue().client != null && user.getValue().client.isConnected()){
					user.getValue().client.call("processGameAction", null, amfAction);
				}
			}
			set = null;
			initiator = null;
			amfAction = null;
		}
	}
	
	public void wound(IClient client){
		UserClient initiator = getUserByClientID(Integer.toString(client.getClientId()));
		UserGameAim aiminitiator = aimusers.get(Integer.toString(initiator.user.id));
		if(aiminitiator != null && hunter != null && aiminitiator.countLife > 0){
			int damage = 1;
			if(gun != null) damage = gun.param3;
			aiminitiator.countLife = Math.max(0, aiminitiator.countLife - damage);
			
			AMFDataObj amfAction = AMFWObjectBuilder.createObjGameActionEvent(GameActionType.ACTION, this.id, GameActionSubType.WOUND, aiminitiator.user.user.id, aiminitiator.countLife);
			
			Set<Entry<String, UserClient>> set = users.entrySet();
			for (Map.Entry<String, UserClient> user:set){
				if(user.getValue().client != null && user.getValue().client.isConnected()){
					user.getValue().client.call("processGameAction", null, amfAction);
				}
			}
			set = null;
			amfAction = null;
			
			if(aiminitiator.countLife == 0){
				hunter.countKilled++;
				if (outusers.get(Integer.toString(aiminitiator.user.user.id)) == null){
					outusers.put(Integer.toString(aiminitiator.user.user.id), aiminitiator.user);
				}				
				if (outusers.size() + exitusers.size() + 1 == users.size()){
					endGame();
				}
			}else{
				damagePoints+=Config.experienceWoundBonus();
			}
			initiator = null;
			aiminitiator = null;
		}
	}
	
	public void getBullet(IClient client, int sourceId){
		UserClient initiatorConn = getUserByClientID(Integer.toString(client.getClientId()));
		if (initiatorConn != null ){
			User initiator = initiatorConn.user;
			UserGameAim aimuser = aimusers.get(Integer.toString(initiator.id));
			
			if(aimuser != null && getbulletids.get(sourceId) == null){
				getbulletids.put(sourceId, sourceId);
				aimuser.countBullet++;
				
				AMFDataObj amfAction = AMFWObjectBuilder.createObjGameActionEvent(GameActionType.ACTION, this.id, GameActionSubType.GET_BULLET, initiator.id, sourceId);
				
				Set<Entry<String, UserClient>> set = users.entrySet();
				for (Map.Entry<String, UserClient> user:set){
					if(user.getValue().client != null && user.getValue().client.isConnected()){
						user.getValue().client.call("processGameAction", null, amfAction);
					}
				}
				set = null;
				amfAction = null;
			}
			initiator = null;
			initiatorConn = null;
		}		
	}
	
	public void getSource(IClient client, int sourceId){
		UserClient initiatorConn = getUserByClientID(Integer.toString(client.getClientId()));
		if (initiatorConn != null ){
			User initiator = initiatorConn.user;
			UserGameAim aimuser = aimusers.get(Integer.toString(initiator.id));
			if(aimuser != null && getsourceids.get(sourceId) == null){
				getsourceids.put(sourceId, sourceId);
				srcCollected++;
				aimuser.countSource++;
				AMFDataObj amfAction = AMFWObjectBuilder.createObjGameActionEvent(GameActionType.ACTION, this.id, GameActionSubType.GET_SOURCE, initiator.id, sourceId);
				
				Set<Entry<String, UserClient>> set = users.entrySet();
				for (Map.Entry<String, UserClient> user:set){
					if(user.getValue().client != null && user.getValue().client.isConnected()){
						user.getValue().client.call("processGameAction", null, amfAction);
					}
				}
				set = null;
				amfAction = null;
			}
			initiator = null;
			initiatorConn = null;
		}
	}	
	
	public void userout(IClient client){
		UserClient initiatorConn = getUserByClientID(Integer.toString(client.getClientId()));
		UserGameAim aiminitiator = aimusers.get(Integer.toString(initiatorConn.user.id));
		
		if(aiminitiator != null){
			aiminitiator.countLife = 0;
		}
		if (initiatorConn != null && (exitusers.get(Integer.toString(initiatorConn.user.id)) != null)){
			exitusers.remove(Integer.toString(initiatorConn.user.id));
		}
		
		AMFDataObj amfAction = AMFWObjectBuilder.createObjGameActionEvent(GameActionType.ACTION, this.id, GameActionSubType.OUT, aiminitiator.user.user.id, 0);		
		Set<Entry<String, UserClient>> set = users.entrySet();
		for (Map.Entry<String, UserClient> user:set){
			if(user.getValue().client != null && user.getValue().client.isConnected()){
				user.getValue().client.call("processGameAction", null, amfAction);
			}
		}
		set = null;
		amfAction = null;
		
		if (initiatorConn != null && (outusers.get(Integer.toString(initiatorConn.user.id)) == null)){
			if (outusers.get(Integer.toString(initiatorConn.user.id)) == null){
				outusers.put(Integer.toString(initiatorConn.user.id), initiatorConn);
			}				
			if (outusers.size() + exitusers.size() + 1 == users.size()){				
				endGame();
			}
		}
		initiatorConn = null;
		aiminitiator = null;
	}	
	
	public void userexit(IClient client){
		UserClient initiatorConn = getUserByClientID(Integer.toString(client.getClientId()));
		UserGameAim aiminitiator = aimusers.get(Integer.toString(initiatorConn.user.id));
		
		if(aiminitiator != null){
			aiminitiator.countLife = 0;
			aiminitiator.exitFlag = true;
		}
		if (initiatorConn != null && (outusers.get(Integer.toString(initiatorConn.user.id)) != null)){
			outusers.remove(Integer.toString(initiatorConn.user.id));
		}
		
		if(aiminitiator != null){
			AMFDataObj amfAction = AMFWObjectBuilder.createObjGameActionEvent(GameActionType.ACTION, this.id, GameActionSubType.EXIT, aiminitiator.user.user.id, 0);		
			Set<Entry<String, UserClient>> set = users.entrySet();
			for (Map.Entry<String, UserClient> user:set){
				if(user.getValue().client != null && user.getValue().client.isConnected()){
					user.getValue().client.call("processGameAction", null, amfAction);
				}
			}
			set = null;
			amfAction = null;
		}
		
		if (initiatorConn != null && (exitusers.get(Integer.toString(initiatorConn.user.id)) == null)){
			if (exitusers.get(Integer.toString(initiatorConn.user.id)) == null){
				exitusers.put(Integer.toString(initiatorConn.user.id), initiatorConn);
			}				
			if (outusers.size() + exitusers.size() + 1 == users.size()){				
				endGame();
			}
		}
		initiatorConn = null;
		aiminitiator = null;
	}
	
	public void selectGun(IClient client, int gunID){
		UserClient initiatorConn = getUserByClientID(Integer.toString(client.getClientId()));
		if (initiatorConn != null ){
			User initiator = initiatorConn.user;
			if(hunter != null){
				gun = hunter.user.user.itemsObj.get(gunID);
				
				int gunPtototypeID = 0;
				if(gun != null && gun.param1 == ShopCategory.ARSENAL && gun.onuser == 1 && gun.quality > 0){
					gunPtototypeID = gun.pid;
				}else{
					gun = null;
					gunPtototypeID = 0;
				}
				
				//дл€ обновлени€ прицела, при изменении вида оружи€ (sourceId - id прототипа орижи€, т.к. item оружи€ есть только у владельца)
				AMFDataObj amfAction = AMFWObjectBuilder.createObjGameActionEvent(GameActionType.ACTION, this.id, GameActionSubType.SELECT_GUN, initiator.id, gunPtototypeID);
				
				Set<Entry<String, UserClient>> set = users.entrySet();
				for (Map.Entry<String, UserClient> user:set){
					if(user.getValue().client != null && user.getValue().client.isConnected() && user.getValue().user.id != initiator.id){
						user.getValue().client.call("processGameAction", null, amfAction);
					}
				}
				set = null;
				amfAction = null;
			}
		}
	}
}
