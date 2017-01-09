package utils.managers.navigation
{
	import application.GameApplication;
	import application.components.preloader.PreLoaderCircle;
	import application.gamecontainer.scene.admin.AdminPanel;
	import application.gamecontainer.scene.bag.Bag;
	import application.gamecontainer.scene.catalog.Catalog;
	import application.gamecontainer.scene.clans.ClanRoom;
	import application.gamecontainer.scene.clans.ClansRoom;
	import application.gamecontainer.scene.constructor.Constructor;
	import application.gamecontainer.scene.game.GameWorld;
	import application.gamecontainer.scene.home.HomePage;
	import application.gamecontainer.scene.minigames.MiniGames;
	import application.gamecontainer.scene.minigames.auction.Auction;
	import application.gamecontainer.scene.minigames.betsroom.BetsRoom;
	import application.gamecontainer.scene.minigames.fortuna.Fortuna;
	import application.gamecontainer.scene.minigames.simplerule.SimpleRule;
	import application.gamecontainer.scene.top.Top;
	
	import flash.events.EventDispatcher;
	import flash.events.IEventDispatcher;
	import flash.geom.Rectangle;
	
	import spark.components.Group;
	
	import utils.interfaces.ISceneContent;
	
	public class NavigationManager extends EventDispatcher
	{
		public var currentSceneContent:ISceneContent;
		
		public function NavigationManager(target:IEventDispatcher=null)
		{
			super(target);
		}
		
		public function clear():void{
			if(currentSceneContent){
				currentSceneContent.onHide();
				GameApplication.app.gameContainer.scene.removeElement(currentSceneContent as Group);
			} 
		}
		
		public function goHome():void{			
			clear();
			
			var hp:HomePage = new HomePage();			
			GameApplication.app.gameContainer.scene.addElement(hp);
			currentSceneContent = hp;
		}
		
		public function goGameWorld(roomID:int, locationXML:XML, users:Array, hunterId:int, gt:int):GameWorld{			
			clear();
			
			var r:Rectangle = new Rectangle(0, 0, GameApplication.app.gameContainer.scene.width, GameApplication.app.gameContainer.scene.height);
			var gw:GameWorld = new GameWorld(roomID, r, locationXML, users, hunterId, gt);
			GameApplication.app.gameContainer.scene.addElement(gw);
			currentSceneContent = gw;
			return gw;
		}
		
		public function goFindUsersScreen(wt:int):void{
			clear();
			
			var fu:PreLoaderCircle = new PreLoaderCircle();
			fu.darkMode = true;
			fu.text = "Идет поиск соперников...";
			fu.time = wt;
			GameApplication.app.gameContainer.scene.addElement(fu);
			currentSceneContent = fu;
		}
		
		public function goShop():void{
			clear();
			
			var catalog:Catalog = new Catalog();
			GameApplication.app.gameContainer.scene.addElement(catalog);
			currentSceneContent = catalog;		
		}
		
		public function goBag():void{
			clear();			
			
			var bag:Bag = new Bag();
			GameApplication.app.gameContainer.scene.addElement(bag);
			currentSceneContent = bag;		
		}
		
		public function goTop():void{
			clear();			
			
			var top:Top = new Top();
			GameApplication.app.gameContainer.scene.addElement(top);
			currentSceneContent = top;		
		}
		
		public function goMiniGames():void{
			clear();			
			
			var minigames:MiniGames = new MiniGames();
			GameApplication.app.gameContainer.scene.addElement(minigames);
			currentSceneContent = minigames;		
		}
		
		public function goAdminPanel():void{
			clear();			
			
			var admin:AdminPanel = new AdminPanel();
			GameApplication.app.gameContainer.scene.addElement(admin);
			currentSceneContent = admin;		
		}
		
		public function goFortuna():void{
			clear();			
			
			var fortuna:Fortuna = new Fortuna();
			GameApplication.app.gameContainer.scene.addElement(fortuna);
			currentSceneContent = fortuna;		
		}
		
		public function goSimpleRule(type:int):void{
			clear();			
			
			var simplerule:SimpleRule = new SimpleRule();
			simplerule.roomtype = type;
			GameApplication.app.gameContainer.scene.addElement(simplerule);
			currentSceneContent = simplerule;		
		}
		
		public function goBetsRoom():void{
			clear();			
			
			var betsroom:BetsRoom = new BetsRoom();
			GameApplication.app.gameContainer.scene.addElement(betsroom);
			currentSceneContent = betsroom;		
		}
		
		public function goAuction():void{
			clear();
			
			var auction:Auction = new Auction();
			GameApplication.app.gameContainer.scene.addElement(auction);
			currentSceneContent = auction;		
		}
		
		public function goClansRoom():void{
			clear();			
			
			var clans:ClansRoom = new ClansRoom();
			GameApplication.app.gameContainer.scene.addElement(clans);
			currentSceneContent = clans;		
		}
		public function goClanRoom(idclan:int):void{
			clear();			
			
			var clan:ClanRoom = new ClanRoom();
			clan.idclan = idclan;
			GameApplication.app.gameContainer.scene.addElement(clan);
			currentSceneContent = clan;		
		}
		
		public function goConstructor(xml:XML = null):Constructor{
			clear();
			
			var r:Rectangle = new Rectangle(0, 0, GameApplication.app.gameContainer.scene.width, GameApplication.app.gameContainer.scene.height);
			var contructor:Constructor = new Constructor();			
			GameApplication.app.gameContainer.scene.addElement(contructor);
			currentSceneContent = contructor;
			GameApplication.app.constructor.constructor = contructor;			
			
			if(xml){
				GameApplication.app.constructor.init(xml);
			}
			
			return contructor;
		}
	}
}