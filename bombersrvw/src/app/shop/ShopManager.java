package app.shop;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import app.Config;
import app.ServerApplication;
import app.logger.MyLogger;
import app.shop.buyMoneyResult.BuyMoneyErrorCode;
import app.shop.buyMoneyResult.BuyMoneyResult;
import app.shop.buyresult.BuyErrorCode;
import app.shop.buyresult.BuyPresentResult;
import app.shop.buyresult.BuyResult;
import app.shop.item.Item;
import app.shop.item.ItemPresent;
import app.shop.itemprototype.ItemPrototype;
import app.shop.presentprototype.PresentPrototype;
import app.shop.useresult.UseErrorCode;
import app.shop.useresult.UseResult;
import app.user.UserClient;
import app.user.UserRole;
import app.utils.amfwobjectbuilder.AMFWObjectBuilder;
import app.utils.ban.BanType;
import app.utils.changeinfo.ChangeInfoParams;
import app.utils.md5.MD5;
import app.utils.sort.SortPresents;
import app.utils.sort.SortShopItems;

import com.wowza.wms.amf.AMFDataArray;
import com.wowza.wms.amf.AMFDataList;
import com.wowza.wms.application.IApplicationInstance;
import com.wowza.wms.client.IClient;
import com.wowza.wms.module.ModuleBase;
import com.wowza.wms.request.RequestFunction;

public class ShopManager extends ModuleBase{
	public MyLogger logger = new MyLogger(ShopManager.class.getName());
	public Map<Integer, ItemPrototype> itemprototypes = new HashMap<Integer, ItemPrototype>();
	public Map<Integer, PresentPrototype> presentprototypes = new HashMap<Integer, PresentPrototype>();
	public static Map<Integer, Integer> mapcreators = new HashMap<Integer, Integer>();								//сколько раз создавали карту
	public AMFDataArray itemprototypesAMFW = new AMFDataArray();
	public AMFDataArray presentprototypesAMFW = new AMFDataArray();
	
	//вместо конструктора
	public void onAppStart(IApplicationInstance appInstance){
		initialize();
	}
	public void initialize(){
		initializeItemPrototypes();
		initializePresentPrototypes();
	}
	
	//инициализация прототипов вещей
	public void initializeItemPrototypes(){		
		Connection _sqlconnection = null;
		PreparedStatement select = null;
		ResultSet selectRes = null;
		ArrayList<ItemPrototype> itemprototypesArr = new ArrayList<ItemPrototype>();
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			select = _sqlconnection.prepareStatement("SELECT * FROM itemprototype");
			
			selectRes = select.executeQuery();    		
			while(selectRes.next()){
    			ItemPrototype item = new ItemPrototype(selectRes.getInt("id"), selectRes.getString("title"), selectRes.getString("description"), 
    													selectRes.getInt("params"), selectRes.getInt("price"), selectRes.getInt("showed"), 
    													selectRes.getInt("maxquality"), selectRes.getInt("minlevel"), selectRes.getString("url"));
    			
    			itemprototypes.put(item.id, item);
    			itemprototypesArr.add(item);
    		}
			SortShopItems comparator = new SortShopItems();
			Collections.sort(itemprototypesArr, comparator);
			
			itemprototypesAMFW = AMFWObjectBuilder.createObjItemPrototypes(itemprototypesArr);
			
			itemprototypesArr = null;
		} catch (SQLException e) {
			logger.error("initializeItemPrototypes SQL ERROR: " + e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	if (select != null) select.close();
		    	if (selectRes != null) selectRes.close();
		    	_sqlconnection = null;
		    	select = null;
		    	selectRes = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
	}
	
	//инициализация прототипов подарков
	public void initializePresentPrototypes(){		
		Connection _sqlconnection = null;
		PreparedStatement select = null;
		ResultSet selectRes = null;
		ArrayList<PresentPrototype> presentprototypesArr = new ArrayList<PresentPrototype>();
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			select = _sqlconnection.prepareStatement("SELECT * FROM presentprototype");
			
			selectRes = select.executeQuery();    		
			while(selectRes.next()){
    			PresentPrototype present = new PresentPrototype(selectRes.getInt("id"), selectRes.getString("title"), selectRes.getInt("price"), selectRes.getInt("showed"), selectRes.getString("url"));
    			presentprototypes.put(present.id, present);
    			presentprototypesArr.add(present);
    		}
			SortPresents comparator = new SortPresents();
			Collections.sort(presentprototypesArr, comparator);
			
			presentprototypesAMFW = AMFWObjectBuilder.createObjPresentPrototypes(presentprototypesArr);
			
			presentprototypesArr = null;
		} catch (SQLException e) {
			logger.error("initializePresentPrototypes SQL ERROR: " + e.toString());
		}
		finally
		{
		    try{
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	if (select != null) select.close();
		    	if (selectRes != null) selectRes.close();
		    	_sqlconnection = null;
		    	select = null;
		    	selectRes = null;
		    }
		    catch (SQLException sqlx) {		     
		    }
		}
	}
	
	//получение списка прототипов вещей
	public void shopGetItemPrototypes(IClient client, RequestFunction function, AMFDataList params){		
		//return ArrayList<ItemPrototype>
		sendResult(client, params, itemprototypesAMFW);
	}
	
	//получение списка прототипов подарков
	public void shopGetPresentPrototypes(IClient client, RequestFunction function, AMFDataList params){		
		//return ArrayList<PresentPrototype>
		sendResult(client, params, presentprototypesAMFW);
	}
	
	//заполнение списка вещей пользователя
	public void shopFillItems(UserClient user){
		Connection _sqlconnection = null;
		PreparedStatement select = null;
		ResultSet selectRes = null;		
		
		if(user != null){
			user.user.itemsArr = new ArrayList<Item>();
			user.user.itemsObj = new HashMap<Integer, Item>();
			
			try {
				_sqlconnection = ServerApplication.application.sqlpool.getConnection();
				select = _sqlconnection.prepareStatement("SELECT * FROM usersitems WHERE iduser=?");
				select.setInt(1, user.user.id);
				selectRes = select.executeQuery(); 		
				ItemPrototype prototype;
				while(selectRes.next()){
					prototype = itemprototypes.get(selectRes.getInt("idprototype"));
	    			Item item = new Item(selectRes.getInt("id"), prototype.id, prototype.title, prototype.description, 
	    								prototype.param1, prototype.param2, prototype.param3, prototype.param4, 
	    								prototype.param5, prototype.param6, prototype.param7, prototype.price, 
	    								selectRes.getInt("maxquality"), selectRes.getInt("quality"), selectRes.getInt("onuser"), prototype.url);		    								
	    			user.user.itemsArr.add(item);
	    			user.user.itemsObj.put(selectRes.getInt("id"), item);
	    		}
				prototype = null;
			} catch (SQLException e) {
				logger.error("shopGetUserItems SQL ERROR: " + e.toString());
			}
			finally
			{
			    try{
			    	if (_sqlconnection != null) _sqlconnection.close(); 
			    	if (select != null) select.close(); 
			    	if (selectRes != null) selectRes.close();
			    	_sqlconnection = null;
			    	select = null;
			    	selectRes = null;
			    }
			    catch (SQLException sqlx) {		     
			    }
			}
		}
	}
	
	//получение списка подарков пользователя
	public void shopGetUserPresents(IClient client, RequestFunction function, AMFDataList params){
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		Connection _sqlconnection = null;
		PreparedStatement select = null;
		ResultSet selectRes = null;	

		ArrayList<ItemPresent> presentsArr = new ArrayList<ItemPresent>();		
		if(user != null){
			try {
				_sqlconnection = ServerApplication.application.sqlpool.getConnection();
				select = _sqlconnection.prepareStatement("SELECT * FROM userspresents INNER JOIN user ON userspresents.idpresenter=user.id WHERE iduser=?");
				select.setInt(1, user.user.id);
				selectRes = select.executeQuery(); 		
				PresentPrototype prototype;
				while(selectRes.next()){
					prototype = presentprototypes.get(selectRes.getInt("idprototype"));
	    			ItemPresent item = new ItemPresent(selectRes.getInt("userspresents.id"), selectRes.getInt("userspresents.iduser"), selectRes.getString("user.title"), prototype.title, prototype.price, prototype.showed, prototype.url);		    								
	    			presentsArr.add(item);
	    		}
				prototype = null;
			} catch (SQLException e) {
				logger.error("shopGetUserPresents SQL ERROR: " + e.toString());
			}
			finally
			{
			    try{
			    	if (_sqlconnection != null) _sqlconnection.close(); 
			    	if (select != null) select.close(); 
			    	if (selectRes != null) selectRes.close();
			    	_sqlconnection = null;
			    	select = null;
			    	selectRes = null;
			    }
			    catch (SQLException sqlx) {		     
			    }
			}
		}
		
		//return ArrayList<Item>
		sendResult(client, params, AMFWObjectBuilder.createObjItemsPresent(presentsArr));
		user = null;
	}
	
	//покупка вещи
	public void shopBuyItem(IClient client, RequestFunction function, AMFDataList params){
    	int ipid = getParamInt(params, PARAM1);	
		BuyResult buyresult = new BuyResult();
		
		Connection _sqlconnection = null;
		PreparedStatement insertitem = null;
		PreparedStatement select = null;
		ResultSet selectRes = null;	
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			ItemPrototype prototype = itemprototypes.get(ipid);
			 		
			if(prototype != null){
    			UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
    			if (user.user.money >= prototype.price){
    				if(prototype.minlevel <= user.user.level){
	    				insertitem = _sqlconnection.prepareStatement("INSERT INTO usersitems (iduser, idprototype, onuser, quality, maxquality) VALUES(?,?,?,?,?)");
		    			insertitem.setInt(1, user.user.id);
		    			insertitem.setInt(2, prototype.id);
		    			insertitem.setInt(3, 0);
		    			insertitem.setInt(4, prototype.maxquality);
		    			insertitem.setInt(5, prototype.maxquality);
		    			
		    			if (insertitem.executeUpdate() > 0){
		    				//поиск и добавление новой вещи
		    				select = _sqlconnection.prepareStatement("SELECT * FROM usersitems WHERE iduser=?");
		    				select.setInt(1, user.user.id);
		    				selectRes = select.executeQuery();
		    				
		    				Item userItem;
		    				while(selectRes.next()){
		    					userItem = user.user.itemsObj.get(selectRes.getInt("id"));
		    					if(userItem == null){
		    						prototype = itemprototypes.get(selectRes.getInt("idprototype"));
		    						userItem = new Item(selectRes.getInt("id"), prototype.id, prototype.title, prototype.description, 
		    								prototype.param1, prototype.param2, prototype.param3, prototype.param4, 
		    								prototype.param5, prototype.param6, prototype.param7, prototype.price, 
		    								selectRes.getInt("maxquality"), selectRes.getInt("quality"), selectRes.getInt("onuser"), prototype.url);		    								
					    			user.user.itemsArr.add(userItem);
					    			user.user.itemsObj.put(selectRes.getInt("id"), userItem);
					    			
					    			buyresult.item = userItem;
		    					}
		    	    		}
		    				userItem = null;
		    			}else{    	    				
		    				buyresult.error = BuyErrorCode.OTHER;
		    				//return BuyResult
	            			sendResult(client, params, AMFWObjectBuilder.createObjBuyResult(buyresult));
	            			return;
		    			}    
	    				
	    				user.user.updateMoney(user.user.money - prototype.price);
	    				user.user.popular = user.user.popular + (int) Math.floor((double) prototype.price / 10);
	    				ServerApplication.application.commonroom.changeUserInfoByID(user.user.id, ChangeInfoParams.USER_MONEY_POPULAR, user.user.money, user.user.popular);
	    				
	    				buyresult.error = BuyErrorCode.OK;
	    				
	    				prototype = null;
	    				user = null;
	    			}else{
	    				buyresult.error = BuyErrorCode.LOW_LEVEL;
	    			}
    			}else{
    				buyresult.error = BuyErrorCode.NOT_ENOUGH_MONEY;
    			}
    		}else{
    			buyresult.error = BuyErrorCode.NOT_PROTOTYPE;
    		}		
		} catch (SQLException e) {
			buyresult.error = BuyErrorCode.SQL_ERROR;
			logger.error("shopBuyItem SQL ERROR: " + e.toString());
		}
		finally
		{
		    try{
		    	if (insertitem != null) insertitem.close();
		    	if (select != null) select.close();
		    	if (selectRes != null) selectRes.close();
		    	if (_sqlconnection != null) _sqlconnection.close();
		    	insertitem = null;	
		    	select = null;
		    	selectRes = null;
		    	_sqlconnection = null;
		    }
		    catch (SQLException sqlx) {
		    }
		}
		//return BuyResult
		sendResult(client, params, AMFWObjectBuilder.createObjBuyResult(buyresult));
		return;
	}
	
	//покупка подарка
	public void shopBuyPresent(IClient client, RequestFunction function, AMFDataList params){
    	int ipid = getParamInt(params, PARAM1);
    	int userid = getParamInt(params, PARAM2);
	
		BuyPresentResult buyresult = new BuyPresentResult();
		
		Connection _sqlconnection = null;		
		PreparedStatement insertitem = null;		
		
		try {
			_sqlconnection = ServerApplication.application.sqlpool.getConnection();
			PresentPrototype prototype = presentprototypes.get(ipid);
			
			if(prototype != null){   			
    			UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
    			if(user != null && user.client.isConnected()){
	    			if (user.user.money >= prototype.price){    				
	    				insertitem = _sqlconnection.prepareStatement("INSERT INTO userspresents (iduser, idpresenter, idprototype) VALUES(?,?,?)");
		    			insertitem.setInt(1, userid);
		    			insertitem.setInt(2, user.user.id);
		    			insertitem.setInt(3, prototype.id);
		    			if (insertitem.executeUpdate() > 0){
		    			}else{
		    				buyresult.error = BuyErrorCode.OTHER;
		    				//return BuyResult
		    				sendResult(client, params, AMFWObjectBuilder.createObjBuyPresentResult(buyresult));
		    				return;
		    			}
		    			
	    				user.user.updateMoney(user.user.money - prototype.price);
	    				user.user.popular = user.user.popular + (int) Math.floor((double) prototype.price / 10);	    				
	    				ServerApplication.application.commonroom.changeUserInfoByID(user.user.id, ChangeInfoParams.USER_MONEY_POPULAR, user.user.money, user.user.popular);
	    				
	    				ServerApplication.application.commonroom.sendMessagePresent(prototype.id, new Integer(user.client.getClientId()).toString(), Integer.toString(userid));	            		
	            		
	    				buyresult.prototype = prototype;
	    				buyresult.error = BuyErrorCode.OK;
	    				
	    				prototype = null;
	    				user = null;
	    			}else{
	    				buyresult.error = BuyErrorCode.NOT_ENOUGH_MONEY;
	    			}
    			}else{
    				buyresult.error = BuyErrorCode.OTHER;
    				//return BuyResult
    				sendResult(client, params, AMFWObjectBuilder.createObjBuyPresentResult(buyresult));
    				return;
    			}
    		}else{
    			buyresult.error = BuyErrorCode.NOT_PROTOTYPE;
    		}		
		} catch (SQLException e) {
			buyresult.error = BuyErrorCode.SQL_ERROR;
			logger.error("shopBuyPresent SQL ERROR: " + e.toString());
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
		//return BuyResult
		sendResult(client, params, AMFWObjectBuilder.createObjBuyPresentResult(buyresult));
		return;
	}
	
	//продать подарок
	public void shopSalePresent(IClient client, RequestFunction function, AMFDataList params){
    	int itemid = getParamInt(params, PARAM1);
	
		Boolean sqlok = false;
		UseResult result = new UseResult();
		result.itemid = itemid;		
		
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		if(user != null && user.client.isConnected()){		
			Connection _sqlconnection = null;
			PreparedStatement finditem = null;
			ResultSet finditemRes = null;
			PreparedStatement deleteitem = null;
			
			try {
				_sqlconnection = ServerApplication.application.sqlpool.getConnection();
				finditem = _sqlconnection.prepareStatement("SELECT * FROM userspresents WHERE id=? AND iduser=?");   				
				finditem.setInt(1, itemid);
				finditem.setInt(2, user.user.id);
				finditemRes = finditem.executeQuery(); 		
				if(finditemRes.next()){
					deleteitem = _sqlconnection.prepareStatement("DELETE FROM userspresents WHERE id=?");
					deleteitem.setInt(1, itemid);			
	        		if (deleteitem.executeUpdate() > 0){
	        			sqlok = true;
	        		}else{        			
	        			result.error = UseErrorCode.ERROR;
	        		}   
					
					if (sqlok){
						PresentPrototype prototype = presentprototypes.get(finditemRes.getInt("idprototype"));
						if(prototype != null){
							int price = (int)Math.floor((float)prototype.price * 0.5);
							
							user.user.updateMoney(user.user.money + price);
							ServerApplication.application.commonroom.changeUserInfoByID(user.user.id, ChangeInfoParams.USER_MONEY, user.user.money, 0);
		    				
		            		result.error = UseErrorCode.GAMEACTION_OK;
						}
					}
	    		}else{
	    			result.error = UseErrorCode.ERROR;
	    		}			
			} catch (SQLException e) {
				logger.error("shopSalePresent SQL ERROR: " + e.toString());
			}
			finally
			{
			    try{
			    	if (_sqlconnection != null) _sqlconnection.close(); 
			    	if (finditem != null) finditem.close(); 
			    	if (finditemRes != null) finditemRes.close();		    	
			    	if (deleteitem != null) deleteitem.close();
			    	_sqlconnection = null;
			    	finditem = null;
			    	finditemRes = null;		    	
			    	deleteitem = null;
			    }
			    catch (SQLException sqlx) {		     
			    }
			    user = null;
			}
		}		
		//return UseResult
		sendResult(client, params, AMFWObjectBuilder.createObjUseResult(result));
		return;
	}
	
	//починить вещь
	public void shopFixItem(IClient client, RequestFunction function, AMFDataList params){
    	int itemid = getParamInt(params, PARAM1);	
		
		BuyResult result = new BuyResult();
		result.error = BuyErrorCode.OTHER;
		
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		if(user != null && user.client.isConnected()){		
			Item item = user.user.itemsObj.get(itemid);
			if(item != null){
				int price = (int) Math.floor((double) (item.price * 0.5 * 0.5));
				if(user.user.money >= price){
					int maxquality = item.maxquality;					
					int beemaxquality = (int) Math.floor((double) (maxquality * 0.8));
					
					item.quality = item.maxquality = beemaxquality;
					item.change = true;
					
					user.user.updateMoney(user.user.money - price);
    				ServerApplication.application.commonroom.changeUserInfoByID(user.user.id, ChangeInfoParams.USER_MONEY, user.user.money, 0);
    				
					result.error = BuyErrorCode.OK;
					result.item = item;
				}
			}
		}		
		//return BuyResult
		sendResult(client, params, AMFWObjectBuilder.createObjBuyResult(result));
		return;
	}
	
	//надеть вещь
	public void shopPutItem(IClient client, RequestFunction function, AMFDataList params){
    	int itemid = getParamInt(params, PARAM1);	
		
    	UseResult result = new UseResult();
		result.error = UseErrorCode.ERROR;
		
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		if(user != null && user.client.isConnected()){
			Item item = user.user.itemsObj.get(itemid);
			if(item != null){
				item.onuser = 1;
				item.change = true;
				
				result.error = UseErrorCode.GAMEACTION_OK;
				result.itemid = item.id;
			}
		}		
		//return BuyResult
		sendResult(client, params, AMFWObjectBuilder.createObjUseResult(result));
		return;
	}
	
	//снять вещь
	public void shopTakeOffItem(IClient client, RequestFunction function, AMFDataList params){
    	int itemid = getParamInt(params, PARAM1);	
		
    	UseResult result = new UseResult();
		result.error = UseErrorCode.ERROR;
		
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		if(user != null && user.client.isConnected()){
			Item item = user.user.itemsObj.get(itemid);
			if(item != null){
				item.onuser = 0;
				item.change = true;
				
				result.error = UseErrorCode.GAMEACTION_OK;
				result.itemid = item.id;
			}
		}
		//return BuyResult
		sendResult(client, params, AMFWObjectBuilder.createObjUseResult(result));
		return;
	}
	
	//выбросить вещь
	public void shopDropItem(IClient client, RequestFunction function, AMFDataList params){
    	int itemid = getParamInt(params, PARAM1);
    	
		UseResult result = new UseResult();
		result.error = UseErrorCode.ERROR;
		result.itemid = itemid;		
		
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		if(user != null && user.client.isConnected()){		
			Connection _sqlconnection = null;
			PreparedStatement deleteItem = null;
			
			try {
				_sqlconnection = ServerApplication.application.sqlpool.getConnection();
				deleteItem = _sqlconnection.prepareStatement("DELETE FROM usersitems WHERE id=?");   				
				deleteItem.setInt(1, itemid);
				if(deleteItem.executeUpdate() > 0){
					result.error = UseErrorCode.GAMEACTION_OK;
					
					Set<Entry<Integer, Item>> set = user.user.itemsObj.entrySet();
					for (Map.Entry<Integer, Item> item:set){
						if(item.getValue().id == itemid){
							user.user.itemsObj.remove(item.getValue().id);
							break;
						}
					}						
					for(int i = 0; i < user.user.itemsArr.size(); i++){					
						if(user.user.itemsArr.get(i).id == itemid){
							user.user.itemsArr.remove(i);
							break;
						}
					}					
				}	
			} catch (SQLException e) {
				logger.error("shopFixItem SQL ERROR: " + e.toString());
			}
			finally
			{
			    try{
			    	if (_sqlconnection != null) _sqlconnection.close(); 
			    	if (deleteItem != null) deleteItem.close(); 
			    	_sqlconnection = null;
			    	deleteItem = null;
			    }
			    catch (SQLException sqlx) {		     
			    }
			    user = null;
			}
		}		
		//return UseResult
		sendResult(client, params, AMFWObjectBuilder.createObjUseResult(result));
		return;
	}
	
	public void shopWithdrawVotesVK(IClient client, RequestFunction function, AMFDataList params){
    	int uid = getParamInt(params, PARAM1);
    	int votes = getParamInt(params, PARAM2);
    	String api_url = getParamString(params, PARAM3);
//    	int appID = getParamInt(params, PARAM4);
    	int addMoneyUserID = getParamInt(params, PARAM5);

		BuyMoneyResult result = new BuyMoneyResult();

		UserClient user = ServerApplication.application.commonroom.users.get(Integer.toString(addMoneyUserID));
    	if(user != null){
    		String social = user.user.idSocial.substring(0, 2);
    		if(social.equals("vk")){
    			uid = new Integer(user.user.idSocial.substring(2, user.user.idSocial.length()));
    		}
    	}
		
		if(votes > 0){
			int withdrawVotes = 0;
			
			try
			{
				 URL url = new URL(api_url);
				 DataOutputStream wr = null;
				 InputStream is = null;
				 BufferedReader rd = null;
				 InputStream istr = null;
				
				 try{				 
					 long time = (new Date()).getTime();
					 int random = Math.round((float)Math.random() * Integer.MAX_VALUE);
						
					 String sigstr = "api_id=" + Config.appIdVK() + "method=" + "secure.withdrawVotes" +
		 							"random=" + random + "timestamp=" + time + "uid=" + uid + "v=3.0" + "votes=" + votes + Config.protectedSecretVK();				 
					 
					 String urlparams = "api_id=" + Config.appIdVK() + "&method=" + "secure.withdrawVotes" +
						 				"&random=" + random + "&timestamp=" + time +
						 				"&uid=" + uid + "&v=3.0" + "&votes=" + votes + "&sig=" + MD5.getMD5(sigstr);
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
			        		         
			         is = urlConnection.getInputStream();
			        
			         rd = new BufferedReader(new InputStreamReader(is));			         
			         String line;
			         StringBuffer response = new StringBuffer();
			         int counter = 0;
			         while((line = rd.readLine()) != null && counter < 250) {
						response.append(line);
						response.append('\r');
						counter++;				
			         }
			        
			         rd.close();
			         String answer = response.toString();	       
			       
			         Document document = null;
			         DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			         factory.setValidating(false);
			         factory.setNamespaceAware(true);
			         
			         try
			         {
			        	 if(answer.indexOf("transferred") == -1){
			        		 ServerApplication.application.logger.log("BAD ANSWER: " + answer);
			        		 answer = "bad answer";
			        	 }
			        	 			        	 
			        	 istr = new ByteArrayInputStream(answer.getBytes());		        	 
			        	 DocumentBuilder builder = factory.newDocumentBuilder();						
						
						try{							
							document = builder.parse(istr);
						}catch (Throwable e) {
							ServerApplication.application.logger.log("not parse " + e.toString());
						}			
						
						document.getDocumentElement().normalize();						
						NodeList nodelist = document.getDocumentElement().getElementsByTagName("transferred");
						Element fstNmElmnt = (Element) nodelist.item(0);		             
						withdrawVotes = new Integer(fstNmElmnt.getTextContent());
						
			            if (withdrawVotes > 0){
			            	 int addmoney = Math.round((float)((float) withdrawVotes / 100) * Config.moneyToVote());		            	 
			            	 if (ServerApplication.application.userinfomanager.addMoney(addMoneyUserID, addmoney, user)){			            		 
			            		 result.money = addmoney;
			            		 result.error = BuyMoneyErrorCode.OK;
			            	 }
			             }		            
						builder = null;
						nodelist = null;
						fstNmElmnt = null;
			         }
			         catch (Exception e){
			         	logger.error("Error Parse VK withdrawVotes answer: " + answer);		         	
			         }			         
			         factory = null;
			         document = null;			         
			         urlConnection = null;
				 }				
				 catch(IOException e){
					 logger.log("SM9" + e.toString());
				 }
				 finally
					{
					    try{
					    	if (wr != null) wr.close();
					    	if (is != null) is.close();
					    	if (rd != null) rd.close();
					    	if (istr != null) istr.close();
					    	istr = null;
					    	wr = null;
					    	is = null;
					    	rd = null;					    	
					    }
					    catch (Throwable e) {
					    	logger.log("SM15" + e.getMessage());
					    }
					}
			}catch(MalformedURLException e){
				logger.log("SM10" + e.toString());
			}
			user = null;
		}
		
		//return BuyMoneyResult
		sendResult(client, params, AMFWObjectBuilder.createObjBuyMoneyResult(result));
		return;
	}
	
	public void shopBuyLink(IClient client, RequestFunction function, AMFDataList params){
		BuyResult result = new BuyResult();	
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));		
		
		boolean needbuy = true;
		if (user.user.role == UserRole.MODERATOR || user.user.role == UserRole.ADMINISTRATOR || user.user.role == UserRole.ADMINISTRATOR_MAIN){
			needbuy = false;
		}
		
		if(user != null && user.client.isConnected()){		
			if (user.user.money >= Config.showLinkPrice() || !needbuy){
				if(needbuy){
					user.user.updateMoney(user.user.money - Config.showLinkPrice());
					ServerApplication.application.commonroom.changeUserInfoByID(user.user.id, ChangeInfoParams.USER_MONEY, user.user.money, 0);
				}    			
    			result.error = BuyErrorCode.OK;
			}else{
				result.error = BuyErrorCode.NOT_ENOUGH_MONEY;
			}
		}else{
			result.error = BuyErrorCode.OTHER;
			//return BuyResult
			sendResult(client, params, AMFWObjectBuilder.createObjBuyResult(result));
			return;
		}
		user = null;		
		//return BuyResult
		sendResult(client, params, AMFWObjectBuilder.createObjBuyResult(result));
		return;
	}
	
	public void shopBuyBullets(IClient client, RequestFunction function, AMFDataList params){
		BuyResult result = new BuyResult();	
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		
		if(user != null && user.client.isConnected()){	
			int bulletsPrice = 100;
			int bulletsCount = 100;
			if (user.user.money >= bulletsPrice){
				user.user.updateMoney(user.user.money - bulletsPrice);
				user.user.bullets += bulletsCount;
				user.user.popular = user.user.popular + (int) Math.floor((double) bulletsPrice / 10);
				ServerApplication.application.commonroom.changeUserInfoByID(user.user.id, ChangeInfoParams.USER_MONEY_POPULAR, user.user.money, user.user.popular);
				
				ServerApplication.application.commonroom.changeUserInfoByID(user.user.id, ChangeInfoParams.USER_MONEY_EXPERIENCE_BULLETS, user.user.money, user.user.experience, user.user.bullets);
    			result.error = BuyErrorCode.OK;
			}else{
				result.error = BuyErrorCode.NOT_ENOUGH_MONEY;
			}
		}else{
			result.error = BuyErrorCode.OTHER;
			//return BuyResult
			sendResult(client, params, AMFWObjectBuilder.createObjBuyResult(result));
			return;
		}
		user = null;		
		//return BuyResult
		sendResult(client, params, AMFWObjectBuilder.createObjBuyResult(result));
		return;
	}
	
	public void shopGetPriceBanOff(IClient client, RequestFunction function, AMFDataList params){
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		
		int allduration = 0;
		if (user.user.bantype == BanType.MINUT5){
			allduration = 5 * 60;
		}else if (user.user.bantype == BanType.MINUT15){
			allduration = 15 * 60;
		}else if (user.user.bantype == BanType.MINUT30){
			allduration = 30 * 60;
		}else if (user.user.bantype == BanType.HOUR1){
			allduration = 60 * 60;
		}else if (user.user.bantype == BanType.DAY1){
			allduration = 60 * 60 * 24;
		}
		
		int price = (int) Math.ceil((float)(Math.max(0, user.user.setbanat + allduration - user.user.changebanat)) / 60) * Config.banminutePrice();
		user = null;		
		//return int
		sendResult(client, params, price);
		return;
	}
	
	public void shopBuyBanOff(IClient client, RequestFunction function, AMFDataList params){
		BuyResult result = new BuyResult();	
		UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
		
		if(user != null && user.client.isConnected()){
			int allduration = 0;
			if (user.user.bantype == BanType.MINUT5){
				allduration = 5 * 60;
			}else if (user.user.bantype == BanType.MINUT15){
				allduration = 15 * 60;
			}else if (user.user.bantype == BanType.MINUT30){
				allduration = 30 * 60;
			}else if (user.user.bantype == BanType.HOUR1){
				allduration = 60 * 60;
			}else if (user.user.bantype == BanType.DAY1){
				allduration = 60 * 60 * 24;
			}
			
			int price = (int) Math.ceil((float)(Math.max(0, user.user.setbanat + allduration - user.user.changebanat)) / 60) * Config.banminutePrice();
			
			if (user.user.role == UserRole.MODERATOR || user.user.role == UserRole.ADMINISTRATOR){
				price = (int) Math.floor((float) price / 10);
			}
			if (user.user.role == UserRole.ADMINISTRATOR_MAIN){
				price = 0;
			}
			
			if (user.user.money >= price){
				user.user.updateMoney(user.user.money - Math.abs(price));
				
				ServerApplication.application.userinfomanager.banoff(user.user.id, user.user.ip);
    			result.error = BuyErrorCode.OK;
    			
    			user.user.bantype = BanType.NO_BAN;
				
    			ServerApplication.application.commonroom.sendBanOutMessage(user.user.id);
				ServerApplication.application.commonroom.changeUserInfoByID(user.user.id, ChangeInfoParams.USER_MONEY_BANTYPE, user.user.money, user.user.bantype);
			}else{
				result.error = BuyErrorCode.NOT_ENOUGH_MONEY;
			}
		}else{
			result.error = BuyErrorCode.OTHER;
			//return BuyResult
			sendResult(client, params, AMFWObjectBuilder.createObjBuyResult(result));
			return;
		}
		user = null;
		
		//return BuyResult
		sendResult(client, params, AMFWObjectBuilder.createObjBuyResult(result));
		return;
	}
	
	public void shopSaveMap(IClient client, RequestFunction function, AMFDataList params){
		String fileName = getParamString(params, PARAM1);
    	String content = getParamString(params, PARAM2);
		
    	int price = Config.saveMapPrice();
    	int countCreate = 0;
    	UserClient user = ServerApplication.application.commonroom.getUserByClientID(Integer.toString(client.getClientId()));
    	
    	if(mapcreators.get(user.user.id) != null){
    		countCreate = mapcreators.get(user.user.id);
    	}
    	BuyResult buyresult = new BuyResult();
    	
    	if(user != null){
    		if(user.user.level >= 5){
    			if(user.user.money >= price){
    				if(countCreate < 3){
    					File f = new File("newmaps/" + fileName);
        				if (!f.exists()){
        					try {
        						f.createNewFile();
        						
        						Writer out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(f.getPath()), "UTF-8"));
    							try {
    							    out.write(content);
    							} finally {
    							    out.close();
    							}
        						
        						user.user.updateMoney(user.user.money - price);          		
        						ServerApplication.application.commonroom.changeUserInfoByID(user.user.id, ChangeInfoParams.USER_MONEY, user.user.money, 0);
        						
        						buyresult.error = BuyErrorCode.OK;

        						countCreate++;
        						mapcreators.put(user.user.id, countCreate);
        					} catch (Exception e) {	
        						buyresult.error = BuyErrorCode.OTHER;
        						ServerApplication.application.getLoggerInstance().info("don't create map file: " + e.getMessage());				 
        					}
        				}else{
        					buyresult.error = BuyErrorCode.EXIST;
        				}
    				}else{
    					buyresult.error = BuyErrorCode.OTHER;
    				}    				
    			}else{
    				buyresult.error = BuyErrorCode.NOT_ENOUGH_MONEY;
    			}
    		}else{
    			buyresult.error = BuyErrorCode.LOW_LEVEL;
    		}
    	}else{
    		buyresult.error = BuyErrorCode.OTHER;
    	}
    	
    	
		//return BuyResult		
		sendResult(client, params, AMFWObjectBuilder.createObjBuyResult(buyresult));
	}
}