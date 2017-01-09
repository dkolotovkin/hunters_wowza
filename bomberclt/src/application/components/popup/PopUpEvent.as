package application.components.popup {
	import flash.events.Event;

	/**
	 * @author dkolotovkin
	 */
	public class PopUpEvent extends Event {
		
		
		
		public var popup:PopUp;
		
		public function PopUpEvent(type : String,popup:PopUp) {
			super(type, false, false);
			this.popup = popup;
		}

		override public function clone() : Event {
			return new PopUpEvent (type,popup);
		}
	}
}