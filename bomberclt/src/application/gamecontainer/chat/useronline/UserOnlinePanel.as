package application.gamecontainer.chat.useronline
{
	import application.gamecontainer.chat.useronline.title.TitleOnline;
	import application.gamecontainer.chat.useronline.title.TitleOnlineEvent;
	
	import spark.components.Group;
	import spark.components.VGroup;
	
	import utils.chat.room.Room;
	import utils.user.User;

	public class UserOnlinePanel extends Group
	{	
		private var _vgroup:VGroup = new VGroup();
		private var _titleonline:TitleOnline = new TitleOnline();	
		private var _userPanelOnline:ChatUserPanel;

		private var _rooms:Object = new Object();
		private var _currentRoomID:int;
		
		public function UserOnlinePanel(){			
			_vgroup.gap = 0;
			_userPanelOnline = new ChatUserPanel();			
			if (_rooms){
				for each (var r : Room in _rooms) {
					_userPanelOnline.addRoom(int(r.id), r);
				}				
			}
			
			_titleonline.addEventListener(TitleOnlineEvent.SHOW_FIND_USERS, onShowFindUsers, false, 0, true);
			_titleonline.addEventListener(TitleOnlineEvent.HIDE_FIND_USERS, onHideFindUsers, false, 0, true);
		}
		
		
		private function onShowFindUsers(e:TitleOnlineEvent):void{			
			_userPanelOnline.visible = false;
		}
		private function onHideFindUsers(e:TitleOnlineEvent):void{
			_userPanelOnline.visible = true;
		}
		
		private function setCountOnline(count:int) : void {
			_titleonline.count = count;
		}
		
		public function addActiveUser(user:User, id:int) : void {
			_userPanelOnline.addActiveUser(user, id);
			setCountOnline(_userPanelOnline.getcountActiv());
		}
		
		public function setActiveRoom(id:int) : void {
			_currentRoomID = id;
			_userPanelOnline.setActiveRoom (id);
			setCountOnline(_userPanelOnline.getcountActiv());
		}
		
		public function removeUser(id:int,roomId:int) : void {
			_userPanelOnline.removeUser(id,roomId);
			setCountOnline(_userPanelOnline.getcountActiv());
		}
		
		public function addUser(user:User, id:int, flash:Boolean = false) : void {
			_userPanelOnline.addUser(user, id, flash);
			setCountOnline(_userPanelOnline.getcountActiv());
		}
		
		public function removeRoom(id:String) : void {			
			delete _rooms[id];
			_userPanelOnline.removeRoom(id);
		}
		
		public function addRoom(room:Room) : void {			
			_rooms[room.id] = room;
			_userPanelOnline.addRoom(int(room.id), room);			
		}
		
		override protected function updateDisplayList(w:Number, h:Number):void{			
			this.graphics.clear();
			
			this.graphics.beginFill(0xDB9668, 1);
			this.graphics.drawRect(0, 0, w, h);
			this.graphics.endFill();
		
			this.graphics.beginFill(0xFBEFC4, 1);
			this.graphics.drawRect(1, 1, w - 2, h - 2);
			this.graphics.endFill();
			
			_vgroup.height = height;
			_vgroup.width = width;
			_vgroup.percentWidth = 100;
			_vgroup.percentHeight = 100;
		}
		
		override protected function createChildren() : void {
			super.createChildren();
			
			addElement(_vgroup);			
			_vgroup.addElement(_titleonline);			
			_vgroup.addElement(_userPanelOnline);
			
			_userPanelOnline.percentHeight = 100;
			_userPanelOnline.percentWidth = 100;
		}		
	}
}