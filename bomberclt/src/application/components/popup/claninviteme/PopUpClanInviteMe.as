package application.components.popup.claninviteme
{
	import application.components.popup.PopUpTitled;
	
	public class PopUpClanInviteMe extends PopUpTitled
	{
		private var _content:ClanInviteMeContent = new ClanInviteMeContent();
		
		public function PopUpClanInviteMe(ownertitle:String, clantitle:String){
			super();	
			_content.ownertitle = ownertitle;
			_content.clantitle = clantitle;
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