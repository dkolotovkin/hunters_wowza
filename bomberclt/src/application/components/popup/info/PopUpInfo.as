package application.components.popup.info
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpInfo extends PopUpTitled
	{
		private var _info:Info = new Info();
		public function PopUpInfo(text:String, priority:uint = 0)
		{
			super();
			_info.text = text;
			this.priority = priority;
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_info);
		}
	}
}