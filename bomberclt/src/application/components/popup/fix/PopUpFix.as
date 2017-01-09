package application.components.popup.fix
{
	import application.components.popup.PopUpTitled;
	
	import utils.shop.item.Item;
	
	public class PopUpFix extends PopUpTitled
	{
		private var _info:FixInfo = new FixInfo();
		public function PopUpFix(item:Item)
		{
			super();
			_info.init(item);
			_info.closefunction = closepopup;
		}
		
		private function closepopup():void{
			onHide();
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_info);
		}
	}
}