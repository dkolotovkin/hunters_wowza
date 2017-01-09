package utils.managers.apimanager
{
	import application.GameApplication;
	import application.components.popup.buymoney.PopUpBuyMoney;
	
	import flash.events.Event;
	import flash.net.Responder;
	import flash.utils.clearInterval;
	import flash.utils.setInterval;
	
	import mx.controls.Alert;
	
	import utils.shop.BuyMoneyResultCode;
	import utils.user.Sex;
	import utils.user.UserRole;
	import utils.vk.APIConnection;
	import utils.vk.events.CustomEvent;
	
	public class ApiManagerVK extends ApiManager
	{
		public var vkapi:APIConnection;
		private var _flashVars:Object;
		private var _si:int = -1;
		
		private var _updateBalanceDelay:uint = 60 * 1000 * 3;
		private var _updateBalanceInterval:int = -1;
		
		public function ApiManagerVK()
		{
			super();
			autukey = GameApplication.app.config.auth_key;
			vid = GameApplication.app.config.viewer_id;
			appid = String(GameApplication.app.config.api_id);
			idsocail = "vk" + vid;			
			
			_flashVars = GameApplication.app.stage.loaderInfo.parameters as Object;
			vkapi = new APIConnection(_flashVars);
			vkapi.addEventListener('onApplicationAdded', onApplicationAdded, false, 0, true);
			vkapi.addEventListener('onBalanceChanged', onBalanceChanged, false, 0, true);
			vkapi.addEventListener('onSettingsChanged', onSettingsChanged, false, 0, true);
			
			if(_updateBalanceInterval != -1){
				clearInterval(_updateBalanceInterval);
				_updateBalanceInterval = -1;
			}
			_updateBalanceInterval = setInterval(updateBalance, int(_updateBalanceDelay + Math.round(Math.random() * _updateBalanceDelay)));
		}
		
		
		
		private function onApplicationAdded(e:Event):void{			
			init();			
		}
		private function onBalanceChanged(e:CustomEvent):void{
			if(int(GameApplication.app.config.viewer_id) > 0 && int(GameApplication.app.config.api_id) > 0){
				GameApplication.app.connection.call("shopWithdrawVotesVK", new Responder(onAddMoneyVK, onAddMoneyVKError), int(GameApplication.app.config.viewer_id), int(e.params[0]), GameApplication.app.config.api_url, int(GameApplication.app.config.api_id), addMoneyUserID);
			}else{
				GameApplication.app.popuper.showInfoPopUp("Ошибка при переводе голосов! Сообщите администраторам (в чате с розовым ником). Спасибо.\nКод ошибки: int api_id(str): " + int(GameApplication.app.config.api_id) + "(" + GameApplication.app.config.api_id + ") int viewer_id(str): " + int(GameApplication.app.config.viewer_id) + "(" + GameApplication.app.config.viewer_id + ")", 100);
			}
			
			//убрать, когда проблема с переводом решится
			if(GameApplication.app.userinfomanager.myuser.role == UserRole.ADMINISTRATOR_MAIN){
				GameApplication.app.popuper.showInfoPopUp("Информация о переводе голосов.\nКоды: int api_id(str): " + int(GameApplication.app.config.api_id) + "(" + GameApplication.app.config.api_id + ") int viewer_id(str): " + int(GameApplication.app.config.viewer_id) + "(" + GameApplication.app.config.viewer_id + ")", 100);
			}
		}
		private function onAddMoneyVK(result:Object):void{
			if (int(result["error"]) == BuyMoneyResultCode.OK){
				GameApplication.app.popuper.showInfoPopUp("Поздвавляем! Баланс пополнен на " + int(result["money"]) + " монет!");
			}else{
				GameApplication.app.popuper.showInfoPopUp("К сожалению, данная операция невозможна.");
			}
		}
		private function onAddMoneyVKError(error:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Ошибка при переводе голосов.");
		}
		private function onSettingsChanged(e:CustomEvent):void{			
			if (_si != -1){
				clearInterval(_si);
				_si = -1;
			}
			_si = setInterval(checkInstallSettings, 3100, int(e.params[0]));
			checkInstallSettings(int(e.params[0]));
		}		
		
		override public function init():void{
			if (GameApplication.app.config.is_app_user){
				checkInstallSettings(int(GameApplication.app.config.api_settings));
			}else{
				vkapi.callMethod("showInstallBox");
			}
			
			GameApplication.app.gameContainer.promotionMode = true;
		}
		private function checkInstallSettings(settings:int):void{
			if (_si != -1){
				clearInterval(_si);
				_si = -1;
			}
			if (settings & 2){
				//GameApplication.app.serversmanager.checkServers();
				GameApplication.app.serversmanager.currentServerIndex = 2;
				GameApplication.app.connect();
				vkapi.api('getProfiles', { uids: _flashVars['viewer_id'], fields:" uid, first_name, last_name, nickname, sex" }, onGetUserInfo, onGetUserInfoError);				
			}else{
				vkapi.callMethod("showSettingsBox", 259);
			}
		}
		 
		private function onGetUserInfo(data: Object): void {			
			idsocail = "vk" + data[0]["uid"];
			url = "http://vkontakte.ru/id" + data[0]["uid"];
			title = data[0]["last_name"] + " " + data[0]["first_name"];
			if (String(data[0]["sex"]) == String(2)){
				sex = String(Sex.MALE);
			}else{
				sex = String(Sex.FEMALE);
			}
			//GameApplication.app.connect();
			_sid = setInterval(updateParams, 3000);
		}
		private function onGetUserInfoError(data: Object): void {			
			trace(data);
		}
		
		override public function inviteFriends():void{
			vkapi.callMethod("showInviteBox");
		}
		override public function buyMoney(money:uint = 0, gamemoney:uint = 0):void{
			vkapi.callMethod("showOrderBox", {
				type: "item",
				item: money
			});
			GameApplication.app.popuper.hidePopUp();
		}
		
		override public function showBuyMoneyPopUp():void{
			GameApplication.app.popuper.show(new PopUpBuyMoney("Вы можете покупать монеты за голоса по курсу: 1 голос = 400 монет"));
		}
		
		override public function getFriendsBonus():void{			
		}
		
		public function updateBalance():void{
			if(_updateBalanceInterval != -1){
				clearInterval(_updateBalanceInterval);
				_updateBalanceInterval = -1;
			}
			_updateBalanceInterval = setInterval(updateBalance, int(_updateBalanceDelay + Math.round(Math.random() * _updateBalanceDelay)));
			vkapi.api('getUserBalance', {}, onGetBalance, onGetBalanceError);		
		}
		
		private function onGetBalance(result:*):void{			
			GameApplication.app.connection.call("shopWithdrawVotesVK", new Responder(onAddMoneyVK, onAddMoneyVKError), GameApplication.app.config.viewer_id, int(result), GameApplication.app.config.api_url, GameApplication.app.config.api_id, addMoneyUserID);
		}
		
		private function onGetBalanceError(result:*):void{
			//Alert.show("bad: " + XML(result).toXMLString());
		}
		
		override public function getMouseAppUrl():String{
			return "http://vk.com/mouserunru";
		}
		
		override public function getReraceAppUrl():String{
			return "http://vk.com/app3544908";
		}
	}
}