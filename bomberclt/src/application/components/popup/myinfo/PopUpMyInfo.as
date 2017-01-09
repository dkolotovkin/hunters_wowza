package application.components.popup.myinfo
{
	import application.components.popup.PopUpTitled;
	
	import utils.user.User;
	
	public class PopUpMyInfo extends PopUpTitled
	{
		private var _userinfo:MyUserInfo = new MyUserInfo();
		
		public function PopUpMyInfo(user:User)
		{
			super();
			title = "Информация о моем персонаже";
			_userinfo.user = user;
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_userinfo);
		}
	}
}