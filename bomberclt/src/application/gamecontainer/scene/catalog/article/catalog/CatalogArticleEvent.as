package application.gamecontainer.scene.catalog.article.catalog
{
	import flash.events.Event;
	
	public class CatalogArticleEvent extends Event
	{
		public static const SELECTED:String = "selected";
		public static const UNSELECTED:String = "unselected";
		
		public var article:CatalogArticle;
		
		public function CatalogArticleEvent(type : String, article:CatalogArticle) {
			super(type, false, false);
			this.article = article;
		}
		
		override public function clone() : Event {
			return new CatalogArticleEvent(type, article);
		}
	}
}