package utils.managers.tooltip
{	
	public interface IToolTiped {
		
		function get toolTipDelay ():int;
		
		function set toolTip (value:String):void; 
		function get toolTip ():String;
		
		function get toolTipDX ():int;
		function get toolTipDY ():int;	
		function get toolTipType():int;
	}
}
