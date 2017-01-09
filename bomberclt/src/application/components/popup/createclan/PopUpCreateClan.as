package application.components.popup.createclan
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpCreateClan extends PopUpTitled
	{
		private var _content:CreateClanContent = new CreateClanContent();
		
		public function PopUpCreateClan()
		{
			super();
			title = "Создание альянса";
			_content.setCloseFunction(closePopUp);
		}
		
		private function closePopUp():void{
			onHide();
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}