package utils.time
{
	public class TimeUtils
	{
		public static var months:Array = ["января", "февраля", "марта", "апреля", "мая", "июня", "июля", "августа", "сентября", "октября", "ноября", "декабря"];
		
		public static function getGameTime(s:int):String {
			return fillZero(s/60,2)+":"+fillZero(s%60,2);
		}
		
		public static function fillZero (num:int,len:int):String{
			return String("0000"+num).substr(4+String(num).length-len,len);
		}
		
		public static function getFormatTime (seconds:Number):String {
			var h:int = int(seconds/3600);
			
			if (h > 0){
				return fillZero(h,2)+":"+fillZero(int(seconds%3600/60),2)+":"+fillZero (int(seconds%60),2);
			}
			return fillZero(int(seconds%3600/60),2)+":"+fillZero (int(seconds%60),2);
		}
		
		public static function getTimeStr (seconds:int):String{
			var d:int = seconds/86400
			var h:int = seconds%86400/3600;
			var m:int = seconds%3600/60;
			var s:int = seconds%60;
			
			/*if (d > 0){
				return d+WordUtils.getCorrectWord(d,[" день "," дня "," дней "])+h+WordUtils.getCorrectWord(h,[" час"," часа"," часов"]);
			}else if (h > 0){
				return h+WordUtils.getCorrectWord(h,[" час "," часа "," часов "])+m+WordUtils.getCorrectWord(m,[" минуту", " минуты"," минут"]);
			}else if (m > 0){
				return m+WordUtils.getCorrectWord(m,[" минуту ", " минуты "," минут "])+s+WordUtils.getCorrectWord(s,[" секунду", " секунды"," секунд"]);
			}
			return s+WordUtils.getCorrectWord(s,[" секунду", " секунды"," секунд"]);*/
			return null;
		}
		
		
		public static function getTimeStrRod (seconds:int):String{
			var d:int = seconds/86400
			var h:int = seconds%86400/3600;
			var m:int = seconds%3600/60;
			var s:int = seconds%60;
			
			/*if (d > 0){
				return d+WordUtils.getCorrectWord(d,[" день "," дня "," дней "])+h+WordUtils.getCorrectWord(h,[" час"," часа"," часов"]);
			}else if (h > 0){
				return h+WordUtils.getCorrectWord(h,[" час "," часа "," часов "])+m+WordUtils.getCorrectWord(m,[" минута", " минуты"," минут"]);
			}else if (m > 0){
				return m+WordUtils.getCorrectWord(m,[" минута ", " минуты "," минут "])+s+WordUtils.getCorrectWord(s,[" секунда", " секунды"," секунд"]);
			}
			return s+WordUtils.getCorrectWord(s,[" секунда", " секунды"," секунд"]);*/
			return null;
		}
		
		
		
		
		
		public static function getDateStr (t:Number):String {
			var date:Date = new Date ();
			date.setTime(t);
			
			return date.date + " "+months[date.month]+" в "+ fillZero(date.hours,2)+":"+fillZero(date.minutes,2);
		}
		
	}
}