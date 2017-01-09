package utils.chat.room
{
	import flash.events.Event;
	
	import utils.user.User;
	
	public class RoomEvent extends Event
	{
		public static var ADD_USER:String = "ADD_USER";
		public static var REMOVE_USER:String = "REMOVE_USER";
		public static var ADD_MESSAGE:String = "ADD_MESSAGE";
		public static var SHOW_ACTION_MENU:String = "SHOW_ACTION_MENU";
		
		public var user:User;
		public var room:Room;
		
		public function RoomEvent(type:String, u:User, r:Room)
		{
			super(type, false, false);
			this.user = u;
			this.room = r;
		}
		
		override public function clone() : Event {
			return new RoomEvent(type, user, room);
		}
	}
}