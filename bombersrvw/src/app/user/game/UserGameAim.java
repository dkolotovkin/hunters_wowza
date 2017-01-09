package app.user.game;

import app.user.UserClient;

public class UserGameAim {
	public UserClient user;
	public int countSource;			//���������� ��������� �������� (������)
	public int countBullet;			//���������� ��������� �������� (��������)
	public int countLife;			//���������� ������
	public boolean exitFlag;		//������������ �����
	
	public UserGameAim(UserClient u, int cl){
		this.user = u;
		this.countSource = 0;
		this.countBullet = 0;
		this.countLife = cl;
	}
}
