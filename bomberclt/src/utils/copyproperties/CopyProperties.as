package utils.copyproperties
{
	public class CopyProperties
	{
		public function CopyProperties(){
		}
		
		public static function copy(fromObj:Object, toObj:Object):void{
			for (var property:* in fromObj){
				if(toObj.hasOwnProperty(property) == true){
					toObj[property] = fromObj[property];
				}
			}
		}
	}
}