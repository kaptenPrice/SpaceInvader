package model;

public class Constants {

    public final static double SCREENHEIGHT = 600;
    public final static double SCREENWIDTH = 900;

    public final static String playerShipURL = "model/resources/player.png";
    public final static double playerShipHeight = SCREENHEIGHT * 0.12;
    public final static double playerShipWidth = SCREENWIDTH * 0.08;
    public final static double playerShipMovementSpeed = SCREENHEIGHT * 0.008;
    public final static double playerShipStartPosX = SCREENWIDTH/2;
    public final static double playerShipStartPosY= SCREENHEIGHT * 0.95;
    public final static int playerLifes = 3;

    public final static String enemyShipURL = "model/resources/enemy.png";
    public final static double enemyShipHeight = SCREENHEIGHT * 0.12;
    public final static double enemyShipWidth = SCREENWIDTH * 0.08;
    public final static double enemyShipMovementSpeed = SCREENHEIGHT * 0.008;
    public final static double enemyShipStartPosX = SCREENWIDTH * 0.1;
    public final static double enemyShipStartPosY = SCREENHEIGHT -(SCREENHEIGHT+45);
    public final static double enemySpawnSpread = SCREENWIDTH * 0.08;
    public final static int enemyLifes = 1;
    public final static double enemyLaserGunShootingInterval = 300;
    public final static double enemyShipMovmentInterval = 300;

    public final static String enemyDroneShipUrl = "model/resources/enemy1_4.png";
    public final static double enemyDroneShipHeight = SCREENHEIGHT*0.12;
    public final static double enemyDroneShipWidth = SCREENWIDTH*0.08;
    public final static double enemyDroneShipMovmentSpeed = SCREENHEIGHT*0.016;
    public final static double enemyDroneShipStartPosX = SCREENWIDTH*0.1;
    public final static double enemyDroneShipStartPosY = SCREENHEIGHT -(SCREENHEIGHT +45);
    public final static double enemyDroneShipSpawnSpread = SCREENWIDTH*0.08;
    public final static int enemyDroneShipLifes =2;
    public final static double enemyDroneShipLaserGunShootingInterval = 200;

    public final static String enemyBigBossUrl = "model/resources/enemy1_5.png";
    public final static double enemyBigBossHeight = SCREENHEIGHT*0.3;
    public final static double enemyBigBossWidth = SCREENWIDTH*0.32;
    public final static double enemyBigBossMovmentSpeed = SCREENHEIGHT*0.032;
    public final static double enemyBigBossStartPosX = SCREENWIDTH*0.1;
    public final static double enemyBigBossStartPosY = SCREENHEIGHT -(SCREENHEIGHT+45);
    public final static int enemyBigBossLifes = 100;
    public final static double enemyBigBossShootingInterval =50;

    public final static String BackGroundImage = "model/resources/Background3.png";
    public final static String meteorImage = "model/resources/meteor.png";


    public final static String laserBulletURL = "model/resources/bulletShot.png";
    public final static double laserBulletHeight = SCREENHEIGHT * 0.04;
    public final static double laserBulletWidth = SCREENWIDTH * 0.025;
    public final static double laserBulletMovementSpeed = SCREENHEIGHT * 0.016;
    public final static double laserBulletShootInterval = 15;

    public final static String heartURL = "model/resources/heart.png";
    public final static double heartStartX = SCREENWIDTH * 0.87;
    public final static double heartStartY = SCREENHEIGHT * 0.95;
    public final static double heartHeight = SCREENHEIGHT * 0.04;
    public final static double heartWidth = SCREENWIDTH * 0.04;

    public final static String gameOverSubSceneBackground = "model/resources/subScenBG.png";
    public final static String gameBootSceneBackGround ="model/resources/background2.png";
    public final static String pointLabelBackGround ="model/resources/pointLabel.png";

    public final static double spawnPointMeteor = Math.random()*SCREENWIDTH;


    public static final String buttonBgClickedURL = "model/resources/buttonOnClick.png"; //TODO
    public static final String buttonBgURL = "model/resources/buttonImg.png"; // TODO
    public static final double menuButtonWidth = SCREENWIDTH * 0.15;
    public static final double menuButtonStartingXPos = SCREENWIDTH * 0.1;
    public static final double menuButtonStartingYPos = SCREENHEIGHT * 0.7;
}
