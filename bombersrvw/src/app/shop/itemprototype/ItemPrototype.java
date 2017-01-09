package app.shop.itemprototype;

public class ItemPrototype {
	public int id;
	public String title;
	public String description;
	public int param1;
	public int param2;
	public int param3;
	public int param4;
	public int param5;
	public int param6;
	public int param7;
	public int price;
	public int showed;
	public int maxquality;
	public int minlevel;
	public String url;
	
	public ItemPrototype(int id, String title, String description, 
							int params, int price, int showed, int maxquality, int minlevel, String url){
		this.id = id;
		this.title = title;
		this.description = description;
		
		this.param7 = (int) Math.floor((double) (params / 1000000));
		this.param6 = (int) Math.floor((double) (params - param7 * 1000000) / 100000);
		this.param5 = (int) Math.floor((double) (params - (param7 * 1000000 + param6 * 100000)) / 10000);
		this.param4 = (int) Math.floor((double) (params - (param7 * 1000000 + param6 * 100000 + param5 * 10000)) / 1000);
		this.param3 = (int) Math.floor((double) (params - (param7 * 1000000 + param6 * 100000 + param5 * 10000 + param4 * 1000)) / 100);				
		this.param2 = (int) Math.floor((double) (params - (param7 * 1000000 + param6 * 100000 + param5 * 10000 + param4 * 1000 + param3 * 100)) / 10);
		this.param1 = (int) Math.floor((double) (params - (param7 * 1000000 + param6 * 100000 + param5 * 10000 + param4 * 1000 + param3 * 100 + param2 * 10)) / 1);				
		
		//param1 - первый разряд params
		//param2 - второй разряд params
		//...
		//paramN - N-й разряд params
		
		this.price = price;
		this.maxquality = maxquality;
		this.minlevel = minlevel;
		this.showed = showed;
		this.url = url;
	}
}
