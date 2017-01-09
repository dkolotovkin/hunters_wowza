package utils.game.action
{
	public class GameActionType
	{
		public static var WAIT_START:uint = 0;
		public static var NOT_ENOUGH_USERS:uint = 1;
		public static var NOT_ENOUGH_ENERGY:uint = 2;
		public static var START:uint = 3;
		public static var FINISH:uint = 4;
		public static var ACTION:uint = 5;
		public static var USE_GAMEITEM:uint = 6;	
		public static var GET_GWITEMS:uint = 7;
		public static var UPDATE_GWITEMS:uint = 8;
		public static var NOT_ENOUGH_MONEY:uint = 9;
		public static var NO_ROOM:uint = 10;
		public static var NO_SEATS:uint = 11;
		public static var BAD_PASSWARD:uint = 12;
		
		public static var RETURN_MONEY:uint = 13;
		public static var ORGANIZER_EXIT:uint = 14;
		public static var FINISH_BETS:uint = 15;
		public static var BAD_PARAMS:uint = 16;
		public static var BETS_CONTENT:uint = 17;
		
		public function GameActionType()
		{
		}
	}
}