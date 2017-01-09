package app.user;

public class GameInfo{	
	public int id;	
	public String title;	
	public int level;
	public int sex;
	
	public GameInfo(int id, String title, int level, int sex){
		this.id = id;
		this.title = title;
		this.level = level;
		this.sex = sex;	
	}
	
	public static GameInfo createFromUser(User u){
		if(u != null){
			return new GameInfo(u.id, u.title, u.level, u.sex);
		}
		return null;
	}
}