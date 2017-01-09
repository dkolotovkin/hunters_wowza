package app.user.game;

import app.user.UserClient;

public class UserGameAim {
	public UserClient user;
	public int countSource;			//количество собранных ресурсов (орехов)
	public int countBullet;			//количество собранных ресурсов (патронов)
	public int countLife;			//количество жизней
	public boolean exitFlag;		//пользователь вышел
	
	public UserGameAim(UserClient u, int cl){
		this.user = u;
		this.countSource = 0;
		this.countBullet = 0;
		this.countLife = cl;
	}
}
