package application.gamecontainer.scene.game
{
	import Box2D.Collision.Shapes.b2CircleDef;
	import Box2D.Collision.Shapes.b2MassData;
	import Box2D.Collision.Shapes.b2PolygonDef;
	import Box2D.Common.Math.b2Vec2;
	import Box2D.Dynamics.Joints.b2PrismaticJoint;
	import Box2D.Dynamics.Joints.b2PrismaticJointDef;
	import Box2D.Dynamics.b2Body;
	import Box2D.Dynamics.b2BodyDef;
	import Box2D.Dynamics.b2World;
	
	import application.GameApplication;
	import application.components.dressingroom.DressHero;
	import application.components.lifeindicator.LifeIndicator;
	import application.components.usertitle.UserTitle;
	
	import flash.display.MovieClip;
	
	import mx.core.UIComponent;
	
	import utils.game.GameConfig;
	import utils.managers.gameparams.GameParamsManager;
	import utils.shop.ShopManager;
	import utils.shop.item.Item;
	import utils.shop.itemprototype.ItemPrototype;
	import utils.user.User;
	
	public class GameWorldCreator
	{
		private var _dinamicID:uint = 1;
		private var _persfriction:Number = 2;
		private var _pesmass:uint = 700;		
		private var world:b2World;
		private var ui:UIComponent;
		private var _userstitles:Object;
		private var _lifeindicators:Object;
		
		public function GameWorldCreator(world:b2World, ui:UIComponent, pt:Object, indicators:Object)
		{
			this.world = world;
			this.ui = ui;
			_userstitles = pt;
			_lifeindicators = indicators;
		}
		
		public function createStaticBox(bx:Number, by:Number, bw:Number, bh:Number, mcClass:Class = null):b2Body{
			var bodyDef:b2BodyDef = new b2BodyDef();			
			bodyDef.position.Set(bx, by);
			
			if(mcClass){
				bodyDef.userData = new mcClass();
				bodyDef.userData.width = bw; 
				bodyDef.userData.height = bh;
				ui.addChild(bodyDef.userData);
			}
			
			var body:b2Body = world.CreateBody(bodyDef);
			var shape:b2PolygonDef = new b2PolygonDef();
			shape.SetAsBox(bw / 2, bh / 2);	
			shape.friction = .1;			
			
			body.CreateShape(shape);
			body.SetMassFromShapes();
			return body;
		}
		
		public function createStaticRedBox(bx:Number, by:Number, bw:Number, bh:Number, mcClass:Class = null):b2Body{
			var bodyDef:b2BodyDef = new b2BodyDef();			
			bodyDef.position.Set(bx, by);
			
			if(mcClass){
				bodyDef.userData = new mcClass();
				bodyDef.userData.width = bw; 
				bodyDef.userData.height = bh;
				ui.addChild(bodyDef.userData);
			}
			
			var body:b2Body = world.CreateBody(bodyDef);
			var shape:b2PolygonDef = new b2PolygonDef();
			shape.SetAsBox(bw / 2, bh / 2);	
			shape.friction = .1;
			shape.restitution = 2;
			
			body.CreateShape(shape);
			body.SetMassFromShapes();
			return body;
		}
		
		public function createStaticBlueBox(bx:Number, by:Number, bw:Number, bh:Number, mcClass:Class = null):b2Body{
			var bodyDef:b2BodyDef = new b2BodyDef();			
			bodyDef.position.Set(bx, by);
			
			if(mcClass){
				bodyDef.userData = new mcClass();
				bodyDef.userData.width = bw; 
				bodyDef.userData.height = bh;
				ui.addChild(bodyDef.userData);
			}
			
			var body:b2Body = world.CreateBody(bodyDef);
			var shape:b2PolygonDef = new b2PolygonDef();
			shape.SetAsBox(bw / 2, bh / 2);	
			shape.friction = 0;			
			
			body.CreateShape(shape);
			body.SetMassFromShapes();
			return body;
		}
		
		public function createBox(bx:Number, by:Number, bw:Number, bh:Number, mcClass:Class = null):b2Body{			
			var bodyDef:b2BodyDef = new b2BodyDef();			
			bodyDef.position.Set(bx, by);
			
			if(mcClass){
				bodyDef.userData = new mcClass();
				bodyDef.userData.width = bw; 
				bodyDef.userData.height = bh;				
				ui.addChild(bodyDef.userData);
			}
			
			var body:b2Body = world.CreateBody(bodyDef);
			var shape:b2PolygonDef = new b2PolygonDef();
			shape.SetAsBox(bw / 2, bh / 2);
			shape.density = 4;
			shape.friction = .1;
			shape.restitution = 0;
			
			body.CreateShape(shape);
			body.SetMassFromShapes();
			return body;
		}
		
		public function createHeavyBox(bx:Number, by:Number, bw:Number, bh:Number, mcClass:Class = null):b2Body{
			var bodyDef:b2BodyDef = new b2BodyDef();			
			bodyDef.position.Set(bx, by);
			
			if(mcClass){
				bodyDef.userData = new mcClass();
				bodyDef.userData.width = bw; 
				bodyDef.userData.height = bh;
				ui.addChild(bodyDef.userData);
			}
			
			var body:b2Body = world.CreateBody(bodyDef);
			var shape:b2PolygonDef = new b2PolygonDef();
			shape.SetAsBox(bw / 2, bh / 2);
			shape.density = 3;
			shape.friction = .5;
			shape.restitution = 0;
			
			body.CreateShape(shape);
			body.SetMassFromShapes();	
			return body;
		}
		
		public function createStick(bx:Number, by:Number, bw:Number, bh:Number, mcClass:Class = null):b2Body{
			var bodyDef:b2BodyDef = new b2BodyDef();			
			bodyDef.position.Set(bx, by);
			
			if(mcClass){
				bodyDef.userData = new mcClass();
				bodyDef.userData.width = bw; 
				bodyDef.userData.height = bh;
				ui.addChild(bodyDef.userData);
			}
			
			var body:b2Body = world.CreateBody(bodyDef);
			var shape:b2PolygonDef = new b2PolygonDef();
			shape.SetAsBox(bw / 2, bh / 2);
			shape.density = .7;
			shape.friction = .1;
			shape.restitution = 0;
			
			body.CreateShape(shape);
			body.SetMassFromShapes();	
			return body;
		}
		
		public function createBall(bx:Number, by:Number, r:Number, mcClass:Class = null):b2Body{
			var bodyDef:b2BodyDef = new b2BodyDef();			
			bodyDef.position.Set(bx, by);
			
			if(mcClass){
				bodyDef.userData = new mcClass();
				bodyDef.userData.width = r * 2; 
				bodyDef.userData.height = r * 2;
				ui.addChild(bodyDef.userData);
			}
			
			var body:b2Body = world.CreateBody(bodyDef);
			var shape:b2CircleDef = new b2CircleDef();
			shape.radius = r;
			shape.density = .5;
			shape.friction = .9;
			shape.restitution = .7;
			
			body.CreateShape(shape);
			body.SetMassFromShapes();	
			return body;
		}
		
		public function createKernelRight(bx:Number, by:Number, r:Number, mcClass:Class = null):b2Body{
			var bodyDef:b2BodyDef = new b2BodyDef();			
			bodyDef.position.Set(bx, by);
			
			if(mcClass){
				bodyDef.userData = new mcClass();
				bodyDef.userData.width = r * 2; 
				bodyDef.userData.height = r * 2;
				ui.addChild(bodyDef.userData);
			}
			
			var body:b2Body = world.CreateBody(bodyDef);
			var shape:b2CircleDef = new b2CircleDef();
			shape.radius = r;
			shape.density = 2;
			shape.friction = .9;
			shape.restitution = 0;
			
			body.CreateShape(shape);
			body.SetMassFromShapes();
			
			body.SetLinearVelocity(new b2Vec2(3000, 0));
			return body;
		}	
		public function createKernelLeft(bx:Number, by:Number, r:Number, mcClass:Class = null):b2Body{
			var bodyDef:b2BodyDef = new b2BodyDef();			
			bodyDef.position.Set(bx, by);
			
			if(mcClass){
				bodyDef.userData = new mcClass();
				bodyDef.userData.width = r * 2; 
				bodyDef.userData.height = r * 2;
				ui.addChild(bodyDef.userData);
			}
			
			var body:b2Body = world.CreateBody(bodyDef);
			var shape:b2CircleDef = new b2CircleDef();
			shape.radius = r;
			shape.density = 2;
			shape.friction = .9;
			shape.restitution = 0;
			
			body.CreateShape(shape);
			body.SetMassFromShapes();
			
			body.SetLinearVelocity(new b2Vec2(-3000, 0));	
			return body;
		}
		
		public function createCarrierH(x:Number, y:Number, w:Number, boxw:Number, boxh:Number, mcClass:Class = null):b2Body{
			var boxbodydef:b2BodyDef = new b2BodyDef();			
			boxbodydef.position.Set(x + boxw / 2, y);		
			boxbodydef.isSleeping = false;
			boxbodydef.userData = new mcClass();			
			if (mcClass)
			{
				boxbodydef.userData.width = boxw; 
				boxbodydef.userData.height = boxh;
				boxbodydef.userData.name = "toright";
				ui.addChild(boxbodydef.userData);
			}
			var boxbody:b2Body = world.CreateBody(boxbodydef);
			
			var boxdef:b2PolygonDef = new b2PolygonDef();
			boxdef.SetAsBox(boxw / 2, boxh / 2);
			boxdef.density = 10;
			
			boxbody.CreateShape(boxdef);
			boxbody.SetMassFromShapes();	
			
			var prism_joint:b2PrismaticJointDef = new b2PrismaticJointDef();
			prism_joint.Initialize(world.GetGroundBody(), boxbody, new b2Vec2(x + boxw / 2, y + boxh / 2),new b2Vec2(1,0));
			prism_joint.lowerTranslation = 0.0;
			prism_joint.upperTranslation = w - boxw;
			prism_joint.enableLimit = true;
			prism_joint.userData = new mcClass();
			
			var joint_added:b2PrismaticJoint = world.CreateJoint(prism_joint) as b2PrismaticJoint;	
			boxbody.SetLinearVelocity(new b2Vec2(20, 0));
			
			return boxbody;
		}
		
		public function createCarrierV(x:Number, y:Number, h:Number, boxw:Number, boxh:Number, mcClass:Class = null):b2Body{
			var boxbodydef:b2BodyDef = new b2BodyDef();			
			boxbodydef.position.Set(x, y + boxh / 2);		
			boxbodydef.isSleeping = false;
			boxbodydef.userData = new mcClass();			
			if (mcClass)
			{
				boxbodydef.userData.width = boxw; 
				boxbodydef.userData.height = boxh;
				boxbodydef.userData.name = "tobottom";
				ui.addChild(boxbodydef.userData);
			}
			var boxbody:b2Body = world.CreateBody(boxbodydef);
			
			var boxdef:b2PolygonDef = new b2PolygonDef();
			boxdef.SetAsBox(boxw / 2, boxh / 2);
			boxdef.density = 10;
			
			boxbody.CreateShape(boxdef);
			boxbody.SetMassFromShapes();	
			
			var prism_joint:b2PrismaticJointDef = new b2PrismaticJointDef();
			prism_joint.Initialize(world.GetGroundBody(), boxbody, new b2Vec2(x, y + boxh / 2),new b2Vec2(0, 1));
			prism_joint.lowerTranslation = 0.0;
			prism_joint.upperTranslation = h - boxh;
			prism_joint.enableLimit = true;
			prism_joint.userData = new mcClass();
			
			var joint_added:b2PrismaticJoint = world.CreateJoint(prism_joint) as b2PrismaticJoint;	
			boxbody.SetLinearVelocity(new b2Vec2(0, 10));
			
			return boxbody;
		}
		
		public function CreateSpringboard(bx:Number, by:Number, bw:Number, bh:Number, mcClass:Class = null):b2Body{
			var bodyDef:b2BodyDef = new b2BodyDef();			
			bodyDef.position.Set(bx, by);
			
			if(mcClass){
				bodyDef.userData = new mcClass();
				bodyDef.userData.width = bw; 
				bodyDef.userData.height = bh;
				bodyDef.userData.name = "Springboard";
				ui.addChild(bodyDef.userData);
			}
			
			var body:b2Body = world.CreateBody(bodyDef);
			var shape:b2PolygonDef = new b2PolygonDef();
			shape.SetAsBox(bw / 2, bh / 2);	
			shape.friction = .5;
			
			body.CreateShape(shape);
			body.SetMassFromShapes();
			return body;
		}
		
		public function CreateSource(bx:Number, by:Number, bw:Number, bh:Number, mcClass:Class = null):void{
			var bodyDef:b2BodyDef = new b2BodyDef();			
			bodyDef.position.Set(bx, by);
			
			if(mcClass){
				bodyDef.userData = new mcClass();
				bodyDef.userData.width = bw; 
				bodyDef.userData.height = bh;
				bodyDef.userData.name = "Source";
				bodyDef.userData["id"] = _dinamicID++;
				ui.addChildAt(bodyDef.userData, 0);
			}
			
			var body:b2Body = world.CreateBody(bodyDef);
			var shape:b2PolygonDef = new b2PolygonDef();
			shape.SetAsBox(bw / 2, bh / 2);	
			shape.friction = .5;
			shape.isSensor = true;
			
			body.CreateShape(shape);
			body.SetMassFromShapes();
		}
		
		public function CreateBullet(bx:Number, by:Number, bw:Number, bh:Number, mcClass:Class = null):void{
			var bodyDef:b2BodyDef = new b2BodyDef();			
			bodyDef.position.Set(bx, by);
			
			if(mcClass){
				bodyDef.userData = new mcClass();
				bodyDef.userData.width = bw; 
				bodyDef.userData.height = bh;
				bodyDef.userData.name = "Bullet";
				bodyDef.userData["id"] = _dinamicID++;
				ui.addChildAt(bodyDef.userData, 0);
			}
			
			var body:b2Body = world.CreateBody(bodyDef);
			var shape:b2PolygonDef = new b2PolygonDef();
			shape.SetAsBox(bw / 2, bh / 2);	
			shape.friction = .5;
			shape.isSensor = true;
			
			body.CreateShape(shape);
			body.SetMassFromShapes();
		}
		
		public function createPers(mx:Number, my:Number, mw:Number, mh:Number, user:Object, pids:Array):b2Body{
			var ismyuser:Boolean = false;
			if(int(user["id"]) == GameApplication.app.userinfomanager.myuser.id) ismyuser = true;
			var bodyDef:b2BodyDef = new b2BodyDef();
			bodyDef.position.Set(mx, my);
			bodyDef.isSleeping = false;
			
			bodyDef.userData = new Hero();
			bodyDef.userData.name = "Hero";			
			ui.addChild(bodyDef.userData);
			
			var speedbonus:uint = 0;
			var jumpbonus:uint = 0;
			var skills:uint = 0;
			
			var dressContainer:MovieClip = new MovieClip();
			dressContainer.name = "dressContainer";
			bodyDef.userData.addChild(dressContainer);
			
			var dress:Array = new Array();
			if(pids && pids.length){
				for(var k:uint; k < pids.length; k++){
					var ip:ItemPrototype = GameApplication.app.shopmanager.getItemPrototypeById(pids[k]);
					speedbonus += ip.param5;
					jumpbonus += ip.param6;
					skills += ip.param7;
					
					var item:Item = Item.createFromItemPrototype(ip);
					var dressMc:DressHero = new DressHero(item);
					
					var added:Boolean = false;
					for(var n:uint = 0; n < dress.length; n++){
						if((dress[n] as DressHero).item.param2 > dressMc.item.param2){
							dressContainer.addChildAt(dressMc, n);
							added = true;
							break;
						}
					}
					if(!added) dressContainer.addChild(dressMc);
					
					dress.push(dressMc);
				}
			}
			dress = null;		
			
			bodyDef.userData["kspeed"] = GameConfig.START_SPEED + Math.min(1, (speedbonus / GameConfig.MAX_SPEED_BONUS)) * (GameConfig.MAX_SPEED - GameConfig.START_SPEED);
			bodyDef.userData["kjump"] = GameConfig.START_JUMP + Math.min(1, (jumpbonus / GameConfig.MAX_JUMP_BONUS)) * (GameConfig.MAX_JUMP - GameConfig.START_JUMP);			
			bodyDef.userData["skills"] = Math.min(GameConfig.MAX_SKILLS, skills);
				
			var body:b2Body = world.CreateBody(bodyDef);			
			var shapeDef:b2PolygonDef = new b2PolygonDef();
			shapeDef.SetAsBox(mw / 2, mh / 2);			
			shapeDef.density = 0.1;
			shapeDef.friction = _persfriction;
			shapeDef.restitution = 0;
			shapeDef.filter.groupIndex = -1;
			
			body.CreateShape(shapeDef);
			body.SetMassFromShapes();
			
			var mass:b2MassData = new b2MassData();
			mass.mass = _pesmass;
			mass.center = new b2Vec2(0, mh / 2)
			
			body.SetMass(mass);			
			createPersTitle(user["id"], user["title"], user["level"]);
			createLifeIndicator(user["id"]);
			return body;
		}
		
		private function createLifeIndicator(userID:int):void{
			var li:LifeIndicator = new LifeIndicator();
			_lifeindicators[userID] = li;
			ui.addChild(li);
		}
		
		private function createPersTitle(userID:int, title:String, level:int):void{
			var ismyuser:Boolean = (userID == GameApplication.app.userinfomanager.myuser.id);
			var ut:UserTitle = new UserTitle(title, level, ismyuser);
			_userstitles[userID] = ut;
			ui.addChild(ut);
		}		
	}
}