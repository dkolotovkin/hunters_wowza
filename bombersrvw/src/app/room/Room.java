package app.room;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import app.Config;
import app.logger.MyLogger;
import app.message.Message;
import app.message.MessageType;
import app.message.ban.BanMessage;
import app.message.simple.MessageSimple;
import app.user.ChatInfo;
import app.user.User;
import app.user.UserClient;
import app.utils.amfwobjectbuilder.AMFWObjectBuilder;
import app.utils.ban.BanType;

import com.wowza.wms.amf.AMFDataObj;

public class Room {
	public MyLogger logger = new MyLogger(Room.class.getName());
	
	public int id;
	public String title;
	public Map<String, UserClient> users;
	public Map<String, String> userClientIDtoID;
	public Map<String, String> userSocialIDtoID;
	public List<Message> messages;
	
	public Room(int id, String title){
		this.id = id;
		this.title = title;
		
		users = new ConcurrentHashMap<String, UserClient>();
		userClientIDtoID = new ConcurrentHashMap<String, String>();
		userSocialIDtoID = new ConcurrentHashMap<String, String>();
		messages = new ArrayList<Message>();
	}
	
	public void addUser(UserClient u){		
		if (u != null && u.client != null && u.client.isConnected()){
			
			users.put(Integer.toString(u.user.id), u);	
			userClientIDtoID.put(new Integer(u.client.getClientId()).toString(), Integer.toString(u.user.id));			
			userSocialIDtoID.put(u.user.idSocial, Integer.toString(u.user.id));			
			
			AMFDataObj amfMessage = AMFWObjectBuilder.createObjMessageStatus(MessageType.USER_IN, this, ChatInfo.createFromUser(u.user));
			List<ChatInfo> usersinroom = new ArrayList<ChatInfo>();			
			
			Set<Entry<String, UserClient>> set = users.entrySet();
			for (Map.Entry<String, UserClient> user:set){
				usersinroom.add(ChatInfo.createFromUser(user.getValue().user));
				if(user.getValue().user.id != u.user.id){
					if(user.getValue().client.isConnected()){
						user.getValue().client.call("processMassage", null, amfMessage);
					}					
				}
			}			
			set = null;
			
			amfMessage = null;
			amfMessage = AMFWObjectBuilder.createObjMessageStatus(MessageType.USER_IN, this, ChatInfo.createFromUser(u.user), messages, usersinroom);
			
			if(u.client != null && u.client.isConnected()){
				u.client.call("processMassage", null, amfMessage);
			}
			
			usersinroom = null;
			amfMessage = null;			
		}
	}
	
	public void removeUserByClientID(String connID){
		String userID = userClientIDtoID.get(connID);
		if (userID != null && userID != "null"){
			AMFDataObj amfMessage = AMFWObjectBuilder.createObjMessageOutStatus(MessageType.USER_OUT, this, users.get(userID).user.id);
			
			Set<Entry<String, UserClient>> set = users.entrySet();
			for (Map.Entry<String, UserClient> user:set){
				if(user.getValue().client.isConnected()){
					user.getValue().client.call("processMassage", null, amfMessage);
				}
			}
			set = null;
			
			if(users.get(userID) != null) userSocialIDtoID.remove(users.get(userID).user.idSocial);
			userClientIDtoID.remove(connID);
			users.remove(userID);			
			
			amfMessage = null;
		}
	}
	
	public UserClient getUserByClientID(String cID){
		String userID = userClientIDtoID.get(cID);
		if (userID != null && userID != "null"){
			return users.get(userID);
		}
		return null;
	}
	
	public UserClient getUserBySocialID(String socialID){
		String userID = userSocialIDtoID.get(socialID);
		if (userID != null && userID != "null"){
			return users.get(userID);
		}
		return null;
	}
	
	public void sendMessage(String mtext, String initiatorConnID, String receiverID){
		String userID = userClientIDtoID.get(initiatorConnID);		
		if (userID != null && userID != "null" && users.get(userID) != null && users.get(userID).user.bantype == BanType.NO_BAN &&
			users.get(userID).user.level >= 2){
			
			User receiverUser = null;
			UserClient receiverUserConnetion = users.get(receiverID);			
			if (receiverUserConnetion != null){
				receiverUser = receiverUserConnetion.user;
			}
			
			int fromId = 0;
			if(users.get(userID).user != null) fromId = users.get(userID).user.id;
			int toId = 0;
			if(receiverUser != null) toId = receiverUser.id;
			
			MessageSimple message = new MessageSimple(MessageType.MESSAGE, this.id, mtext, fromId, toId);
			AMFDataObj amfMessage = AMFWObjectBuilder.createObjMessageSimple(MessageType.MESSAGE, this.id, mtext, fromId, toId);
			
			Set<Entry<String, UserClient>> set = users.entrySet();
			for (Map.Entry<String, UserClient> user:set){
				if(fromId != user.getValue().user.id){
					if(user.getValue().client.isConnected()){
						user.getValue().client.call("processMassage", null, amfMessage);
					}
				}
			}
			set = null;
			
			messages.add(message);
			if (messages.size() > Config.maxMessageCountInRoom()){
				messages.remove(0);
			}			
			
			receiverUserConnetion = null;
			receiverUser = null;
			amfMessage = null;
			message = null;
		}
	}
	
	public void sendMessagePresent(int prototypeID, String initiatorConnID, String receiverID){
		String userID = userClientIDtoID.get(initiatorConnID);		
		if (userID != null && userID != "null" && users.get(userID).user.bantype == BanType.NO_BAN){
			
			User receiverUser = null;
			UserClient receiverUserConnetion = users.get(receiverID);			
			if (receiverUserConnetion != null){
				receiverUser = receiverUserConnetion.user;
			}
			
			int fromId = 0;
			if(users.get(userID).user != null) fromId = users.get(userID).user.id;
			int toId = 0;
			if(receiverUser != null) toId = receiverUser.id;
			
//			MessagePresent message = new MessagePresent(MessageType.PRESENT, this.id, prototypeID, fromId, toId);
			AMFDataObj amfMessage = AMFWObjectBuilder.createObjMessagePresent(MessageType.PRESENT, this.id, prototypeID, fromId, toId);			
			
			Set<Entry<String, UserClient>> set = users.entrySet();
			for (Map.Entry<String, UserClient> user:set){
				if(user.getValue().client.isConnected()){
					user.getValue().client.call("processMassage", null, amfMessage);
				}
			}
			set = null;		
			
			receiverUserConnetion = null;
			receiverUser = null;
//			message = null;
			amfMessage = null;
		}
	}
	
	public void sendBanMessage(int bannedId, String initiatorConnId, byte type){
		UserClient initiator = getUserByClientID(initiatorConnId);
		UserClient banned = users.get(Integer.toString(bannedId));
		if (initiator != null){		
			
			BanMessage message = null;
			
			int fromId = 0;
			if(initiator != null && initiator.user != null) fromId = initiator.user.id;
			int toId = 0;
			if(banned != null && banned.user != null) toId = banned.user.id;
			
			message = new BanMessage(MessageType.BAN, this.id, fromId, toId, type);
			AMFDataObj amfMessage = AMFWObjectBuilder.createObjMessageBan(MessageType.BAN, this.id, fromId, toId, type);			
			
			Set<Entry<String, UserClient>> set = users.entrySet();
			for (Map.Entry<String, UserClient> user:set){
				if(user.getValue().client.isConnected()){
					user.getValue().client.call("processMassage", null, amfMessage);
				}
			}
			set = null;
			
			messages.add(message);
			if (messages.size() > Config.maxMessageCountInRoom()){
				messages.remove(0);
			}
			amfMessage = null; 
			message = null;
		}
		initiator = null;
		banned = null;
	}
	
	public void sendSystemMessage(String text){		
		MessageSimple message = new MessageSimple(MessageType.SYSTEM, this.id, text, 0, 0);
		AMFDataObj amfMessage = AMFWObjectBuilder.createObjMessageSimple(MessageType.SYSTEM, this.id, text, 0, 0);		
		
		Set<Entry<String, UserClient>> set = users.entrySet();
		for (Map.Entry<String, UserClient> user:set){
			if(user.getValue().client.isConnected()){
				user.getValue().client.call("processMassage", null, amfMessage);
			}
		}	
		
		messages.add(message);
		if (messages.size() > Config.maxMessageCountInRoom()){
			messages.remove(0);
		}
		
		amfMessage = null;
		set = null;
		message = null;
	}
	
	public void sendBanOutMessage(int userID){
		UserClient initiator = users.get(Integer.toString(userID));
		if (initiator != null){
			
			int fromId = 0;
			if(initiator.user != null) fromId = initiator.user.id;
			int toId = 0;			
			
			BanMessage message = new BanMessage(MessageType.BAN_OUT, this.id, fromId, toId, (byte)0);
			AMFDataObj amfMessage = AMFWObjectBuilder.createObjMessageBan(MessageType.BAN_OUT, this.id, fromId, toId, (byte)0);			
			
			Set<Entry<String, UserClient>> set = users.entrySet();
			for (Map.Entry<String, UserClient> user:set){
				if(user.getValue().client.isConnected()){
					user.getValue().client.call("processMassage", null, amfMessage);
				}
			}
			set = null;
			
			messages.add(message);
			if (messages.size() > Config.maxMessageCountInRoom()){
				messages.remove(0);
			}
			amfMessage = null;
			message = null;
		}
		initiator = null;		
	}
	
	public void sendPrivateMessage(String mtext, String initiatorConnID, String receiverID, int proomID){
		String userID = userClientIDtoID.get(initiatorConnID);		
		if (userID != null && userID != "null" && users.get(userID).user.bantype == BanType.NO_BAN){			
			User receiverUser = null;
			UserClient receiverUserConnetion = users.get(receiverID);			
			if (receiverUserConnetion != null){
				receiverUser = receiverUserConnetion.user;				
			}
			
			int fromId = 0;
			if(users.get(userID).user != null) fromId = users.get(userID).user.id;
			int toId = 0;
			if(receiverUser != null) toId = receiverUser.id;
			
			AMFDataObj amfMessage = AMFWObjectBuilder.createObjMessageSimple(MessageType.PRIVATE, proomID, mtext, fromId, toId);
			
			if(receiverUserConnetion != null && receiverUserConnetion.client.isConnected()){
				if(receiverUserConnetion.client.isConnected()){
					receiverUserConnetion.client.call("processMassage", null, amfMessage);
				}
			}
			receiverUserConnetion = null;
			receiverUser = null;
			amfMessage = null;
		}
	}
	
	public void changeUserInfoByID(int userID, byte param, int value1, int value2){
		if (users.get(Integer.toString(userID)) != null){
			AMFDataObj amfMessage = AMFWObjectBuilder.createObjMessageChangeInfo(MessageType.CHANGEINFO, this.id, param, value1, value2);
			
			try{
				UserClient user = users.get(Integer.toString(userID));
				if(user.client.isConnected()){
					user.client.call("processMassage", null, amfMessage);
				}
				user = null;
			}catch(Throwable e){
				logger.log(e.toString());
			}
			amfMessage = null;
		}
	}
	
	public void changeUserInfoByID(int userID, byte param, int value1, int value2, int value3){
		if (users.get(Integer.toString(userID)) != null){
			AMFDataObj amfMessage = AMFWObjectBuilder.createObjMessageChangeInfo3(MessageType.CHANGEINFO, this.id, param, value1, value2, value3);			
			
			try{
				UserClient user = users.get(Integer.toString(userID));
				if(user.client.isConnected()){
					user.client.call("processMassage", null, amfMessage);
				}
				user = null;
			}catch(Throwable e){
				logger.log(e.toString());
			}
			amfMessage = null;
		}
	}	
	
	public void roomClear(){		
		Set<Entry<String, UserClient>> set = users.entrySet();
		for (Map.Entry<String, UserClient> user:set){
			AMFDataObj amfMessage = AMFWObjectBuilder.createObjMessageOutStatus(MessageType.USER_OUT, this, user.getValue().user.id);
			
			if (user.getValue().client.isConnected()) {
				user.getValue().client.call("processMassage", null, amfMessage);
	    	}
			amfMessage = null;
		}
		set = null;		
		
		users.clear();
		userClientIDtoID.clear();
		userSocialIDtoID.clear();
		messages.clear();
		logger = null;
	}
}