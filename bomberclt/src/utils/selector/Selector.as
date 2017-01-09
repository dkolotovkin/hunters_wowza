package utils.selector { 	
	import flash.events.EventDispatcher;
	
	/**
	 * @author ivaskov
	 */
	public class Selector extends EventDispatcher{
		
		private var _multiselect:Boolean = false;
		
		private var _hash:Object = new Object ();
		
		
		private var _selection:ISelected;
		
		
		
		public function get multiselect ():Boolean {
			return _multiselect;
		}
		
		public function set multiselect (value:Boolean):void {
			if (_multiselect != value){
				_multiselect = value;
				if (_selection){
					if (_multiselect){
						
						(_hash[_selection.id] = _selection);
						
					}else{
						for each (var item : ISelected in _hash) {
							item.selected = false;
						}
						_hash = new Object();
						_selection = item;
						_selection.selected = true;
					}
				}
			}
		}
		
		public function clear() : void {
			var old : ISelected = _selection;
			if (old){
				old.selected = false;
				dispatchEvent(new SelectorEvent (SelectorEvent.UNSELECTED,old));
			}
		}
		
		public function selected (item:ISelected):Boolean {
			if (item){				
				if (_multiselect) {
					_selection = item;
					if (!_hash[item.id]){
						_hash[item.id] = item;
					}
					item.selected = true;					
					dispatchEvent(new SelectorEvent (SelectorEvent.SELECTED,item));
					return true;
				}else{
					
					if (_selection != item){
						
						var old : ISelected = _selection;
						_selection = item;
						if (old){
							old.selected = false;
							dispatchEvent(new SelectorEvent (SelectorEvent.UNSELECTED,old));
						}
						if (item){			
							item.selected = true;
							dispatchEvent(new SelectorEvent (SelectorEvent.SELECTED,item));
						}
						return true;
					}
				}
			}
			return false;
		}
		
		public function unselected (item:ISelected):void {			
			if (_multiselect) {
				if (_hash[item.id]){
					delete _hash[item.id];
					if (_selection == item){
						_selection = null;
					}
					item.selected = false;					
					dispatchEvent(new SelectorEvent (SelectorEvent.UNSELECTED,item));
				}
			}else {
				
				if (_selection == item){
					_selection = null;
					item.selected = false;					
					dispatchEvent(new SelectorEvent (SelectorEvent.UNSELECTED,item));
				}
			}
		}
		
		
		public function get selection ():ISelected {
			return _selection;
		}
	}
}
