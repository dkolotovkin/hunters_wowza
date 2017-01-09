package application.gamecontainer.chat.actionmenu.button {
	import flash.events.Event;

	/**
	 * @author dkolotovkin
	 */
	public class ActionMenuButtonEvent extends Event {		
		
		public static const CLICK_ACTION:String = "CLICK_ACTION";
		
		public var button:ActionMenuButton;
		
		public function ActionMenuButtonEvent(type : String,button:ActionMenuButton) {
			super(type, false, false);
			this.button = button;
		}
	}
}
