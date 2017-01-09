package utils.popup
{	
	import application.GameApplication;
	import application.components.popup.PopUp;
	import application.components.popup.PopUpEvent;
	import application.components.popup.PopUpTitled;
	import application.components.popup.info.PopUpInfo;
	import application.components.popup.zone.PopUpZone;
	
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	
	import spark.components.Label;
	import spark.components.SkinnableContainer;
	import spark.components.VGroup;
	
	public class PopUpManager extends EventDispatcher
	{
		public var popupzone:PopUpZone;
		public var currentpopup:PopUp;
		
		public function PopUpManager(target:IEventDispatcher=null)
		{
			super(target);
			popupzone = new PopUpZone();			
			popupzone.visible = false;
		}
		
		public function show(pp:PopUp):void{
			if(currentpopup && currentpopup.priority > pp.priority){
				return;
			}
			hidePopUp();
			GameApplication.app.actionShowerMenu.hideMenu();
			popupzone.zone.addElement(pp);
			popupzone.visible = true;
			currentpopup = pp;
			currentpopup.onShow();
			currentpopup.addEventListener(PopUpEvent.HIDE_POPUP, onPopUpClose);
		}
		private function onPopUpClose(e:PopUpEvent):void{			
			hidePopUp();
		}
		
		public function showInfoPopUp(txt:String, priority:uint = 0):void{
			var popup:PopUpInfo = new PopUpInfo(txt, priority);			
			show(popup);
		}
		
		public function hidePopUp():void{
			popupzone.zone.removeAllElements();
			popupzone.visible = false;
			if (currentpopup){
				currentpopup.removeEventListener(PopUpEvent.HIDE_POPUP, onPopUpClose);
				currentpopup = null;
			}
		}
	}
}