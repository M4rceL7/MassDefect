package com.example.movObject;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class Projectile extends MovableObject {

    public Projectile() {
        super();
        super.setName("Projectile");
        super.setUrl("file:src/main/resources/com/example/assets/art/projectile.png");
        super.setSpeed(-2);
        super.setView(new ImageView(new Image("file:src/main/resources/com/example/assets/art/projectile.png")));
    }
}
