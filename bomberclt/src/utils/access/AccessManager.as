package utils.access
{
	import application.GameApplication;
	import application.GameMode;
	
	public class AccessManager
	{
		public function AccessManager(){
		}
		
		//проверка на алкоголь для одноклассников
		public static function checkAccessPresent(pid:int):Boolean{			
			return true;
		}
	}
}