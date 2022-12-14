package edu.vanier.ufo.game;

import edu.vanier.ufo.helpers.ResourcesManager;
import edu.vanier.ufo.engine.Sprite;
import javafx.animation.FadeTransition;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.util.Duration;

/**
 * A spaceship with 32 directions When two atoms collide each will fade and
 * become removed from the scene. The method called implode() implements a fade
 * transition effect.
 *
 * @author cdea
 */
public class Ship extends Sprite {
    
    private final static double CONSTANT_VELOCITY = 3d;

    private boolean shieldOn;
    
    private boolean outOfBoundWidth;
    
    private boolean outOfBoundHeight;
    
    private final FadeTransition shieldFade;
    
    private int rocketNameIterationCounter;
    
    private String rocketName;

    public Ship(String imagePath, int missileId) {
        super(imagePath);
        
        this.rocketNameIterationCounter = missileId;
        this.rocketName = ResourcesManager.WEAPONS_1[rocketNameIterationCounter];
        
        this.shieldFade = getShieldFadeTransition();
        
        setUpShield();
    }

    /**
     * Change the velocity of the atom particle.
     */
    @Override
    public void update() {
        this.setTranslateX(this.getTranslateX() + this.vX);
        this.setTranslateY(this.getTranslateY() + this.vY);
    }
    
    /**
     * Update the velocity of atom particles.
     * 
     * @param wPressed
     * @param aPressed
     * @param sPressed
     * @param dPressed 
     */
    public void move(boolean wPressed, boolean aPressed, boolean sPressed, boolean dPressed) {
        if (wPressed) this.vY = -CONSTANT_VELOCITY;
        if (sPressed) this.vY = CONSTANT_VELOCITY;
        if ((!sPressed && !wPressed) || this.outOfBoundWidth) this.vY = 0;
        if (aPressed) this.vX = -CONSTANT_VELOCITY;
        if (dPressed) this.vX = CONSTANT_VELOCITY;
        if ((!aPressed && !dPressed) || this.outOfBoundHeight) this.vX = 0;
        if (this.vX == 0 && this.vY == 0) return;

        double angle = Math.atan2(this.vY, this.vX);
        this.vX = Math.cos(angle) * CONSTANT_VELOCITY;
        this.vY = Math.sin(angle) * CONSTANT_VELOCITY;

        this.imageView.rotateProperty().setValue(Math.toDegrees(angle));
    }
  
    public Missile[] fire(double mousePositionX, double mousePositionY, int size) {
        Missile[] missiles = new Missile[size];
        
        Missile fireMissile;
        Point2D centerScene = this.localToScene(
            this.collisionBond.getCenterX(),
            this.collisionBond.getCenterY()
        );
        
        double angleShip = Math.atan2(mousePositionY - centerScene.getY(), mousePositionX - centerScene.getX());
        this.imageView.rotateProperty().setValue(Math.toDegrees(angleShip));
        
        fireMissile = getMissle(this.rocketName, angleShip);
        fireMissile.getImageViewNode().rotateProperty().setValue(Math.toDegrees(angleShip) + 90);
        
        missiles[0] = fireMissile;
        
        if (size > 1) {
            fireMissile = getMissle(this.rocketName, angleShip + 0.1);
            fireMissile.getImageViewNode().rotateProperty().setValue(Math.toDegrees(angleShip + 0.1) + 90);
            missiles[1] = fireMissile;
        }
        if (size > 2) {
            fireMissile = getMissle(this.rocketName, angleShip - 0.1);
            fireMissile.getImageViewNode().rotateProperty().setValue(Math.toDegrees(angleShip - 0.1) + 90);
            missiles[2] = fireMissile;
        }
        
        return missiles;
    }

    public void shieldToggle() {
        this.collisionBond.setOpacity(.7);
        
        this.shieldFade.playFromStart();

        this.shieldOn = !this.shieldOn;
        
        if (this.shieldOn) {
            this.shieldFade.playFromStart();
            this.collisionBond.setOpacity(.7);
        } else {
            this.shieldFade.stop();
            this.collisionBond.setOpacity(0);
        }
    }
    
    public void changeWeapon() {
        String[] weapons = ResourcesManager.WEAPONS_1;
        if(++this.rocketNameIterationCounter < weapons.length) this.rocketName = weapons[this.rocketNameIterationCounter];
        else {
            this.rocketNameIterationCounter = 0;
            this.rocketName = weapons[this.rocketNameIterationCounter];
        }
    }
    
    public boolean getShieldOn() {
        return this.shieldOn;
    }
    
    public void setOutOfBoundHeight(boolean outOfBound) {
        this.outOfBoundHeight = outOfBound;
    }
    
    public void setOutOfBoundWidth(boolean outOfBound) {
        this.outOfBoundWidth = outOfBound;
    }
    
    private FadeTransition getShieldFadeTransition() {
        FadeTransition shieldTransition = new FadeTransition();
        shieldTransition.setFromValue(1);
        shieldTransition.setToValue(.40);
        shieldTransition.setDuration(Duration.millis(1000));
        shieldTransition.setCycleCount(12);
        shieldTransition.setAutoReverse(true);
        shieldTransition.setNode(this.collisionBond);
        shieldTransition.setOnFinished((ActionEvent actionEvent) -> {
            this.shieldOn = false;
            this.collisionBond.setOpacity(0);
            shieldTransition.stop();
        });
        return shieldTransition;
    }
    
    private Missile getMissle(String filepath, double angle) {
        Missile fireMissile = new Missile(filepath);
        double offsetX = (this.imageView.getBoundsInLocal().getWidth() - fireMissile.getBoundsInLocal().getWidth()) / 2;
        double offsetY = (this.imageView.getBoundsInLocal().getHeight() - fireMissile.getBoundsInLocal().getHeight()) / 2;
        
        fireMissile.setVelocityX(Math.cos(angle) * CONSTANT_VELOCITY);
        fireMissile.setVelocityY(Math.sin(angle) * CONSTANT_VELOCITY);
        fireMissile.setTranslateX(getTranslateX() + (offsetX + fireMissile.getVelocityX()));
        fireMissile.setTranslateY(getTranslateY() + (offsetY + fireMissile.getVelocityY()));
        
        return fireMissile;
    }
    
    private void setUpShield() {
        this.collisionBond.setStrokeWidth(5);
        this.collisionBond.setStroke(Color.LIMEGREEN);
        this.collisionBond.setFill(new ImagePattern(
                new Image(ResourcesManager.SHIELD)
        ));
    }
}