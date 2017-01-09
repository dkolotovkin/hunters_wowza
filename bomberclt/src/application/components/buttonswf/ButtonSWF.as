package application.components.buttonswf
{
	import application.GameApplication;
	import application.components.iconswf.IconSWF;
	
	import flash.events.MouseEvent;
	
	import utils.chat.IFlashing;
	import utils.selector.ISelected;
	
	public class ButtonSWF extends IconSWF implements ISelected, IFlashing
	{
		protected var _down:Boolean;
		protected var _over:Boolean;
		protected var _selected:Boolean;
		protected var _flash:Boolean;
		
		
		//		private var _disable:Boolean;
		
		protected var _toogle:Boolean;
		
		
		override public function set width(value : Number) : void {
			super.width = value;
			updateState();
		}
		
		override public function set x(value : Number) : void {
			super.x = int(value);
		}
		
		override public function set y(value : Number) : void {
			super.y = int(value);
		}
		
		
		public function get flash ():Boolean {
			return _flash;
		}
		
		public function set flash (value:Boolean):void {
			if (_flash != value){
				_flash = value;
				updateState();
			}	
		}
		
		
		public function get toogle ():Boolean {
			return _toogle;
		}
		
		public function set toogle (value:Boolean):void {
			if (_toogle != value){
				_toogle = value;
			}	
		}
		
		
		
		public function get selected ():Boolean {
			return _selected;
		}
		
		public function set selected (value:Boolean):void {
			if (_selected != value){
				_selected = value;
				
				if (value){
					dispatchEvent(new ButtonSWFEvent(ButtonSWFEvent.SELECTED,this));
				}else {
					dispatchEvent(new ButtonSWFEvent(ButtonSWFEvent.UNSELECTED,this));
				}
				updateState();
				
			}	
		}
		
		
		public function get over ():Boolean {
			return _over;
		}
		
		public function set over (value:Boolean):void {
			if (_over != value){
				_over = value;
				updateState();
			}	
		}
		
		
		
		override public function set enabled(value : Boolean) : void {
			super.enabled = value;
			updateState();
		}
		
		
		//		public function get disable ():Boolean {
		//			return _disable;
		//		}
		//		
		//		public function set disable (value:Boolean):void {
		//			if (_disable != value){
		//				_disable = value;
		//				updateState();
		//			}	
		//		}
		
		
		
		public function get down ():Boolean {
			return _down;
		}
		
		public function set down (value:Boolean):void {
			if (_down != value){
				_down = value;
				updateState();
			}	
		}
		
		public function ButtonSWF(IconClass:Class) {
			super (IconClass);
			
			buttonMode = true;
			
			addEventListener(MouseEvent.ROLL_OVER, onRollOver, false, 0, true);
			addEventListener(MouseEvent.ROLL_OUT, onRollOut, false, 0, true);
			addEventListener(MouseEvent.MOUSE_DOWN, onMouseDown, false, 0, true);
			addEventListener(MouseEvent.CLICK, onClick, false, 0, true);
		}
		
		private function onClick(event : MouseEvent) : void {	
			if (_toogle){
				selected = !selected;
			}
			dispatchEvent(new ButtonSWFEvent(ButtonSWFEvent.CLICK, this));
		}
		
		private function onMouseUp(event : MouseEvent) : void {
			down = false;
			GameApplication.app.stage.removeEventListener(MouseEvent.MOUSE_UP, onMouseUp);
		}
		
		private function onMouseDown(event : MouseEvent) : void {
			down = true;
			GameApplication.app.stage.addEventListener(MouseEvent.MOUSE_UP, onMouseUp, false, 0, true);
		}
		
		private function onRollOut(event : MouseEvent) : void {
			over = false;
		}
		
		private function onRollOver(event : MouseEvent) : void {
			over = true;
		}
		
		protected function updateState ():Boolean{
			if (initialized){
				if (enabled){
					mouseEnabled = true;
					mouseChildren = true;
					_icon.alpha = 1;
					
					if (_selected || _down){
						_icon.gotoAndStop(3);
					}else{
						if (_over || _flash){
							_icon.gotoAndStop(2);
						}else{
							_icon.gotoAndStop(1);
						}
					}
				}else{
					if (_icon.totalFrames >3){
						_icon.gotoAndStop(4);
						_icon.alpha = 1;
					}else{
						_icon.gotoAndStop(1);
						_icon.alpha = .3;
					}
					mouseEnabled = false;
					mouseChildren = false;
				}
				return true;
			}
			return false;
		}
		
		
		override public function set initialized(value : Boolean) : void {
			super.initialized = value;
			updateState();
		}
	}
}