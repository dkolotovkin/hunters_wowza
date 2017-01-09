package application.components.popup.sendmail
{
	import application.components.popup.PopUpTitled;
	
	import utils.user.User;
	
	public class PopUpSendMail extends PopUpTitled
	{
		private var _content:SendMailContent = new SendMailContent();
		
		public function PopUpSendMail(u:User)
		{
			super();
			title = "Отправить почту";
			_content.user = u;
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}