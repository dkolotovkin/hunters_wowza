package application.components.popup.savemap
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpSaveMap extends PopUpTitled
	{
		private var _content:SaveMapContent = new SaveMapContent();
		
		public function PopUpSaveMap(xml:XML)
		{
			super();
			title = "Сохранить на сервер";
			_content.mapXML = xml;
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}