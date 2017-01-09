package utils.chat {
	import flash.display.Sprite;
	
	import utils.user.User;

	/**
	 * @author dkolotovkin
	 */
	public class UserSprite extends Sprite {
		private var _user:User = new User;
		
		public function get user ():User {
		    return _user;
		}
		
		public function set user (value:User):void {
		    if (_user != value){
				_user = value;
		    }    
		}
		
		public function UserSprite() {
		}
	}
}
