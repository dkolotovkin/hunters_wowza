package app.user.game;

import app.user.UserClient;

public class UserGameHunter {
	public UserClient user;
	public int countKilled;			//количество убитых
	
	public UserGameHunter(UserClient u){
		this.user = u;
		this.countKilled = 0;
	}
}
