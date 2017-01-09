package application.gamecontainer.chat.actionmenu.button {
	import application.GameApplication;	
	import flash.display.MovieClip;
	import flash.events.MouseEvent;
	
	import mx.core.UIComponent;

	/**
	 * @author dkolotovkin
	 */
	public class ActionBarButton extends UIComponent {		
		protected var _type:String;	
		protected var _mc:MovieClip;
		protected var _press:Boolean = false;
		protected var _over:Boolean = false;
		
		
		public function get over ():Boolean {
			return _over;
		}
		
		public function set over (value:Boolean):void {
			if (_over != value){
				_over = value;
				updateState();
			}	
		}
		
		
		public function get press ():Boolean {
			return _press;
		}
		
		public function set press (value:Boolean):void {
			if (_press != value){
				_press = value;
				updateState();
			}	
		}
		
		
		
		public function ActionBarButton(type:String,bigsize:Boolean = false) {
			_type = type;
			if (bigsize){
				width = height = 36;			}else{
				width = height = 22;			
			}
			buttonMode = true;
			addEventListener(MouseEvent.MOUSE_DOWN, onMouseDown, false, 0, true);
			addEventListener(MouseEvent.ROLL_OVER, onMouseOver, false, 0, true);
			addEventListener(MouseEvent.ROLL_OUT, onMouseOut, false, 0, true);			addEventListener(MouseEvent.CLICK, onClick, false, 0, true);
		}
		
		protected function onClick(event : MouseEvent) : void {
			dispatchEvent(new ActionBarButtonEvent (ActionBarButtonEvent.CLICK_BUTTON,this));
		}	
		
		
		
		public function get type ():String {
		    return _type;
		}

		protected function getIcon():MovieClip
		{
			return null;
		}
		
		protected function onMouseUp(event : MouseEvent) : void {
			GameApplication.app.stage.removeEventListener(MouseEvent.MOUSE_UP, onMouseUp);
			press = false;	
		}

		protected function onMouseDown(event : MouseEvent) : void {
			GameApplication.app.stage.addEventListener(MouseEvent.MOUSE_UP, onMouseUp, false, 0, true);
			press = true;
		}
		protected function onMouseOut(event : MouseEvent) : void {
			over = false;	
		}

		protected function onMouseOver(event : MouseEvent) : void {
			over = true;
		}
		
		
		
		protected function updateState ():void {
			if (_press){
				_mc.gotoAndStop(3);
			} else if (_over){
				_mc.gotoAndStop(2);
			}else{
				_mc.gotoAndStop(1);
			}
		}
		
		

		override protected function createChildren() : void {
			super.createChildren();
			_mc = getIcon();
			if (_mc) {
				addChild(_mc);			
				_mc.stop();
				_mc.cacheAsBitmap = true;
			}
		}
	}
}
