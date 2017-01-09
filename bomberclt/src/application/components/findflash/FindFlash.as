package application.components.findflash
{
	import flash.display.MovieClip;
	import flash.display.Sprite;
	import flash.events.Event;
	
	public class FindFlash extends MovieClip
	{
		private var _mc:MovieClip;
		
		public function FindFlash(MovieClass:Class)
		{
			super();
			_mc = new MovieClass();
			addChild(_mc);
			addEventListener(Event.ENTER_FRAME, onEnterFrame);
		}
		
		private function onEnterFrame(e:Event):void{
			if (_mc.currentFrame == _mc.totalFrames){
				removeEventListener(Event.ENTER_FRAME, onEnterFrame);
				this.parent.removeChild(this);			
			}
		}
	}
}