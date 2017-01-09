package app;

import java.util.Arrays;
import java.util.List;

public class Config {
	
	public static byte TEST = 0;
	public static byte RELEASE = 1;
	
	public static int mode(){return RELEASE;}
	public static int currentVersion(){return 10;}											//������ �������
	
	public static List<Integer> exphourprizes = Arrays.asList(400, 200, 100, 60, 40);		//������� ������ �� ���
	public static List<Integer> expdayprizes = Arrays.asList(4000, 2000, 1000, 600, 400);	//������� ������ �� ����
	
	public static int maxMessageCountInRoom(){return 50;} 						//���������� ���������, ������� ������ � �������	
	public static int valuePopularUpdateInBan(){return 2;}						//�������� �� ������� ���������� ������������ � ���� �� ������	 
	public static int banminutePrice(){return 20;}								//���� �� ������ ����	
	public static int changeInfoPrice(){return 2000;}							//���� �� ����� ���������� � ���������	
	public static int saveMapPrice(){return 100;}								//���� �� ���������� ����� �� �������
	public static int sendMailPrice(){return 20;}								//���� �� �������� �����	
	public static int createClanPrice(){return 50000;}							//���� �� ������� �����	
	public static int createClanNeedLevel(){return 10;}							//����������� ������� ��� �������� �����	
	public static int showLinkPrice(){return 50;}								//���� �� �������� ������	
	public static int victorinaPrize(){return 5;}								//������� ����� �� ����� � ���������	
	public static int friendBonus(){return 30;}									//����� �� ������������ �����
	public static int maxUsersInClan(){return 20;}								//������������ ���������� ���������� ������������� � �����
	
	public static int moneyPrize(){return 12;}									//������� ����� �� ��� ��������� �����
	public static int experiencePrizeAim(){return 6;}							//������� ����� �� ������ � ������ ��� ���� (���� �����)
	public static int experienceWoundBonus(){return 4;}							//������� ����� �� ��������� � ������ ��� ��������
	public static int experiencePrize(){return 10;}								//������� ����� �� ������ � ������ ��� �������� (��� �������� - * ���������� ������)
	public static int experienceClanPrize(){return 1;}							//������� ����� ����� �� ������ � ������ (��� ���� - ���� �����, ��� �������� - * ���������� ������)
		
	public static int waitTimeToStart(){if(Config.mode() == Config.TEST){return 3;}else{return 20;}}		//����� �������� ����	
	
	public static int minUsersInGame(){if(Config.mode() == Config.TEST){return 1;}else{return 2;}}			//����������� ���������� ������������� � ����		
	public static int maxUsersInGame(){return 8;}								//������������ ���������� ������������� � ����	
	
	public static int moneyToVote(){return 400;}								//����������� ����� �� 1 ����� VK
	
	public static String protectedSecretSiteVK(){return "6P91Ih1kcOqLgt5bCHPy";}				//protected secret VK (for site)
	public static String protectedSecretVK(){return "Msp1sZ1OioXerund0xNQ";}					//protected secret VK	 
	public static String protectedSecretSiteMM(){return "f3a1a00db731f731ec615d8ee9b12978";}	//protected secret MM (for site)
	public static String protectedSecretMM(){return "616f670474d37c4dbb390c923adcdc97";}		//protected secret MM
	public static String protectedSecretSiteOD(){return "616f670474d37c4dbb390c923adcdc97";}	//protected secret OD (for site)
	public static String applicationKeySiteOD(){return "CBADFNBBABABABABA";}					//application key OD  (for site)
	public static String protectedSecretOD(){return "0C9D4D9684A0970266715B77";}				//protected secret OD
	public static String publicSecretOD(){return "CBAIOCFBABABABABA";}							//public secret OD

	public static int appIdVK(){return 2473027;}												//������� ��� �����������	
	public static int appIdMM(){return 644633;}													//������� ��� �����������	
	public static int appIdOD(){return 5367552;}												//������� ��� �����������	
	
	public static String apiUrlMM(){return "http://www.appsmail.ru/platform/api";}				//������� ��� �����������
	public static String apiUrlVK(){return "http://api.vkontakte.ru/api.php";}					//������� ��� �����������
	public static String apiUrlOD(){return "http://api.odnoklassniki.ru/";}						//������� ��� �����������
	
	public static String loginUrlVK(){return "https://api.vkontakte.ru/oauth/access_token";}	//������� ��� �����
	public static String loginUrlMM(){return "https://connect.mail.ru/oauth/token";}			//������� ��� �����
	public static String loginUrlOD(){return "http://api.odnoklassniki.ru/oauth/token.do";}		//������� ��� �����
	
	
	public static String oficalSiteUrl(){return "http://mouserun.ru";}							//������� ��� �����
}
