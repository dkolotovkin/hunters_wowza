package utils.chat.formats {
	import flash.text.engine.FontWeight;

	/**
	 * @author dkolotovkin
	 */
	public class ChatActionFormat {
		public var font:String;		public var size:uint;		public var color:uint;		public var bgc:uint;		public var bold:String;		public var italic:Boolean;
		
		
		public function ChatActionFormat(_font:String = "_sans", _size:uint = 12, _color:uint = 0x000000, _bgc:uint = 0xf7f7f7, _bold:String = FontWeight.NORMAL, _italic:Boolean = false) {
			font = _font;
			size = _size;
			color = _color;
			bgc = _bgc;
			bold = _bold;
			italic = _italic;
		}
		
		public function clone():ChatActionFormat{
			return new ChatActionFormat(font, size, color, bgc, bold, italic);
		}
	}
}
