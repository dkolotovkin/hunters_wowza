package application.gamecontainer.chat.useronline {	
	import spark.components.SkinnableContainer;
	
	import utils.chat.room.Room;
	import utils.user.User;

	/**
	 * @author dkolotovkin
	 */
	public class ChatUserPanel extends SkinnableContainer{
		
		private var _rooms:Object = new Object ();
		private var _activeList:ChatUserList;

		public function getcountActiv():int{			
			return _activeList.countActiv;
		}
		
		public function ChatUserPanel() {
			setStyle("skinClass", ChatUserListSkin);			
		}
		
		public function addActiveUser(user : User, roomId:int) : void {
			addUser (user, roomId, true);
		}
		
		public function addUser (user:User,roomId:int,active:Boolean = false):Boolean{
			var userList:ChatUserList = _rooms[roomId];
			if (!userList){
				userList = addRoom (roomId);
			}
			return userList.addUser(user, active);
		}
		
		
		public function removeUser (userId:int,roomId:int):Boolean{
			var list:ChatUserList = _rooms[roomId];
			
			if (list){
				return list.removeUser(userId);
			}
			return false;
		}
		
		public function addRoom (roomId:int, room:Room = null):ChatUserList{
			var list:ChatUserList = _rooms[roomId];
			if (!list){
				list = new ChatUserList ();
				
				list.left = 4;
				list.right = 4;
				list.top = 4;
				list.bottom = 4;
				list.room = room;
				list.percentWidth = 100;				list.percentHeight = 100;
				list.id = String(roomId);
				_rooms[roomId] = list;
				
			}			setActivRoomAfterInit(int(list.room.id));
			return list;
		}	
		
		
		public function removeRoom (roomId:String):Boolean{
			if (_rooms[roomId]){
				delete _rooms[roomId];
				return true;
			}
			return false;
		}		
		
		public function setActivRoomAfterInit(roomId:int):void{
			setActiveRoom(roomId);
			
			for each (var user : User in (_rooms[roomId] as ChatUserList).room.users) {					
				addUser(user, roomId);
			}		
		}
		
		public function setActiveRoom (roomId:int):Boolean{
			var newlist:ChatUserList = _rooms[roomId];
			if (!newlist){
				newlist = addRoom(roomId);
			}
			if (newlist != _activeList){
				if (_activeList){
					_activeList.hide();					
					removeElement(_activeList);
				}
				_activeList = newlist;				
				_activeList.show();				
				addElement(_activeList);
				return true;
			}
			return false;
		}
	}
}
