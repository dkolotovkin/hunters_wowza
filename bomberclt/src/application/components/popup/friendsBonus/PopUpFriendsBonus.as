package application.components.popup.friendsBonus
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpFriendsBonus extends PopUpTitled
	{
		private var _content:FriendsBonusContent = new FriendsBonusContent();
		
		public function PopUpFriendsBonus(count:int)
		{
			super();
			title = "";
			_content.count = count;
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}