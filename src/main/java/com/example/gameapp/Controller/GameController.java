package com.example.gameapp.Controller;


import com.example.movObject.*;
import javafx.animation.*;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Point2D;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import java.util.ArrayList;
import java.util.Random;

import javafx.scene.media.*;

import static com.example.gameapp.GameApp.log;

public class GameController {
    public Player player;
    public ImageView playerView;
    public VBox content;
    public ArrayList<MovableObject> objectList = new ArrayList<>();
    public AnchorPane ap;
    public StackPane stackPane;
    public Label currentScore;
    public int score = 0;
    public int highScore = 0;
    private static final AudioClip shootSound = new AudioClip("file:src/main/resources/com/example/assets/sfx/shoot.mp3");
    //public AudioClip score_up = new AudioClip("file:src/main/resources/com/example/assets/sfx/coin_sound.mp3");
    private static final AudioClip explosion = new AudioClip("file:src/main/resources/com/example/assets/sfx/explosionSFX.wav");

    //Start Method
    public void initialize() {
        shootSound.setVolume(0.2);
        DoubleProperty xPosition = new SimpleDoubleProperty(Animation.INDEFINITE);
        xPosition.addListener((observable, oldValue, newValue) -> setBackgroundPositions(stackPane, xPosition.get()));
        Timeline timeline = new Timeline(new KeyFrame(Duration.ZERO, new KeyValue(xPosition, -15000)), new KeyFrame(Duration.seconds(200), new KeyValue(xPosition, Animation.INDEFINITE)));
        timeline.setCycleCount(Animation.INDEFINITE);
        timeline.playFromStart();
        currentScore.setText("SCORE: " + score);
    }

    void setBackgroundPositions(Region region, double xPosition) {
        String style = "-fx-background-position: " + "top " + xPosition / 2 + "px left," + "top " + xPosition + "px left;";
        region.setStyle(style);
    }

    public void placePlayer(double xCenter, double y) {
        xCenter = xCenter / 2;

        System.out.println("Center XY " + xCenter + " " + y);
        player = new Player(playerView);
        player.getView().setImage(new Image("file:src/main/resources/com/example/assets/art/Enemy001.png"));
        player.getView().setFitHeight(player.getHeight());
        player.getView().setFitWidth(player.getWidth());
        player.getView().setLayoutX(xCenter);
        player.getView().setLayoutY(y);
        player.setView(playerView);
        log.info("placed player");
    }

    public Enemy addEnemy(double xStart, double yStart) {
        ImageView enemyView = new ImageView((new Image("file:src/main/resources/com/example/assets/art/Ship.png")));
        ap.getChildren().add(enemyView);
        Enemy e = new Enemy();
        e.setView(enemyView);
        e.getView().setFitWidth(e.getWidth());
        e.getView().setFitHeight(e.getHeight());
        enemyView.relocate(xStart, yStart);
        objectList.add(e);
        e.getView().setRotate(180);
        log.info("added enemy (x,y)");
        return e;
    }


    public void movePlayer(Point2D point2D) {
        this.player.getView().relocate(this.player.getView().getLayoutX() + point2D.getX() * this.player.getSpeed(), this.player.getView().getLayoutY() + point2D.getY() * this.player.getSpeed());
    }

    public void move(Point2D point2D, MovableObject o) {
        ImageView v = o.getView();
        v.relocate(v.getLayoutX() + point2D.getX() * o.getSpeed(), v.getLayoutY() + point2D.getY() * o.getSpeed());
    }


    Random rand = new Random();

    public boolean checkCollision(MovableObject o) {
        boolean b = false;
        if (!objectList.isEmpty()) {
            for (int i = 0; i < objectList.size(); i++) {
                MovableObject temp = objectList.get(i);
                if (o != objectList.get(i)) {
                    if (o.getView().getBoundsInParent().intersects(temp.getView().getBoundsInParent()) && o.isEnemyCollision()) {
                        if (temp instanceof Enemy e) {
                            KeyFrame animation = new KeyFrame(Duration.ZERO, actionEvent -> {
                                Image explosionGif;
                                if (rand.nextInt(2) == 0) {
                                    explosionGif = new Image("file:src/main/resources/com/example/assets/art/Explosion.gif");
                                } else {
                                    explosionGif = new Image("file:src/main/resources/com/example/assets/art/VRwF.gif");
                                }
                                e.setShoot(false);
                                e.getView().setScaleX(2);
                                e.getView().setScaleY(2);
                                e.getView().setImage(explosionGif);
                                log.info("Animation duration");
                            });
                            KeyFrame removeEnemy = new KeyFrame(Duration.millis(400), actionEvent -> {
                                remove(temp);
                                log.info("remove Enemy after animation");
                            });
                            Timeline animationExp = new Timeline(animation, removeEnemy);
                            animationExp.play();
                        }
                        if (temp instanceof Projectile && o instanceof Projectile) {
                            //do nothing
                        } else {
                            explosion.setVolume(0.05);
                            explosion.play();
                            remove(o);
                            score += 10;
                            //score_up.setVolume(0.05);
                            //score_up.play();
                            currentScore.setText("SCORE: " + score);
                            b = true;
                            log.info("" + o.toString() + " collided with " + temp.toString());
                            log.info("SCORE = " + score);
                        }

                    }
                }
            }
        }

        return b;
    }

    public boolean checkPlayerCollision(MovableObject o) {
        boolean b = false;
        if (!objectList.isEmpty()) {
            for (int i = 0; i < objectList.size(); i++) {
                MovableObject temp = objectList.get(i);
                if (temp instanceof Enemy) {
                    if (player.getView().getBoundsInParent().intersects(temp.getView().getBoundsInParent())) {
                        b = true;
                        log.info("player collided with " + o.toString());
                    }
                } else if (temp instanceof Projectile && temp.isPlayerCollision()) {
                    if (player.getView().getBoundsInParent().intersects(temp.getView().getBoundsInParent())) {
                        b = true;
                        log.info("player collided with " + o.toString());
                    }
                }
            }
        }

        return b;
    }

    public void objectAtBorder(double height, double width) {
        double topBound, bottomBound, leftBound, rightBound;
        topBound = 0;
        bottomBound = height - playerView.getFitHeight() * 2;
        leftBound = 0;
        rightBound = width - playerView.getFitWidth();
        if (player.getView().getLayoutY() <= topBound) {
            player.getView().setLayoutY(topBound);
        }
        if (player.getView().getLayoutY() >= bottomBound) {
            player.getView().setLayoutY(bottomBound);
        }
        if (player.getView().getLayoutX() <= leftBound) {
            player.getView().setLayoutX(leftBound);
        }
        if (player.getView().getLayoutX() >= rightBound) {
            player.getView().setLayoutX(rightBound);
        }
    }

    public void shoot(Player pl) {
        shootSound.setVolume(0.05);
        shootSound.play();
        Projectile pr = new Projectile();
        ImageView pView = pr.getView();
        pView.setFitWidth(pr.getWidth());
        pView.setFitHeight(pr.getHeight());
        player.setPlayerCollision(false);
        ap.getChildren().add(pView);
        pr.getView().setRotate(180);
        pView.relocate(pl.getView().getLayoutX(), pl.getView().getLayoutY() - pl.getHeight());
        objectList.add(pr);
        log.info("Player shoot");
    }

    public void shoot(Enemy e) {
        Projectile p = new Projectile();
        ImageView pView = p.getView();
        pView.setFitWidth(p.getWidth());
        pView.setFitHeight(p.getHeight());
        p.setPlayerCollision(true);
        p.setEnemyCollision(true);
        p.setSpeed(2);
        ap.getChildren().add(pView);
        p.getView().setRotate(0);
        pView.relocate(e.getView().getLayoutX(), e.getView().getLayoutY() + e.getView().getFitHeight() * 2);
        objectList.add(p);
        log.info("Enemy shoot");
    }


    public void reset(double x, double y) {
        clearAllEnemies(getObjectList());
        currentScore.setText("SCORE: 0");
        placePlayer(x, y);
        log.info("restart");
    }

    private void clearAllEnemies(ArrayList<MovableObject> oL) {
        if (!oL.isEmpty()) {
            while (!oL.isEmpty()) {
                int i = 0;
                MovableObject o = oL.get(i);
                remove(oL.get(i));
                log.info("removed" + o.toString());
            }
        }
    }

    public void deleteOffScreen(double width, double height, MovableObject o) {
        if (o.getView().getLayoutX() > width || o.getView().getLayoutY() > height || o.getView().getLayoutX() < -20 || o.getView().getLayoutY() < -20) {
            remove(o);
            log.info("deleted offscreen");
        }
    }


    public void remove(MovableObject o) {
        ap.getChildren().remove(o.getView());
        objectList.remove(o);
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public ImageView getPlayerView() {
        return playerView;
    }

    public void setPlayerView(ImageView playerView) {
        this.playerView = playerView;
    }

    public VBox getContent() {
        return content;
    }

    public void setContent(VBox content) {
        this.content = content;
    }

    public ArrayList<MovableObject> getObjectList() {
        return objectList;
    }

    public void setObjectList(ArrayList<MovableObject> objectList) {
        this.objectList = objectList;
    }

    public AnchorPane getAp() {
        return ap;
    }

    public void setAp(AnchorPane ap) {
        this.ap = ap;
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public void setStackPane(StackPane stackPane) {
        this.stackPane = stackPane;
    }

    public Label getCurrentScore() {
        return currentScore;
    }

    public void setCurrentScore(Label currentScore) {
        this.currentScore = currentScore;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getHighScore() {
        return highScore;
    }

    public void setHighScore(int highScore) {
        this.highScore = highScore;
    }

    public AudioClip getShootSound() {
        return shootSound;
    }
}