package application.components.iconswf {
	import flash.display.MovieClip;
	
	import mx.core.UIComponent;

	/**
	 * @author dkolotovkin
	 */
	public class IconMXML extends UIComponent {
		protected var _icon:MovieClip;
		private var _scale:Number = 1;
		
		public function get icon():MovieClip{
			return _icon;
		}
		
		override public function set width(value:Number):void{
			super.width = value;
			_icon && (_icon.width = value);
		}
		
		public function set iconClass(ClipClass:Class):void{
			if(_icon == null){
				_icon = new ClipClass();
				_icon.scaleX = _icon.scaleY = _scale;
				_icon.stop();
				width = _icon.width;
				height = _icon.height;
				addChild(_icon);
			}
		}
		
		public function set scale(value:Number):void{
			_scale = value;
		}

		public function IconMXML() {			
			super();
		}		
		
		public function play():void{
			_icon.play();
		}
		
		public function gotoAndPlay(frame:uint):void{
			_icon.gotoAndPlay(frame);
		}
		
		public function gotoLabelAndPlay(label:String):void{
			_icon.gotoAndPlay(label);
		}
	}
}
