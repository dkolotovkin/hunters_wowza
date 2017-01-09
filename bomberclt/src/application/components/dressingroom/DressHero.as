package application.components.dressingroom
{
	import application.GameApplication;
	import application.gamecontainer.chat.actionmenu.bagarticle.ActionMenuBagItemArticle;
	
	import flash.display.MovieClip;
	import flash.events.Event;
	import flash.events.MouseEvent;
	import flash.filters.GlowFilter;
	
	import utils.managers.tooltip.IToolTiped;
	import utils.managers.tooltip.ToolTipType;
	import utils.shop.item.Item;
	
	public class DressHero extends MovieClip implements IToolTiped
	{
		private var _item:Item;
		private var _glow:GlowFilter = new GlowFilter(0xffffff, 1, 5, 5, 1);
		private var _mc:MovieClip;
		
		public function get item():Item{
			return _item;
		}
		
		public function DressHero(item:Item)
		{
			super();
			this.buttonMode = true;
			_item = item;
			var url:String = GameApplication.app.config.swfDataAddress + "/" + item.url;
			GameApplication.app.classloader.load(url, onLoad, null, onLoadError);
			
			addEventListener(Event.REMOVED_FROM_STAGE, onRemoveFromStage, false, 0, true);
			addEventListener(MouseEvent.CLICK, onClick, false, 0, true);
			addEventListener(MouseEvent.ROLL_OVER, onRollOver, false, 0, true);
			addEventListener(MouseEvent.ROLL_OUT, onRollOut, false, 0, true);
		}
		
		private function onRemoveFromStage(e:Event):void{
			removeEventListener(Event.REMOVED_FROM_STAGE, onRemoveFromStage);
			removeEventListener(MouseEvent.CLICK, onClick);
			removeEventListener(MouseEvent.ROLL_OVER, onRollOver);
			removeEventListener(MouseEvent.ROLL_OUT, onRollOut);
		}
		
		private function onClick(e:MouseEvent):void{
			GameApplication.app.actionShowerMenu.showMenu(new ActionMenuBagItemArticle(_item));
		}
		private function onRollOver(e:MouseEvent):void{
			filters = [_glow];
		}
		private function onRollOut(e:MouseEvent):void{
			filters = [];
		}
		
		private function onLoad(classMovie:Class):void{
			_mc = new classMovie();
			_mc.gotoAndStop(1);
			addChild(_mc);
		}
		
		override public function gotoAndPlay(frame:Object, scene:String=null):void{
			super.gotoAndPlay(frame, scene);
			if(_mc){
				_mc.gotoAndPlay(frame);
			}
		}
		
		override public function gotoAndStop(frame:Object, scene:String=null):void{
			super.gotoAndStop(frame, scene);
			if(_mc){
				_mc.gotoAndStop(frame);
			}
		}
		
		private function onLoadError():void{
			trace("Ошибка при загрузке: " + _item.url);
			onLoad(IconButHelp);
		}
		
		public function set toolTip(value:String) : void {
		}
		public function get toolTip() : String {				
			return "";
		}
		
		public function get toolTipDelay() : int {				
			return 400;
		}
		
		public function get toolTipDX() : int {
			return 10;
		}
		
		public function get toolTipDY() : int {
			return 2;
		}
		
		public function get toolTipType() : int {				
			return ToolTipType.TITLEANDDESCRIPTION;
		}
	}
}