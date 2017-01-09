package utils.managers.persinfo
{
	import application.GameApplication;
	import application.components.popup.PopUp;
	import application.components.popup.PopUpTitled;
	import application.components.popup.changeInfo.PopUpChangeInfo;
	import application.components.popup.enemies.PopUpEnemies;
	import application.components.popup.extraction.PopUpExtraction;
	import application.components.popup.friends.PopUpFriends;
	import application.components.popup.help.testgame.PopUpTestGame;
	import application.components.popup.messages.PopUpMessages;
	import application.components.popup.myinfo.PopUpMyInfo;
	import application.components.popup.pay.PopUpPay;
	import application.components.popup.userinfo.PopUpUserInfo;
	
	import flash.events.EventDispatcher;
	import flash.net.Responder;
	import flash.utils.clearInterval;
	import flash.utils.setInterval;
	
	import mx.controls.Alert;
	
	import spark.components.Group;
	
	import utils.changeinfo.ChangeInfoParams;
	import utils.chat.message.MessageType;
	import utils.copyproperties.CopyProperties;
	import utils.game.betroominfo.BetResult;
	import utils.managers.ban.BanType;
	import utils.shop.BuyResultCode;
	import utils.shop.item.Item;
	import utils.user.ClanUserInfo;
	import utils.user.User;
	import utils.user.change.ChangeResult;

	public class UserInfoManager extends EventDispatcher
	{
		public var users:Object = new Object();
		public var enemiesIds:Object = new Object();
		[Bindable]
		public var myuser:User;
		
		public var popularparts:Array = new Array(); 
		public var populartitles:Array = new Array();
		public var popularicons:Array = new Array(IconPopular1, IconPopular2, IconPopular3, IconPopular4, IconPopular5, IconPopular6, IconPopular7, IconPopular8, IconPopular9);		
	
		private var _getFriendsCallBack:Function;
		private var _getEnemiesCallBack:Function;
		private var _getMessagesCallBack:Function;
		private var _checkLuckCallBack:Function;
		
		private var _timeToUpdate:int = 0;
		
		public function UserInfoManager()
		{	
			myuser = new User();
		}
		
		public function initPersParams(userParams:Object):void{
			if(userParams["claninfo"]){
				myuser.claninfo = new ClanUserInfo(userParams["claninfo"]["clanid"], userParams["claninfo"]["clantitle"], userParams["claninfo"]["maxusers"], userParams["claninfo"]["clandepositm"],
													userParams["claninfo"]["clandeposite"], userParams["claninfo"]["clanrole"], userParams["claninfo"]["getclanmoneyat"]);
			}			
			if(userParams["popularparts"]){
				popularparts = userParams["popularparts"];
			}
			if(userParams["populartitles"]){
				populartitles = userParams["populartitles"];
			}
			
			myuser.id = int(userParams["id"]);
			myuser.sex = int(userParams["sex"]);
			myuser.role = int(userParams["role"]);
			myuser.title = String(userParams["title"]);	
			myuser.level = int(userParams["level"]);
			myuser.king = int(userParams["king"]);
			myuser.experience = int(userParams["experience"]);
			myuser.bullets = int(userParams["bullets"]);
			myuser.exphour = int(userParams["exphour"]);
			myuser.expday = int(userParams["expday"]);
			myuser.popular = int(userParams["popular"]);
			myuser.maxExperience = int(userParams["nextLevelExperience"]);			
			myuser.money = int(userParams["money"]);
			GameApplication.app.banmanager.setBanTime(int(userParams["bantime"]));
			
			GameApplication.app.apimanager.addMoneyUserID = myuser.id;
			
			myuser.itemsArr = new Array();
			myuser.itemsObj = new Object();
			
			var items:Array = userParams["items"] as Array;
			if(items && items.length){
				var item:Item;
				for(var i:uint = 0; i < items.length; i++){				
					item = new Item();
					CopyProperties.copy(items[i], item);
					myuser.itemsArr.push(item);
					myuser.itemsObj[item.id] = item;
				}
			}			
			
			_timeToUpdate = 60 * 5 + Math.floor(Math.random() * (60 * 5));
			setInterval(updateInterval, 1000);
		}
		
		public function updateInterval():void{
			_timeToUpdate--;
			if(_timeToUpdate <= 0){
				GameApplication.app.connection.call("updateUser", null);
				_timeToUpdate = 60 * 5 + Math.floor(Math.random() * (60 * 5));
			}
		}
		
		public function setPersParams(userParams:Object):void{			
			myuser.id = int(userParams["id"]);
			myuser.sex = int(userParams["sex"]);
			myuser.title = String(userParams["title"]);	
			myuser.level = int(userParams["level"]);
			myuser.popular = int(userParams["popular"]);
			myuser.experience = int(userParams["experience"]);
			myuser.bullets = int(userParams["bullets"]);
			myuser.exphour = int(userParams["exphour"]);
			myuser.expday = int(userParams["expday"]);
			myuser.maxExperience = int(userParams["nextLevelExperience"]);			
			myuser.money = int(userParams["money"]);
			if(userParams["claninfo"]){
				myuser.claninfo = new ClanUserInfo(userParams["claninfo"]["clanid"], userParams["claninfo"]["clantitle"], userParams["claninfo"]["maxusers"], userParams["claninfo"]["clandepositm"],
					userParams["claninfo"]["clandeposite"], userParams["claninfo"]["clanrole"], userParams["claninfo"]["getclanmoneyat"]);
			}			
		}
		
		public function changeMyParams(obj:Object):void{
			if(obj["param"] == ChangeInfoParams.USER_MONEY){
				myuser.money = obj["value1"];
			}else if(obj["param"] == ChangeInfoParams.USER_EXPERIENCE){
				myuser.experience = obj["value1"];
			}else if(obj["param"] == ChangeInfoParams.USER_POPULAR){
				myuser.popular = obj["value1"];
			}else if(obj["param"] == ChangeInfoParams.USER_EXPERIENCE_AND_POPULAR){
				myuser.experience = obj["value1"];
				myuser.popular = obj["value2"];
			}else if(obj["param"] == ChangeInfoParams.USER_MONEY_POPULAR){
				myuser.money = obj["value1"];
				myuser.popular = obj["value2"];
			}else if(obj["param"] == ChangeInfoParams.USER_MONEY_EXPERIENCE_BULLETS){
				myuser.money = obj["value1"];
				myuser.experience = obj["value2"];
				myuser.bullets = obj["value3"];
			}else if(obj["param"] == ChangeInfoParams.USER_MONEY_EXPERIENCE){
				myuser.money = obj["value1"];
				myuser.experience = obj["value2"];
			}else if(obj["param"] == ChangeInfoParams.USER_MONEY_BANTYPE){
				myuser.money = obj["value1"];
				if(BanType.NO_BAN == obj["value2"]) GameApplication.app.banmanager.banoff();
			}else if(obj["param"] == ChangeInfoParams.USER_MONEY_MAX_CLAN_SEATS){
				myuser.money = obj["value1"];
				if(myuser.claninfo) myuser.claninfo.maxusers = obj["value2"];
			}else if(obj["param"] == ChangeInfoParams.USER_EXPERIENCE_MAXEXPERIENCE_LEVEL){
				myuser.experience = obj["value1"];
				myuser.maxExperience = obj["value2"];
				myuser.level = obj["value3"];
			}			
		}
		
		public function isBadPlayer():void{
			GameApplication.app.connection.call("isBadPlayer", null);
		}
		
		public function addToFriend(uid:int):void{
			GameApplication.app.connection.call("addToFriend", null, uid);
		}
		
		public function addToEnemy(uid:int):void{
			enemiesIds[uid] = uid;
			GameApplication.app.connection.call("addToEnemy", null, uid);
		}
		
		public function getMessages(callBack:Function):void{
			_getMessagesCallBack = callBack;
			GameApplication.app.connection.call("getMailMessages", new Responder(onGetMessages, onGetMessagesError));
		}
		
		private function onGetMessages(messages:Array):void{
			_getMessagesCallBack && _getMessagesCallBack(messages);
			_getMessagesCallBack = null;
		}
		
		private function onGetMessagesError(u:Object):void{			
			GameApplication.app.popuper.showInfoPopUp("Произошла ошибка при обращении к серверу");
			_getMessagesCallBack = null;
		}
		
		public function removeMailMessage(mid:int):void{
			GameApplication.app.connection.call("removeMailMessage", new Responder(onRemoveMailMessage, onRemoveMailMessageError), mid);
		}
		
		private function onRemoveMailMessage(result:Object):void{
			GameApplication.app.popuper.show(new PopUpMessages());
		}
		
		private function onRemoveMailMessageError(u:Object):void{			
			GameApplication.app.popuper.showInfoPopUp("Произошла ошибка при обращении к серверу");
		}
		
		public function getFriends(callBack:Function):void{
			_getFriendsCallBack = callBack;
			GameApplication.app.connection.call("getFiends", new Responder(onGetFriends, onGetFriendsError));
		}
		
		private function onGetFriends(friends:Array):void{
			_getFriendsCallBack && _getFriendsCallBack(friends);
			_getFriendsCallBack = null;
		}
		
		private function onGetFriendsError(u:Object):void{			
			GameApplication.app.popuper.showInfoPopUp("Произошла ошибка при обращении к серверу");
			_getFriendsCallBack = null;
		}
		
		public function getEnemies(callBack:Function):void{
			_getEnemiesCallBack = callBack;
			GameApplication.app.connection.call("getEnemies", new Responder(onGetEnemies, onGetEnemiesError));
		}
		
		private function onGetEnemies(enemies:Array):void{
			enemiesIds = new Object();
			for(var i:int = 0; i < enemies.length; i++){
				enemiesIds[enemies[i]["id"]] = enemies[i]["id"];
			}
			
			_getEnemiesCallBack && _getEnemiesCallBack(enemies);
			_getEnemiesCallBack = null;
		}
		
		private function onGetEnemiesError(u:Object):void{			
			GameApplication.app.popuper.showInfoPopUp("Произошла ошибка при обращении к серверу");
			_getEnemiesCallBack = null;
		}
		
		public function checkLuck(callBack:Function, bet:int):void{
			_checkLuckCallBack = callBack;
			GameApplication.app.connection.call("checkLuck", new Responder(onCheckLuck, onGetFriendsError), bet);
		}
		
		private function onCheckLuck(result:Object):void{
			_checkLuckCallBack && _checkLuckCallBack(result);
			_checkLuckCallBack = null;
		}
		
		public function userInVictorina():void{
			GameApplication.app.connection.call("userInVictorina", null);
		}
		
		public function userOutVictorina():void{
			GameApplication.app.connection.call("userOutVictorina", null);
		}
		
		public function userInAuction():void{
			GameApplication.app.connection.call("userInAuction", null);
		}
		
		public function userOutAuction():void{
			GameApplication.app.connection.call("userOutAuction", null);
		}
		
		public function auctionBet(bet:int):void{
			GameApplication.app.connection.call("auctionBet", new Responder(onAuctionBet, null), bet);
		}
		
		private function onAuctionBet(err:int):void{			
			if(err == BetResult.NO_MONEY){
				GameApplication.app.popuper.showInfoPopUp("У вас недостаточно денег");
			}else if(err == BetResult.OTHER){
				GameApplication.app.popuper.showInfoPopUp("Невозможно сделать ставку. Возможно ваша ставка ниже минимальной.");
			}
		}
		
		public function removeFriend(uid:int):void{
			GameApplication.app.connection.call("removeFriend", new Responder(onRemoveFriend, onRemoveFriendError), uid);
		}	
		
		private function onRemoveFriend(result:Object):void{
			GameApplication.app.popuper.show(new PopUpFriends());
		}
		
		private function onRemoveFriendError(u:Object):void{			
			GameApplication.app.popuper.showInfoPopUp("Произошла ошибка при обращении к серверу");
		}
		
		public function removeEnemy(uid:int):void{
			GameApplication.app.connection.call("removeEnemy", new Responder(onRemoveEnemy, onRemoveEnemyError), uid);
		}	
		
		private function onRemoveEnemy(result:Object):void{
			GameApplication.app.popuper.show(new PopUpEnemies());
		}
		
		private function onRemoveEnemyError(u:Object):void{			
			GameApplication.app.popuper.showInfoPopUp("Произошла ошибка при обращении к серверу");
		}
		
		public function sendMail(uid:int, message:String):void{
			GameApplication.app.connection.call("sendMail", new Responder(onSendMail, onSendMailError), uid, message);
		}
		
		private function onSendMail(buyresult:Object):void{	
			if (buyresult["error"] == BuyResultCode.OK){
				GameApplication.app.popuper.showInfoPopUp("Ваше сообщение успешно доставлено");
			}else if (buyresult["error"] == BuyResultCode.NOT_ENOUGH_MONEY){
				GameApplication.app.popuper.showInfoPopUp("У Вас не достаточно денег для отправки почты.");
			}else{
				GameApplication.app.popuper.showInfoPopUp("Произошла ошибка при обращении к серверу");
			}
		}
		
		private function onSendMailError(error:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Произошла ошибка при обращении к серверу");
		}
		
		public function showUserInfo(user:User):void{			
			GameApplication.app.connection.call("getUserInfoByID", new Responder(onGetUserInfo, onError), user.id);
		}
		
		private function onGetUserInfo(u:Object):void{
			if(u != null){
				var popUp:PopUp;
				if(int(u["id"]) == GameApplication.app.userinfomanager.myuser.id){
					popUp = new PopUpMyInfo(User.createFromObject(u));
				}else{
					popUp = new PopUpUserInfo(User.createFromObject(u));			
				}
				GameApplication.app.popuper.show(popUp);
			}else{
				var m:Object = new Object();
				m["type"] = MessageType.SYSTEM;
				m["text"] = "Пользователь вышел из игры";
				GameApplication.app.gameContainer.chat.activeRoom.addMessage(m);
			}
		}
		private function onError(error:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Произошла ошибка при обращении к серверу");
		}
		
		public function showchangeInfo():void{
			var popUp:PopUpChangeInfo = new PopUpChangeInfo();			
			GameApplication.app.popuper.show(popUp);			
		}
		
		public function changeInfo(newtitle:String, newsex:int):void{
			if (newtitle.length){				
				var changeResponder:Responder = new Responder(onChange, onChangeError);				
				GameApplication.app.connection.call("changeInfo", changeResponder, newtitle, newsex);
				GameApplication.app.popuper.hidePopUp();
			}else{
				GameApplication.app.popuper.showInfoPopUp("Необходимо ввести имя пользователя");
			}
		}
		private function onChange(result:Object):void{			
			var code:int = result["errorCode"];
			if (code == ChangeResult.OK){
				GameApplication.app.userinfomanager.myuser.title = result["user"]["title"];
				GameApplication.app.userinfomanager.myuser.sex = result["user"]["sex"];
				GameApplication.app.userinfomanager.myuser.money = result["user"]["money"];
				GameApplication.app.popuper.showInfoPopUp("Параметры пользователя успешно изменены.");
			}else if (code == ChangeResult.NO_MONEY){
				GameApplication.app.popuper.showInfoPopUp("У Вас недостаточно денег для этой операции.");
			}else if (code == ChangeResult.USER_EXIST){
				GameApplication.app.popuper.showInfoPopUp("Неверное имя пользователя. Пользователь с таким именем уже существует.");
			}else if (code == ChangeResult.UNDEFINED){
				GameApplication.app.popuper.showInfoPopUp("Произошла ошибка при изменении параметров пользователя. Сообщите об этом разработчикам!");
			}
		}
		private function onChangeError(err:Object):void{			
			GameApplication.app.popuper.showInfoPopUp("Произошла ошибка при изменении параметров пользователя. Сообщите об этом разработчикам!");			
		}
		
		public function startChangeInfo(newtitle:String, newsex:int):void{
			if (newtitle.length == 0){				
				newtitle = GameApplication.app.userinfomanager.myuser.title;
			}
			
			var changeResponder:Responder = new Responder(onStartChange, null);
			GameApplication.app.connection.call("startbonusChangeInfo", changeResponder, newtitle, newsex);
			GameApplication.app.popuper.hidePopUp(); 
		}
		private function onStartChange(result:Object):void{
			var code:int = result["errorCode"];
			
			if (code == ChangeResult.OK){
				GameApplication.app.userinfomanager.myuser.title = result["user"]["title"];
				GameApplication.app.userinfomanager.myuser.sex = result["user"]["sex"];
				GameApplication.app.userinfomanager.myuser.money = result["user"]["money"];
				GameApplication.app.popuper.show(new PopUpTestGame());
				
				GameApplication.app.gameContainer.chat.getUserByID(GameApplication.app.userinfomanager.myuser.id).title = GameApplication.app.userinfomanager.myuser.title; 
			}
		}		
		
		public function getPopularTitle(value:int):String{
			for(var i:int; i < popularparts.length - 1; i++){
				if(popularparts[i] <= value && popularparts[i + 1] > value){
					return populartitles[i];
				}
			}
			return populartitles[populartitles.length - 1]
		}
		
		public function getPopularIconClass(value:int):Class{			
			for(var i:int; i < popularparts.length - 1; i++){
				if(popularparts[i] <= value && popularparts[i + 1] > value){
					return popularicons[i];
				}
			}
			return popularicons[popularicons.length - 1]
		}
		
		public function showOnlineTimeMoneyInfo():void{			
			GameApplication.app.connection.call("getOnlineTimeMoneyInfo", new Responder(onGetOnlineTimeMoneyInfo, onError));
		}
		
		private function onGetOnlineTimeMoneyInfo(money:int):void{
			GameApplication.app.popuper.show(new PopUpPay(money));			
		}
		public function getOnlineTimeMoney():void{			
			GameApplication.app.connection.call("getOnlineTimeMoney", new Responder(null, onError));
		}
	}
}