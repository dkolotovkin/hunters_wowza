package utils.managers.useitemingame
{
	import Box2D.Dynamics.b2Body;
	
	import application.GameApplication;
	
	import flash.display.MovieClip;
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	import flash.events.KeyboardEvent;
	import flash.events.MouseEvent;
	import flash.geom.Rectangle;
	import flash.ui.Keyboard;
	
	import mx.core.UIComponent;
	
	import utils.chat.message.MessageType;
	import utils.chat.room.Room;
	import utils.shop.item.Item;
	
	public class UseItemInGameManager extends EventDispatcher
	{
		private var _gameitemmc:MovieClip;
		private var _curritem:Item;
		private var _currentpid:int;
		
		public function UseItemInGameManager(target:IEventDispatcher=null)
		{
			super(target);
		}
		
		public function useItem(item:Item):void{			
			
		}
		
		private function addListeners():void{
			GameApplication.app.stage.addEventListener(MouseEvent.CLICK, onClick, false, 0, true);
			GameApplication.app.stage.addEventListener(MouseEvent.MOUSE_MOVE, onMouseMove, false, 0, true);
			GameApplication.app.stage.addEventListener(KeyboardEvent.KEY_DOWN, onKeyDown, false, 0, true);		
		}
		
		public function useshopitem(prototypeid:int):void{
		}
		
		private function onKeyDown(e:KeyboardEvent):void{			
			if (e.keyCode == 27){
				clear();
			}
		}
		
		private function onClick(e:MouseEvent):void{				
		}
		
		private function sendWarningMsg(msg:String):void{
			var message:Object = new Object();
			message["from"] = GameApplication.app.userinfomanager.myuser;
			message["type"] = MessageType.USEITEM;
			message["text"] = msg;
			var room:Room = GameApplication.app.gameContainer.chat.getRoom(GameApplication.app.gamemanager.gameworld.roomID);
			room.addMessage(message);
		}
		
		public function clear():void{
			if(_curritem || _currentpid){
				GameApplication.app.stage.removeEventListener(MouseEvent.CLICK, onClick);
				GameApplication.app.stage.removeEventListener(MouseEvent.MOUSE_MOVE, onMouseMove);
				GameApplication.app.stage.removeEventListener(KeyboardEvent.KEY_DOWN, onKeyDown);
				
				if (_gameitemmc != null && GameApplication.app.gamemanager.gameworld.gameUI.contains(_gameitemmc)){
					GameApplication.app.gamemanager.gameworld.gameUI.removeChild(_gameitemmc);
				}
				_gameitemmc = null;
				_curritem = null;
			}
		}
		
		private function onMouseMove(e:MouseEvent):void{
			updateXY();
		}
		
		private function updateXY():void{
			if (_gameitemmc){
				_gameitemmc.x = GameApplication.app.gamemanager.gameworld.gameUI.mouseX;
				_gameitemmc.y = GameApplication.app.gamemanager.gameworld.gameUI.mouseY;
			}
		}
	}
}