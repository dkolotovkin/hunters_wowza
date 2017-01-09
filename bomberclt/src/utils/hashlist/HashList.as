package utils.hashlist {
	import flash.events.EventDispatcher;
	
	import utils.interfaces.IID;

	/**
	 * @author dkolotovkin
	 */
	public class HashList extends EventDispatcher{
		
		protected var _hash:Object = new Object ();
		protected var _list:Vector.<IID>;		protected var _resortFunction:Function;
		
		
		public function get list():Vector.<IID>{
			return _list;
		}
		
		
		public function get hash():Object {
			return _hash;
		}
		
		public function addItem (item:IID,needResort:Boolean = false):Boolean{
			return addItemAt(item, _list.length, needResort);
		}
		
		public function addItemAt (item:IID,index:uint,needResort:Boolean = false):Boolean{						
			if (!_hash[item.id]){
				if (index < _list.length) {
					_list.splice(index, 0, item);				}else{
					_list.push(item);
				}
				_hash[item.id] = item;
				needResort && resort ();
				return true;
			}
			return false;
		}
		
		
		public function removeItem (id:String):IID{
			var item:IID = _hash[id];
			if (item){
				for (var i : uint = 0, len:uint = _list.length; i <  len; i++) {
					if (_list[i].id == id){
						_list.splice(i, 1);
						break;
					}
				}
				delete _hash[id];
			}
			return item;
		}
		
		
		
		public function getItem (id:String):IID{
			return _hash[id];
		}
		
		public function isItem (id:String):Boolean{
			return Boolean(_hash[id]);
		}
		
		public function getItemAt (index:uint):IID{
			return _list[index];
		}
		
		public function get length ():uint{
			return list.length;
		}
		
		
		public function clear ():void {
			_hash = new Object ();
			_list = new Vector.<IID> ();
		}
		
		public function sort (compareFunction:Function):void {
			_list.sort(compareFunction);
		}
		
		public function resort ():void{			
			if (_resortFunction != null){
				_list.sort (_resortFunction);
			}
		}
		
		public function HashList(length:int = 0,resortFunction:Function = null) {
			_resortFunction = resortFunction;
			if (length > 0){
				_list = new Vector.<IID> (length);			}else{
				_list = new Vector.<IID> ();			
			}
		}
		
		override public function toString() : String {
			return "[ HashList : "+_list+" ]";
		}
		
	}
}
