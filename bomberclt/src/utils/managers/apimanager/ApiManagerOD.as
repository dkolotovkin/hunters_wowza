package utils.managers.apimanager
{
	import application.GameApplication;
	import application.components.popup.buymoney.od.PopUpBuyMoneyOD;
	
	import flash.events.Event;
	import flash.utils.clearInterval;
	import flash.utils.setInterval;
	
	import mx.controls.Alert;
	
	import utils.od.api.forticom.ApiCallbackEvent;
	import utils.od.api.forticom.ForticomAPI;
	import utils.vk.api.serialization.json.JSON;

	public class ApiManagerOD extends ApiManager
	{
		private var uid:String;
		private var _connectInterval:int = -1;
		
		public function ApiManagerOD(){			
			super();
			ForticomAPI.connection = GameApplication.app.config.apiconnection;
			ForticomAPI.addEventListener(ForticomAPI.CONNECTED, apiIsReady);			
			ForticomAPI.addEventListener(ApiCallbackEvent.CALL_BACK, handleApiEvent);

			autukey = GameApplication.app.config.auth_sig;
			appid = GameApplication.app.config.session_key;									//в appID для одноклассников передается параметр session_key, т.к. он необходим для авторизации и appID не передается в flashvars
			vid = GameApplication.app.config.logged_user_id;
			idsocail = "od" + vid;
//			url = "http://www.odnoklassniki.ru/profile/" + vid;								//неправильная ссылка, т.к. API не предоставляет реального ID пользователя, а ссылки на пользователей временные
					
			if(_connectInterval != -1){
				clearInterval(_connectInterval);
				_connectInterval = -1;
			}
			_connectInterval = setInterval(checkAddedAndConnect, 3000);
		}
		
		protected function apiIsReady(event : Event) : void
		{
			ForticomAPI.removeEventListener(ForticomAPI.CONNECTED, apiIsReady);
			checkAddedAndConnect();
		}
		
		protected function handleApiEvent(event : ApiCallbackEvent) : void{
			//Alert.show(event.method + "\n" + event.result + "\n" + event.data);
		}
		
		override public function init():void{
			GameApplication.app.gameContainer.promotionMode = true;
		}
	
		private function checkAddedAndConnect():void{			
			ForticomAPI.removeEventListener(ForticomAPI.CONNECTED, apiIsReady);			
			
			if(_connectInterval != -1){
				clearInterval(_connectInterval);
				_connectInterval = -1;
			}
//			GameApplication.app.serversmanager.checkServers();
			GameApplication.app.serversmanager.currentServerIndex = 2;
			GameApplication.app.connect();
			
//			_sid = setInterval(updateParams, 3000);											//нельзя получить ссылку, рабочую для всех пользователей
		}
		
		override public function inviteFriends():void{
			ForticomAPI.showInvite();
		}
		override public function buyMoney(money:uint = 0, gamemoney:uint = 0):void{
			var buyName:String = "игровые евро";
			if(money == 30){
				buyName = 2000 + " игровыx евро";
			}else if(money == 90){
				buyName = 7000 + " игровыx евро";
			}else if(money == 140){
				buyName = 13000 + " игровыx евро";
			}else if(money == 450){
				buyName = 45000 + " игровыx евро";
			}
			
			//если необходимо закодировать несколько значений
			var attributesObj:Object = new Object();
			attributesObj["userID"] = addMoneyUserID;
			var attributes:String = JSON.encode(attributesObj);
			
			ForticomAPI.showPayment(buyName, 'Все платные действия в игре, в том числе покупки, оплачиваются игровыми евро.', String(money), money, null, String(addMoneyUserID), 'OK', 'false');
		}
		override public function showBuyMoneyPopUp():void{
			GameApplication.app.popuper.show(new PopUpBuyMoneyOD());
		}
		override public function getFriendsBonus():void{			
		}
		private function onGetFriendsBonus(count:int):void{
		}
		private function onGetFriendsBonusError(error:Object):void{			
		}
		
		override public function getMouseAppUrl():String{
			return "http://www.odnoklassniki.ru/game/mouserun";
		}
		
		override public function getReraceAppUrl():String{
			return "http://www.odnoklassniki.ru/game/rerace";
		}
	}
}