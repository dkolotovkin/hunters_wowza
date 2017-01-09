package app;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Driver;
import java.sql.DriverManager;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import snaq.db.ConnectionPool;
import app.admin.AdministratorManager;
import app.clan.ClanManager;
import app.db.DataBaseConfig;
import app.game.GameManager;
import app.kings.KingsManager;
import app.logger.MyLogger;
import app.room.Room;
import app.room.auction.AuctionRoom;
import app.room.common.CommonRoom;
import app.room.minigamebets.MiniGameBetsRoom;
import app.room.victorina.VictorinaRoom;
import app.shop.ShopManager;
import app.startbonus.StartBonusManager;
import app.user.UserClient;
import app.user.chage.ChangeResult;
import app.user.info.UserInfoManager;
import app.utils.amfwobjectbuilder.AMFWObjectBuilder;
import app.utils.authorization.Authorization;
import app.utils.jsonutil.JSONUtil;
import app.utils.md5.MD5;
import app.utils.random.RoomRandom;

import com.wowza.wms.amf.AMFDataList;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.client.IClient;
import com.wowza.wms.logging.WMSLogger;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.request.RequestFunction;

public class ServerApplication extends ModuleBase{	
	public static ServerApplication application;
	public MyLogger logger;
	
	public HashMap<String, Room> rooms;
	public CommonRoom commonroom;
	public Room modsroom;
	public AuctionRoom auctionroom;
	public MiniGameBetsRoom betsroom;
	public VictorinaRoom victorinaroom;
	public UserInfoManager userinfomanager;
	public GameManager gamemanager;
	public ShopManager shopmanager;
	public ClanManager clanmanager;
	public StartBonusManager startbonus;
	public AdministratorManager adminmanager;
	public KingsManager kingsmanager;
	
	public ConnectionPool sqlpool;	
	
	public void onAppStart(IApplicationInstance appInstance){
		application = this;
		logger = new MyLogger("ServerMouseApplication");
		logger.log("Start Application...");		
		gamemanager = new GameManager();
		clanmanager = new ClanManager();
		
		rooms = new HashMap<String, Room>();		
		commonroom = new CommonRoom(1, "Œ·˘ËÈ ˜‡Ú");
		rooms.put(Integer.toString(commonroom.id), commonroom);
		
		modsroom = new Room(RoomRandom.getRoomID(), "ÃÓ‰Â‡ÚÓ˚");
		rooms.put(Integer.toString(modsroom.id), modsroom);
		
		auctionroom = new AuctionRoom(RoomRandom.getRoomID(), "¿ÛÍˆËÓÌ");
		rooms.put(Integer.toString(auctionroom.id), auctionroom);
		
		betsroom = new MiniGameBetsRoom(RoomRandom.getRoomID(), " ÓÏÌ‡Ú‡ ÒÚ‡‚ÓÍ");
		rooms.put(Integer.toString(betsroom.id), betsroom);
		
		try {
    		Class c = Class.forName("com.mysql.jdbc.Driver");
    		Driver driver = (Driver)c.newInstance();
    		DriverManager.registerDriver(driver);
    		logger.log("Loading JDBC Driver ok");
		} catch (Exception e) {
			logger.error(e.toString());
		}
		sqlpool = new ConnectionPool(DataBaseConfig.dbname(), 50, 2000, 2000, 200000, DataBaseConfig.connectionUrl(), DataBaseConfig.dblogin(), DataBaseConfig.dbpassward());
		
		victorinaroom = new VictorinaRoom(RoomRandom.getRoomID(), "¬ËÍÚÓËÌ‡");
		rooms.put(Integer.toString(victorinaroom.id), victorinaroom);
		
		shopmanager = new ShopManager();
		shopmanager.initialize();
		userinfomanager = new UserInfoManager();		
		adminmanager = new AdministratorManager();
		startbonus = new StartBonusManager();
		kingsmanager = new KingsManager();
	}

	public void onAppStop(IApplicationInstance appInstance){			
		logger.log("Stop application. Send Closed Message to client");		
		
		Set<Entry<String, Room>> set = rooms.entrySet();
		for (Map.Entry<String, Room> room:set){
			room.getValue().roomClear();		
		}
		rooms.clear();		
	}
	
	public void onDisconnect(IClient client){
		if(client != null){
	    	UserClient user = commonroom.getUserByClientID(Integer.toString(client.getClientId()));
	    	
	    	userinfomanager.removeUserByClientID(Integer.toString(client.getClientId()));
	    	if(user != null) userinfomanager.updateUser(user.user);
	    	user = null;
    	}
	}

    public void logIn(IClient client, RequestFunction function, AMFDataList params){
    	String socialid = getParamString(params, PARAM1);
    	String passward = getParamString(params, PARAM2);
    	String authkey = getParamString(params, PARAM3);
    	String vid = getParamString(params, PARAM4);
    	int mode = getParamInt(params, PARAM5);
    	String appID = getParamString(params, PARAM6);
    	int version = getParamInt(params, PARAM7);
    	
    	try{
    		if(version == Config.currentVersion()){
		    	if (Authorization.check(authkey, vid, mode, appID) == true && client.isConnected() == true){	
		    		UserClient user = commonroom.getUserBySocialID(socialid);
		    		if(user != null){
			    		userinfomanager.removeUserByClientID(new Integer(client.getClientId()).toString());
			    		userinfomanager.updateUser(user.user);
			    		if(user.client.isConnected() == true){
			    			user.client.setShutdownClient(true);			    			
			    		}
			    		user = null;
		    		}
		    		if(userinfomanager.clientConnect(client, socialid, passward, client.getIp())){
		    			sendResult(client, params, true);
		    			return;
		    		}else{
		    			logger.log("Client not be connect: " + socialid);
		    		}
		    	}else{		    		
		    	}
    		}
    	}catch(Throwable e){
    		logger.log("START ERROR: " + e.toString() + " ||| " + e.getMessage());
    	}    	
    	sendResult(client, params, false);    	
    	client.setShutdownClient(true);    	
    	client = null;    	
    }
    
    public void loginSiteVK(IClient client, RequestFunction function, AMFDataList params){  
    	String code = getParamString(params, PARAM1);
    	int siteAppId = getParamInt(params, PARAM2);
    	int version = getParamInt(params, PARAM3);
    	
    	try
		{
    		if(version == Config.currentVersion()){
				URL url = new URL(Config.loginUrlVK());
				DataOutputStream wr = null;			
				try{			 
					String urlparams = "client_id=" + siteAppId + "&client_secret=" + Config.protectedSecretSiteVK() + "&code=" + code;
			        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
			        urlConnection.setConnectTimeout(1000);
			        urlConnection.setReadTimeout(1000);
			         
			        urlConnection.setDoInput(true);
			        urlConnection.setDoOutput(true);
			        urlConnection.setUseCaches(false);
			        urlConnection.setRequestMethod("GET");		         
			         
			        wr = new DataOutputStream (urlConnection.getOutputStream());
			        wr.writeBytes(urlparams);
			        wr.flush();
			        wr.close();
			         
			        InputStream is = urlConnection.getInputStream();			        
			        BufferedReader rd = new BufferedReader(new InputStreamReader(is));			         
			        String line;
			        StringBuffer response = new StringBuffer();
			        int counter = 0;
			        while((line = rd.readLine()) != null && counter < 1000) {
			        	response.append(line);
						response.append('\r');
						counter++;				
			        }
			         
			        rd.close();
			        rd = null;
			        is.close();
			        is = null;
			        wr = null;
			        url = null;
			        urlConnection.disconnect();
			        urlConnection = null;
			        
			        String userId = JSONUtil.getValueByName(response.toString(), "user_id");			        
			        if(userId != null){
			        	String socialid = "vk" + userId;
			       	        
				        UserClient user = commonroom.getUserBySocialID(socialid);			        
			    		if(user != null){
				    		userinfomanager.removeUserByClientID(Integer.toString(user.client.getClientId()));
				    		userinfomanager.updateUser(user.user);
				    		if(user.client != null && user.client.isConnected()){
				    			user.client.setShutdownClient(true);				    			
				    		}
				    		user = null;
			    		}
			    		
			    		if(userinfomanager.clientConnect(client, socialid, null, client.getIp())){
			    			sendResult(client, params, true);
			    			return;
			    		}else{
			    			logger.log("Client not be connect: " + socialid);
			    		}
			        }else{
			        	sendResult(client, params, false);
			        	return;
			        }
				 }				
				 catch(IOException e){
					 logger.log("SM2" + e.toString());				
				 }
				 finally
					{
					    try{
					    	if (wr != null) wr.close();				    	
					    	wr = null;			    	
					    }
					    catch (Throwable e) {
					    	logger.log("SM3" + e.getMessage());				    	
					    }
					}
    		}
		}catch(MalformedURLException e){
			logger.log("SM4" + e.toString());			
		}
		client.setShutdownClient(true);
		sendResult(client, params, false);
		return;
    }
    
    public void loginSiteOD(IClient client, RequestFunction function, AMFDataList params){  
    	String code = getParamString(params, PARAM1);
    	int siteAppId = getParamInt(params, PARAM2);
    	int version = getParamInt(params, PARAM3);
    	
    	try
		{
    		if(version == Config.currentVersion()){
				URL url = new URL(Config.loginUrlOD());
				DataOutputStream wr = null;			
				try{			 
					String urlparams = "client_id=" + siteAppId + "&client_secret=" + Config.protectedSecretSiteOD() + 
										"&grant_type=" + "authorization_code" + "&code=" + code +
										"&redirect_uri=" + Config.oficalSiteUrl();
			        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
			        urlConnection.setConnectTimeout(1000);
			        urlConnection.setReadTimeout(1000);
			         
			        urlConnection.setDoInput(true);
			        urlConnection.setDoOutput(true);
			        urlConnection.setUseCaches(false);
			        urlConnection.setRequestMethod("POST");		         
			         
			        wr = new DataOutputStream (urlConnection.getOutputStream());
			        wr.writeBytes(urlparams);
			        wr.flush();
			        wr.close();
			         
			        InputStream is = urlConnection.getInputStream();			        
			        BufferedReader rd = new BufferedReader(new InputStreamReader(is));			         
			        String line;
			        StringBuffer response = new StringBuffer();
			        int counter = 0;
			        while((line = rd.readLine()) != null && counter < 1000) {
			        	response.append(line);
						response.append('\r');
						counter++;				
			        }
			         
			        rd.close();
			        rd = null;
			        is.close();
			        is = null;
			        wr = null;
			        url = null;
			        urlConnection.disconnect();
			        urlConnection = null;
			        
			        String access_token = JSONUtil.getValueByName(response.toString(), "access_token");
			        access_token = access_token.substring(1, access_token.length() - 1);			        
			        
			        url = new URL("http://api.odnoklassniki.ru/api/users/getLoggedInUser");
			        String appkey = "application_key=" + Config.applicationKeySiteOD();
			        String access_token_param = "access_token=" + access_token;			       
			        String and = "&";
			        String sig = "sig=" + MD5.getMD5(new String(appkey) + MD5.getMD5(access_token + Config.protectedSecretSiteOD()));
			        urlparams = appkey + and + sig + and + access_token_param;
			        
					urlConnection = (HttpURLConnection)url.openConnection();
					urlConnection.setConnectTimeout(1000);
					urlConnection.setReadTimeout(1000);
					 
					urlConnection.setDoInput(true);
					urlConnection.setDoOutput(true);
					urlConnection.setUseCaches(false);
					urlConnection.setRequestMethod("GET");		         
					 
					wr = new DataOutputStream (urlConnection.getOutputStream());
					wr.writeBytes(urlparams);
					wr.flush();
					wr.close();
			        
					is = urlConnection.getInputStream();			        
			        rd = new BufferedReader(new InputStreamReader(is));
			        response = new StringBuffer();
			        counter = 0;
			        while((line = rd.readLine()) != null && counter < 1000) {
			        	response.append(line);
						response.append('\r');
						counter++;				
			        }
			         
			        rd.close();
			        rd = null;
			        is.close();
			        is = null;
			        wr = null;
			        url = null;
			        urlConnection.disconnect();
			        urlConnection = null;
			        
			        String userId = response.toString();			       
			        userId = userId.substring(1, userId.length() - 2);
			       
			        if(userId != null){
			        	String socialid = "od" + userId;
			       	        
				        UserClient user = commonroom.getUserBySocialID(socialid);			        
			    		if(user != null){
				    		userinfomanager.removeUserByClientID(Integer.toString(user.client.getClientId()));
				    		userinfomanager.updateUser(user.user);
				    		if(user.client != null && user.client.isConnected()){
				    			user.client.setShutdownClient(true);
				    		}
				    		user = null;
			    		}
			    		
			    		if(userinfomanager.clientConnect(client, socialid, null, client.getIp())){    			
			    			sendResult(client, params, true);
				        	return;
			    		}else{
			    			logger.log("Client not be connect: " + socialid);
			    		}
			        }else{
			        	sendResult(client, params, false);
			        	return;
			        }
				 }				
				 catch(IOException e){
					 logger.log("SM2" + e.toString());				
				 }
				 finally
					{
					    try{
					    	if (wr != null) wr.close();				    	
					    	wr = null;			    	
					    }
					    catch (Throwable e) {
					    	logger.log("SM3" + e.getMessage());		    	
					    }
					}
    		}
		}catch(MalformedURLException e){
			logger.log("SM4" + e.toString());			
		}
		client.setShutdownClient(true);
		sendResult(client, params, false);
		return;
    }
    
    public void loginSiteMM(IClient client, RequestFunction function, AMFDataList params){  
    	String code = getParamString(params, PARAM1);
    	int siteAppId = getParamInt(params, PARAM2);
    	int version = getParamInt(params, PARAM3);
    	
    	try
		{
    		if(version == Config.currentVersion()){
				URL url = new URL(Config.loginUrlMM());
				DataOutputStream wr = null;			
				try{				 
					String urlparams = "client_id=" + siteAppId + "&client_secret=" + Config.protectedSecretSiteMM() + 
										"&grant_type=" + "authorization_code" + "&code=" + code +
										"&redirect_uri=" + Config.oficalSiteUrl();
			        HttpURLConnection urlConnection = (HttpURLConnection)url.openConnection();
			        urlConnection.setConnectTimeout(1000);
			        urlConnection.setReadTimeout(1000);
			         
			        urlConnection.setDoInput(true);
			        urlConnection.setDoOutput(true);
			        urlConnection.setUseCaches(false);
			        urlConnection.setRequestMethod("POST");		         
			         
			        wr = new DataOutputStream (urlConnection.getOutputStream());
			        wr.writeBytes(urlparams);
			        wr.flush();
			        wr.close();
			         
			        InputStream is = urlConnection.getInputStream();			        
			        BufferedReader rd = new BufferedReader(new InputStreamReader(is));			         
			        String line;
			        StringBuffer response = new StringBuffer();
			        int counter = 0;
			        while((line = rd.readLine()) != null && counter < 1000) {
			        	response.append(line);
						response.append('\r');
						counter++;				
			        }
			         
			        rd.close();
			        rd = null;
			        is.close();
			        is = null;
			        wr = null;
			        url = null;
			        urlConnection.disconnect();
			        urlConnection = null;			       
			        
			        String userId = JSONUtil.getValueByName(response.toString(), "x_mailru_vid");			        
			        userId = userId.substring(1, userId.length() - 1);
			        
			        if(userId != null){
			        	String socialid = "mm" + userId;
			       	        
				        UserClient user = commonroom.getUserBySocialID(socialid);			        
			    		if(user != null){
				    		userinfomanager.removeUserByClientID(Integer.toString(user.client.getClientId()));
				    		userinfomanager.updateUser(user.user);
				    		if(user.client != null && user.client.isConnected()){
				    			user.client.setShutdownClient(true);
				    		}
				    		user = null;
			    		}
			    		
			    		if(userinfomanager.clientConnect(client, socialid, null, client.getIp())){    			
			    			sendResult(client, params, true);
				        	return;
			    		}else{
			    			logger.log("Client not be connect: " + socialid);
			    		}
			        }else{
			        	sendResult(client, params, false);
			        	return;
			        }
				 }				
				 catch(IOException e){
					 logger.log("SM2" + e.toString());				
				 }
				 finally
					{
					    try{
					    	if (wr != null) wr.close();				    	
					    	wr = null;			    	
					    }
					    catch (Throwable e) {
					    	logger.log("SM3" + e.getMessage());				    	
					    }
					}
    		}
		}catch(MalformedURLException e){
			logger.log("SM4" + e.toString());			
		}
		client.setShutdownClient(true);
		sendResult(client, params, false);
		return;
    }
    
    public void getOnlineUsersCount(IClient client, RequestFunction function, AMFDataList params){
    	logger.log("getOnlineUsersCount...");
    	//return int
    	sendResult(client, params, commonroom.users.size());
    	client.setShutdownClient(true);
    }
    
    public void getFriendsBonus(IClient client, RequestFunction function, AMFDataList params){
    	String vid = getParamString(params, PARAM1);
    	int mode = getParamInt(params, PARAM2);
    	int appID = getParamInt(params, PARAM3);
    	String sessionKey = getParamString(params, PARAM4);
    	
    	//return int
    	sendResult(client, params, userinfomanager.getFriendsBonus(commonroom.getUserByClientID(Integer.toString(client.getClientId())), vid, mode, appID, sessionKey));    	
    }
    
    public void updateUser(IClient client, RequestFunction function, AMFDataList params){
    	UserClient user = commonroom.getUserByClientID(Integer.toString(client.getClientId()));
    	if(user != null) userinfomanager.updateUser(user.user);
    	user = null;
    }
    
    public void updateParams(IClient client, RequestFunction function, AMFDataList params){
    	UserClient user = commonroom.getUserByClientID(Integer.toString(client.getClientId()));
    	userinfomanager.updateParams(user, getParamString(params, PARAM1));
    }
    
    //Œ“œ–¿¬ ¿ —ŒŒ¡Ÿ≈Õ»… ¬ ◊¿“
    public void sendMessage(IClient client, RequestFunction function, AMFDataList params){
    	String mtext = getParamString(params, PARAM1);
    	String receiverID = getParamString(params, PARAM2);
    	String roomID = getParamString(params, PARAM3);
    	 	
    	Room room = rooms.get(roomID);
    	if (room != null){    		
    		room.sendMessage(mtext, Integer.toString(client.getClientId()), receiverID);
    	}else{    		
    		Room gameroom = GameManager.gamerooms.get(roomID);
    		if (gameroom != null){    			
    			gameroom.sendMessage(mtext, Integer.toString(client.getClientId()), receiverID);
    		}else{
    			commonroom.sendPrivateMessage(mtext, Integer.toString(client.getClientId()), receiverID, new Integer(roomID));
    		}
    		gameroom = null;
    	}
    	room = null;    	
    }
    
    public void sendMail(IClient client, RequestFunction function, AMFDataList params){
    	int uid =  getParamInt(params, PARAM1);
    	String message = getParamString(params, PARAM2);
    	
    	//return BuyResult
    	sendResult(client, params, AMFWObjectBuilder.createObjBuyResult(userinfomanager.sendMail(commonroom.getUserByClientID(Integer.toString(client.getClientId())), uid, message)));    	
    }
    
    public void getMailMessages(IClient client, RequestFunction function, AMFDataList params){
    	//return ArrayList<UserMailMessage>
    	sendResult(client, params, AMFWObjectBuilder.createObjUsersMailMessage(userinfomanager.getMailMessages(commonroom.getUserByClientID(Integer.toString(client.getClientId())).user.id)));    	
    }
    
    public void removeMailMessage(IClient client, RequestFunction function, AMFDataList params){
    	userinfomanager.removeMailMessage(getParamInt(params, PARAM1));
    	sendResult(client, params, true);
    }
    
    public void isBadPlayer(IClient client, RequestFunction function, AMFDataList params){
    	UserClient u = commonroom.getUserByClientID(Integer.toString(client.getClientId()));
    	if(u != null){
//    		logger.log("BAD PLAYER->  id:" + u.user.id + " title:" + u.user.title);
    		u.client.setShutdownClient(true);
    	}
    }
    
    public void checkLuck(IClient client, RequestFunction function, AMFDataList params){
    	UserClient u = commonroom.getUserByClientID(Integer.toString(client.getClientId()));     	
    	//return CheckLuckResult
    	sendResult(client, params, AMFWObjectBuilder.createObjCheckLuckResult(userinfomanager.checkLuck(u, getParamInt(params, PARAM1))));
    }
    
    //»«Ã≈Õ≈Õ»≈ œ¿–¿Ã≈“–Œ¬ œ≈–—ŒÕ¿∆¿
    public void changeInfo(IClient client, RequestFunction function, AMFDataList params){
    	String title = getParamString(params, PARAM1);
    	int sex = getParamInt(params, PARAM2);
    	
    	ChangeResult result = new ChangeResult();
    	result = userinfomanager.changeInfo(title, sex, Integer.toString(client.getClientId()), result, false);
    	
    	//return ChangeResult
    	sendResult(client, params, AMFWObjectBuilder.createObjChangeResult(result));
    }
    
    public void addToFriend(IClient client, RequestFunction function, AMFDataList params){
    	userinfomanager.addToFriend(commonroom.getUserByClientID(Integer.toString(client.getClientId())).user.id, getParamInt(params, PARAM1));
    	sendResult(client, params, true);
    }
    
    public void addToEnemy(IClient client, RequestFunction function, AMFDataList params){
    	userinfomanager.addToEnemy(commonroom.getUserByClientID(Integer.toString(client.getClientId())).user.id, getParamInt(params, PARAM1));
    	sendResult(client, params, true);
    }
    
    public void removeFriend(IClient client, RequestFunction function, AMFDataList params){
    	userinfomanager.removeFriend(commonroom.getUserByClientID(Integer.toString(client.getClientId())).user.id, getParamInt(params, PARAM1));
    	sendResult(client, params, true);
    }
    
    public void removeEnemy(IClient client, RequestFunction function, AMFDataList params){
    	userinfomanager.removeEnemy(commonroom.getUserByClientID(Integer.toString(client.getClientId())).user.id, getParamInt(params, PARAM1));
    	sendResult(client, params, true);
    }
    
    public void getFiends(IClient client, RequestFunction function, AMFDataList params){
    	//return ArrayList<UserFriend>
    	UserClient user = commonroom.getUserByClientID(Integer.toString(client.getClientId()));
    	if(user != null && user.client != null && user.client.isConnected()){
    		sendResult(client, params, AMFWObjectBuilder.createObjUsersFriend(userinfomanager.getFiends(user.user.id)));
    		user = null;
    	}
    }
    
    public void getEnemies(IClient client, RequestFunction function, AMFDataList params){
    	//return ArrayList<UserFriend>
    	UserClient user = commonroom.getUserByClientID(Integer.toString(client.getClientId()));
    	if(user != null && user.client != null && user.client.isConnected()){
    		sendResult(client, params, AMFWObjectBuilder.createObjUsersFriend(userinfomanager.getEnemies(user.user.id)));
    		user = null;
    	}
    }
    
    public void gettopusers(IClient client, RequestFunction function, AMFDataList params){
    	//return ArrayList<UserForTop>
    	sendResult(client, params, userinfomanager.topUsersAMF);    	
    }
    
    public void gettophourusers(IClient client, RequestFunction function, AMFDataList params){
    	//return ArrayList<UserForTop>
    	sendResult(client, params, userinfomanager.topHourUsersAMF);    	
    }
    
    public void gettopdayusers(IClient client, RequestFunction function, AMFDataList params){
    	//return ArrayList<UserForTop>
    	sendResult(client, params, userinfomanager.topDayUsersAMF);    	
    }
    
    public void gettoppopularusers(IClient client, RequestFunction function, AMFDataList params){
    	//return ArrayList<UserForTop>
    	sendResult(client, params, userinfomanager.topPopularUsersAMF);    	
    }
    
    public void ban(IClient client, RequestFunction function, AMFDataList params){
    	int userID = getParamInt(params, PARAM1);
    	byte type = (byte) getParamInt(params, PARAM2);
    	boolean byip = getParamBoolean(params, PARAM3);
    	//return byte
    	sendResult(client, params, userinfomanager.ban(userID, Integer.toString(client.getClientId()), type, byip));    	
    }
    
    public void getUserInfoByID(IClient client, RequestFunction function, AMFDataList params){
    	int userID = getParamInt(params, PARAM1);
    	UserClient user = commonroom.users.get(Integer.toString(userID));
    	if (user != null){    		
			sendResult(client, params, AMFWObjectBuilder.createObjInitUser(user.user, 0, null, null));    		
    	}else{
    		UserClient u = userinfomanager.getOfflineUser(userID);
    		sendResult(client, params, AMFWObjectBuilder.createObjInitUser(u.user, 0, null, null));    		 
    	}
    	//return UserForInit
    }
    
    public void userInAuction(IClient client, RequestFunction function, AMFDataList params){
    	UserClient u = commonroom.getUserByClientID(new Integer(client.getClientId()).toString());
    	auctionroom.addUser(u);
    	u = null;
    }    
    public void userOutAuction(IClient client, RequestFunction function, AMFDataList params){
    	auctionroom.removeUserByClientID(new Integer(client.getClientId()).toString());
    }    
    public void auctionBet(IClient client, RequestFunction function, AMFDataList params){    	
    	//return int
    	sendResult(client, params, auctionroom.bet(commonroom.getUserByClientID(new Integer(client.getClientId()).toString()), getParamInt(params, PARAM1)));
    }
    
    public void userInVictorina(IClient client, RequestFunction function, AMFDataList params){
    	UserClient u = commonroom.getUserByClientID(new Integer(client.getClientId()).toString());
    	victorinaroom.addUser(u);
    	u = null;
    }
    public void userOutVictorina(IClient client, RequestFunction function, AMFDataList params){
    	victorinaroom.removeUserByClientID(new Integer(client.getClientId()).toString());
    }
    
    //Á‡ÔÎ‡Ú‡
    public void getOnlineTimeMoneyInfo(IClient client, RequestFunction function, AMFDataList params){
    	UserClient initiator = ServerApplication.application.commonroom.getUserByClientID(new Integer(client.getClientId()).toString());
    	//return int 
    	sendResult(client, params, userinfomanager.getOnlineTimeMoneyInfo(initiator));
	}
    public void getOnlineTimeMoney(IClient client, RequestFunction function, AMFDataList params){	
    	UserClient initiator = ServerApplication.application.commonroom.getUserByClientID(new Integer(client.getClientId()).toString());
    	userinfomanager.getOnlineTimeMoney(initiator);
    	sendResult(client, params, true);
    }
    
    public void printStackTrace(String srt){
		StringWriter sw = new StringWriter();
	    PrintWriter pw = new PrintWriter(sw, true);
	    new Error().printStackTrace(pw);
	    pw.flush();
	    sw.flush();
		logger.log(srt + " " + sw.toString());
	}
    
	public WMSLogger getLoggerInstance(){return getLogger();}
	public void disconnect(IClient client){client.shutdownClient();}
	public void onConnect(IClient client, RequestFunction function, AMFDataList params){}	
	public void onConnectAccept(IClient client){}
	public void onConnectReject(IClient client){}
}