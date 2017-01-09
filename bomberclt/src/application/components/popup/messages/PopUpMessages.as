package application.components.popup.messages
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpMessages extends PopUpTitled
	{
		private var _content:MessagesContent = new MessagesContent();
		
		public function PopUpMessages()
		{
			super();
			title = "Мои сообщения";
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}