package utils.game
{
	public class GameConfig
	{
		public static var START_SPEED:Number = 1;
		public static var START_JUMP:Number = 1;
		public static var MAX_SPEED:Number = 2;
		public static var MAX_JUMP:Number = 1.3;
		public static var MAX_SPEED_BONUS:Number = 3;
		public static var MAX_JUMP_BONUS:Number = 3;
		
		public static var MAX_SKILLS:Number = 6;
		
		public static var MIN_ACCURACY:int = 5;
		public static var radiusHit:Number = 30;					//радиус попадания
		public static var percentSkillByOnePoint:int = 5;			//сколько процентов приходится на 1 единицу в param7
		public static var countMiliSecondByOnePoint:int = 200;		//сколько милисекунд приходится на 1 единицу в param4 (скорость перезаряда)
		
		public function GameConfig()
		{
		}
	}
}