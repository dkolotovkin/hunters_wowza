package application.components.popup.banoff
{
	import application.GameApplication;
	import application.components.popup.PopUpTitled;
	
	import utils.user.UserRole;
	
	public class PopUpBanOff extends PopUpTitled
	{
		private var _content:BanOffContent = new BanOffContent();
		
		public function PopUpBanOff(price:int)
		{
			super();
			title = "Выход из бана";
			if(GameApplication.app.userinfomanager.myuser.role == UserRole.MODERATOR ||
				GameApplication.app.userinfomanager.myuser.role == UserRole.ADMINISTRATOR ||
				GameApplication.app.userinfomanager.myuser.role == UserRole.ADMINISTRATOR_MAIN){
				_content.money.money = Math.floor(price / 10);
			}else{
				_content.money.money = price;
			}
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}