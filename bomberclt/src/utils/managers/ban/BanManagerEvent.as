package utils.managers.ban
{
	import flash.events.Event;
	
	public class BanManagerEvent extends Event
	{
		public static var TIME_UPDATE:String = "TIME_UPDATE";
		
		public var time:int;
		
		public function BanManagerEvent(type:String, time:int)
		{
			super(type, false, false);
			this.time = time;
		}
	}
}