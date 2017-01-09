package app.kings;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import app.ServerApplication;
import app.logger.MyLogger;
import app.message.MessageType;
import app.room.gamebet.BetInfo;
import app.room.gamebet.BetResult;
import app.user.UserClient;
import app.user.UserForTop;
import app.utils.amfwobjectbuilder.AMFWObjectBuilder;
import app.utils.changeinfo.ChangeInfoParams;
import app.utils.sex.Sex;

import com.wowza.wms.amf.AMFDataList;
import com.wowza.wms.amf.AMFDataObj;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.client.IClient;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.request.RequestFunction;

public class KingsManager extends ModuleBase{
	public MyLogger logger = new MyLogger(KingsManager.class.getName());
	public Timer kingstimer;
	Date date;
	
	public int startTime;
	public int roundDuration;
	public Map<Integer, UserForTop> aspirants;
	public List<BetInfo> bets;
	
	public UserForTop queen;
	public UserForTop king;
	
	public void onAppStart(IApplicationInstance appInstance){
		aspirants = new ConcurrentHashMap<Integer, UserForTop>();
		bets = new ArrayList<BetInfo>();
		getKingsInfoFromDB();
		
		kingstimer = new Timer();
		kingstimer.schedule(new KingsTimerTask(), 60 * 1000, 60 * 1000);
		
		int minute = 60;
		int hour = minute * 60;
		int day = hour * 24;
		roundDuration = 5 * day;
	}
	
	private void getKingsInfoFromDB(){
		Connection _sqlconnection = null;
		PreparedStatement selectKings = null;	
		ResultSet selectKingsResult = null;
		PreparedStatement selectAspirant = null;	
		ResultSet selectAspirantResult = null;
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			selectKings = _sqlconnection.prepareStatement("SELECT * FROM user where king=1");
			
			selectKingsResult = selectKings.executeQuery();					
			while(selectKingsResult.next()){
				if(selectKingsResult.getInt("sex") == Sex.MALE){
					king = new UserForTop(selectKingsResult.getInt("id"), selectKingsResult.getInt("sex"), selectKingsResult.getString("title"), ServerApplication.application.userinfomanager.getLevelByExperience(selectKingsResult.getInt("experience")), selectKingsResult.getInt("exphour"), selectKingsResult.getInt("expday"), selectKingsResult.getInt("popular"), selectKingsResult.getString("url"));
				}else{
					queen = new UserForTop(selectKingsResult.getInt("id"), selectKingsResult.getInt("sex"), selectKingsResult.getString("title"), ServerApplication.application.userinfomanager.getLevelByExperience(selectKingsResult.getInt("experience")), selectKingsResult.getInt("exphour"), selectKingsResult.getInt("expday"), selectKingsResult.getInt("popular"), selectKingsResult.getString("url"));
				}
    		}
			
			selectKings = _sqlconnection.prepareStatement("SELECT * FROM kings");			
			selectKingsResult = selectKings.executeQuery();
			
			BetInfo binfo;
			UserForTop aspirant;
			while(selectKingsResult.next()){
				int uid = selectKingsResult.getInt("uid");
				int buid = selectKingsResult.getInt("buid");
				int param = selectKingsResult.getInt("param");
				
				if(uid == -1 && buid == -1){
					startTime = param;
				}
				if(uid > 0 && buid > 0){
					binfo = new BetInfo(uid, buid, param);
					bets.add(binfo);
				}
				if(uid > 0 && buid == -1 && param == -1){
					selectAspirant = _sqlconnection.prepareStatement("SELECT * FROM user where id=" + uid);
					selectAspirantResult = selectAspirant.executeQuery();
					if(selectAspirantResult.next()){
						aspirant = new UserForTop(selectAspirantResult.getInt("id"), selectAspirantResult.getInt("sex"), selectAspirantResult.getString("title"), ServerApplication.application.userinfomanager.getLevelByExperience(selectAspirantResult.getInt("experience")), selectAspirantResult.getInt("exphour"), selectAspirantResult.getInt("expday"), selectAspirantResult.getInt("popular"), selectAspirantResult.getString("url"));
						aspirants.put(aspirant.id, aspirant);
					}
				}
			}
		}catch (SQLException e) {
			logger.error("KM1 " + e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	if (selectKings != null) selectKings.close();
		    	if (selectKingsResult != null) selectKingsResult.close();
		    	if (selectAspirant != null) selectAspirant.close();
		    	if (selectAspirantResult != null) selectAspirantResult.close();
		    	_sqlconnection = null;
		    	selectKings = null;
		    	selectKingsResult = null;
		    	selectAspirant = null;
		    	selectAspirantResult = null;
		    }
		    catch (SQLException sqlx) {
		    }
		}
		
		if(startTime == 0){
			startNewRound();
		}
	}
	
	public void startNewRound(){
		int currenttime = (int)(date.getTime() / 1000);
		if(startTime == 0){
			startTime = currenttime;
		}else{
			startTime = startTime + roundDuration;
		}
		
		aspirants = new ConcurrentHashMap<Integer, UserForTop>();
		bets = new ArrayList<BetInfo>();
		
		Connection _sqlconnection = null;
		PreparedStatement delete = null;
		PreparedStatement insert = null;
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			delete = _sqlconnection.prepareStatement("DELETE FROM kings");
			if (delete.executeUpdate() > 0){
				//добавление информации о начале выборов
				insert = _sqlconnection.prepareStatement("INSERT INTO kings (uid, buid, param) VALUES(?,?,?)");
    			insert.setInt(1, -1);
    			insert.setInt(2, -1);
    			insert.setInt(3, startTime);
    			insert.executeUpdate();
			}
		}catch (SQLException e) {
			logger.error("KM4" + e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();	
		    	if (delete != null) delete.close();
		    	if (insert != null) insert.close();
		    	_sqlconnection = null;	
		    	delete = null;
		    	insert = null;
		    }
		    catch (SQLException sqlx) {
		    }
		}			
	}
	
	public void kingsGetAllInfo(IClient client, RequestFunction function, AMFDataList params){
		date = new Date();
		int currenttime = (int)(date.getTime() / 1000);
    	AMFDataObj infoAMFW = AMFWObjectBuilder.createObjKingsInfo(startTime, roundDuration, currenttime, king, queen, aspirants, bets);
		
		//return AMFDataObj
		sendResult(client, params, infoAMFW);
	}
	
	public void kingsBet(IClient client, RequestFunction function, AMFDataList params){
		int betuserid = getParamInt(params, PARAM1);
		int bet = getParamInt(params, PARAM2);
		
		Connection _sqlconnection = null;
		PreparedStatement insert = null;
		PreparedStatement update = null;
		
		int betresult = BetResult.OTHER;
		
		int maxBet = 50000;
		
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			
			if(user != null){
				BetInfo lastBet = null;
				int lastBetMoney = 0;
				for(int i = 0; i < bets.size(); i++){
					if(bets.get(i).userid == user.user.id && bets.get(i).betuserid == betuserid){
						lastBet = bets.get(i);
						lastBetMoney = lastBet.bet;
					}
				}
				if((maxBet - lastBetMoney) >= bet){
					if(user.user.money >= bet && bet > 0){
						if(lastBet != null){
							lastBet.bet += bet;
							
							update = _sqlconnection.prepareStatement("UPDATE kings SET param=? WHERE uid=? AND buid=?");
	    					update.setInt(1, lastBet.bet);        				
	    					update.setInt(2, lastBet.userid);
	    					update.setInt(3, lastBet.betuserid);
	    					update.executeUpdate();
						}else{
							lastBet = new BetInfo(user.user.id, betuserid, bet);
							bets.add(lastBet);
							
							insert = _sqlconnection.prepareStatement("INSERT INTO kings (uid, buid, param) VALUES(?,?,?)");
			    			insert.setInt(1, lastBet.userid);
			    			insert.setInt(2, lastBet.betuserid);
			    			insert.setInt(3, lastBet.bet);
			    			insert.executeUpdate();
						}
						
						user.user.updateMoney(user.user.money - bet);
						ServerApplication.application.commonroom.changeUserInfoByID(user.user.id, ChangeInfoParams.USER_MONEY, user.user.money, 0);
						
						betresult = BetResult.OK;
					}else{
						betresult = BetResult.NO_MONEY;
					}
				}			
			}
		}catch (SQLException e) {
			logger.error("KM2" + e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();	
		    	if (insert != null) insert.close();
		    	if (update != null) update.close();
		    	_sqlconnection = null;	
		    	insert = null;
		    	update = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
		
		//return int
		sendResult(client, params, betresult);
	}
	
	public void kingsAspirantRequest(IClient client, RequestFunction function, AMFDataList params){
		int aspirantRequestPrice = 1000;
		int betresult = BetResult.OTHER;
		
		Connection _sqlconnection = null;
		PreparedStatement insert = null;
		
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			
			if(user != null){
				boolean q = false;
				boolean k = false;
				if(queen != null && user.user.id == queen.id) q = true;
				if(king != null && user.user.id == king.id) k = true;
				if(aspirants.get(user.user.id) == null && !q && !k){
					if(user.user.level >= 5){
						if(user.user.money >= aspirantRequestPrice){
							UserForTop aspirant = new UserForTop(user.user.id, user.user.sex, user.user.title, user.user.level, user.user.exphour, user.user.expday, user.user.popular, user.user.url);
							aspirants.put(aspirant.id, aspirant);
							
							insert = _sqlconnection.prepareStatement("INSERT INTO kings (uid, buid, param) VALUES(?,?,?)");
			    			insert.setInt(1, user.user.id);
			    			insert.setInt(2, -1);
			    			insert.setInt(3, -1);
			    			insert.executeUpdate();
							
							user.user.updateMoney(user.user.money - aspirantRequestPrice);
							ServerApplication.application.commonroom.changeUserInfoByID(user.user.id, ChangeInfoParams.USER_MONEY, user.user.money, 0);
							
							betresult = BetResult.OK;
						}else{
							betresult = BetResult.NO_MONEY;
						}
					}
				}
			}
		}catch (SQLException e) {
			logger.error("KM3" + e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();	
		    	if (insert != null) insert.close();
		    	_sqlconnection = null;	
		    	insert = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
		
		//return int
		sendResult(client, params, betresult);
	}
	
	public void calculateKings(){		
		UserForTop newking = getKingIDBySex(Sex.MALE);
		UserForTop newqueen = getKingIDBySex(Sex.FEMALE);
		
		int allbetsKing = 0;
		int winbetsKing = 0;
		int allbetsQueen = 0;
		int winbetsQueen = 0;
		UserForTop aspirant;
		
		for(int i = 0; i < bets.size(); i++){
			int betuserid = bets.get(i).betuserid;
			aspirant = aspirants.get(betuserid);
			
			if(newking != null){
				if(aspirant != null && aspirant.sex == Sex.MALE){
					allbetsKing += bets.get(i).bet;
					if(bets.get(i).betuserid == newking.id){
						winbetsKing += bets.get(i).bet;
					}
				}
			}
			if(newqueen != null){
				if(aspirant != null && aspirant.sex == Sex.FEMALE){
					allbetsQueen += bets.get(i).bet;
					if(bets.get(i).betuserid == newqueen.id){
						winbetsQueen += bets.get(i).bet;
					}
				}
			}
		}
		
		int komissionKing = (int) Math.floor((float) allbetsKing * 0.15);
		int komissionQueen = (int) Math.floor((float) allbetsQueen * 0.15);
		allbetsKing = allbetsKing - komissionKing;									//деньги на ветер
		allbetsQueen = allbetsQueen - komissionQueen;								//деньги на ветер		
		
		if(newking != null){
			float percent;
			int prize = 0;			
			for(int i = 0; i < bets.size(); i++){				
				if(bets.get(i).betuserid == newking.id){
					percent = (float) bets.get(i).bet / winbetsKing;
					prize = (int) Math.floor((float) allbetsKing * percent);
					
					addToUsersExtractions(bets.get(i).userid, MessageType.KING_BET, prize);
				}				
			}			
			ServerApplication.application.commonroom.sendSystemMessage("Да здравствует новый король! Его имя: " + newking.title);
		}
		
		if(newqueen != null){
			float percent;
			int prize = 0;			
			for(int i = 0; i < bets.size(); i++){				
				if(bets.get(i).betuserid == newqueen.id){
					percent = (float) bets.get(i).bet / winbetsQueen;
					prize = (int) Math.floor((float) allbetsQueen * percent);
					
					addToUsersExtractions(bets.get(i).userid, MessageType.QUEEN_BET, prize);
				}				
			}
			ServerApplication.application.commonroom.sendSystemMessage("Да здравствует новая королева! Ее имя: " + newqueen.title);
		}
		
		UserClient userClient;
		
		if(king != null){
			updateKingStateDB(king.id, 0);
			userClient = ServerApplication.application.commonroom.users.get(Integer.toString(king.id));
			if(userClient != null && userClient.client != null && userClient.client.isConnected()){
				userClient.client.setShutdownClient(true);
			}
		}
		
		if(queen != null){
			updateKingStateDB(queen.id, 0);
			userClient = ServerApplication.application.commonroom.users.get(Integer.toString(queen.id));
			if(userClient != null && userClient.client != null && userClient.client.isConnected()){
				userClient.client.setShutdownClient(true);
			}
		}
		if(newking != null){
			addToUsersExtractions(newking.id, MessageType.KING, 5000);
			updateKingStateDB(newking.id, 1);
			userClient = ServerApplication.application.commonroom.users.get(Integer.toString(newking.id));
			if(userClient != null && userClient.client != null && userClient.client.isConnected()){
				userClient.client.setShutdownClient(true);
			}
		}
		if(newqueen != null){
			addToUsersExtractions(newqueen.id, MessageType.QUEEN, 5000);
			updateKingStateDB(newqueen.id, 1);
			userClient = ServerApplication.application.commonroom.users.get(Integer.toString(newqueen.id));
			if(userClient != null && userClient.client != null && userClient.client.isConnected()){
				userClient.client.setShutdownClient(true);
			}
		}
		
		userClient = null;
		king = newking;
		queen = newqueen;
	}
	
	private void addToUsersExtractions(int userid, int type, int prize){
		Connection _sqlconnection = null;
		PreparedStatement insertitem = null;
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			
			insertitem = _sqlconnection.prepareStatement("INSERT INTO usersextractions (userid, type, money) VALUES(?,?,?)");
			insertitem.setInt(1, userid);
			insertitem.setInt(2, type);
			insertitem.setInt(3, prize);
			insertitem.executeUpdate();
		} catch (SQLException e) {
			logger.error("error king/queen result " + e.toString());
		}
		finally
		{
		    try{	    	
		    	if (insertitem != null) insertitem.close();		    	
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	insertitem = null;		    	
		    	_sqlconnection = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
	}
	
	private void updateKingStateDB(int userid, int state){
		Connection _sqlconnection = null;
		PreparedStatement update = null;
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			
			update = _sqlconnection.prepareStatement("UPDATE user SET king=? WHERE id=?");
			update.setInt(1, state);        				
			update.setInt(2, userid);
			update.executeUpdate();
		} catch (SQLException e) {		
			logger.error("error king/queen update " + e.toString());
		}
		finally
		{
		    try{	    	
		    	if (update != null) update.close();		    	
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	update = null;		    	
		    	_sqlconnection = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
	}
	private UserForTop getKingIDBySex(int sex){
		Map<Integer, BetInfo> betsByUserId = new ConcurrentHashMap<Integer, BetInfo>();
		
		UserForTop aspirant;
		for(int i = 0; i < bets.size(); i++){
			int betuserid = bets.get(i).betuserid;
			int bet = bets.get(i).bet;
			aspirant = aspirants.get(betuserid);
			if(aspirant != null && aspirant.sex == sex){
				if(betsByUserId.get(betuserid) != null){
					betsByUserId.get(betuserid).bet += bet;
				}else{
					betsByUserId.put(betuserid, new BetInfo(-1, betuserid, bet));
				}
			}
		}
		
		aspirant = null;
		int maxBet = 0;
		Set<Entry<Integer, BetInfo>> set = betsByUserId.entrySet();
		for (Map.Entry<Integer, BetInfo> betinfo:set){
			if(betinfo.getValue().bet > maxBet){
				maxBet = betinfo.getValue().bet;
				aspirant = aspirants.get(betinfo.getValue().betuserid);
			}			
		}			
		set = null;
		
		return aspirant;
	}
	
	
	class KingsTimerTask extends TimerTask{
        public void run(){
        	try{
        		date = new Date();
				int currenttime = (int)(date.getTime() / 1000);

				if(startTime + roundDuration < currenttime)
				{
					calculateKings();
					startNewRound();
				}
				
				//обновление состояния онлайна
				UserClient onlineuser;
				Set<Entry<Integer, UserForTop>> set = aspirants.entrySet();
				for (Map.Entry<Integer, UserForTop> user:set){
					onlineuser = ServerApplication.application.commonroom.users.get(Integer.toString(user.getValue().id));
					if(onlineuser != null){
						user.getValue().isonline = true;
						user.getValue().title = onlineuser.user.title;
	    			}else{
	    				user.getValue().isonline = false;
	    			}
				}
				set = null;
				
				if(king != null){
					onlineuser = ServerApplication.application.commonroom.users.get(Integer.toString(king.id));
					if(onlineuser != null){
						king.isonline = true;
						king.title = onlineuser.user.title;
					}else{
						king.isonline = false;
					}
				}
				if(queen != null){
					onlineuser = ServerApplication.application.commonroom.users.get(Integer.toString(queen.id));
					if(onlineuser != null){
						queen.isonline = true;
						queen.title = onlineuser.user.title;
					}else{
						queen.isonline = false;
					}
				}
				onlineuser = null;
        	}catch(Throwable e){
        		ServerApplication.application.logger.log("Throwable: " + e.toString());
        	}
         }
	}
}
