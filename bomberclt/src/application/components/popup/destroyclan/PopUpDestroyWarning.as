package application.components.popup.destroyclan
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpDestroyWarning extends PopUpTitled
	{
		private var _content:DestroyWarningContent = new DestroyWarningContent();
		
		public function PopUpDestroyWarning(){
			super();
			_content.closefunction = closepopup;
		}
		
		private function closepopup():void{
			onHide();
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}