package application.gamecontainer.chat.useronline.useritem {
	import application.GameApplication;
	import application.components.buttonswf.ButtonMXML;
	import application.components.buttonswf.ButtonSWF;
	import application.components.iconswf.IconSWF;
	import application.gamecontainer.chat.actionmenu.chatuser.ActionMenuChatUser;
	
	import flash.display.Sprite;
	import flash.events.MouseEvent;
	import flash.text.TextField;
	
	import mx.core.UIComponent;
	
	import utils.chat.Flasher;
	import utils.chat.IFlashing;
	import utils.chat.room.RoomEvent;
	import utils.user.Sex;
	import utils.user.User;
	import utils.user.UserEvent;
	import utils.user.UserRole;
	
	public class UserItem extends UIComponent implements IFlashing
	{		
		private var _userInfo:User;		
		private var _over:Boolean;
		private var _flash:Boolean;		
		private var _flasher:Flasher;		
		private var _selected:Boolean;
		private var _tf:TextField = new TextField();
		private var _icon:IconSWF;
		private var _bg:Sprite = new Sprite ();
		
		private var _color:String = "E27125";
		private var bs:String = "";
		private var be:String = "";	
		
		public function get selected ():Boolean {
			return _selected;
		}
		
		public function set selected (value:Boolean):void {
			if (_selected != value){
				_selected = value;
				updateState();
			}
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
		
		public function startFlash ():void {
			_flasher.start(400, 4);
		}
		
		public function stopFlash ():void {
			_flasher.stop();
		}
		
		public function get over ():Boolean {
			return _over;
		}
		
		public function set over (value:Boolean):void {
			if (_over != value){
				_over = value;
				initialized && updateState ();
			}	
		}
		
		public function clearState ():void {
			_selected = false;
			_over = false;
			_flasher.stop();
		}
		
		private function onPersClick(event : MouseEvent) : void {			
			GameApplication.app.actionShowerMenu.showMenu(new ActionMenuChatUser(_userInfo));			
		}
		
		public function UserItem(userInfo:User)	{
			super();
			_icon = new IconSWF(IconUserOnline);
			_userInfo = userInfo;
			
			if (_userInfo.sex == Sex.MALE){
				_icon.icon.gotoAndStop(1);
			}else{
				_icon.icon.gotoAndStop(2);
			}
			
			height = 20;
			width = 150;
			_flasher = new Flasher(this);
			_userInfo.addEventListener(UserEvent.UPDATE, onUpdateUser);
			mouseEnabled = false;
			
			id = userInfo.id.toString();
			
			_tf.width = 140;
			_tf.height = height;
			_tf.selectable = false;
			_tf.wordWrap = false;
			_tf.multiline = false;
			_tf.mouseEnabled = false;
			_tf.mouseWheelEnabled = false;
			_tf.x = 15;
			_tf.y = -2;
			
			_bg.addEventListener(MouseEvent.CLICK,onPersClick);
			addEventListener(MouseEvent.MOUSE_OVER, onMouseOver);
			addEventListener(MouseEvent.MOUSE_OUT, onMouseOut);
			_icon.addEventListener(MouseEvent.CLICK, onPersClick);			
			
			
			buttonMode = true;
			_bg.graphics.beginFill(0x000000, 0);
			_bg.graphics.drawRect(0, 0, width, height);
			_bg.graphics.endFill();
		}
		
		private function onUpdateUser(e:UserEvent):void{			
			if (_userInfo.sex == Sex.MALE){
				_icon.icon.gotoAndStop(1);
			}else{
				_icon.icon.gotoAndStop(2);
			}			
			updateState();
		}
		
		
		override protected function createChildren ():void{
			super.createChildren ();
			
			addChild(_tf);
			addChild(_bg);
			addChild(_icon);			
		}		
		
		private function updateState ():void {
			trace("************* " + _userInfo.role);
			if (initialized){
				if (_selected){
					_color = "00FF00";
					bs = "<b>";
					be = "</b>";
				}else if (_over){
					_color = "CCFF66";
					bs = "";
					be = "";
				}else if (_flash){
					_color = "CCFF00";
					bs = "";
					be = "";					
				}else{
					
					if (_userInfo.king == 1){
						_color = "FF0000";
					}else if (_userInfo.role == UserRole.MODERATOR){
						_color = "00FFFF";
					}else if (_userInfo.role == UserRole.ADMINISTRATOR || _userInfo.role == UserRole.ADMINISTRATOR_MAIN){
						_color = "FF66CC";
					}else{
						_color = "00FF00";
					}
					bs = "";
					be = "";
				}
				_tf.htmlText = 
					"<p align='left'>"+ 
					"<font face='_sans' color='#"+ _color +"' size='12'>"+ bs + _userInfo.title + be + " </font>"+
					"<font face='_sans' color='#00FFFF' size='14'>[" + _userInfo.level + "]</font>" +
					"</p>";				 
				//_icon.flash = _flash || _over;
			}
		}
		
		private function onMouseOut(event : MouseEvent) : void {			
			over = false;			
		}
		
		private function onMouseOver(event : MouseEvent) : void {			
			over = true;			
		}	
		
		private function onClick (event:MouseEvent):void{
		}
		
		override public function set initialized(value : Boolean) : void {
			super.initialized = value;
			updateState ();
		}
	}
}