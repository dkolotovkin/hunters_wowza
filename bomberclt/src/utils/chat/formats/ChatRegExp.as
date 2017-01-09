package utils.chat.formats {

	/**
	 * @author dkolotovkin
	 */
	public class ChatRegExp {
		public var reg:RegExp;
		public var replace:String;
		public static var seprator:String = "%";		
		
		public function ChatRegExp(r:RegExp, repl:String) {
			reg = r;			
			replace = seprator + repl + seprator;			
		}
	}
}
