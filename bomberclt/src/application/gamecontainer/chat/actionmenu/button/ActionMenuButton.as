package application.gamecontainer.chat.actionmenu.button {
	import application.GameApplication;
	
	import flash.events.MouseEvent;
	
	import spark.components.SkinnableContainer;
	
	import utils.interfaces.IID;

	/**
	 * @author dkolotovkin
	 */
	public class ActionMenuButton extends SkinnableContainer implements IID {
		private var  _title:String;
		private var _over:Boolean;		private var _down:Boolean;	
		
		override public function set enabled(value : Boolean) : void {
			super.enabled = value;			
			updateState();
		}		
		
		public function get down ():Boolean {
			return _down;
		}
		
		public function set down (value:Boolean):void {
			if (_down != value){
				_down = value;
				updateState ();
			}	
		}		
		
		public function get over ():Boolean {
			return _over;
		}
		
		public function set over (value:Boolean):void {
			if (_over != value){
				_over = value;
				updateState ();
			}	
		}
		
		private function updateState() : void {			
			if (initialized){
				if (enabled){
					if (_down){
						skin.currentState = ActionMenuButtonSkinState.DOWN;
					}else {
						if (_over){							skin.currentState = ActionMenuButtonSkinState.OVER;
						}else{
							skin.currentState = ActionMenuButtonSkinState.NORMAL;			
						}
					}
				}else{					
					skin.currentState = ActionMenuButtonSkinState.DISABLED;	
				}
			}
			
		}

		
		override public function set initialized(value : Boolean) : void {
			super.initialized = value;
			updateState();			
		}

		
		public function get title ():String {
			return _title;
		}
		
		public function set title (value:String):void {
			if (_title != value){
				_title = value;
			}	
		}
		
		
		public function ActionMenuButton() {
			setStyle("skinClass", ActionMenuButtonSkin);			
			buttonMode = true;
			
			addEventListener(MouseEvent.ROLL_OVER, onRollOver, false, 0, false);			addEventListener(MouseEvent.ROLL_OUT, onRollOut, false, 0, false);			addEventListener(MouseEvent.MOUSE_DOWN, onMouseDown, false, 0, false);			addEventListener(MouseEvent.CLICK, onClick, false, 0, false);
		}
		
		private function onClick(event : MouseEvent) : void {
			dispatchEvent(new ActionMenuButtonEvent(ActionMenuButtonEvent.CLICK_ACTION, this));			
		}

		private function onMouseDown(event : MouseEvent) : void {
			down = true;
			stage.addEventListener(MouseEvent.MOUSE_UP, onMouseUp, false, 0, true);		}
		
		private function onMouseUp(event : MouseEvent) : void {
			GameApplication.app.stage.removeEventListener(MouseEvent.MOUSE_UP, onMouseUp);
			down = false;
		}

		private function onRollOut(event : MouseEvent) : void {
			over = false;
		}

		private function onRollOver(event : MouseEvent) : void {
			over = true;
		}

		override protected function updateDisplayList (w:Number,h:Number):void{
			super.updateDisplayList (w,h);
			
			(skin as ActionMenuButtonSkin).titleTxt.text = _title;
			updateState();
		}
		
	}
}
