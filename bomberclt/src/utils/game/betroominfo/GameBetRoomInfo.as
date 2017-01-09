package utils.game.betroominfo
{
	import application.GameApplication;

	public class GameBetRoomInfo
	{
		public var id:int;
		public var creatorid:int;
		[Bindable]
		public var bet:int;
		[Bindable]
		public var time:int;
		[Bindable]
		public var isseats:Boolean;
		[Bindable]
		public var passward:Boolean;
		public var users:Array = new Array();
		
		public function GameBetRoomInfo(id:int, bet:int, time:int, isseats:Boolean, passward:Boolean, users:Array, creatorid:int = 0){
			this.id = id;
			this.bet = bet;
			this.time = time;
			this.isseats = isseats;
			this.passward = passward;
			this.creatorid = creatorid;
			
			for(var i:int = 0; i < users.length; i++){
				if(GameApplication.app.chatmanager.commonroom.getUser(int(users[i]))){
					this.users.push(GameApplication.app.chatmanager.commonroom.getUser(int(users[i])).title);
				}
			}
		}
	}
}