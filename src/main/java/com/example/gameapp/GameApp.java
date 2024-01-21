package com.example.gameapp;

import com.example.gameapp.Controller.DeathController;
import com.example.gameapp.Controller.GameController;
import com.example.gameapp.Controller.MenuController;
import com.example.gameapp.Controller.PauseController;
import com.example.movObject.Enemy;
import com.example.movObject.MovableObject;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Point2D;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.MouseButton;
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.util.Duration;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class GameApp extends Application {

    private AnimationTimer timer;
    public static final Logger log = LogManager.getLogger(GameApp.class.getName());
    private boolean goNorth, goSouth, goEast, goWest;

    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader gameLoader = new FXMLLoader(GameApp.class.getResource("game-view.fxml"));
        FXMLLoader menuLoader = new FXMLLoader(GameApp.class.getResource("game-menu.fxml"));
        FXMLLoader pauseLoader = new FXMLLoader(GameApp.class.getResource("game-pause.fxml"));
        FXMLLoader deathLoader = new FXMLLoader(GameApp.class.getResource("game-over.fxml"));

        AudioClip bg = new AudioClip("file:src/main/resources/com/example/assets/ost/background_music.mp3");
        bg.setVolume(0.05);
        bg.setCycleCount(AudioClip.INDEFINITE);
        bg.play();

        Scene game = new Scene(gameLoader.load());
        Scene gameMenu = new Scene(menuLoader.load());
        Scene gamePause = new Scene(pauseLoader.load());
        Scene gameOver = new Scene(deathLoader.load());
        stage.setTitle("MassDefectFX!");
        stage.setHeight(1000);
        stage.setWidth(500);
        stage.setScene(gameMenu);
        stage.show();
        GameController controller = gameLoader.getController();
        MenuController menuController = menuLoader.getController();
        PauseController pauseController = pauseLoader.getController();
        DeathController deathController = deathLoader.getController();
        ArrayList<MovableObject> oL = controller.getObjectList();

        //enemy spawner
        Timeline spawner = new Timeline(new KeyFrame(Duration.millis(900), event -> {
            double xStart;
            xStart = (System.nanoTime() * 777) % stage.getWidth();
            log.info("" + xStart);
            Enemy e = controller.addEnemy(xStart, -5);
            controller.shoot(e);
            Timer t = new Timer(true);
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    e.setShoot(true);
                }
            };
            t.scheduleAtFixedRate(task, 0, 1500);
        }));
        spawner.setCycleCount(Timeline.INDEFINITE);
        //Pause Screen Event Handling
        gamePause.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.ESCAPE) {
                stage.setScene(game);
                timer.start();
                spawner.playFromStart();
            }
        });

        //Game Screen Event Handling
        game.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case W -> goNorth = true;
                case S -> goSouth = true;
                case A -> goWest = true;
                case D -> goEast = true;
                case ESCAPE -> {
                    stage.setScene(gamePause);
                    timer.stop();
                    spawner.stop();
                }
                case SPACE -> {
                    if (controller.getPlayer().isShoot()){
                        controller.shoot(controller.getPlayer());
                        controller.getPlayer().setShoot(false);
                        Timer coolDown = new Timer(true);
                        TimerTask task = new TimerTask() {
                            @Override
                            public void run() {
                                controller.getPlayer().setShoot(true);
                            }
                        };
                        coolDown.schedule(task, 2);
                    }


                }
            }
        });
        game.setOnKeyReleased(event -> {
            switch (event.getCode()) {
                case W -> goNorth = false;
                case S -> goSouth = false;
                case A -> goWest = false;
                case D -> goEast = false;
            }
        });
        //GameOver Event Handler
        gameOver.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE -> stage.close();
                case ENTER -> startGame(stage, game, timer, spawner, controller);
            }
        });

        //UpdateMethod PlayerCollision + Collision + Movement
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                //  Player Collision
                if (controller.checkPlayerCollision(controller.getPlayer())) {
                    stage.setScene(gameOver);
                    deathController.labelscore.setText("SCORE: " + controller.getScore());
                    if (controller.getScore() > controller.getHighScore()) {
                        controller.setHighScore(controller.getScore());
                        deathController.labelhighscore.setText("HIGHSCORE: " + controller.getHighScore());
                    }
                    spawner.stop();
                    timer.stop();
                    controller.reset(stage.getWidth(), stage.getHeight());
                    // Input reset
                    goEast = false;
                    goNorth = false;
                    goSouth = false;
                    goWest = false;
                }
                //Collision Detection and Movement für Objects
                if (!oL.isEmpty()) {
                    for (int i = 0; i < oL.size(); i++) {
                        MovableObject currentObject = controller.getObjectList().get(i);
                        controller.deleteOffScreen(stage.getWidth(), stage.getHeight(), currentObject);
                        controller.move(new Point2D(0, 1), currentObject);
                        if(currentObject instanceof Enemy){
                            Enemy e;
                            e = (Enemy) currentObject;
                            if(e.isShoot()){
                                controller.shoot(e);
                                e.setShoot(false);
                            }
                        }
                        if (currentObject.isEnemyCollision()) {
                            controller.checkCollision(currentObject);
                        }
                    }
                }

                // Input Translated
                int dx = 0, dy = 0;
                if (goNorth) dy -= 1;
                if (goSouth) dy += 1;
                if (goEast) dx += 1;
                if (goWest) dx -= 1;
                controller.movePlayer(new Point2D(dx, dy).normalize());
                controller.objectAtBorder(stage.getHeight(), stage.getWidth());
            }
        };

        //Menu Screen Event Handling
        gameMenu.setOnKeyPressed(event -> {
            switch (event.getCode()) {
                case ESCAPE -> stage.close();
                case ENTER -> startGame(stage, game, timer, spawner, controller);
            }
        });

        //PauseController Event Handling
        pauseController.mainMenu.setOnMouseClicked(event -> {
            //restart game
            if (event.getButton() == MouseButton.PRIMARY) {
                stage.setScene(gameMenu);
                timer.stop();
            }
        });

        pauseController.weiterlaufen.setOnMouseClicked(event -> {
            //restart game
            if (event.getButton() == MouseButton.PRIMARY) {
                stage.setScene(game);
                timer.start();
                spawner.playFromStart();
                controller.placePlayer(stage.getWidth(), stage.getHeight());
            }
        });

        //MenuController Event Handling
        menuController.start.setOnMouseClicked(event -> {
            //restart game
            if (event.getButton() == MouseButton.PRIMARY) {
                startGame(stage, game, timer, spawner, controller);
            }
        });
        menuController.exit.setOnMouseClicked(event -> {
            //restart game
            if (event.getButton() == MouseButton.PRIMARY) {
                stage.close();
            }
        });
    }
    public void startGame(Stage stage, Scene scene, AnimationTimer t, Timeline tl, GameController c){
        c.reset(stage.getWidth(), stage.getHeight());
        stage.setScene(scene);
        c.setScore(0);
        timer.start();
        t.start();
        tl.playFromStart();
    }
}
