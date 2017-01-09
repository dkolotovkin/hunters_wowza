package utils.managers.clan
{
	import application.GameApplication;
	
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	import flash.net.Responder;
	
	import mx.controls.Alert;
	
	import utils.clan.ClanError;
	import utils.game.GameManager;
	import utils.shop.BuyResultCode;
	import utils.user.ClanUserRole;
	
	public class ClanManager extends EventDispatcher
	{
		private var _currentGetClanFunction:Function;
		private var _currentGetClanInfoFunction:Function;
		
		public function ClanManager(target:IEventDispatcher=null)
		{
			super(target);
		}
		
		public function getClansInfo(f:Function):void{
			_currentGetClanFunction = f;			
			GameApplication.app.connection.call("clanGetClansInfo", new Responder(ongetClansInfo, ongetClansInfoError));			
		}
		private function ongetClansInfo(clans:Array):void{
			_currentGetClanFunction(clans);
			_currentGetClanFunction = null;
		}		
		private function ongetClansInfoError(err:Object):void{			
		}
		
		public function getClanAllInfo(f:Function, clanid:int):void{
			_currentGetClanInfoFunction = f;
			GameApplication.app.connection.call("clanGetClanAllInfo", new Responder(ongetClanAllInfo, ongetClanAllInfoError), clanid);			
		}
		private function ongetClanAllInfo(claninfo:Object):void{
			_currentGetClanInfoFunction(claninfo);
			_currentGetClanInfoFunction = null;
		}		
		private function ongetClanAllInfoError(err:Object):void{			
		}
		
		public function createClan(title:String):void{
			GameApplication.app.connection.call("clanCreateClan", new Responder(oncreateClan, oncreateClanError), title);
		}
		private function oncreateClan(result:Object):void{
			var error:int = result["error"];
			if(error == ClanError.OK){
				GameApplication.app.userinfomanager.myuser.money = int(result["money"]);
				GameApplication.app.userinfomanager.myuser.claninfo.clantitle = result["clantitle"];
				GameApplication.app.userinfomanager.myuser.claninfo.maxusers = result["maxusers"];
				GameApplication.app.userinfomanager.myuser.claninfo.clanid = int(result["clanid"]);
				GameApplication.app.userinfomanager.myuser.claninfo.clanrole = ClanUserRole.OWNER;
				
				GameApplication.app.navigator.goClansRoom();
				GameApplication.app.popuper.showInfoPopUp("Поздравляем! Ваш альянс успешно создан!");
			}else if(error == ClanError.LOWLEVEL){
				GameApplication.app.popuper.showInfoPopUp("У вас маленький уровень. Альянс можно создавать только с 10 уровня.");
			}else if(error == ClanError.NOT_ENOUGHT_MONEY){
				GameApplication.app.popuper.showInfoPopUp("У вас недостаточно денег для покупки альянса.");
			}else if(error == ClanError.CLAN_EXIST){
				GameApplication.app.popuper.showInfoPopUp("Альянс с таким названием уже существует. Выберите уникальное название альянса.");
			}else if(error == ClanError.INOTHERCLAN){
				GameApplication.app.popuper.showInfoPopUp("Вы уже состоите в другом альянсе. Вам нужно покинуть альянс для создания нового.");
			}else if(error == ClanError.OTHER){
				GameApplication.app.popuper.showInfoPopUp("Невозможно создать альянс.");
			}else {
				GameApplication.app.popuper.showInfoPopUp("Невозможно создать альянс.");
			}
		}		
		private function oncreateClanError(err:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Невозможно создать альянс.");
		}
		
		public function inviteuser(userID:String):void{
			GameApplication.app.connection.call("clanInviteUser", new Responder(onInvite, onInviteError), userID);
		}
		private function onInvite(result:int):void{
			if(result == ClanError.OK){
				GameApplication.app.popuper.showInfoPopUp("Приглашение доставлено.");
			}else if(result == ClanError.YOUNOTOWNER){
				GameApplication.app.popuper.showInfoPopUp("Вы не являетесь владельцем альянса и не можете приглашать других игроков.");
			}else if(result == ClanError.USEROFFLINE){
				GameApplication.app.popuper.showInfoPopUp("Пользователь вышел из игры.");
			}else if(result == ClanError.INOTHERCLAN){
				GameApplication.app.popuper.showInfoPopUp("Пользователь уже состоит в альянсе.");
			}else if(result == ClanError.OTHER){
				GameApplication.app.popuper.showInfoPopUp("Невозможно пригласить пользователя.");
			}else if(result == ClanError.NOT_ENOUGHT_SEATS){
				GameApplication.app.popuper.showInfoPopUp("В альянсе больше нет мест для новых игроков. При необходимости можно увеличить количество мест (купить).");
			}else {
				GameApplication.app.popuper.showInfoPopUp("Невозможно пригласить пользователя.");
			}
		}		
		private function onInviteError(err:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Невозможно пригласить пользователя.");
		}
		
		public function inviteAccept():void{
			GameApplication.app.connection.call("clanInviteAccept", new Responder(onAccept, onAcceptError));
		}
		private function onAccept(clanid:int):void{
			if(clanid > 0){
				GameApplication.app.userinfomanager.myuser.claninfo.clanid = clanid;
				GameApplication.app.userinfomanager.myuser.claninfo.clanrole = ClanUserRole.ROLE1;
			}else{
				GameApplication.app.popuper.showInfoPopUp("Невозможно вступить в альянс");
			}
		}		
		private function onAcceptError(err:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Невозможно вступить в альянс");
		}
		
		public function kick(userID:int):void{
			GameApplication.app.connection.call("clanKick", new Responder(onKick, onKickError), String(userID));
		}
		private function onKick(error:int):void{
			if(error == ClanError.OK){
				GameApplication.app.navigator.goClanRoom(GameApplication.app.userinfomanager.myuser.claninfo.clanid);
			}else if(error == ClanError.YOUNOTOWNER){
				GameApplication.app.popuper.showInfoPopUp("Вы не являетесь владельцем альянса, в котором состоит этот пользователь.");
			}else if(error == ClanError.INOTHERCLAN){
				GameApplication.app.popuper.showInfoPopUp("Вы не являетесь владельцем альянса, в котором состоит этот пользователь.");
			}else if(error == ClanError.OTHER){
				GameApplication.app.popuper.showInfoPopUp("Невозможно выгнать пользователя");
			}else {
				GameApplication.app.popuper.showInfoPopUp("Невозможно выгнать пользователя");
			}
		}		
		private function onKickError(err:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Невозможно выгнать пользователя");
		}
		
		public function setrole(userID:int, role:int):void{
			GameApplication.app.connection.call("clanSetRole", new Responder(onSetRole, onSetRoleError), String(userID), role);
		}
		private function onSetRole(error:int):void{
			if(error == ClanError.OK){
			}else if(error == ClanError.NOROLE){
				GameApplication.app.popuper.showInfoPopUp("Попытка установить несуществующую роль пользователю.");
			}else if(error == ClanError.YOUNOTOWNER){
				GameApplication.app.popuper.showInfoPopUp("Вы не являетесь владельцем альянса, в котором состоит этот пользователь.");
			}else if(error == ClanError.INOTHERCLAN){
				GameApplication.app.popuper.showInfoPopUp("Вы не являетесь владельцем альянса, в котором состоит этот пользователь.");
			}else if(error == ClanError.OTHER){
				GameApplication.app.popuper.showInfoPopUp("Невозможно установить роль пользователя.");
			}else {
				GameApplication.app.popuper.showInfoPopUp("Невозможно установить роль пользователя.");
			}
		}		
		private function onSetRoleError(err:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Невозможно установить роль пользователя.");
		}
		
		public function leave():void{
			GameApplication.app.connection.call("clanLeave", new Responder(onLeave, onLeaveError));
		}
		private function onLeave(error:int):void{
			if(error == ClanError.OK){
				GameApplication.app.navigator.goClansRoom();
				GameApplication.app.userinfomanager.myuser.claninfo.clanid = 0;
				GameApplication.app.userinfomanager.myuser.claninfo.clandeposite = 0;
				GameApplication.app.userinfomanager.myuser.claninfo.clandepositm = 0;
				GameApplication.app.userinfomanager.myuser.claninfo.clanrole = ClanUserRole.NO_ROLE;
			}else {
				GameApplication.app.popuper.showInfoPopUp("Невозможно покинуть альянс.");
			}
		}		
		private function onLeaveError(err:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Невозможно покинуть альянс.");
		}
		
		public function reset():void{
			GameApplication.app.connection.call("clanReset", new Responder(onReset, onResetError));
		}
		private function onReset(error:int):void{
			if(error == ClanError.OK){
				GameApplication.app.navigator.goClanRoom(GameApplication.app.userinfomanager.myuser.claninfo.clanid);
			}else if(error == ClanError.YOUNOTOWNER) {
				GameApplication.app.popuper.showInfoPopUp("Вы не являетесь владельцем альянса.");
			}else {
				GameApplication.app.popuper.showInfoPopUp("Невозможно сбросить показатели.");
			}
		}		
		private function onResetError(err:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Невозможно сбросить показатели.");
		}
		
		public function destroy():void{
			GameApplication.app.connection.call("clanDestroy", new Responder(onDestroy, onDestroyError));
		}
		private function onDestroy(error:int):void{
			if(error == ClanError.OK){
				GameApplication.app.popuper.showInfoPopUp("Ваш альянс удален.");
				GameApplication.app.navigator.goClansRoom();
				
				GameApplication.app.userinfomanager.myuser.claninfo.clanid = 0;
				GameApplication.app.userinfomanager.myuser.claninfo.clandeposite = 0;
				GameApplication.app.userinfomanager.myuser.claninfo.clandepositm = 0;
				GameApplication.app.userinfomanager.myuser.claninfo.clanrole = ClanUserRole.NO_ROLE;
			}else if(error == ClanError.YOUNOTOWNER) {
				GameApplication.app.popuper.showInfoPopUp("Вы не являетесь владельцем альянса.");
			}else {
				GameApplication.app.popuper.showInfoPopUp("Невозможно распустить альянс.");
			}
		}		
		private function onDestroyError(err:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Невозможно распустить альянс.");
		}
		
		public function getMoney():void{
			GameApplication.app.connection.call("clanGetMoney", new Responder(ongetMoney, ongetMoneyError));
		}
		private function ongetMoney(error:int):void{
			if(error == ClanError.OK){
				GameApplication.app.navigator.goClanRoom(GameApplication.app.userinfomanager.myuser.claninfo.clanid);
			}else if(error == ClanError.INOTHERCLAN) {
				GameApplication.app.popuper.showInfoPopUp("Вы пытаетесь забрать зарплату не из своего альянса.");
			}else if(error == ClanError.TIMEERROR) {
				GameApplication.app.popuper.showInfoPopUp("Можете забрать зарплату чуть позже.");
			}else {
				GameApplication.app.popuper.showInfoPopUp("Невозможно забрать зарплату.");
			}
		}		
		private function ongetMoneyError(err:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Невозможно забрать зарплату.");
		}
		
		public function buyseats(count:int):void{
			GameApplication.app.connection.call("clanBuySeats", new Responder(onBuySeats, onBuySeatsError), count);
		}		
		private function onBuySeats(buyresult:Object):void{
			if (buyresult["error"] == BuyResultCode.OK){
				GameApplication.app.navigator.goClansRoom();
				GameApplication.app.popuper.showInfoPopUp("Поздравляем с удачной покупкой мест!");
			}else if (buyresult["error"] == BuyResultCode.NOT_ENOUGH_MONEY){
				GameApplication.app.popuper.showInfoPopUp("У Вас не достаточно денег для этой покупки.");
			}else if (buyresult["error"] == BuyResultCode.SQL_ERROR){
				GameApplication.app.popuper.showInfoPopUp("Ошибка при покупке. Код ошибки 756. Сообщите об ошибке разработчикам.");
			}else if (buyresult["error"] == BuyResultCode.OTHER){
				GameApplication.app.popuper.showInfoPopUp("Ошибка при покупке. Код ошибки 791. Сообщите об ошибке разработчикам.");
			}
		}
		private function onBuySeatsError(result:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Ошибка при покупке мест.");
		}
	}
}