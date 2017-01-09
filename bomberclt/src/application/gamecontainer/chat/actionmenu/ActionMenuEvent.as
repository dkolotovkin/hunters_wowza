package application.gamecontainer.chat.actionmenu {
	
	import flash.events.Event;

	/**
	 * @author dkolotovkin
	 */
	public class ActionMenuEvent extends Event {
		
		public static const ACTION:String = "ACTION";		public static const HIDE:String = "HIDE";		public static const SHOW:String = "SHOW";
		
		
		public var actionID:String;
		
		public var menu:ActionMenu;
		
		public function ActionMenuEvent(type : String,menu:ActionMenu,actionID:String) {
			super(type, false, false);
			
			this.menu = menu;			this.actionID = actionID;
		}

		
		override public function clone() : Event {
			return new ActionMenuEvent (type,menu,actionID);
			
		}
	}
}
