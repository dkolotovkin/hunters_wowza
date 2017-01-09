package application.components.usertitle
{
	import flash.display.MovieClip;
	import flash.display.Sprite;
	import flash.text.TextField;
	import flash.text.TextFormat;
	
	import flashx.textLayout.formats.TextAlign;
	
	public class UserTitle extends Sprite
	{
		private var _tf:TextField = new TextField();
		
		public function set sourcevisible(value:Boolean):void{
		}
		
		public function UserTitle(title:String, level:int, ismyuser:Boolean)
		{
			super();
			
			var tfn:TextFormat = new TextFormat();
			tfn.align = TextAlign.CENTER;
			if(ismyuser){
				tfn.bold = true;
				tfn.color = "0xff0000";
			}else{
				tfn.color = "0xCE9D6C";
			}
			tfn.size = 12;
			
			var tfl:TextFormat = new TextFormat();
			if(ismyuser){
				tfl.bold = true;
				tfl.color = "0xff0000";
			}else{
				tfl.color = "0xCE9D6C";
			}
			tfl.bold = true;
			tfl.size = 12;
			
			_tf.selectable = false		
			_tf.text = title + " [" + String(level) + "]";
			_tf.setTextFormat(tfn, 0, title.length);
			_tf.setTextFormat(tfl, title.length, _tf.text.length);		
			
			addChild(_tf);
		}
	}
}