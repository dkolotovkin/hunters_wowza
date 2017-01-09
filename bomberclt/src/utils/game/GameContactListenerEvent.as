package utils.game
{
	import flash.events.Event;
	
	public class GameContactListenerEvent extends Event
	{
		public static var BULLET:String = "BULLET";
		public static var SOURCE:String = "SOURCE";
		public static var CONTACTGROUND:String = "CONTACTGROUND";
		
		public var id:uint;
		
		public function GameContactListenerEvent(type:String, id:uint)
		{
			super(type, false, false);
			this.id = id;
		}
	}
}