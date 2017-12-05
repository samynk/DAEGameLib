package org.dae.game;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.BetterCharacterControl;
import com.jme3.input.ChaseCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.system.AppSettings;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Creates a new standard game.
 *
 * @author Koen Samyn
 */
public class DAEGame extends SimpleApplication implements ActionListener {

    /**
     * The current level to show.
     */
    private Spatial currentLevel;
    /**
     * The location of the level.
     */
    private String levelLocation;
    /**
     * The assetfolder
     */
    private String assetFolders;
    /**
     *
     * @param assetFolders
     * @param scene
     */
    private ChaseCamera chaseCam;
    private boolean left, right, up, down;
    private Vector3f walkDirection = new Vector3f();
    private Spatial mainChar;
    private AnimChannel mainCharAC;

    public DAEGame(String assetFolders, String scene) {
        this.assetFolders = assetFolders;
        levelLocation = scene;
    }

    /**
     * Initialize the game.
     */
    @Override
    public void simpleInitApp() {
        String folders[] = assetFolders.split(";");
        for (String folder : folders) {
            Logger.getLogger("DArtE").log(Level.INFO, "Adding asset folder : {0}", folder);
            assetManager.registerLocator(folder, FileLocator.class);
        }

        BulletAppState bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);

        if (levelLocation != null) {

            currentLevel = assetManager.loadModel(levelLocation);
            rootNode.attachChild(currentLevel);
            if (currentLevel instanceof Node) {
                Node currentLevelNode = (Node) currentLevel;
                List<Spatial> cameras = currentLevelNode.descendantMatches("^camera.+$");
                Logger.getLogger("DArtE").log(Level.INFO, "Number of cameras: {0}", cameras.size());


                for (Spatial sp : cameras) {
                    Logger.getLogger("DArtE").log(Level.INFO, "Start cam user property : {0}", sp.getUserData("startcam"));
                    Boolean startCam = sp.getUserData("startcam");
                    if ( startCam == true) {
                        cam.setLocation(sp.getWorldTranslation());
                        cam.setRotation(sp.getWorldRotation());
                    }
                }

                Spatial character = currentLevelNode.getChild("Mesh5");
                if (character != null) {

                    AnimControl ac = character.getControl(AnimControl.class);
                    Logger.getLogger("DArtE").log(Level.INFO, "character != null and character has animation control {0}", ac != null);
                    Logger.getLogger("DArtE").log(Level.INFO, "character has animation channel : {0}", ac.getNumChannels());
                    mainCharAC = ac.createChannel();
                    mainCharAC.setAnim("idle1");

                    flyCam.setEnabled(false);
                    chaseCam = new ChaseCamera(cam, character, inputManager);
                    chaseCam.setUpVector(Vector3f.UNIT_Y);
                    chaseCam.setLookAtOffset(new Vector3f(0, 1.8f, 0));
                    mainChar = character;
                    setupKeys();
                }

                List<TerrainQuad> terrains = ((Node) currentLevel).descendantMatches(TerrainQuad.class);
                for (TerrainQuad quad : terrains) {
                    quad.addControl(new TerrainLodControl(quad, cam));
                }
            }
            bulletAppState.getPhysicsSpace().addAll(currentLevel);
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (mainChar == null) {
            return;
        }
        BetterCharacterControl character = mainChar.getControl(BetterCharacterControl.class);
        //Logger.getLogger("DArtE").log(Level.INFO, "character control is enabled : {0}", character.isEnabled());
        Vector3f camDir = cam.getDirection().clone().multLocal(1.0f);
        Vector3f camLeft = cam.getLeft().clone().multLocal(1.0f);
        camDir.y = 0;
        camLeft.y = 0;
        walkDirection.set(0, 0, 0);
        if (left) {
            walkDirection.addLocal(camLeft);
        }
        if (right) {
            walkDirection.addLocal(camLeft.negate());
        }
        if (up) {
            walkDirection.addLocal(camDir);
        }
        if (down) {
            walkDirection.addLocal(camDir.negate());
        }


        if (walkDirection.length() == 0) {
            if (!"idle1".equals(mainCharAC.getAnimationName())) {
                mainCharAC.setAnim("idle1", 1f);
            }
        } else {
            character.setViewDirection(walkDirection);
            if (!"walk".equals(mainCharAC.getAnimationName())) {
                mainCharAC.setAnim("walk", 0.7f);
            }
        }
        Logger.getLogger("DArtE").log(Level.INFO, "Setting walk direction to {0}", walkDirection);
        character.setWalkDirection(walkDirection);
    }

    private void setupKeys() {
        inputManager.addMapping("wireframe", new KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(this, new String[]{"wireframe"});
        inputManager.addMapping("CharLeft", new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping("CharRight", new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping("CharUp", new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping("CharDown", new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping("CharSpace", new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addMapping("CharShoot", new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addListener(this, new String[]{"CharLeft", "CharRight", "CharUp", "CharDown", "CharSpace", "CharShoot"});
    }

    /**
     * Starts the dae game.
     *
     * @param args the arguments
     */
    public static void main(String[] args) {
        String assetpath = null, scene = null;
        if (args.length > 0) {
            assetpath = args[0];
        }

        if (args.length > 1) {
            scene = args[1];
        }

        DAEGame app = new DAEGame(assetpath, scene);
        app.setShowSettings(false);
        AppSettings gameSettings = null;
        gameSettings = new AppSettings(false);
        gameSettings.setResolution(1280, 720);
        gameSettings.setFullscreen(false);
        gameSettings.setVSync(true);
        gameSettings.setRenderer("LWJGL-OpenGL2");
        gameSettings.setTitle("Rig -- Procedural animations");
        gameSettings.setUseInput(true);
        gameSettings.setSamples(4);
        app.settings = gameSettings;
        app.start();
    }

    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("CharLeft")) {
            if (value) {
                left = true;
            } else {
                left = false;
            }
        } else if (binding.equals("CharRight")) {
            if (value) {
                right = true;
            } else {
                right = false;
            }
        } else if (binding.equals("CharUp")) {
            if (value) {
                up = true;
            } else {
                up = false;
            }
        } else if (binding.equals("CharDown")) {
            if (value) {
                down = true;
            } else {
                down = false;
            }
        } else if (binding.equals("CharSpace")) {
            //character.jump();
        } else if (binding.equals("CharShoot") && !value) {
            //bulletControl();
        }
    }
}