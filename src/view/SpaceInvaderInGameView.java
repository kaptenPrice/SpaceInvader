package view;

import controller.SpaceInvaderButtonListener;
import controller.SpaceInvaderListener;
import javafx.animation.AnimationTimer;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SubScene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import model.*;

import java.util.ArrayList;

public class SpaceInvaderInGameView implements IViewState {


    private static SpaceInvaderInGameView gameView;
    private static InGameModel model;

    private static AnchorPane gamePane;
    private static Scene gameScene;

    private ArrayList<ImageView> enemiesImageList = new ArrayList<>();
    private ArrayList<ImageView> bulletsImageList = new ArrayList<>();
    private ArrayList<ImageView> meteorImageList = new ArrayList<>();

    private ImageView playerImage;
    private ImageView firstBackGroundImage = new ImageView(Constants.BackGroundImage);
    private ImageView secondBackGroundImage = new ImageView(Constants.BackGroundImage);
    private ArrayList<ImageView> playerLifeImages;
    private Label pointsLabel;
    private int rotation=0;

    private SubScene deathSubScene;
    private long spawnNewEnemies =0;
    private AnimationTimer inGameTimer;

    public static Scene getGameScene() {
        return gameScene;
    }

    public static SpaceInvaderInGameView getGameView() {
        if (gameView == null) {
            gameView = new SpaceInvaderInGameView();
        }
        return gameView;
    }

    private SpaceInvaderInGameView() {

        model = InGameModel.getGameModel();
        gamePane = new AnchorPane();
        gameScene = new Scene(gamePane, Constants.SCREENWIDTH, Constants.SCREENHEIGHT);

        initializeLevelToPane();  //adds all images to the pane.
        initializeGameListener(); // add key listener to game
        createGameLoop(); // starts animator.

    }

    public void resetGame() {
        gameView = null;

    }

    private void createGameLoop() {
        inGameTimer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                // checks and update movement of images
                updateIfSpawnNewEnemies();
                updateIfPlayerIsShooting();
                updateIfEnemyIsShooting();
                updateAllModels(); // update all models before checks.
                updateAllImageviews();
                updateIfLevelIsDone();
                spawnNewEnemies++;
            }
        };

        inGameTimer.start();
    }

    /****************** update Methods below  ******************************/

    //add all model updates here
    private void updateAllModels() {
        model.updateBullets();
        model.updateWeaponsState();
        model.updatePlayerMovement();
        model.checkIfEnemyIsmoving();
        model.moveMeteorModel();
        model.checkIfMeteorCollide();

    }

    //add all imagesviews here
    private void updateAllImageviews() {
        updateBackGround();
        updateBulletsImage();
        updatePlayerImage();
        updateEnemyImages();
        updatePointsLabel();
        updatePlayerLifeImages();
        updateMeteorImages();
        updateMeteorRotation();
    }

    private void updatePointsLabel() {
        int myPoints = model.getPoints();
        String pointText = "Points: " + myPoints;
        pointsLabel.setText(pointText);
    }

    private void updateIfLevelIsDone() {
        if (model.getPlayerModel().getLifes() <=0) {
            initializeDeathSubScene();
            inGameTimer.stop();
        }
    }
    //Spawns Enemys at an interval.
    private void updateIfSpawnNewEnemies() {

        if (spawnNewEnemies==0|| spawnNewEnemies ==600 || spawnNewEnemies == 1200 || spawnNewEnemies == 1600) {
            model.createDefaultEnemieWave();
            initializeEnemies();
        }
        if ((Math.random()*100)<2) {
            model.createMeteor();
            updateLastMeteor();
        }
        if (spawnNewEnemies ==2200) {
            spawnNewEnemies =0;
        }
    }

    private void updatePlayerLifeImages() {
        int playerLifes = model.getPlayerModel().getLifes();

        if (playerLifes != playerLifeImages.size()) {
            int differenceInLife = playerLifeImages.size() - playerLifes;
            for (int i = 0; i < Math.abs(differenceInLife) ; i++) {
                if (differenceInLife < 0) {
                    createPlayerLifeImage(playerLifeImages.size());
                }
                else {
                    removeFromGamePane(playerLifeImages.get(playerLifeImages.size()-1));
                    playerLifeImages.remove(playerLifeImages.size()-1);
                }
            }
        }

        if (playerLifes < 1) {   // TODO Pop up menu when dead
            initializeDeathSubScene();
            inGameTimer.stop();
        }
    }

    private void updatePlayerImage() {
        PlayerShip player = model.getPlayerModel();
        playerImage.setX(player.getItemCoordX());
        playerImage.setY(player.getItemCoordY());

    }

    // update the bullets images to mirror the model bullets.
    private void updateBulletsImage() {
        ArrayList<IBullet> bulletsModelList = model.getBulletsModelList();

        if (!bulletsImageList.isEmpty()) {
            for (int i = 0; i < bulletsImageList.size(); i++) {
                ImageView theImageBullet = bulletsImageList.get(i);
                if (i < bulletsModelList.size()) {
                    OnScreenItems theModelBullet = (OnScreenItems) bulletsModelList.get(i);
                    theImageBullet.setX(theModelBullet.getItemCoordX());
                    theImageBullet.setY(theModelBullet.getItemCoordY());
                }
            }
        }
        ArrayList<IBullet> bulletsToRemove = model.getBulletRemoveList(); // adds all bullets who are out of screen and those who collided.
        for (IBullet bullet : bulletsToRemove) {
            int bulletIndex = bulletsModelList.indexOf(bullet);  // gets index of the model bullet.
            model.getBulletsModelList().remove(bulletIndex); // removes the model bullet from our list.
            removeFromGamePane(bulletsImageList.get(bulletIndex)); // removes bullet image from pane.
            bulletsImageList.remove(bulletIndex); // removes bullet image from our bullet image list.
            System.out.println("Bullet removed");
        }
        ArrayList<IBullet> bulletsToRemoveMeteor = model.checkIfMeteorShoot();
        for (IBullet bullet: bulletsToRemoveMeteor) {
            int bulletIndexFromMeteor = bulletsModelList.indexOf(bullet);
            model.getBulletsModelList().remove(bulletIndexFromMeteor);
            removeFromGamePane(bulletsImageList.get(bulletIndexFromMeteor));
            bulletsImageList.remove(bulletIndexFromMeteor);
            System.out.println("remove bullet from meteorshoot");
        }

    }//updates the movement of the meteor and removes it Y>=then the ScreenHeight
    private ArrayList<ImageView> updateMeteorImages() {
        if (model.getMeteorModelList().isEmpty()&&!meteorImageList.isEmpty()) {
            removeFromGamePane(meteorImageList.get(0));
            meteorImageList.clear(); }
        if (model.getMeteorModelList() !=null) {
            ArrayList<Meteor> allMetoerModels = model.getMeteorModelList();
            for (int i = 0; i < allMetoerModels.size(); i++) {
                if (allMetoerModels.size() != meteorImageList.size()) {
                    removeFromGamePane(meteorImageList.get(model.getLastRemovedMeteorIndex()));
                    meteorImageList.remove(model.getLastRemovedMeteorIndex());
                }
                meteorImageList.get(i).setY(allMetoerModels.get(i).getItemCoordY());
                meteorImageList.get(i).setX(allMetoerModels.get(i).getItemCoordX());
                if (meteorImageList.get(i).getY() >= Constants.SCREENHEIGHT+300) {
                    model.removeMeteorFromList(allMetoerModels.get(i));
                    removeFromGamePane(meteorImageList.get(i));
                    meteorImageList.remove(i);
                    System.out.println("meteor removed at: " + i + " cheers");
                }
            }
        }
        return meteorImageList;
    }//rotates the meteor
    private ArrayList<ImageView> updateMeteorRotation() {
        for (ImageView meteor: meteorImageList) {
            meteor.setRotate(rotation);
           if (rotation >= 360) {
               rotation=0;
           }
        }
        rotation++;
        return meteorImageList;
    }

    private void updateEnemyImages() {
        ArrayList<EnemyShip> allEnemyModels = model.getEnemyModelList();
        ArrayList<EnemyShip> modelEnemiesToRemove = model.getDeadEnemies();

        ArrayList <EnemyShip> enemymove = model.getEnemyModelList();
        for (int i = 0; i < enemymove.size() ; i++) {
            enemiesImageList.get(i).setY(enemymove.get(i).getItemCoordY());
            enemiesImageList.get(i).setX(enemymove.get(i).getItemCoordX());
        }

      if (!modelEnemiesToRemove.isEmpty()) {
          for (EnemyShip enemyModel : modelEnemiesToRemove) {
              int enemyIndex = allEnemyModels.indexOf(enemyModel);
              model.getEnemyModelList().remove(enemyIndex);
              removeFromGamePane(enemiesImageList.get(enemyIndex));
              enemiesImageList.remove(enemyIndex);
              System.out.println("Enemy images removed");
          }
      }
    }

    private void updateIfPlayerIsShooting() {
        IBullet currentBullet = model.checkIfPlayerIsShooting();
        if (currentBullet != null) {
            createBullet(currentBullet);
        }
    }

    private void updateIfEnemyIsShooting() {
        ArrayList<IBullet> allEnemyModelBullets = model.checkIfEnemyIsShooting();
        for (IBullet enemyModelBullet: allEnemyModelBullets) {
            createBullet(enemyModelBullet);
        }
    }

    private void updateBackGround() {
        firstBackGroundImage.setY(firstBackGroundImage.getY() + 5);
        secondBackGroundImage.setY(secondBackGroundImage.getY() + 5);
        if (firstBackGroundImage.getY() >= Constants.SCREENHEIGHT) {
            firstBackGroundImage.setY(-3600);
        }
        if (secondBackGroundImage.getY() >= Constants.SCREENHEIGHT) {
            secondBackGroundImage.setY(-3600);
        }
    }

    /****************** update Methods ENDS  ******************************/

    /****************** Initialize Methods below  ******************************/

    private void initializeLevelToPane() {

        initializeBackground();
        initializePointLabel();
        initializePlayerLifes();
        initializePlayer();
        initializeEnemies();
        //TODO add all starting images.
    }

    public void initializePointLabel() {
        pointsLabel = new Label("Points: ");
        pointsLabel.setPrefWidth(130); // TODO CHANGE TO CONSTANTS
        pointsLabel.setPrefHeight(50);
        BackgroundImage backgroundImage = new BackgroundImage(new Image("model/resources/blue_info_label.png", 130,50,false,true), BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, null);
        pointsLabel.setBackground(new Background(backgroundImage));
        pointsLabel.setAlignment(Pos.CENTER_LEFT);
        pointsLabel.setPadding(new Insets(10,10,10,10));
        pointsLabel.setFont(Font.font("Verdana", 15));
        pointsLabel.setLayoutX(Constants.SCREENWIDTH *0.9);
        pointsLabel.setLayoutY(Constants.SCREENHEIGHT * 0.02);
        gamePane.getChildren().add(pointsLabel);
    }

    private void initializeBackground() {
        secondBackGroundImage.setY(-3600);
        addToGamePane(firstBackGroundImage);
        addToGamePane(secondBackGroundImage);
    }

    private void initializePlayerLifes() {
        playerLifeImages = new ArrayList<>();
        for (int i = 0; i <  model.getPlayerModel().getLifes(); i++) {
            createPlayerLifeImage(i);
        }
    }
private void updateLastMeteor() {
        if (model.getMeteorModelList() !=null) {
        ArrayList<Meteor> meteorsModelList = model.getMeteorModelList();
            // Meteor meteorModel = new Meteor();
            ImageView meteorImage = new ImageView((new Image(Constants.meteorImage)));
            meteorImage.setX(meteorsModelList.get(meteorsModelList.size()-1).getItemCoordX());
            meteorImage.setY(meteorsModelList.get(meteorsModelList.size()-1).getItemCoordY());
            addToGamePane(meteorImage);
            meteorImageList.add(meteorImage);

        }
    }

    private void initializeEnemies() {
        ArrayList<EnemyShip> enemyModelList = model.getEnemyModelList();
        for (int i = 0; i < enemyModelList.size(); i++) {
            EnemyShip enemyModel = enemyModelList.get(i);
            ImageView enemyImage = new ImageView(new Image(Constants.enemyShipURL));
            enemyImage.setX(enemyModel.getItemCoordX());
            enemyImage.setY(enemyModel.getItemCoordY());
            enemyImage.setRotate(180);
            addToGamePane(enemyImage);
            enemiesImageList.add(enemyImage);
        }
    }

    //Creates the image of the player and set it's position and add to pane.
    private void initializePlayer() {
        PlayerShip playerModel = model.getPlayerModel();
        playerImage = new ImageView(playerModel.getImageUrl());
        playerImage.setX(playerModel.getItemCoordX());
        playerImage.setY(playerModel.getItemCoordY());
        playerImage.setPreserveRatio(true);
        playerImage.setFitHeight(playerModel.getItemHeight());
        playerImage.setFitWidth(playerModel.getItemWidth());
        //playerImage.resize(playerModel.getItemWidth(), playerModel.getItemHeight());
        addToGamePane(playerImage);
    }

    private void initializeDeathSubScene() {

        deathSubScene = new SubScene(new AnchorPane(),Constants.SCREENWIDTH * 0.45, Constants.SCREENHEIGHT * 0.45);

        BackgroundImage image = new BackgroundImage(new Image(Constants.GameOverSubSceneBackground,Constants.SCREENWIDTH * 0.45,Constants.SCREENHEIGHT * 0.45, false, true),
                BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT, BackgroundPosition.DEFAULT, null);

        AnchorPane deathAnchor = (AnchorPane) deathSubScene.getRoot();
        deathAnchor.setBackground(new Background(image));

        deathSubScene.setLayoutX(Constants.SCREENWIDTH/3);
        deathSubScene.setLayoutY(Constants.SCREENHEIGHT/3);

        Text playerDeadText = new Text("GAME OVER");
        playerDeadText.setX(deathAnchor.getWidth() * 0.20);
        playerDeadText.setY(deathAnchor.getHeight() * 0.20);
        playerDeadText.setFont(Font.font("Verdana", 30));
        deathAnchor.getChildren().add(playerDeadText);

        Text yourScoreText = new Text("Your score: " + model.getPoints());
        yourScoreText.setX(deathAnchor.getWidth() * 0.20);
        yourScoreText.setY(deathAnchor.getHeight() * 0.35);
        yourScoreText.setFont(Font.font("Verdana", 15));
        deathAnchor.getChildren().add(yourScoreText);

        Text highScoreText = new Text("Current Highscore: "); // TODO add highscore in the line
        highScoreText.setX(deathAnchor.getWidth() * 0.20);
        highScoreText.setY(deathAnchor.getHeight() * 0.50);
        highScoreText.setFont(Font.font("Verdana", 15));
        deathAnchor.getChildren().add(highScoreText);

        Text enterNameText = new Text("Enter your username: ");
        enterNameText.setX(deathAnchor.getWidth() * 0.20);
        enterNameText.setY(deathAnchor.getHeight() * 0.65);
        enterNameText.setFont(Font.font("Verdana", 15));
        deathAnchor.getChildren().add(enterNameText);

        TextField enterNameField = new TextField();
        enterNameField.setLayoutX(deathAnchor.getWidth() * 0.25);
        enterNameField.setLayoutY(deathAnchor.getHeight() * 0.70);
        deathAnchor.getChildren().add(enterNameField);

        Button saveScoreButton = new Button("Save Score");
        saveScoreButton.setLayoutX(deathAnchor.getWidth() * 0.10);
        saveScoreButton.setLayoutY(deathAnchor.getHeight() * 0.85);
        saveScoreButton.setOnAction(e->model.setNameInput(enterNameField.getText()));
        deathAnchor.getChildren().add(saveScoreButton);


        Button playAgainButton = new Button("Play again");
        playAgainButton.setLayoutX(deathAnchor.getWidth() * 0.70);
        playAgainButton.setLayoutY(deathAnchor.getHeight() * 0.85);
        playAgainButton.addEventFilter(MouseEvent.MOUSE_CLICKED, SpaceInvaderButtonListener.getButtonListener().resetGameEvent);
        deathAnchor.getChildren().add(playAgainButton);

        addToGamePane(deathSubScene);
    }

    // starts the listeners.
    private void initializeGameListener() {
        gameScene.setOnKeyPressed(SpaceInvaderListener.getListener());
        gameScene.setOnKeyReleased(SpaceInvaderListener.getListener());
    }

    /****************** Initialize Methods ends  ******************************/


    /****************** Create Methods below  ******************************/

    private void createPlayerLifeImage(int lifeNumber) {
        ImageView playerLifeImage = new ImageView(Constants.playerShipURL);
        playerLifeImage.setLayoutX(Constants.heartStartX + (lifeNumber * Constants.heartWidth));
        playerLifeImage.setLayoutY(Constants.heartStartY);
        playerLifeImage.setPreserveRatio(true);
        playerLifeImage.setFitWidth(Constants.heartWidth);
        playerLifeImage.setFitHeight(Constants.heartHeight);

        playerLifeImages.add(playerLifeImage);
        addToGamePane(playerLifeImage);
    }


    // sets the imageView based on the model Ibullet.
    private void createBullet(IBullet bullet) {
        OnScreenItems itemBullet = (OnScreenItems) bullet;
        System.out.println("bullet image created at x: " + itemBullet.getItemCoordX() + " y: " + itemBullet.getItemCoordY());
        ImageView imageBullet = new ImageView(itemBullet.getImageUrl());
        imageBullet.setX(itemBullet.getItemCoordX());
        imageBullet.setY(itemBullet.getItemCoordY());
        imageBullet.setPreserveRatio(true);
        imageBullet.setFitHeight(itemBullet.getItemHeight());
        imageBullet.setFitWidth(itemBullet.getItemWidth());
        //imageBullet.resize(itemBullet.getItemWidth(), itemBullet.getItemHeight());

        if (itemBullet.isFacingPlayer()) {
            imageBullet.setRotate(180);
        }

        bulletsImageList.add(imageBullet);
        addToGamePane(imageBullet);
    }

    /****************** Create Methods ENDS  ******************************/


    /****************** Helper Methods below  ******************************/

    private void addToGamePane(Node node) {
        gamePane.getChildren().add(node);
    }

    private void removeFromGamePane(ImageView imageItem) {
        gamePane.getChildren().remove(imageItem);
    }

    /****************** Helper Methods ENDS  ******************************/


}
