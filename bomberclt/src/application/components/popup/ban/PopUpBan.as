package application.components.popup.ban
{
	import application.components.popup.PopUpTitled;
	
	import flash.events.Event;
	
	public class PopUpBan extends PopUpTitled
	{
		private var _content:ContentBan = new ContentBan();
		
		public function PopUpBan(userID:int)
		{
			super();
			title = "";	
			_content.userID = userID;
			_content.addEventListener("closepopup", onClosePopUp);
		}
		
		private function onClosePopUp(e:Event):void{
			onHide();
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}