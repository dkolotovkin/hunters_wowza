package application.gamecontainer.scene.bag.article
{
	import flash.events.Event;
	
	public class ConstructorArticleEvent extends Event
	{
		public static const SELECTED:String = "selected";
		public static const UNSELECTED:String = "unselected";
		
		public var article:ConstructorArticle;
		
		public function ConstructorArticleEvent(type : String, article:ConstructorArticle) {
			super(type, false, false);
			this.article = article;
		}
		
		override public function clone() : Event {
			return new ConstructorArticleEvent(type, article);
		}
	}
}