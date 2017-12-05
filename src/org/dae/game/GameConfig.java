package org.dae.game;

import java.util.ArrayList;

/**
 * Describes the game configuration.
 * @author Koen Samyn
 */
public class GameConfig {
    private ArrayList<String> assetFolders = 
            new ArrayList<>();
    
    /**
     * Adds an asset folder to the game configuration file.
     * @param file the asset folder to add.
     */
    public void addAssetFolder(String file) {
        assetFolders.add(file);
    }
}
