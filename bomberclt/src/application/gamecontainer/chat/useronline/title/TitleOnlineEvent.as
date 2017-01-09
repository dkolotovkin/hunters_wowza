package application.gamecontainer.chat.useronline.title
{
	import flash.events.Event;
	
	public class TitleOnlineEvent extends Event
	{
		public static var SHOW_FIND_USERS:String = "SHOW_FIND_USERS";
		public static var HIDE_FIND_USERS:String = "HIDE_FIND_USERS";
		
		public function TitleOnlineEvent(type:String)
		{
			super(type, false, false);
		}
	}
}