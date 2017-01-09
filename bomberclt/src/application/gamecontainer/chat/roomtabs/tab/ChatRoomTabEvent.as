package application.gamecontainer.chat.roomtabs.tab
{
	import flash.events.Event;
	
	public class ChatRoomTabEvent extends Event
	{
		public static const NEED_SELECTED_ROOM:String = "NEED_SELECTED_ROOM";
		public static const NEED_CLOSE_ROOM:String = "NEED_CLOSE_ROOM";
		public static const ADD_TAB:String = "ADD_TAB";
		
		public var tab:ChatRoomTab;
		
		public function ChatRoomTabEvent(type : String, _tab:ChatRoomTab = null) {
			super(type, bubbles, cancelable);			
			tab = _tab;
		}
		
		override public function clone() : Event {
			return new ChatRoomTabEvent (type, tab);
		}
	}
}