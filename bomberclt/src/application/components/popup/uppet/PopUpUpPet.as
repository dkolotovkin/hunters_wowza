package application.components.popup.uppet
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpUpPet extends PopUpTitled
	{
		private var _content:UpPetContent = new UpPetContent();
		
		public function PopUpUpPet(t:String)
		{
			super();
			title = t;			
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}