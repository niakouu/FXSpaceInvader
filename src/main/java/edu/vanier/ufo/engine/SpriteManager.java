package edu.vanier.ufo.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Sprite manager is responsible for holding all sprite objects, and cleaning up
 * sprite objects to be removed. All collections are used by the JavaFX
 * application thread. During each cycle (animation frame) sprite management
 * occurs. This assists the user of the API to not have to create lists to later
 * be garbage collected. Should provide some performance gain.
 *
 * @author cdea
 */
public class SpriteManager {

    /**
     * All the sprite objects currently in play
     */
    private final static List<Sprite> sprites = new ArrayList<>();

    /**
     * A global single threaded list used to check collision against other
     * sprite objects.
     */
    private final static List<Sprite> collisionList = new ArrayList<>();

    /**
     * A global single threaded set used to cleanup or remove sprite objects in
     * play.
     */
    private final static Set<Sprite> spritesToBeRemoved = new HashSet<>();

    /**
     * Get the list of sprites.
     * @return a list of sprites.
     */
    public List<Sprite> getAllSprites() {
        return SpriteManager.sprites;
    }

    /**
     * VarArgs of sprite objects to be added to the game.
     *
     * @param inSprites
     */
    public void addSprites(Sprite... inSprites) {        
        SpriteManager.sprites.addAll(Arrays.asList(inSprites));
    }

    /**
     * VarArgs of sprite objects to be removed from the game.
     *
     * @param inSprites
     */
    public void removeSprites(Sprite... inSprites) {
        SpriteManager.sprites.removeAll(Arrays.asList(inSprites));
    }

    /**
     * Returns a set of sprite objects to be removed from the GAME_ACTORS.
     *
     * @return CLEAN_UP_SPRITES
     */
    public Set<Sprite> getSpritesToBeRemoved() {
        return SpriteManager.spritesToBeRemoved;
    }

    /**
     * Adds sprite objects to be removed
     *
     * @param sprites varargs of sprite objects.
     */
    public void addSpritesToBeRemoved(Sprite... sprites) {
        if (sprites.length > 1) {
            SpriteManager.spritesToBeRemoved.addAll(Arrays.asList(sprites));
        } else {
            SpriteManager.spritesToBeRemoved.add(sprites[0]);
        }
    }

    /**
     * Returns a list of sprite objects to assist in collision checks. This is a
     * temporary and is a copy of all current sprite objects (copy of
     * GAME_ACTORS).
     *
     * @return CHECK_COLLISION_LIST
     */
    public List<Sprite> getCollisionsToCheck() {
        return SpriteManager.collisionList;
    }

    /**
     * Clears the list of sprite objects in the collision check collection
     * (CHECK_COLLISION_LIST).
     */
    public void resetCollisionsToCheck() {
        SpriteManager.collisionList.clear();
        SpriteManager.collisionList.addAll(SpriteManager.sprites);
    }

    /**
     * Removes sprite objects and nodes from all temporary collections such as:
     * CLEAN_UP_SPRITES. The sprite to be removed will also be removed from the
     * list of all sprite objects called (GAME_ACTORS).
     */
    public void cleanupSprites() {

        // remove from actors list
        SpriteManager.sprites.removeAll(SpriteManager.spritesToBeRemoved);

        // reset the clean up sprites
        SpriteManager.spritesToBeRemoved.clear();
    }
}
