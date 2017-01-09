package application.components.popup.userinfo
{
	import application.components.popup.PopUpTitled;
	
	import utils.user.User;
	
	public class PopUpUserInfo extends PopUpTitled
	{
		private var _userinfo:UserInfo = new UserInfo();
		public function PopUpUserInfo(user:User)
		{
			super();
			title = "Информация о персонаже";
			_userinfo.user = user;
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_userinfo);
		}
	}
}