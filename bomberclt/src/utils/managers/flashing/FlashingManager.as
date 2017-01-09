package utils.managers.flashing
{
	import flash.display.DisplayObject;
	import flash.display.Graphics;
	import flash.display.Sprite;
	import flash.errors.IllegalOperationError;
	import flash.events.Event;
	import flash.utils.clearInterval;
	import flash.utils.setInterval;
	
	import mx.core.FlexGlobals;
	import mx.managers.ISystemManager;
	
	public class FlashingManager
	{
		private var flashingControllers:Array;
		
		private var _initialized:Boolean;
		private var _sid:int = -1;
		private var _selected:Boolean;
		private var _interval:int;
		
		public function FlashingManager(interval:int) 
		{
			_interval = interval;
		}
		
		public function init():void{
			if(_initialized) return;
			
			flashingControllers = new Array();
			_sid = setInterval(updateFlashingElements, _interval);
			_initialized = true;
		}
		
		public function clear():void{
			clearFlashingInterval();			
			
			if(_initialized){
				var flashingController:FlashingController;
				for (var j:int = 0; j < flashingControllers.length; j++) 
				{
					flashingController = flashingControllers[j];
					flashingController.Target.alpha = 1;
				}
				flashingControllers = null;
				_initialized = false;
			}
		}
		
		private function clearFlashingInterval():void{
			if(_sid != -1){
				clearInterval(_sid);
				_sid = -1;
			}
		}
		
		private function updateFlashingElements():void{
			_selected = !_selected;
			var _currentAlpha:Number = 1;
			if(_selected) _currentAlpha = .5;
			
			var i:int;
			var flashingController:FlashingController;
			for (i = 0; i < flashingControllers.length; i++) 
			{
				flashingController = flashingControllers[i];
				if(flashingController){
					flashingController.Update(_selected);
					if(flashingController.Finished){
						flashingController.Target.alpha = 1;
						flashingControllers.splice(i, 1);
						i--;
					}
				}				
			}
			
			if(flashingControllers.length == 0) clear();
		}
		
		public function addFlashingElement(target:DisplayObject, repeatCount:int = 1):void
		{	
			init();
			var fc:FlashingController = new FlashingController(target, repeatCount);
			flashingControllers.push(fc);
		}
	}
}

import flash.display.DisplayObject;

class FlashingController
{
	private var target:DisplayObject;
	private var repeatCount:int;
	private var finished:Boolean;
	
	public function FlashingController(target:DisplayObject, repeatCount:int):void
	{
		this.target = target;
		this.repeatCount = repeatCount;
	}
	
	public function Update(selected:Boolean):void
	{
		if(selected) target.alpha = .5;
		else target.alpha = 1;
		
		repeatCount--;
		if (repeatCount == 0) {
			finished = true;
		}
	}
	
	public function get Target():DisplayObject { return target; }	
	public function get Finished():Boolean { return finished; }
}