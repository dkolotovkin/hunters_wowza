package application.components.popup.present
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpPresent extends PopUpTitled
	{
		private var _content:PresentContent = new PresentContent();
		
		public function PopUpPresent(userid:int)
		{
			super();
			priority = 30;
			title = "Сделать подарок";
			_content.userid = userid;
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}