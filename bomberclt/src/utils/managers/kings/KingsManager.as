package utils.managers.kings
{
	import application.GameApplication;
	import application.components.popup.kings.PopUpKings;
	
	import flash.net.Responder;
	
	import mx.collections.ArrayCollection;
	import mx.collections.Sort;
	
	import utils.game.betroominfo.BetResult;
	import utils.user.Sex;
	import utils.user.User;
	import utils.user.UserAspirantKingData;

	public class KingsManager
	{
		public function KingsManager()
		{
		}
		
		public function getKingsInfo():void{	
			GameApplication.app.connection.call("kingsGetAllInfo", new Responder(onGetKingsInfo, onGetKingsInfoError));
		}
		private function onGetKingsInfo(result:Object):void{			
			var startTime:Number = result["startTime"];
			var currenttime:Number = result["currenttime"];
			var roundDuration:Number = result["roundDuration"];			
			var passtime:Number = Math.max(0, (startTime + roundDuration) - currenttime);
			
			var king:User = User.createFromObject(result["king"]);
			var queen:User = User.createFromObject(result["queen"]);
			
			var iisaspirant:Boolean = false;
			var i:uint;
			var aspirantData:UserAspirantKingData;
			var aspirantsObj:Object = new Object();
			var aArr:Array = result["aspirants"];
			for(i = 0; i < aArr.length; i++){
				aspirantData = new UserAspirantKingData();
				aspirantData.user = User.createFromObject(aArr[i]);
				aspirantsObj[aspirantData.user.id] = aspirantData;
				
				if(aspirantData.user.id == GameApplication.app.userinfomanager.myuser.id) iisaspirant = true;
			}
			var bArr:Array = result["bets"];
			var betuserid:int;
			for(i = 0; i < bArr.length; i++){
				betuserid = bArr[i]["betuserid"];
				aspirantData = aspirantsObj[betuserid];
				if(aspirantData){
					aspirantData.bets += bArr[i]["bet"];
					if(bArr[i]["userid"] == GameApplication.app.userinfomanager.myuser.id){
						aspirantData.mybet += bArr[i]["bet"];
					}
				}
			}
			
			var aspirantsCollection:ArrayCollection = new ArrayCollection();
			
			for each(var aspirant:UserAspirantKingData in aspirantsObj){
				aspirantsCollection.addItem(aspirant);
			}
			var sort:Sort = new Sort();
			sort.compareFunction = sortAspirants;
			aspirantsCollection.sort = sort;
			aspirantsCollection.refresh();
			
			GameApplication.app.popuper.show(new PopUpKings(passtime, king, queen, aspirantsCollection, iisaspirant));
			
		}
		private function onGetKingsInfoError(error:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Ошибка при получении информации о короле и королеве.");
		}
		
		private function sortAspirants(a1:UserAspirantKingData, a2:UserAspirantKingData, fields:Array = null):int{
			if(a1.bets < a2.bets){
				return 1;
			}else{
				return -1;
			}
		}
		
		public function bet(betuserid:int, bet:int):void{	
			GameApplication.app.connection.call("kingsBet", new Responder(onBet, onBetError), betuserid, bet);
		}
		private function onBet(result:int):void{
			if(result == BetResult.OK){
				getKingsInfo();
			}else if(result == BetResult.NO_MONEY){
				GameApplication.app.popuper.showInfoPopUp("У вас недостаточно денег.");
			}else if(result == BetResult.OTHER){
				GameApplication.app.popuper.showInfoPopUp("Невозможно сделать ставку. Возможно ставка на одного претендента превышает максимальную.");
			}
		}
		private function onBetError(error:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Ошибка при выполнении ставки на королей.");
		}
		
		public function sendAspirantRequest():void{	
			GameApplication.app.connection.call("kingsAspirantRequest", new Responder(onSendAspirantRequest, onSendAspirantRequestError));
		}
		private function onSendAspirantRequest(result:int):void{
			if(result == BetResult.OK){
				getKingsInfo();
			}else if(result == BetResult.NO_MONEY){
				GameApplication.app.popuper.showInfoPopUp("У вас недостаточно денег.");
			}else if(result == BetResult.OTHER){
				GameApplication.app.popuper.showInfoPopUp("Невозможно совершить запрос.");
			}
		}
		private function onSendAspirantRequestError(error:Object):void{
			GameApplication.app.popuper.showInfoPopUp("Ошибка при отправке запроса.");
		}
	}
}