package com.example.movObject;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MovableObject {
    private boolean playerCollision = false;
    private double speed = 1;
    private double height = 32;
    private double width = 32;
    private ImageView view;
    private String name;
    private String url = "file:src/main/resources/assets/Enemy001.png";

    private boolean shoot = false;

    private boolean enemyCollision = false;

    public MovableObject(String name, ImageView view) {
        this.name = name;
        this.view = view;
    }

    public MovableObject() {

    }

    public void setName(String name) {
        this.name = name;
    }

    public Image getUrl() {
        return new Image(url);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public double getSpeed() {
        return speed;
    }

    public void setSpeed(double speed) {
        this.speed = speed;
    }

    public double getHeight() {
        return height;
    }

    public void setHeight(double height) {
        this.height = height;
    }

    public double getWidth() {
        return width;
    }

    public void setWidth(double width) {
        this.width = width;
    }

    public ImageView getView() {
        return view;
    }

    public void setView(ImageView view) {
        this.view = view;
    }

    public boolean isShoot() {
        return shoot;
    }

    public void setShoot(boolean shoot) {
        this.shoot = shoot;
    }

    public boolean isEnemyCollision() {
        return !enemyCollision;
    }

    public void setEnemyCollision(boolean enemyCollision) {
        this.enemyCollision = enemyCollision;
    }

    public boolean isPlayerCollision() {
        return playerCollision;
    }

    public void setPlayerCollision(boolean playerCollision) {
        this.playerCollision = playerCollision;
    }

    @Override
    public String toString() {
        return "MovableObject{" + "name='" + name + '\'' + '}';
    }
}
