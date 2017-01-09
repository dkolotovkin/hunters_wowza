package utils.getparams
{
	public class GetParams
	{
		public function GetParams()
		{
		}
		
		public static function getParamByName(params:String, name:String):String{
			var arr1:Array = params.split("?");
			if(arr1.length > 1 && arr1[1]){
				var arr2:Array = (arr1[1] as String).split("&");
				for(var i:uint = 0; i < arr2.length; i++){
					var arr3:Array = (arr2[i] as String).split("=");
					if(arr3.length > 1){
						if(arr3[0] == name){
							return arr3[1];
						}
					}
				}
			}
			return null;
		}
	}
}