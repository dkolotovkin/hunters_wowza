package application.components.popup {	

	/**
	 * @author dkolotovkin
	 */
	public class PopUpTitled extends PopUp {
		
		protected var _title:String;		protected var _titlePopUp:TitlePopUp;
		
		public function get title ():String {
			return _title;
		}
		
		public function set title (value:String):void {
			if (_title != value){
				_title = value;
				updateText();
			}	
		}
		
		protected function updateText ():void {
			_titlePopUp.title = _title;
		}
		
		public function PopUpTitled() {
			super();
			
			_titlePopUp = createHeader();
			_titlePopUp.height = 26;
			_titlePopUp.left = 0;			_titlePopUp.right = 25;			_titlePopUp.top = 0;
		}
		
		/**
		 * переопределять для смены шрифта или цвета шапки
		 */

		protected function createHeader ():TitlePopUp {
			return new TitlePopUp();
		}	
		
		override protected function createChildren() : void {
			super.createChildren();
			addElementAt(_titlePopUp,0);
		}
	}
}
