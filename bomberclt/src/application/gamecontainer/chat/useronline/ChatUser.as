package application.gamecontainer.chat.useronline
{
	import utils.user.User;

	public class ChatUser
	{
		public var user:User;
		public var flash:Boolean = false;
		
		public function ChatUser(user:User, flash:Boolean = false)
		{
			this.user = user;
			this.flash = flash;
		}
	}
}