package utils.user
{
	import flash.events.Event;
	
	public class UserEvent extends Event
	{
		public static var UPDATE:String = "UPDATE";
		
		public function UserEvent(type:String)
		{
			super(type, false, false);
		}
	}
}