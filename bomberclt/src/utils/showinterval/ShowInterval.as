package utils.showinterval {

	/**
	 * @author dkolotovkin
	 */
	public class ShowInterval {
		
		public var min:int;		public var max:int;
		
		
		public function ShowInterval(min:int = 0, max:int = 0) {
			this.min = min;			this.max = max;
		}
		
		
		public function contains (index:int):Boolean{
			return index >= min && index <= max;
		}
		
		
		
		
		public function toString() : String {
			return "{ min:"+min+", max:"+max+" }";
		}
		
		
	}
}
