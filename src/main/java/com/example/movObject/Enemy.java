package com.example.movObject;

import javafx.animation.Timeline;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.Timer;
import java.util.TimerTask;

public class Enemy extends MovableObject {


    public TimerTask t;

    public TimerTask getT() {
        return t;
    }

    public void setT(TimerTask t) {
        this.t = t;
    }


    public Enemy() {
        super();
        super.setName("Enemy");
        super.setShoot(false);
        super.setSpeed(1);
        super.setEnemyCollision(true);
        super.setPlayerCollision(false);
        super.setSpeed(1);
    }
}