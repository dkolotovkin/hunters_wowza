package application.components.popup.friends
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpFriends extends PopUpTitled
	{
		private var _content:FriendsContent = new FriendsContent();
		
		public function PopUpFriends()
		{
			super();
			title = "Мои друзья";
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}