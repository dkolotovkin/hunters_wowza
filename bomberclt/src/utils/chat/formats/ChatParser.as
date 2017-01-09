package utils.chat.formats
{
	public class ChatParser
	{	
		private static var _smile1Finder:RegExp = /\*angel\*/g;
		private static var _smile2Finder:RegExp = /:-\)|:\)/g;
		private static var _smile3Finder:RegExp = /:-\(|:\(/g;
		private static var _smile4Finder:RegExp = /\*yahoo\*/g;
		private static var _smile5Finder:RegExp = /:-P|:P/g;
		private static var _smile6Finder:RegExp = /8-\)/g;		
		private static var _smile7Finder:RegExp = /:-D/g;
		
		private static var _smile8Finder:RegExp = /:-\[/g;
		private static var _smile9Finder:RegExp = /\*yes\*/g;
		private static var _smile10Finder:RegExp = /:-\*/g;
		private static var _smile11Finder:RegExp = /:'\(/g;		
		private static var _smile12Finder:RegExp = /:-X/g;
		private static var _smile13Finder:RegExp = /\*good\*/g;
		private static var _smile14Finder:RegExp = /\*fool\*/g;
		
		private static var _smile15Finder:RegExp = /\*bee\*/g;
		private static var _smile16Finder:RegExp = /\*mosking\*/g;
		private static var _smile17Finder:RegExp = /]:->/g;
		private static var _smile18Finder:RegExp = /\[:-\}/g;
		private static var _smile19Finder:RegExp = /\*airkiss\*/g;
		private static var _smile20Finder:RegExp = /\*bad\*/g;
		private static var _smile21Finder:RegExp = /\*tired\*/g;
		
		private static var _smile22Finder:RegExp = /\*stop\*/g;
		private static var _smile23Finder:RegExp = /\*kissing\*/g;
		private static var _smile24Finder:RegExp = /@}->--/g;
		private static var _smile25Finder:RegExp = /;D/g;
		private static var _smile26Finder:RegExp = /\*drink\*/g;
		private static var _smile27Finder:RegExp = /\*stratch\*/g;
		private static var _smile28Finder:RegExp = /\*dance\*/g;
		
		private static var _smile29Finder:RegExp = /\*help\*/g;
		private static var _smile30Finder:RegExp = /\*dash\*/g;
		private static var _smile31Finder:RegExp = /\*wacko\*/g;
		private static var _smile32Finder:RegExp = /\*ok\*/g;
		private static var _smile33Finder:RegExp = /\*mamba\*/g;
		private static var _smile34Finder:RegExp = /\*sorry\*/g;
		private static var _smile35Finder:RegExp = /\*clapping\*/g;
		private static var _smile36Finder:RegExp = /\*rolf\*/g;
		private static var _smile37Finder:RegExp = /\*pardon\*/g;
		private static var _smile38Finder:RegExp = /\*no\*/g;
		private static var _smile39Finder:RegExp = /\*crazy\*/g;
		private static var _smile40Finder:RegExp = /\*dntknw\*/g;
		
		private static var regArr:Array = new Array();		
		
		private static function setRegArr():void {					
			regArr.push(new ChatRegExp(_smile1Finder, "*\angel\*"));	
			regArr.push(new ChatRegExp(_smile2Finder, ":-\)"));	
			regArr.push(new ChatRegExp(_smile3Finder, ":-\("));
			regArr.push(new ChatRegExp(_smile4Finder, "\*yahoo\*"));	
			regArr.push(new ChatRegExp(_smile5Finder, ":-P"));	
			regArr.push(new ChatRegExp(_smile6Finder, "8-\)"));	
			regArr.push(new ChatRegExp(_smile7Finder, ":-D"));
			
			regArr.push(new ChatRegExp(_smile8Finder, ":-\["));	
			regArr.push(new ChatRegExp(_smile9Finder, "\*yes\*"));	
			regArr.push(new ChatRegExp(_smile10Finder, ":-\*"));	
			regArr.push(new ChatRegExp(_smile11Finder, ":'\("));	
			regArr.push(new ChatRegExp(_smile12Finder, ":-X"));	
			regArr.push(new ChatRegExp(_smile13Finder, "\*good\*"));	
			regArr.push(new ChatRegExp(_smile14Finder, "\*fool\*"));	
			
			regArr.push(new ChatRegExp(_smile15Finder, "\*bee\*"));	
			regArr.push(new ChatRegExp(_smile16Finder, "\*mosking\*"));	
			regArr.push(new ChatRegExp(_smile17Finder, "]:->"));
			regArr.push(new ChatRegExp(_smile18Finder, "[:-}"));
			regArr.push(new ChatRegExp(_smile19Finder, "\*airkiss\*"));
			regArr.push(new ChatRegExp(_smile20Finder, "\*bad\*"));
			regArr.push(new ChatRegExp(_smile21Finder, "\*tired\*"));
			
			regArr.push(new ChatRegExp(_smile22Finder, "\*stop\*"));
			regArr.push(new ChatRegExp(_smile23Finder, "\*kissing\*"));
			regArr.push(new ChatRegExp(_smile24Finder, "@}->--"));
			regArr.push(new ChatRegExp(_smile25Finder, ";D"));
			regArr.push(new ChatRegExp(_smile26Finder, "\*drink\*"));
			regArr.push(new ChatRegExp(_smile27Finder, "\*stratch\*"));
			regArr.push(new ChatRegExp(_smile28Finder, "\*dance\*"));
			
			regArr.push(new ChatRegExp(_smile29Finder, "\*help\*"));
			regArr.push(new ChatRegExp(_smile30Finder, "\*dash\*"));
			regArr.push(new ChatRegExp(_smile31Finder, "\*wacko\*"));
			regArr.push(new ChatRegExp(_smile32Finder, "\*ok\*"));
			regArr.push(new ChatRegExp(_smile33Finder, "\*mamba\*"));
			regArr.push(new ChatRegExp(_smile34Finder, "\*sorry\*"));
			regArr.push(new ChatRegExp(_smile35Finder, "\*clapping\*"));
			
			regArr.push(new ChatRegExp(_smile36Finder, "\*rolf\*"));
			regArr.push(new ChatRegExp(_smile37Finder, "\*pardon\*"));
			regArr.push(new ChatRegExp(_smile38Finder, "\*no\*"));
			regArr.push(new ChatRegExp(_smile39Finder, "\*crazy\*"));
			regArr.push(new ChatRegExp(_smile40Finder, "\*dntknw\*"));
		}
		
		public static function parse(str:String):String{
//			trace("start: " + str);
			if (!regArr.length) setRegArr();				
			for each (var r : ChatRegExp in regArr)
			{
				if (r.replace!="%%"){					
					str = str.replace(r.reg, r.replace);
				}
				else{
					var link:Object = r.reg.exec(str);
					if (!link) link = r.reg.exec(str);
					var replace:String;
					if (link){						
						replace = String(link.toString().split(',')[0]);
						str = str.replace(r.reg, ChatRegExp.seprator + replace + ChatRegExp.seprator);					 
					}
				}				
			}
//			trace("end: " + str);
			return str;
		}
	}
}