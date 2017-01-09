package app;

import java.util.Arrays;
import java.util.List;

public class Config {
	
	public static byte TEST = 0;
	public static byte RELEASE = 1;
	
	public static int mode(){return RELEASE;}
	public static int currentVersion(){return 10;}											//бепяхъ йкхемрю
	
	public static List<Integer> exphourprizes = Arrays.asList(400, 200, 100, 60, 40);		//мюцпюдю ксвьхл гю вюя
	public static List<Integer> expdayprizes = Arrays.asList(4000, 2000, 1000, 600, 400);	//мюцпюдю ксвьхл гю демэ
	
	public static int maxMessageCountInRoom(){return 50;} 						//йнкхвеярбн яннаыемхи, йнрнпше упюмхл б йнлмюре	
	public static int valuePopularUpdateInBan(){return 2;}						//гмювемхе мю йнрнпне хглемъеряъ оноскъпмнярэ б аюме гю лхмсрс	 
	public static int banminutePrice(){return 20;}								//жемю гю лхмсрс аюмю	
	public static int changeInfoPrice(){return 2000;}							//жемю гю ялемс хмтнплюжхх н оепянмюфе	
	public static int saveMapPrice(){return 100;}								//жемю гю янупюмемхе йюпрш мю яепбепе
	public static int sendMailPrice(){return 20;}								//жемю гю нропюбйс онврш	
	public static int createClanPrice(){return 50000;}							//жемю гю онйсойс йкюмю	
	public static int createClanNeedLevel(){return 10;}							//менаундхлши спнбемэ дкъ янгдюмхъ йкюмю	
	public static int showLinkPrice(){return 50;}								//жемю гю опнялнрп яяшкйх	
	public static int victorinaPrize(){return 5;}								//мюцпюдю ношрю гю нрбер б бхйрнпхме	
	public static int friendBonus(){return 30;}									//анмся гю опхбедеммнцн дпсцю
	public static int maxUsersInClan(){return 20;}								//люйяхлюкэмне йнкхвеярбн аеяокюрмшу онкэгнбюрекеи б йкюме
	
	public static int moneyPrize(){return 12;}									//мюцпюдю демец гю бяе янапюммше нпеух
	public static int experiencePrizeAim(){return 6;}							//мюцпюдю ношрю гю онаедс б гюаеце дкъ жекх (ЕЯКХ БШФХК)
	public static int experienceWoundBonus(){return 4;}							//мюцпюдю ношрю гю оноюдюмхъ б гюаеце дкъ нунрмхйю
	public static int experiencePrize(){return 10;}								//мюцпюдю ношрю гю онаедс б гюаеце дкъ нунрмхйю (ДКЪ НУНРМХЙЮ - * ЙНКХВЕЯРБН САХРШУ)
	public static int experienceClanPrize(){return 1;}							//мюцпюдю ношрю йкюмс гю онаедс б гюаеце (ДКЪ ЖЕКХ - ЕЯКХ БШФХК, ДКЪ НУНРМХЙЮ - * ЙНКХВЕЯРБН САХРШУ)
		
	public static int waitTimeToStart(){if(Config.mode() == Config.TEST){return 3;}else{return 20;}}		//бпелъ нфхдюмхъ хцпш	
	
	public static int minUsersInGame(){if(Config.mode() == Config.TEST){return 1;}else{return 2;}}			//лхмхлюкэмне йнкхвеярбн онкэгнбюрекеи б хцпе		
	public static int maxUsersInGame(){return 8;}								//люйяхлюкэмне йнкхвеярбн онкэгнбюрекеи б хцпе	
	
	public static int moneyToVote(){return 400;}								//йнкхвеяйрбн лнмер гю 1 цнкня VK
	
	public static String protectedSecretSiteVK(){return "6P91Ih1kcOqLgt5bCHPy";}				//protected secret VK (for site)
	public static String protectedSecretVK(){return "Msp1sZ1OioXerund0xNQ";}					//protected secret VK	 
	public static String protectedSecretSiteMM(){return "f3a1a00db731f731ec615d8ee9b12978";}	//protected secret MM (for site)
	public static String protectedSecretMM(){return "616f670474d37c4dbb390c923adcdc97";}		//protected secret MM
	public static String protectedSecretSiteOD(){return "616f670474d37c4dbb390c923adcdc97";}	//protected secret OD (for site)
	public static String applicationKeySiteOD(){return "CBADFNBBABABABABA";}					//application key OD  (for site)
	public static String protectedSecretOD(){return "0C9D4D9684A0970266715B77";}				//protected secret OD
	public static String publicSecretOD(){return "CBAIOCFBABABABABA";}							//public secret OD

	public static int appIdVK(){return 2473027;}												//уюпдйнд дкъ сбеднлкемхи	
	public static int appIdMM(){return 644633;}													//уюпдйнд дкъ сбеднлкемхи	
	public static int appIdOD(){return 5367552;}												//уюпдйнд дкъ сбеднлкемхи	
	
	public static String apiUrlMM(){return "http://www.appsmail.ru/platform/api";}				//уюпдйнд дкъ сбеднлкемхи
	public static String apiUrlVK(){return "http://api.vkontakte.ru/api.php";}					//уюпдйнд дкъ сбеднлкемхи
	public static String apiUrlOD(){return "http://api.odnoklassniki.ru/";}						//уюпдйнд дкъ сбеднлкемхи
	
	public static String loginUrlVK(){return "https://api.vkontakte.ru/oauth/access_token";}	//уюпдйнд дкъ яюирю
	public static String loginUrlMM(){return "https://connect.mail.ru/oauth/token";}			//уюпдйнд дкъ яюирю
	public static String loginUrlOD(){return "http://api.odnoklassniki.ru/oauth/token.do";}		//уюпдйнд дкъ яюирю
	
	
	public static String oficalSiteUrl(){return "http://mouserun.ru";}							//уюпдйнд дкъ яюирю
}
