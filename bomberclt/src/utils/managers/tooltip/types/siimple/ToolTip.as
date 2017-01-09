package utils.managers.tooltip.types.siimple {
	import application.GameApplication;
	
	import flash.events.MouseEvent;
	
	import mx.managers.ToolTipManager;
	
	import spark.components.supportClasses.SkinnableComponent;
	
	import utils.managers.tooltip.IToolTipExt;
	import utils.managers.tooltip.IToolTiped;

	
	public class ToolTip extends SkinnableComponent implements IToolTipExt {

		protected var _text : String;
		protected var _dx : int = 10;		protected var _dy : int = 2;

		
		public function ToolTip() {
			setStyle("skinClass", ToolTipSkin);
		}
		
		public function show(dx : int,dy : int) : void {
			if (!super.visible){
				super.visible = true;
				_dx = dx;				_dy = dy;
				
				GameApplication.app.addEventListener(MouseEvent.MOUSE_MOVE, onMouseMove, false, 0, true);
				move(GameApplication.app.mouseX + _dx, GameApplication.app.mouseY + 20+ _dy);
			}
		}

		public function hide() : void {
			if (super.visible){
				super.visible = false;
				GameApplication.app.removeEventListener(MouseEvent.MOUSE_MOVE, onMouseMove);
			}
		}

		
		override public function set visible(value : Boolean) : void {
			if (!(ToolTipManager.currentTarget is IToolTiped)){
				super.visible = value;
				if (value) {
					_dx = 10;
					_dy = 2;
					GameApplication.app.addEventListener(MouseEvent.MOUSE_MOVE, onMouseMove, false, 0, true);
				} else {
					GameApplication.app.removeEventListener(MouseEvent.MOUSE_MOVE, onMouseMove);				
				}
			}else{
				super.visible = false;
			}
		}

		protected function onMouseMove(event : MouseEvent) : void {
			move(GameApplication.app.mouseX + _dx, GameApplication.app.mouseY + 20 + _dy);
			event.updateAfterEvent();
		}
		
		override public function move(x : Number, y : Number) : void {
			onMove(x, y, width, height);
		}

		protected function onMove(x : Number, y : Number,w : Number,h : Number) : void {

			if (initialized) {	
				if (x < screen.x) {
					x = screen.x;
				}else if (x > screen.x + screen.width - w) {
					x = screen.x + screen.width - w;
				}
				
				if (y < screen.y) {
					y = screen.y;
				}else if (y > screen.y + screen.height - h) {
					y = screen.y + screen.height - h;
				}
			}
			this.x = x;
			this.y = y;
		}		
		
		public function get text() : String {
			return _text;
		}

		public function set text(value : String) : void {
			if (_text != value) {
				_text = value;
				updateState();
			}	
		}

		
		override protected function updateDisplayList(w : Number,h : Number) : void {
			super.updateDisplayList(w, h);
			onMove(x, y, w, h);
		}

		public function updateState() : void {
			if (initialized) {
				(skin as ToolTipSkin).text.text = _text;
			}
		}
		
		override public function set initialized(value : Boolean) : void {
			super.initialized = value;
			updateState();
		}
		
		public function set target(value : IToolTiped) : void {
			text = value.toolTip;
		}
	}
}
