package application.components.popup.buylink
{
	import application.GameApplication;
	import application.components.popup.PopUpTitled;
	
	import flash.events.MouseEvent;

	public class PopUpBuyLink extends PopUpTitled
	{
		private var _content:BuyLinkContent;	
		private var _url:String;
		
		public function PopUpBuyLink(url:String){
			super();
			_url = url;
			_content = new BuyLinkContent();
			_content.okbtn.addEventListener(MouseEvent.CLICK, onShowLink);
		}
		
		private function onShowLink(e:MouseEvent):void{
			GameApplication.app.shopmanager.buyLink(_url);
			GameApplication.app.popuper.hidePopUp();
		}
		
		override protected function createChildren():void{
			super.createChildren();			
			addElement(_content);
		}
	}
}