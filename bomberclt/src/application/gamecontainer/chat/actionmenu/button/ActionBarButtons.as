package application.gamecontainer.chat.actionmenu.button {
	/**
	 * @author dkolotovkin
	 */
	public class ActionBarButtons {
		
		private static var _hash:Object;
		
		public static function getClassByType (type:String):Class {
			if (!_hash){
				init ();
			}
			return _hash[type];
		}
		
		private static function init() : void {
			_hash = new Object ();
			/*_hash[Actions.MOVE_THING] = ButtonMoveThing;			_hash[Actions.TRASH] = ButtonTrash;			_hash[Actions.REMOVE] = ButtonRemove;			_hash[Actions.FIGHT] = ButtonFight;			_hash[Actions.GO_TO_PERS] = ButtonGoToPers;			_hash[Actions.PRIVATE_TO] = ButtonPrivateTo;			_hash[Actions.GO_HOME_PERS] = ButtonGoHomePers;			_hash[Actions.FEED_PET] = AButtonFeed;			_hash[Actions.WATER] = AButtonWater;			_hash[Actions.HEAL] = AButtonHeal;			_hash[Actions.REPAIR] = AButtonRepair;
			_hash[Actions.RESTORE] = AButtonRestore;
			_hash[Actions.CLEAN] = AButtonClean;
			_hash[Actions.SALE] = AButtonSale;*/
		}
	}
}
