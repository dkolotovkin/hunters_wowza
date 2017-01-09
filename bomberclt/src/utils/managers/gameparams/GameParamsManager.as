package utils.managers.gameparams
{
	public class GameParamsManager
	{
		public function GameParamsManager()
		{
		}
		
		
//		public static function isPen(atype:int):Boolean{
//			if(atype == Accessorytype.PEN1 || atype == Accessorytype.PEN3 || atype == Accessorytype.PEN10) return true;
//			return false;
//		}
		
		public static function getKSpeed(atype:int, ctype:int):Number{
			var needAccessory:Boolean;
			var needColor:Boolean;
			var k:Number = 1;
//			if(	isPumpkin(atype) ||
//				isNYHat(atype) ||
//				isDoctor(atype) ||
//				isAngel(atype) ||
//				isDemon(atype) ||
//				isCatMask(atype) ||
//				isNY(atype)
//			){
//				needAccessory = true;
//			}			
//			if(	isBlack(ctype) ||
//				isWhite(ctype) ||
//				isBlue(ctype) ||
//				isFiolet(ctype) ||
//				isOrange(ctype)
//				){
//				needColor = true;
//			}
//			
//			if(needAccessory || needColor){
//				k = 1.7;
//			}
//			if(needAccessory && needColor){
//				k = 2;
//			}
			
			return k;
		}
		
		public static function getKJump(atype:int, ctype:int):Number{
			var needAccessory:Boolean;
			var needColor:Boolean;
			var k:Number = 1;
			
//			if(	isCylinder(atype) ||
//				isCookHat(atype) ||
//				isKovboyHat(atype) ||
//				isHipHop(atype) ||
//				isGlamur(atype) ||
//				isLamur(atype) ||
//				isHelmet(atype) ||
//				isPoliceHat(atype) ||
//				isFlashHair(atype) ||
//				isPumpkin(atype) ||
//				isNYHat(atype) || 
//				isDoctor(atype) || 
//				isAngel(atype) || 
//				isDemon(atype) || 
//				isCatMask(atype) ||
//				isNY(atype)
//			){
//				needAccessory = true;
//			}
//			if(	isBlack(ctype) ||
//				isWhite(ctype)
//			){
//				needColor = true;
//			}
//			
//			if(needAccessory || needColor){
//				k = 1.2;
//			}
//			if(needAccessory && needColor){
//				k = 1.3;
//			}
			return k;
		}
		
		public static function getBooking(atype:int, ctype:int):Boolean{
			
//			if(	isDoctor(atype) || 
//				isNY(atype) ||
//				isAngel(atype) ||
//				isCatMask(atype) ||
//				isDemon(atype) 				
//			){
//				return true;
//			}
//			if(	isBlack(ctype) ||
//				isWhite(ctype)
//			){
//				return true;
//			}
			
			return false;
		}
		
		public static function getEnergyUp(atype:int, ctype:int):Boolean{
//			if(
//				isPen(atype) ||
//				isBandage(atype) ||
//				isCrone(atype) ||
//				isCylinder(atype) ||
//				isCookHat(atype) ||
//				isKovboyHat(atype) ||
//				isHipHop(atype) ||
//				isGlamur(atype) ||
//				isLamur(atype) ||
//				isHelmet(atype) ||
//				isPoliceHat(atype) ||
//				isFlashHair(atype) ||
//				isPumpkin(atype) ||
//				isNYHat(atype) ||
//				isNY(atype) ||
//				isDoctor(atype) ||
//				isAngel(atype) ||
//				isDemon(atype) ||
//				isCatMask(atype) ||
//				isBlack(ctype) ||
//				isWhite(ctype) ||
//				isBlue(ctype) ||
//				isFiolet(ctype) ||
//				isOrange(ctype)
//			){
//				return true;
//			}
			return false;
		}
		
		public static function getExperienceBonus(atype:int, ctype:int):Boolean{
//			if(
//				isPen(atype) ||
//				isBandage(atype) ||
//				isCrone(atype) ||
//				isCylinder(atype) ||
//				isCookHat(atype) ||
//				isKovboyHat(atype) ||
//				isHipHop(atype) ||
//				isGlamur(atype) ||
//				isLamur(atype) ||
//				isHelmet(atype) ||
//				isPoliceHat(atype) ||
//				isFlashHair(atype) ||
////				isPumpkin(atype) ||
////				isNYHat(atype) ||
//				isNY(atype) ||
//				isDoctor(atype) ||
//				isAngel(atype) ||
//				isDemon(atype) ||
//				isCatMask(atype) ||
//				isBlack(ctype) ||
//				isWhite(ctype) ||
//				isBlue(ctype) ||
//				isFiolet(ctype) ||
//				isOrange(ctype)
//			){
//				return true;
//			}
			return false;
		}
		
		public static function getExperienceClanBonus(atype:int, ctype:int):Boolean{
//			if(
////				isPen(atype) ||
////				isBandage(atype) ||
////				isCrone(atype) ||
//				isCylinder(atype) ||
//				isCookHat(atype) ||
//				isKovboyHat(atype) ||
//				isHipHop(atype) ||
//				isGlamur(atype) ||
//				isLamur(atype) ||
//				isHelmet(atype) ||
//				isPoliceHat(atype) ||
//				isFlashHair(atype) ||
//				isPumpkin(atype) ||
//				isNYHat(atype) ||
//				isNY(atype) ||
//				isDoctor(atype) ||
//				isAngel(atype) ||
//				isDemon(atype) ||
//				isCatMask(atype) ||
////				isBlack(ctype) ||
////				isWhite(ctype) ||
//				isBlue(ctype) ||
//				isFiolet(ctype) ||
//				isOrange(ctype)
//			){
//				return true;
//			}
			return false;
		}
		
		public static function getMoneyBonus(atype:int, ctype:int):Boolean{
//			if(
////				isPen(atype) ||
////				isBandage(atype) ||
////				isCrone(atype) ||
////				isCylinder(atype) ||
////				isCookHat(atype) ||
////				isKovboyHat(atype) ||
////				isFlashHair(atype) ||
//				isPumpkin(atype) ||
//				isNYHat(atype) ||
//				isNY(atype) ||
//				isDoctor(atype) ||
//				isAngel(atype) ||
//				isDemon(atype) ||
//				isCatMask(atype) ||
//				isBlack(ctype) ||
//				isWhite(ctype)
////				isBlue(ctype) ||
////				isFiolet(ctype) ||
////				isOrange(ctype)
//			){
//				return true;
//			}
			return false;
		}
	}
}