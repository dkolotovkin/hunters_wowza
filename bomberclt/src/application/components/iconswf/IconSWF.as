package application.components.iconswf
{
	import flash.display.MovieClip;
	
	import mx.core.UIComponent;
	
	public class IconSWF extends UIComponent
	{
		protected var _icon:MovieClip;
		
		public function get icon() : MovieClip {
			return _icon;
		}
		
		
		public function IconSWF(IconClass:Class) {
			_icon = new IconClass ();
			_icon.stop();
			super();	
			width = _icon.width;
			height = _icon.height;		
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
		
		override protected function createChildren ():void{
			super.createChildren ();
			addChild(_icon);			
		}
	}
}