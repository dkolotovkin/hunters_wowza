package app.utils.amfwobjectbuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import app.clan.ClanAllInfo;
import app.clan.ClanInfo;
import app.clan.CreateClanResult;
import app.clan.UserOfClan;
import app.message.Message;
import app.message.auctionstate.AuctionStateMessage;
import app.message.ban.BanMessage;
import app.message.present.MessagePresent;
import app.message.simple.MessageSimple;
import app.room.Room;
import app.room.gamebet.BetInfo;
import app.room.minigamebets.MiniGameBetInfo;
import app.room.minigamebets.MiniGameBetsInfo;
import app.shop.buyMoneyResult.BuyMoneyResult;
import app.shop.buyresult.BuyPresentResult;
import app.shop.buyresult.BuyResult;
import app.shop.checkluckresult.CheckLuckResult;
import app.shop.item.Item;
import app.shop.item.ItemPresent;
import app.shop.itemprototype.ItemPrototype;
import app.shop.presentprototype.PresentPrototype;
import app.shop.useresult.UseResult;
import app.user.ChatInfo;
import app.user.GameInfo;
import app.user.User;
import app.user.UserForTop;
import app.user.UserFriend;
import app.user.UserMailMessage;
import app.user.chage.ChangeResult;
import app.user.game.UserGame;
import app.utils.extraction.ExtractionData;
import app.utils.gameaction.GameAction;
import app.utils.gameaction.bets.MiniGameActionBetsContent;
import app.utils.gameaction.finish.GameActionFinish;
import app.utils.gameaction.start.GameActionStart;

import com.wowza.wms.amf.AMFDataArray;
import com.wowza.wms.amf.AMFDataItem;
import com.wowza.wms.amf.AMFDataObj;

public class AMFWObjectBuilder {
	
	public static AMFDataObj createObjGameAction(byte type, int roomID){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(roomID));			
		return retObj;
	}
	
	public static AMFDataObj createObjGameAction(GameAction ga){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(ga.type));
		retObj.put("roomId", new AMFDataItem(ga.roomId));			
		return retObj;
	}
	
	public static AMFDataObj createObjGameActionEvent(byte type, int roomID, byte subtype,int userId, int sourceId){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(roomID));	
		retObj.put("subtype", new AMFDataItem(subtype));	
		retObj.put("sourceId", new AMFDataItem(sourceId));
		retObj.put("userId", new AMFDataItem(userId));		
		return retObj;
	}
	
	public static AMFDataObj createObjGameActionMotion(byte type, int roomID, byte subtype, int initiatorID, Boolean down, double userx, double usery, double lvx, double lvy){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(roomID));
		retObj.put("subtype", new AMFDataItem(subtype));
		retObj.put("initiatorID", new AMFDataItem(initiatorID));
		retObj.put("down", new AMFDataItem(down));
		retObj.put("userx", new AMFDataItem(userx));
		retObj.put("usery", new AMFDataItem(usery));
		retObj.put("lvx", new AMFDataItem(lvx));
		retObj.put("lvy", new AMFDataItem(lvy));
		
		return retObj;
	}
	
	public static AMFDataObj createObjGameActionMotion(byte type, int roomID, byte subtype, int initiatorID, double tx, double ty){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(roomID));
		retObj.put("subtype", new AMFDataItem(subtype));
		retObj.put("initiatorID", new AMFDataItem(initiatorID));
		retObj.put("tx", new AMFDataItem(tx));
		retObj.put("ty", new AMFDataItem(ty));
		
		return retObj;
	}
	
	public static AMFDataObj createObjGameActionHunterShot(byte type, int roomID, byte subtype, int initiatorID, double tx, double ty, int bullets){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(roomID));
		retObj.put("subtype", new AMFDataItem(subtype));
		retObj.put("initiatorID", new AMFDataItem(initiatorID));
		retObj.put("tx", new AMFDataItem(tx));
		retObj.put("ty", new AMFDataItem(ty));
		retObj.put("bullets", new AMFDataItem(bullets));
		
		return retObj;
	}
	
	public static AMFDataObj createObjGameActionStart(GameActionStart action){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(action.type));
		retObj.put("roomId", new AMFDataItem(action.roomId));
		retObj.put("time", new AMFDataItem(action.time));
		retObj.put("creatorName", new AMFDataItem(action.creatorName));
		retObj.put("hunterId", new AMFDataItem(action.hunterId));
		if(action.locationFile == null){retObj.put("locationFile", new AMFDataItem(""));}else{retObj.put("locationFile", new AMFDataItem(action.locationFile));}
		if(action.locationXML == null){retObj.put("locationXML", new AMFDataItem(""));}else{retObj.put("locationXML", new AMFDataItem(action.locationXML));}
		retObj.put("gametype", new AMFDataItem(action.gametype));
		
		if(action.users != null &&  action.users.size() > 0){
				AMFDataArray retArray = new AMFDataArray();
				for(int i = 0; i < action.users.size(); i++){retArray.add(createObjUserGame(action.users.get(i)));}
			retObj.put("users", retArray);
		}
		
		return retObj;
	}
	
	public static AMFDataObj createObjGameActionFinish(byte type, int roomID, ExtractionData extraction){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(roomID));
		
		if(extraction != null){
				AMFDataObj ext = new AMFDataObj();
				ext.put("experience", new AMFDataItem(extraction.experience));
				ext.put("cexperience", new AMFDataItem(extraction.cexperience));
				ext.put("money", new AMFDataItem(extraction.money));
				ext.put("experiencebonus", new AMFDataItem(extraction.experiencebonus));
				ext.put("cexperiencebonus", new AMFDataItem(extraction.cexperiencebonus));
				ext.put("bullets", new AMFDataItem(extraction.bullets));
			
			retObj.put("extraction", ext);
		}
		
		return retObj;
	}
	
	public static AMFDataObj createObjGameActionFinish(GameActionFinish action){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(action.type));
		retObj.put("roomId", new AMFDataItem(action.roomId));
		retObj.put("position", new AMFDataItem(action.position));
		
		if(action.extraction != null){
				AMFDataObj ext = new AMFDataObj();
				ext.put("experience", new AMFDataItem(action.extraction.experience));
				ext.put("cexperience", new AMFDataItem(action.extraction.cexperience));
				ext.put("money", new AMFDataItem(action.extraction.money));
				ext.put("experiencebonus", new AMFDataItem(action.extraction.experiencebonus));
				ext.put("cexperiencebonus", new AMFDataItem(action.extraction.cexperiencebonus));
				ext.put("bullets", new AMFDataItem(action.extraction.bullets));
			retObj.put("extraction", ext);
		}
		
		return retObj;
	}

	public static AMFDataObj createObjGameActionWaitStart(byte type, int roomID, int waittime){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(roomID));
		retObj.put("waittime", new AMFDataItem(waittime));
		return retObj;
	}
	
	public static AMFDataObj createObjGameActionWaitStart(byte type, int roomID, int waittime, boolean added){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(roomID));
		retObj.put("waittime", new AMFDataItem(waittime));
		retObj.put("added", new AMFDataItem(added));
		return retObj;
	}
	
	public static AMFDataObj createObjMessage(byte type, int roomId){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(roomId));			
		return retObj;
	}
	
	public static AMFDataObj createObjMessageChangeInfo(byte type, int roomId, byte param, int value1, int value2){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(roomId));			
		retObj.put("param", new AMFDataItem(param));
		retObj.put("value1", new AMFDataItem(value1));
		retObj.put("value2", new AMFDataItem(value2));
		return retObj;
	}
	
	public static AMFDataObj createObjMessageChangeInfo3(byte type, int roomId, byte param, int value1, int value2, int value3){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(roomId));			
		retObj.put("param", new AMFDataItem(param));
		retObj.put("value1", new AMFDataItem(value1));
		retObj.put("value2", new AMFDataItem(value2));
		retObj.put("value3", new AMFDataItem(value3));
		return retObj;
	}
	
	public static AMFDataObj createObjMessageBan(byte type, int roomId, int fromId, int toId, byte bantype){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(roomId));
		retObj.put("fromId", new AMFDataItem(fromId));
		retObj.put("toId", new AMFDataItem(toId));
		retObj.put("bantype", new AMFDataItem(bantype));
		
		return retObj;
	}
	
	public static AMFDataObj createObjMessagePresent(byte type, int roomId, int prototypeid, int fromId, int toId){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(roomId));
		retObj.put("prototypeid", new AMFDataItem(prototypeid));
		retObj.put("fromId", new AMFDataItem(fromId));
		retObj.put("toId", new AMFDataItem(toId));
		
		return retObj;
	}
	
	public static AMFDataObj createObjMessageOutStatus(byte type, Room room, int initiatorId){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(room.id));		
		retObj.put("initiatorId", new AMFDataItem(initiatorId));
		return retObj;
	}
	
	public static AMFDataObj createObjMessageStatus(byte type, Room room, ChatInfo initiator){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(room.id));	
		if(room.title == null){retObj.put("roomTitle", new AMFDataItem(""));}else{retObj.put("roomTitle", new AMFDataItem(room.title));}
		
		if(initiator != null){
				AMFDataObj iu = new AMFDataObj();
				iu.put("id", new AMFDataItem(initiator.id));
				iu.put("level", new AMFDataItem(initiator.level));
				iu.put("sex", new AMFDataItem(initiator.sex));
				iu.put("role", new AMFDataItem(initiator.role));
				iu.put("king", new AMFDataItem(initiator.king));
				iu.put("popular", new AMFDataItem(initiator.popular));
				if(initiator.title == null){iu.put("title", new AMFDataItem(""));}else{iu.put("title", new AMFDataItem(initiator.title));}			
				if(initiator.idSocial == null){iu.put("idSocial", new AMFDataItem(""));}else{iu.put("idSocial", new AMFDataItem(initiator.idSocial));}
				if(initiator.url == null){iu.put("url", new AMFDataItem(""));}else{iu.put("url", new AMFDataItem(initiator.url));}
			retObj.put("initiator", iu);
		}		
	
		return retObj;
	}
	
	public static AMFDataObj createObjMessageStatus(byte type, Room room, ChatInfo initiator, List<Message> messages, List<ChatInfo> usersinroom){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(room.id));	
		if(room.title == null){retObj.put("roomTitle", new AMFDataItem(""));}else{retObj.put("roomTitle", new AMFDataItem(room.title));}
		
		if(initiator != null){
				AMFDataObj iu = new AMFDataObj();
				iu.put("id", new AMFDataItem(initiator.id));
				iu.put("level", new AMFDataItem(initiator.level));
				iu.put("sex", new AMFDataItem(initiator.sex));
				iu.put("role", new AMFDataItem(initiator.role));
				iu.put("king", new AMFDataItem(initiator.king));
				iu.put("popular", new AMFDataItem(initiator.popular));
				if(initiator.title == null){iu.put("title", new AMFDataItem(""));}else{iu.put("title", new AMFDataItem(initiator.title));}			
				if(initiator.idSocial == null){iu.put("idSocial", new AMFDataItem(""));}else{iu.put("idSocial", new AMFDataItem(initiator.idSocial));}
				if(initiator.url == null){iu.put("url", new AMFDataItem(""));}else{iu.put("url", new AMFDataItem(initiator.url));}
			retObj.put("initiator", iu);
		}
		
		AMFDataArray retArray = new AMFDataArray();
		Message m;
		if(messages != null && messages.size() > 0){
				for(int i = 0; i < messages.size(); i++){
					m = messages.get(i);
					if(m != null){
							AMFDataObj msg = new AMFDataObj();
							if(m instanceof MessageSimple){								
								msg = createObjMessageSimple(((MessageSimple)m).type, ((MessageSimple)m).roomId, ((MessageSimple)m).text, ((MessageSimple)m).fromId, ((MessageSimple)m).toId);
							}else if(m instanceof MessagePresent){
								msg = createObjMessagePresent(((MessagePresent)m).type, ((MessagePresent)m).roomId, ((MessagePresent)m).prototypeid, ((MessagePresent)m).fromId, ((MessagePresent)m).toId);
							}else if(m instanceof BanMessage){
								msg = createObjMessageBan(((BanMessage)m).type, ((BanMessage)m).roomId, ((BanMessage)m).fromId, ((BanMessage)m).toId, ((BanMessage)m).bantype);
							}else{
								msg.put("type", new AMFDataItem(m.type));
								msg.put("roomId", new AMFDataItem(m.roomId));
							}
						retArray.add(msg);
					}
				}
			retObj.put("messages", retArray);
		}
		
		retArray = new AMFDataArray();
		ChatInfo ci;
		if(usersinroom != null && usersinroom.size() > 0){
				for(int i = 0; i < usersinroom.size(); i++){
					ci = usersinroom.get(i);
					if(ci != null){
							AMFDataObj chatuser = new AMFDataObj();
							chatuser.put("id", new AMFDataItem(ci.id));
							chatuser.put("level", new AMFDataItem(ci.level));
							chatuser.put("sex", new AMFDataItem(ci.sex));
							chatuser.put("role", new AMFDataItem(ci.role));
							chatuser.put("king", new AMFDataItem(ci.king));
							chatuser.put("popular", new AMFDataItem(ci.popular));							
							if(ci.title == null){chatuser.put("title", new AMFDataItem(""));}else{chatuser.put("title", new AMFDataItem(ci.title));}
							if(ci.idSocial == null){chatuser.put("idSocial", new AMFDataItem(""));}else{chatuser.put("idSocial", new AMFDataItem(ci.idSocial));}
							if(ci.url == null){chatuser.put("url", new AMFDataItem(""));}else{chatuser.put("url", new AMFDataItem(ci.url));}
						retArray.add(chatuser);
					}
				}
			retObj.put("users", retArray);
		}
		return retObj;
	}
	
	public static AMFDataObj createObjMessageStatusGame(byte type, Room room, GameInfo initiator){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(room.id));
		if(room.title == null){retObj.put("roomTitle", new AMFDataItem(""));}else{retObj.put("roomTitle", new AMFDataItem(room.title));}			
		
		if(initiator != null){
				AMFDataObj iu = new AMFDataObj();
				iu.put("id", new AMFDataItem(initiator.id));
				if(initiator.title == null){iu.put("title", new AMFDataItem(""));}else{iu.put("title", new AMFDataItem(initiator.title));}			
				iu.put("level", new AMFDataItem(initiator.level));
				iu.put("sex", new AMFDataItem(initiator.sex));
			retObj.put("initiator", iu);
		}		
		return retObj;
	}
	
	public static AMFDataObj createObjMessageStatusGame(byte type, Room room, GameInfo initiator, List<Message> messages, List<GameInfo> usersinroom){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(room.id));
		if(room.title == null){retObj.put("roomTitle", new AMFDataItem(""));}else{retObj.put("roomTitle", new AMFDataItem(room.title));}		
		
		if(initiator != null){
				AMFDataObj iu = new AMFDataObj();
				iu.put("id", new AMFDataItem(initiator.id));
				if(initiator.title == null){iu.put("title", new AMFDataItem(""));}else{iu.put("title", new AMFDataItem(initiator.title));}			
				iu.put("level", new AMFDataItem(initiator.level));
				iu.put("sex", new AMFDataItem(initiator.sex));
			retObj.put("initiator", iu);
		}
		
		AMFDataArray retArray = new AMFDataArray();
		Message m;
		if(messages != null && messages.size() > 0){
				for(int i = 0; i < messages.size(); i++){
					m = messages.get(i);
					if(m != null){
							AMFDataObj msg = new AMFDataObj();
							if(m instanceof MessageSimple){								
								msg = createObjMessageSimple(((MessageSimple)m).type, ((MessageSimple)m).roomId, ((MessageSimple)m).text, ((MessageSimple)m).fromId, ((MessageSimple)m).toId);
							}else if(m instanceof MessagePresent){
								msg = createObjMessagePresent(((MessagePresent)m).type, ((MessagePresent)m).roomId, ((MessagePresent)m).prototypeid, ((MessagePresent)m).fromId, ((MessagePresent)m).toId);
							}else if(m instanceof BanMessage){
								msg = createObjMessageBan(((BanMessage)m).type, ((BanMessage)m).roomId, ((BanMessage)m).fromId, ((BanMessage)m).toId, ((BanMessage)m).bantype);
							}else{
								msg.put("type", new AMFDataItem(m.type));
								msg.put("roomId", new AMFDataItem(m.roomId));
							}
						retArray.add(msg);
					}
				}
			retObj.put("messages", retArray);
		}
		
		retArray = new AMFDataArray();
		GameInfo gi;
		if(usersinroom != null && usersinroom.size() > 0){
				for(int i = 0; i < usersinroom.size(); i++){
					gi = usersinroom.get(i);
					if(gi != null){
							AMFDataObj gameuser = new AMFDataObj();
							gameuser.put("id", new AMFDataItem(gi.id));
							if(gi.title == null){gameuser.put("title", new AMFDataItem(""));}else{gameuser.put("title", new AMFDataItem(gi.title));}
							gameuser.put("level", new AMFDataItem(gi.level));
							gameuser.put("sex", new AMFDataItem(gi.sex));					
						retArray.add(gameuser);
					}
				}
			retObj.put("users", retArray);
		}		
		return retObj;
	}
	
	public static AMFDataObj createObjMessageSimple(byte type, int roomId, String text, int fromId, int toId){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(roomId));	
		if(text == null){retObj.put("text", new AMFDataItem(""));}else{retObj.put("text", new AMFDataItem(text));}
		retObj.put("fromId", new AMFDataItem(fromId));	
		retObj.put("toId", new AMFDataItem(toId));	
		return retObj;
	}
	
	public static AMFDataObj createObjMessageAuctionState(AuctionStateMessage msg){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(msg.type));
		retObj.put("roomId", new AMFDataItem(msg.roomId));
		retObj.put("passtime", new AMFDataItem(msg.passtime));	
		retObj.put("winnerid", new AMFDataItem(msg.winnerid));	
		retObj.put("minbet", new AMFDataItem(msg.minbet));	
		retObj.put("bank", new AMFDataItem(msg.bank));	
		return retObj;
	}
	
	public static AMFDataObj createObjMessageAuctionPrize(byte type, int roomId, int prize){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(roomId));		
		retObj.put("prize", new AMFDataItem(prize));		
		return retObj;
	}
	
	public static AMFDataObj createObjMessageNewLevel(byte type, int roomID, int l, int p){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(roomID));
		retObj.put("level", new AMFDataItem(l));
		retObj.put("prize", new AMFDataItem(p));		
		return retObj;
	}
	
	public static AMFDataObj createObjMessageClanInvite(byte type, int roomID, ClanInfo ci){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(roomID));
		
		if(ci != null){
				AMFDataObj claninfo = new AMFDataObj();
				claninfo.put("id", new AMFDataItem(ci.id));
				if(ci.title == null){claninfo.put("title", new AMFDataItem(""));}else{claninfo.put("title", new AMFDataItem(ci.title));}
				if(ci.ownertitle == null){claninfo.put("ownertitle", new AMFDataItem(""));}else{claninfo.put("ownertitle", new AMFDataItem(ci.ownertitle));}
				claninfo.put("ownerid", new AMFDataItem(ci.ownerid));
				claninfo.put("money", new AMFDataItem(ci.money));
				claninfo.put("experience", new AMFDataItem(ci.experience));
			retObj.put("claninfo", claninfo);
		}
		return retObj;
	}
	
	public static AMFDataObj createObjChangeResult(ChangeResult result){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("errorCode", new AMFDataItem(result.errorCode));
		retObj.put("user", createObjUser(result.user));
		
		return retObj;
	}
	
	public static AMFDataObj createObjBuyResult(BuyResult result){
		AMFDataObj retObj = new AMFDataObj();
		if(result.item != null) retObj.put("item", createObjItem(result.item));
		retObj.put("error", new AMFDataItem(result.error));		
		return retObj;
	}
	
	public static AMFDataObj createObjBuyPresentResult(BuyPresentResult result){
		AMFDataObj retObj = new AMFDataObj();
		if(result.prototype != null) retObj.put("prototype", createObjPresentPrototype(result.prototype));
		retObj.put("error", new AMFDataItem(result.error));
		return retObj;
	}
	
	public static AMFDataObj createObjBuyMoneyResult(BuyMoneyResult result){
		AMFDataObj retObj = new AMFDataObj();		
		retObj.put("error", new AMFDataItem(result.error));		
		retObj.put("money", new AMFDataItem(result.money));
		return retObj;
	}
	
	public static AMFDataObj createObjUseResult(UseResult result){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("error", new AMFDataItem(result.error));	
		retObj.put("itemid", new AMFDataItem(result.itemid));
		retObj.put("itemtype", new AMFDataItem(result.itemtype));
		retObj.put("count", new AMFDataItem(result.count));
		
		return retObj;
	}
	
	public static AMFDataObj createObjCheckLuckResult(CheckLuckResult result){
		AMFDataObj retObj = null;
		if(result != null){
			retObj = new AMFDataObj();
			retObj.put("error", new AMFDataItem(result.error));
			retObj.put("usermoney", new AMFDataItem(result.usermoney));
			retObj.put("addmoney", new AMFDataItem(result.addmoney));
			retObj.put("result", new AMFDataItem(result.result));
		}		
		return retObj;
	}
	
	public static AMFDataArray createObjUsersMailMessage(ArrayList<UserMailMessage> users){
		AMFDataArray retArray = new AMFDataArray();
		if(users != null && users.size() > 0){
			for(int i = 0; i < users.size(); i++){retArray.add(createObjUserMailMessage(users.get(i)));}			
		}
		return retArray;
	}
	
	public static AMFDataObj createObjUserMailMessage(UserMailMessage user){
		AMFDataObj retObj = null;
		if(user != null){
			retObj = new AMFDataObj();
			retObj.put("id", new AMFDataItem(user.id));
			retObj.put("popular", new AMFDataItem(user.popular));	
			retObj.put("messageid", new AMFDataItem(user.messageid));		
			retObj.put("isonline", new AMFDataItem(user.isonline));
			retObj.put("level", new AMFDataItem(user.level));		
			if(user.title == null){retObj.put("title", new AMFDataItem(""));}else{retObj.put("title", new AMFDataItem(user.title));}
			if(user.url == null){retObj.put("url", new AMFDataItem(""));}else{retObj.put("url", new AMFDataItem(user.url));}
			if(user.message == null){retObj.put("message", new AMFDataItem(""));}else{retObj.put("message", new AMFDataItem(user.message));}
		}		
		return retObj;
	}
	
	public static AMFDataArray createObjUsersForTop(ArrayList<UserForTop> users){
		AMFDataArray retArray = new AMFDataArray();
		if(users != null && users.size() > 0){
			for(int i = 0; i < users.size(); i++){retArray.add(createObjUserForTop(users.get(i)));}			
		}
		return retArray;
	}
	
	public static AMFDataObj createObjUserForTop(UserForTop user){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("id", new AMFDataItem(user.id));
		retObj.put("sex", new AMFDataItem(user.sex));
		retObj.put("popular", new AMFDataItem(user.popular));	
		retObj.put("exphour", new AMFDataItem(user.exphour));
		retObj.put("expday", new AMFDataItem(user.expday));
		retObj.put("isonline", new AMFDataItem(user.isonline));
		retObj.put("level", new AMFDataItem(user.level));		
		if(user.title == null){retObj.put("title", new AMFDataItem(""));}else{retObj.put("title", new AMFDataItem(user.title));}
		if(user.url == null){retObj.put("url", new AMFDataItem(""));}else{retObj.put("url", new AMFDataItem(user.url));}
		
		return retObj;
	}
	
	public static AMFDataArray createObjUsersFriend(ArrayList<UserFriend> users){
		AMFDataArray retArray = new AMFDataArray();
		if(users != null && users.size() > 0){
			for(int i = 0; i < users.size(); i++){retArray.add(createObjUserFriend(users.get(i)));}			
		}
		return retArray;
	}
	
	public static AMFDataObj createObjUserFriend(UserFriend user){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("id", new AMFDataItem(user.id));
		retObj.put("popular", new AMFDataItem(user.popular));
		retObj.put("isonline", new AMFDataItem(user.isonline));
		retObj.put("level", new AMFDataItem(user.level));		
		if(user.title == null){retObj.put("title", new AMFDataItem(""));}else{retObj.put("title", new AMFDataItem(user.title));}
		if(user.url == null){retObj.put("url", new AMFDataItem(""));}else{retObj.put("url", new AMFDataItem(user.url));}
				
		return retObj;
	}
	
	public static AMFDataObj createObjUser(User u){
		AMFDataObj retObj = new AMFDataObj();;
		if(u != null){			
			
			if(u.idSocial == null){retObj.put("idSocial", new AMFDataItem(""));}else{retObj.put("idSocial", new AMFDataItem(u.idSocial));}
			if(u.ip == null){retObj.put("ip", new AMFDataItem(""));}else{retObj.put("ip", new AMFDataItem(u.ip));}
			if(u.title == null){retObj.put("title", new AMFDataItem(""));}else{retObj.put("title", new AMFDataItem(u.title));}
			if(u.url == null){retObj.put("url", new AMFDataItem(""));}else{retObj.put("url", new AMFDataItem(u.url));}
			
			retObj.put("id", new AMFDataItem(u.id));
			retObj.put("sex", new AMFDataItem(u.sex));
			retObj.put("role", new AMFDataItem(u.role));
			retObj.put("king", new AMFDataItem(u.king));
			retObj.put("level", new AMFDataItem(u.level));
			retObj.put("lastlvl", new AMFDataItem(u.lastlvl));				
			retObj.put("popular", new AMFDataItem(u.popular));
			retObj.put("experience", new AMFDataItem(u.experience));
			retObj.put("exphour", new AMFDataItem(u.exphour));
			retObj.put("bullets", new AMFDataItem(u.bullets));
			retObj.put("expday", new AMFDataItem(u.expday));
			retObj.put("nextLevelExperience", new AMFDataItem(u.nextLevelExperience));
			retObj.put("money", new AMFDataItem(u.money));
			retObj.put("bantype", new AMFDataItem(u.bantype));
			retObj.put("setbanat", new AMFDataItem(u.setbanat));
			retObj.put("changebanat", new AMFDataItem(u.changebanat));			
			
			if(u.claninfo != null){
					AMFDataObj claninfo = new AMFDataObj();
					claninfo.put("clanid", new AMFDataItem(u.claninfo.clanid));
					claninfo.put("maxusers", new AMFDataItem(u.claninfo.maxusers));
					if(u.url == null){claninfo.put("clantitle", new AMFDataItem(""));}else{claninfo.put("url", new AMFDataItem(u.claninfo.clantitle));}				
					claninfo.put("clandepositm", new AMFDataItem(u.claninfo.clandepositm));
					claninfo.put("clandeposite", new AMFDataItem(u.claninfo.clandeposite));
					claninfo.put("clanrole", new AMFDataItem(u.claninfo.clanrole));
					claninfo.put("getclanmoneyat", new AMFDataItem(u.claninfo.getclanmoneyat));
					if(u.claninfo.clantitle == null){claninfo.put("clantitle", new AMFDataItem(""));}else{claninfo.put("clantitle", new AMFDataItem(u.claninfo.clantitle));}
					
				retObj.put("claninfo", claninfo);
			}			
		}
		return retObj;
	}
	
	public static AMFDataObj createObjUserGame(UserGame u){
		AMFDataObj retObj = new AMFDataObj();
		if(u != null){
			retObj.put("id", u.id);
			retObj.put("seatIndex", u.seatIndex);
			
			AMFDataArray retArray = new AMFDataArray();
			if(u.pids != null && u.pids.size() > 0){
				for(int i = 0; i < u.pids.size(); i++){retArray.add(u.pids.get(i));}
				retObj.put("pids", retArray);
			}
		}
		return retObj;
	}
	
	public static AMFDataObj createObjInitUser(User u, int bt, List<Integer> pp, List<String> pt){
		AMFDataObj retObj = null;
		if(u != null){			
			retObj = createObjUser(u);
			retObj.put("items", createObjItems(u.itemsArr));
			retObj.put("bantime", new AMFDataItem(bt));
			
			AMFDataArray retArray = new AMFDataArray();
			if(pp != null && pp.size() > 0){
					for(int i = 0; i < pp.size(); i++){retArray.add(pp.get(i));}
				retObj.put("popularparts", retArray);
			}
			
			retArray = new AMFDataArray();
			if(pt != null && pt.size() > 0){
					for(int i = 0; i < pt.size(); i++){retArray.add(pt.get(i));}
				retObj.put("populartitles", retArray);
			}

		}
		return retObj;
	}
	
	public static AMFDataArray createObjItemPrototypes(ArrayList<ItemPrototype> itps){		
		AMFDataArray retArray = new AMFDataArray();
		if(itps != null && itps.size() > 0){
			for(int i = 0; i < itps.size(); i++){retArray.add(createObjItemPrototype(itps.get(i)));}			
		}
		return retArray;
	}
	
	public static AMFDataObj createObjItemPrototype(ItemPrototype itemp){
		AMFDataObj retObj = new AMFDataObj();;
		if(itemp != null){			
			retObj.put("id", new AMFDataItem(itemp.id));
			retObj.put("param1", new AMFDataItem(itemp.param1));
			retObj.put("param2", new AMFDataItem(itemp.param2));
			retObj.put("param3", new AMFDataItem(itemp.param3));
			retObj.put("param4", new AMFDataItem(itemp.param4));
			retObj.put("param5", new AMFDataItem(itemp.param5));
			retObj.put("param6", new AMFDataItem(itemp.param6));			
			retObj.put("param7", new AMFDataItem(itemp.param7));
			retObj.put("price", new AMFDataItem(itemp.price));
			retObj.put("showed", new AMFDataItem(itemp.showed));		
			retObj.put("maxquality", new AMFDataItem(itemp.maxquality));
			retObj.put("minlevel", new AMFDataItem(itemp.minlevel));
			if(itemp.title == null){retObj.put("title", new AMFDataItem(""));}else{retObj.put("title", new AMFDataItem(itemp.title));}
			if(itemp.description == null){retObj.put("description", new AMFDataItem(""));}else{retObj.put("description", new AMFDataItem(itemp.description));}
			if(itemp.url == null){retObj.put("url", new AMFDataItem(""));}else{retObj.put("url", new AMFDataItem(itemp.url));}			
		}		
		return retObj;
	}
	
	public static AMFDataArray createObjPresentPrototypes(ArrayList<PresentPrototype> prps){		
		AMFDataArray retArray = new AMFDataArray();
		if(prps != null && prps.size() > 0){
			for(int i = 0; i < prps.size(); i++){retArray.add(createObjPresentPrototype(prps.get(i)));}			
		}
		return retArray;
	}
	
	public static AMFDataObj createObjPresentPrototype(PresentPrototype presentp){
		AMFDataObj retObj = new AMFDataObj();;
		if(presentp != null){			
			retObj.put("id", new AMFDataItem(presentp.id));			
			retObj.put("price", new AMFDataItem(presentp.price));
			retObj.put("showed", new AMFDataItem(presentp.showed));
			if(presentp.title == null){retObj.put("title", new AMFDataItem(""));}else{retObj.put("title", new AMFDataItem(presentp.title));}
			if(presentp.url == null){retObj.put("url", new AMFDataItem(""));}else{retObj.put("url", new AMFDataItem(presentp.url));}
		}		
		return retObj;
	}
	
	public static AMFDataArray createObjItemsPresent(ArrayList<ItemPresent> items){		
		AMFDataArray retArray = new AMFDataArray();
		if(items != null && items.size() > 0){
			for(int i = 0; i < items.size(); i++){retArray.add(createObjItemPresent(items.get(i)));}			
		}
		return retArray;
	}
	
	public static AMFDataObj createObjItemPresent(ItemPresent item){
		AMFDataObj retObj = new AMFDataObj();;
		if(item != null){			
			retObj.put("id", new AMFDataItem(item.id));
			retObj.put("iduser", new AMFDataItem(item.iduser));
			retObj.put("price", new AMFDataItem(item.price));
			retObj.put("showed", new AMFDataItem(item.showed));	
			if(item.title == null){retObj.put("title", new AMFDataItem(""));}else{retObj.put("title", new AMFDataItem(item.title));}
			if(item.presenter == null){retObj.put("presenter", new AMFDataItem(""));}else{retObj.put("presenter", new AMFDataItem(item.presenter));}
			if(item.url == null){retObj.put("url", new AMFDataItem(""));}else{retObj.put("url", new AMFDataItem(item.url));}
		}		
		return retObj;
	}
	
	public static AMFDataArray createObjItems(ArrayList<Item> items){		
		AMFDataArray retArray = new AMFDataArray();
		if(items != null && items.size() > 0){
			for(int i = 0; i < items.size(); i++){retArray.add(createObjItem(items.get(i)));}			
		}
		return retArray;
	}
	
	public static AMFDataObj createObjItem(Item item){
		AMFDataObj retObj = new AMFDataObj();;
		if(item != null){			
			retObj.put("id", new AMFDataItem(item.id));
			retObj.put("param1", new AMFDataItem(item.param1));
			retObj.put("param2", new AMFDataItem(item.param2));
			retObj.put("param3", new AMFDataItem(item.param3));
			retObj.put("param4", new AMFDataItem(item.param4));
			retObj.put("param5", new AMFDataItem(item.param5));
			retObj.put("param6", new AMFDataItem(item.param6));			
			retObj.put("param7", new AMFDataItem(item.param7));			
			retObj.put("price", new AMFDataItem(item.price));
			retObj.put("maxquality", new AMFDataItem(item.maxquality));
			retObj.put("quality", new AMFDataItem(item.quality));
			retObj.put("onuser", new AMFDataItem(item.onuser));
			if(item.title == null){retObj.put("title", new AMFDataItem(""));}else{retObj.put("title", new AMFDataItem(item.title));}
			if(item.description == null){retObj.put("description", new AMFDataItem(""));}else{retObj.put("description", new AMFDataItem(item.description));}
			if(item.url == null){retObj.put("url", new AMFDataItem(""));}else{retObj.put("url", new AMFDataItem(item.url));}			
		}
		return retObj;
	}
	
	public static AMFDataArray createObjClansInfo(ArrayList<ClanInfo> clans){		
		AMFDataArray retArray = new AMFDataArray();
		if(clans != null && clans.size() > 0){
			for(int i = 0; i < clans.size(); i++){retArray.add(createObjClanInfo(clans.get(i)));}			
		}
		return retArray;
	}
	
	public static AMFDataObj createObjClanInfo(ClanInfo ci){
		AMFDataObj retObj = new AMFDataObj();;
		if(ci != null){			
			retObj.put("id", new AMFDataItem(ci.id));
			retObj.put("maxusers", new AMFDataItem(ci.maxusers));
			retObj.put("ownerid", new AMFDataItem(ci.ownerid));
			retObj.put("money", new AMFDataItem(ci.money));
			retObj.put("experience", new AMFDataItem(ci.experience));
			if(ci.title == null){retObj.put("title", new AMFDataItem(""));}else{retObj.put("title", new AMFDataItem(ci.title));}
			if(ci.ownertitle == null){retObj.put("ownertitle", new AMFDataItem(""));}else{retObj.put("ownertitle", new AMFDataItem(ci.ownertitle));}			
		}		
		return retObj;
	}
	
	public static AMFDataObj createObjUserOfClan(UserOfClan user){
		AMFDataObj retObj = new AMFDataObj();;
		if(user != null){			
			retObj.put("id", new AMFDataItem(user.id));
			retObj.put("popular", new AMFDataItem(user.popular));
			retObj.put("level", new AMFDataItem(user.level));
			retObj.put("isonline", new AMFDataItem(user.isonline));
			retObj.put("clandepositm", new AMFDataItem(user.clandepositm));
			retObj.put("clandeposite", new AMFDataItem(user.clandeposite));
			retObj.put("clanrole", new AMFDataItem(user.clanrole));
			retObj.put("getclanmoneyat", new AMFDataItem(user.getclanmoneyat));
			if(user.title == null){retObj.put("title", new AMFDataItem(""));}else{retObj.put("title", new AMFDataItem(user.title));}
		}		
		return retObj;
	}
	
	public static AMFDataObj createObjClanAllInfo(ClanAllInfo callinfo){
		AMFDataObj retObj = new AMFDataObj();;
		if(callinfo != null){			
			retObj.put("time", new AMFDataItem(callinfo.time));
			retObj.put("claninfo", createObjClanInfo(callinfo.claninfo));
			
			AMFDataArray retArray = new AMFDataArray();
			if(callinfo.users != null && callinfo.users.size() > 0){
					for(int i = 0; i < callinfo.users.size(); i++){retArray.add(createObjUserOfClan(callinfo.users.get(i)));}
				retObj.put("petprices", retArray);
			}
			retObj.put("users", retArray);
		}		
		return retObj;
	}
	
	public static AMFDataObj createObjCreateClanResult(CreateClanResult result){
		AMFDataObj retObj = new AMFDataObj();;
		if(result != null){			
			retObj.put("clanid", new AMFDataItem(result.clanid));
			retObj.put("maxusers", new AMFDataItem(result.maxusers));
			retObj.put("error", new AMFDataItem(result.error));
			retObj.put("clandeposite", new AMFDataItem(result.clandeposite));
			retObj.put("clandepositm", new AMFDataItem(result.clandepositm));
			retObj.put("clanrole", new AMFDataItem(result.clanrole));
			retObj.put("money", new AMFDataItem(result.money));
			if(result.clantitle == null){retObj.put("clantitle", new AMFDataItem(""));}else{retObj.put("clantitle", new AMFDataItem(result.clantitle));}		
		}		
		return retObj;
	}
	
	public static AMFDataObj createObjMiniGameActionBetsContent(MiniGameActionBetsContent action){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(action.type));
		retObj.put("roomId", new AMFDataItem(action.roomId));
		retObj.put("result", new AMFDataItem(action.result));
		retObj.put("time", new AMFDataItem(action.time));
		
		if(action.betsinfo != null){
				AMFDataObj betsinfo = new AMFDataObj();
				
				AMFDataArray retArray = new AMFDataArray();
				if(action.betsinfo.users != null && action.betsinfo.users.size() > 0){
						for(int i = 0; i < action.betsinfo.users.size(); i++){retArray.add(action.betsinfo.users.get(i));}
					betsinfo.put("users", retArray);
				}
				
				retArray = new AMFDataArray();
				MiniGameBetInfo bi;
				if(action.betsinfo.bets != null && action.betsinfo.bets.size() > 0){
					for(int i = 0; i < action.betsinfo.bets.size(); i++){
						bi = action.betsinfo.bets.get(i);
						if(bi != null){
								AMFDataObj biObj = new AMFDataObj();
								biObj.put("userid", new AMFDataItem(bi.userid));
								biObj.put("bet", new AMFDataItem(bi.bet));
								biObj.put("betsection", new AMFDataItem(bi.betsection));
							retArray.add(biObj);
						}
					}
					betsinfo.put("bets", retArray);
				}
			retObj.put("betsinfo", betsinfo);
		}
		return retObj;
	}
	
	public static AMFDataObj createObjMiniGameBetsInfo(MiniGameBetsInfo bi){
		AMFDataObj retObj = new AMFDataObj();
			
			AMFDataArray retArray = new AMFDataArray();	
			if(bi.users != null &&  bi.users.size() > 0){				
				for(int i = 0; i < bi.users.size(); i++){retArray.add(bi.users.get(i));}
			}
		retObj.put("users", retArray);
		
			retArray = new AMFDataArray();	
			if(bi.bets != null &&  bi.bets.size() > 0){				
				for(int i = 0; i < bi.bets.size(); i++){retArray.add(createObjMiniGameBetInfo(bi.bets.get(i)));}
			}
		retObj.put("bets", retArray);
		
		return retObj;
	}
	
	public static AMFDataObj createObjMiniGameBetInfo(MiniGameBetInfo bi){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("userid", new AMFDataItem(bi.userid));
		retObj.put("bet", new AMFDataItem(bi.bet));
		retObj.put("betsection", new AMFDataItem(bi.betsection));
		
		return retObj;
	}
	
	public static AMFDataObj createObjMiniGameActionFinishBets(byte type, int roomID, int returnmoney, int prizemoney, int section){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("type", new AMFDataItem(type));
		retObj.put("roomId", new AMFDataItem(roomID));
		retObj.put("returnmoney", new AMFDataItem(returnmoney));
		retObj.put("prizemoney", new AMFDataItem(prizemoney));
		retObj.put("section", new AMFDataItem(section));
		return retObj;
	}
	
	public static AMFDataObj createObjKingsInfo(int startTime, int roundDuration, int currenttime, UserForTop king, UserForTop queen, Map<Integer, UserForTop> aspirants, List<BetInfo> bets){		
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("startTime", new AMFDataItem(startTime));
		retObj.put("currenttime", new AMFDataItem(currenttime));
		retObj.put("roundDuration", new AMFDataItem(roundDuration));
		if(king != null){
			retObj.put("king", createObjUserForTop(king));
		}
		if(queen != null){
			retObj.put("queen", createObjUserForTop(queen));
		}
		
		AMFDataArray aArray = new AMFDataArray();
		Set<Entry<Integer, UserForTop>> set = aspirants.entrySet();
		for (Map.Entry<Integer, UserForTop> user:set){
			aArray.add(createObjUserForTop(user.getValue()));
		}			
		set = null;
		
		retObj.put("aspirants", aArray);
		
		AMFDataArray bArray = new AMFDataArray();
		if(bets != null && bets.size() > 0){
			for(int i = 0; i < bets.size(); i++){bArray.add(createObjBetInfo((bets.get(i))));}			
		}
		
		retObj.put("bets", bArray);
		
		return retObj;
	}
	
	public static AMFDataObj createObjBetInfo(BetInfo bi){
		AMFDataObj retObj = new AMFDataObj();
		retObj.put("userid", new AMFDataItem(bi.userid));
		retObj.put("bet", new AMFDataItem(bi.bet));
		retObj.put("betuserid", new AMFDataItem(bi.betuserid));
		
		return retObj;
	}
}
