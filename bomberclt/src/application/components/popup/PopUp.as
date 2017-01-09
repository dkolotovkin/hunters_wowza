package application.components.popup {
	import application.components.buttonswf.ButtonSWF;
	import flash.filters.DropShadowFilter;
	
	import flash.events.MouseEvent;
	
	import spark.components.SkinnableContainer;

	/**
	 * @author dkolotovkin
	 */
	public class PopUp extends SkinnableContainer {
		public var priority:int = 0;
		protected var _closeBt:ButtonSWF;
		
		protected var _showed:Boolean = false;
		protected var _soft:Boolean;
		
		private var _filter:DropShadowFilter = new DropShadowFilter(5, 45, 0, 1, 3, 3, .5);
		
		public function get soft ():Boolean {
			return _soft;
		}
		
		public function set soft (value:Boolean):void {
			if (_soft != value){
				_soft = value;
			}	
		}
		
		
		public function get showed ():Boolean {
			return _showed;
		}
		
		
		public function onShow ():Boolean {			
			if (!_showed){
				_showed = true;
				dispatchEvent(new PopUpEvent (PopUpEvent.SHOW_POPUP,this));
				return true;
			}
			return false;
		}
		
		public function onHide ():Boolean {	
			
			if (_showed){				
				_showed = false;
				dispatchEvent(new PopUpEvent (PopUpEvent.HIDE_POPUP,this));
				return true;
			}
			return false;
		}		
		
		public function PopUp(type:int = 0) {			
			_closeBt = new ButtonSWF(IconButClose);
			setStyle("skinClass", PopUpSkin);
			_closeBt.addEventListener(MouseEvent.CLICK, onClose, false, 0, true);
		}
		
		protected function onClose(event : MouseEvent) : void {	
			event.stopPropagation();
			onHide();
		}
		
		override protected function commitProperties() : void {
			super.commitProperties();		
			_closeBt.right = 0;
			_closeBt.top = 0;		
		}		
		
		override protected function createChildren ():void{
			super.createChildren ();
			addElement(_closeBt);
			this.filters = [_filter];
		}
	}
}
