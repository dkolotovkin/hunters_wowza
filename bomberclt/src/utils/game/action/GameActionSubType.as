package utils.game.action
{
	public class GameActionSubType
	{
		public static var GOTOLEFT:uint = 0;
		public static var GOTORIGHT:uint = 1;
		public static var JUMP:uint = 2;
		
		public static var GET_SOURCE:uint = 3;
		public static var GET_BULLET:uint = 4;
		public static var MOVE_TARGET:uint = 5;
		public static var SELECT_GUN:uint = 6;
		public static var SHOT:uint = 7;
		public static var HUNTER_SHOT:uint = 8;
		public static var DODGE:uint = 9;
		public static var WOUND:uint = 10;
		public static var RESTORE_SOURCE:uint = 11;
		
		public static var EXIT:uint = 12;
		public static var OUT:uint = 13;
		
		public function GameActionSubType()
		{
		}
	}
}