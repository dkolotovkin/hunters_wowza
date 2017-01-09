package utils.user
{
	public class UserClanInfo extends User
	{
		public var clandepositm:int;
		public var clandeposite:int;
		public var clanrole:int;
		public var num:int;
		public var iisowner:Boolean;		
		
		public function UserClanInfo()
		{
			super();
		}
		
		public static function createFromObject(u:Object):UserClanInfo{			
			if(u){
				var user:UserClanInfo = new UserClanInfo();
				user.id = int(u["id"]);
				user.sex = int(u["sex"]);
				user.title = String(u["title"]);	
				user.level = int(u["level"]);
				user.experience = int(u["experience"]);
				user.exphour = int(u["exphour"]);
				user.expday = int(u["expday"]);
				user.popular = int(u["popular"]);
				user.maxExperience = int(u["nextLevelExperience"]);				
				user.money = int(u["money"]);
				user.role = int(u["role"]);
				user.bullets = int(u["bullets"]);
				user.url = String(u["url"]);
				user.isonline = Boolean(u["isonline"]);
				
				user.clandepositm = int(u["clandepositm"]);
				user.clandeposite = int(u["clandeposite"]);
				user.clanrole = int(u["clanrole"]);
				
				if(u["claninfo"] != null){
					user.claninfo = new ClanUserInfo(u["claninfo"]["clanid"], u["claninfo"]["clantitle"], u["claninfo"]["maxusers"], u["claninfo"]["clandepositm"],
						u["claninfo"]["clandeposite"], u["claninfo"]["clanrole"], u["claninfo"]["getclanmoneyat"]);
				}
				return user;
			}
			return null;
		}
	}
}