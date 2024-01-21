package com.example.movObject;

import javafx.scene.image.ImageView;

public class Player extends MovableObject {

    private String url = "file:src/main/resources/assets/art/Enemy001.png";

    public Player(ImageView playerView) {
        super();
        super.setView(playerView);
        super.setName("Player");
        super.setSpeed(3);
        super.setPlayerCollision(true);
        super.setShoot(true);
        super.setHeight(24);
        super.setWidth(24);
    }
}
