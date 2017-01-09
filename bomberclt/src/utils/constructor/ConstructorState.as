package utils.constructor
{
	public class ConstructorState
	{
		[Bindable]
		public static var NONE:uint = 0;
		[Bindable]
		public static var CREATE:uint = 1;
		[Bindable]
		public static var RESIZE:uint = 2;
		
		public function ConstructorState()
		{
		}
	}
}