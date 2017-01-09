package utils.user
{
	import application.GameApplication;
	import application.components.clanitem.ClanItem;
	
	import flash.display.MovieClip;
	import flash.events.EventDispatcher;

	public class User extends EventDispatcher
	{
		[Bindable]
		public var id:uint;
		[Bindable]
		public var title:String;
		[Bindable]
		public var sex:uint;
		[Bindable]
		public var king:uint;
		[Bindable]
		public var level:uint;
		[Bindable]
		public var experience:uint;
		[Bindable]
		public var bullets:uint;
		[Bindable]
		public var exphour:uint;
		[Bindable]
		public var expday:uint;
		[Bindable]
		public var popular:uint;
		[Bindable]
		public var maxExperience:uint;
		[Bindable]
		public var money:uint;
		[Bindable]
		public var role:uint;		
		[Bindable]
		public var url:String;	
		
		public var isonline:Boolean;		
		public var claninfo:ClanUserInfo;
		
		public var itemsArr:Array;
		public var itemsObj:Object;
		
		public function User(){
		}
		
		public function clone():User{			
			var user:User = new User();
			user.id = id;
			user.title = title;
			user.sex = sex;
			user.level = level;
			user.popular = popular;
			user.king = king;
			user.experience = experience;
			user.bullets = bullets;
			user.exphour = exphour;
			user.expday = expday;
			user.maxExperience = maxExperience;			
			user.money = user.money;
			user.role = user.role;
			user.url = user.url;
			return user;
		}
		
		public function update():void{
			if (id == GameApplication.app.userinfomanager.myuser.id){
				GameApplication.app.userinfomanager.myuser.title = title;
				GameApplication.app.userinfomanager.myuser.sex = sex;
				GameApplication.app.userinfomanager.myuser.king = king;
				GameApplication.app.userinfomanager.myuser.level = level;
				GameApplication.app.userinfomanager.myuser.experience = experience;
				GameApplication.app.userinfomanager.myuser.bullets = bullets;
				GameApplication.app.userinfomanager.myuser.exphour = exphour;
				GameApplication.app.userinfomanager.myuser.expday = expday;
				GameApplication.app.userinfomanager.myuser.maxExperience = maxExperience;				
				GameApplication.app.userinfomanager.myuser.money = money;
				GameApplication.app.userinfomanager.myuser.role = role;
				GameApplication.app.userinfomanager.myuser.url = url;
			}
			dispatchEvent(new UserEvent(UserEvent.UPDATE));
		}
		
		public static function createFromObject(u:Object):User{			
			if(u){
				var user:User = new User();
				user.id = int(u["id"]);
				user.sex = int(u["sex"]);
				user.king = int(u["king"]);
				user.title = String(u["title"]);	
				user.level = int(u["level"]);
				user.experience = int(u["experience"]);
				user.bullets = int(u["bullets"]);
				user.exphour = int(u["exphour"]);
				user.expday = int(u["expday"]);
				user.popular = int(u["popular"]);
				user.maxExperience = int(u["nextLevelExperience"]);				
				user.money = int(u["money"]);
				user.role = int(u["role"]);				
				user.url = String(u["url"]);
				user.isonline = Boolean(u["isonline"]);
				
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