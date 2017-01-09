package app.user;

public class UserForTop {
	public int id;
	public int popular;
	public int exphour;
	public int expday;
	public boolean isonline;
	
	public String title;
	public int level;
	public int sex;
	public String url;
	
	public UserForTop(int id, int sex, String title, int level, int exphour, int expday, int popular, String url){
		this.id = id;
		this.sex = sex;
		this.title = title;
		this.level = level;
		this.exphour = exphour;
		this.expday = expday;
		this.popular = popular;
		this.url = url;
	}
}
