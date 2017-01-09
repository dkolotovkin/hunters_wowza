package application.gamecontainer.chat.useronline {
	
	import application.gamecontainer.chat.useronline.useritem.UserItem;
	
	import mx.collections.ArrayList;
	
	import spark.components.Group;
	import spark.components.List;
	import spark.components.Scroller;
	import spark.components.VGroup;
	
	import utils.chat.room.Room;
	import utils.user.User;

	/**
	 * @author dkolotovkin
	 */
	public class ChatUserList extends Group {

		private var _room : Room;
		private var _users : Object = new Object();
		private var _countActiv : int = 0;	
		private var _list:List = new List();
		
		
		public function get countActiv() : int {
			return _countActiv;
		}

		public function set countActiv(value : int) : void {
			if (_countActiv != value) {
				_countActiv = value;
			}    
		}

		public function ChatUserList() {
			_list.dataProvider = new ArrayList();
			_list.itemRenderer = new ChatUserListRenderer();
		}
		
		public function get room() : Room {
			return _room;
		}

		public function set room(value : Room) : void {
			if (_room != value) {
				_room = value;
			}    
		}
		
		public function show() : void {}
		public function hide() : void {}
		
		public function addUser(user : User,flash : Boolean = false) : Boolean {			
			if (!_users[user.id]) {
				
				var cu:ChatUser = new ChatUser(user, flash);				
				_users[user.id] = cu;				
				_list.dataProvider.addItemAt(cu, 0);				
				_countActiv++;
				return true;
			}
			return false;
		}
		override public function set verticalScrollPosition(value : Number) : void {
			super.verticalScrollPosition = value;			
		}
		
		public function removeUser(id : int) : Boolean {
			
			var user : ChatUser = _users[id];
			if (user) {				
				(_list.dataProvider as ArrayList).removeItem(user);
				delete _users[id];
				_countActiv--;
				return true;
			}
			return false;
		}
		
		public function removeAllUsers():void{
			for each(var user:ChatUser in _users){
				removeUser(user.user.id);
			}
		}

		override protected function createChildren() : void {
			super.createChildren();
			
			addElement(_list);
			_list.percentHeight = 100;
			_list.percentWidth = 100;
			_list.setStyle("contentBackgroundAlpha", 0);
			_list.setStyle("borderVisible", false);			
		}
	}
}
