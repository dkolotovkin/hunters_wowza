package utils.constructor
{
	import flash.display.MovieClip;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.filters.GlowFilter;
	
	import utils.managers.constructor.ConstructorElementEvent;
	import utils.selector.ISelected;

	public class ConstructorElement extends MovieClip implements ISelected
	{
		public var elementId:uint;
		public var tagName:String;
		
		private var _selected:Boolean = false;
		
		private var _mc:MovieClip;
		
		public function get id():String{
			return String(elementId);
		}
		
		public function get selected ():Boolean {
			return _selected;
		}
		
		public function set selected (value:Boolean):void {
			if (_selected != value){
				_selected = value;
				if (_selected){
					dispatchEvent(new ConstructorElementEvent(ConstructorElementEvent.SELECTED, this));
				}else{
					dispatchEvent(new ConstructorElementEvent(ConstructorElementEvent.UNSELECTED, this));
				}
				updateState();
			}
		}
		
		private function updateState ():void{
			if (_selected) {
				this.filters = [new GlowFilter(0x000000, 1, 5, 5, 1)];
			}else{
				this.filters = [];
			}
		}
		
		public function set iconClass(className:Class):void{
			if(_mc) removeChild(_mc);
			_mc = new className();
			addChild(_mc);
		}
		
		public function ConstructorElement()
		{
			this.buttonMode = true;
			
			addEventListener(Event.RESIZE, onResize, false, 0, true);
			addEventListener(MouseEvent.MOUSE_DOWN, onMouseDown, false, 0, true);
			addEventListener(MouseEvent.MOUSE_UP, onMouseUp, false, 0, true);
		}
		
		private function onMouseDown(e:MouseEvent):void{
			selected = true;
			this.startDrag();
		}
		
		private function onMouseUp(e:MouseEvent):void{
			this.stopDrag();
		}
		
		private function onResize(e:Event):void{
			_mc.width = this.width;
			_mc.height = this.height;
		}
	}
}