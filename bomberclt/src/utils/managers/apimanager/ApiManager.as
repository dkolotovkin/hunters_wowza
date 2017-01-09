package utils.managers.apimanager
{
	import application.GameApplication;
	import application.components.popup.PopUp;
	import application.components.popup.buymoney.PopUpBuyMoney;
	import application.components.popup.buymoney.mm.PopUpBuyMoneyMM;
	import application.components.popup.buymoney.site.PopUpBuyMoneySite;
	import application.components.popup.friendsBonus.PopUpFriendsBonus;
	import application.gamecontainer.StartSitePage;
	
	import flash.net.Responder;
	import flash.net.URLRequest;
	import flash.net.URLRequestMethod;
	import flash.net.URLVariables;
	import flash.net.navigateToURL;
	import flash.utils.clearInterval;
	
	import mx.controls.Alert;
	
	import utils.shop.BuyMoneyResultCode;
	import utils.user.Sex;
	import utils.vk.api.MD5;

	public class ApiManager
	{
		public var idsocail:String;
		public var title:String;
		public var sex:String;
		public var autukey:String;
		public var vid:String;
		public var appid:String;
		public var url:String = null;
		public var addMoneyUserID:int;
		
		protected var _sid:int = -1;
		
		public function ApiManager(){
		}
		
		public function init():void{
			idsocail = "vk3450745121";
			title = "Дмитрий";
			url = "www.ya.ru"
			sex = String(Sex.MALE);
			GameApplication.app.serversmanager.checkServers();
			
			GameApplication.app.gameContainer.promotionMode = false;
			
//			GameApplication.app.addElement(new StartSitePage());
			updateParams();
		}
		public function inviteFriends():void{
		}
		public function showBuyMoneyPopUp():void{
			GameApplication.app.popuper.show(new PopUpBuyMoney("Покупка монет тестовая"));			
		}
		public function buyMoney(money:uint = 0, gamemoney:uint = 0):void{			
		}
		
		public function getFriendsBonus():void{			
		}
		
		public function updateParams():void{
			if(_sid != -1){
				clearInterval(_sid);
				_sid = -1;
			}			
			var updatedParams:Boolean = Boolean(GameApplication.app.so.data["updatedParams"]);
			
			if(!updatedParams && GameApplication.app.userinfomanager && GameApplication.app.userinfomanager.myuser != null){
				if(url != null && 
					(
						GameApplication.app.userinfomanager.myuser.url == null || 
						(GameApplication.app.userinfomanager.myuser.url && GameApplication.app.userinfomanager.myuser.url.length == 0)
					)
					){
						GameApplication.app.so.data["updatedParams"] = true;
						GameApplication.app.so.flush();
						GameApplication.app.connection.call("updateParams", null, url);
				}
			}
		}
		
		public function getMouseAppUrl():String{
			return "";
		}
		
		public function getReraceAppUrl():String{
			return "";
		}
	}
}