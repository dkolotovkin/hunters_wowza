package application.components.popup.buyclanseats
{
	import application.components.popup.PopUp;
	import application.components.popup.PopUpTitled;
	import application.components.popup.buyclanseats.BuyClanSeats;
	
	public class PopUpBuyClanSeats extends PopUpTitled
	{
		private var _content:BuyClanSeats = new BuyClanSeats();
		
		public function PopUpBuyClanSeats()
		{
			super();
			title = "Покупка мест";
		}
		
		override protected function createChildren():void{
			super.createChildren();
			addElement(_content);
		}
	}
}

