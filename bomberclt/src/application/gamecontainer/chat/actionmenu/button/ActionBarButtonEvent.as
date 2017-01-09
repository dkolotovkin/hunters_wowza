package application.gamecontainer.chat.actionmenu.button {
	import flash.events.Event;

	/**
	 * @author dkolotovkin
	 */
	public class ActionBarButtonEvent extends Event {
		
		public static const CLICK_BUTTON:String = "CLICK_BUTTON";
		
		
		public var button:ActionBarButton;
		
		public function ActionBarButtonEvent(type : String,button:ActionBarButton) {
			super(type, false, false);
			this.button = button;
		}

		override public function clone() : Event {
			return new ActionBarButtonEvent(type,button);
		}
	}
}
