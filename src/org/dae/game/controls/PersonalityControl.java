package org.dae.game.controls;

import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Koen Samyn
 */
public class PersonalityControl implements Control, IControlDebug {

    private float bully;
    private float friendly;
    private Spatial parent;

    public PersonalityControl() {
    }

    public PersonalityControl(float bully, float friendly) {
        this.bully = bully;
        this.friendly = friendly;
    }

    @Override
    public Control cloneForSpatial(Spatial spatial) {
        return new PersonalityControl(bully, friendly);
    }

    @Override
    public void setSpatial(Spatial spatial) {
        this.parent = spatial;
    }

    @Override
    public void update(float tpf) {
    }

    @Override
    public void render(RenderManager rm, ViewPort vp) {
    }

    @Override
    public void write(JmeExporter ex) throws IOException {
        OutputCapsule oc = ex.getCapsule(this);
        oc.write(bully, "bully", 0f);
        oc.write(friendly, "friendly", 0f);
    }

    @Override
    public void read(JmeImporter im) throws IOException {
        InputCapsule ic = im.getCapsule(this);
        bully = ic.readFloat("bully", 0f);
        friendly = ic.readFloat("friendly", 0f);
        
        Logger.getLogger("DArtE").log(Level.INFO, "Setting bully to {0} and friendly to {1}", new Object[]{bully, friendly});
    }
    private StringBuilder builder = new StringBuilder();

    /**
     * Returns a string with the debug info.
     *
     * @return the debug info of this control.
     */
    @Override
    public String getDebugInfo() {
        builder.delete(0, builder.length());
        builder.append("bully:");
        builder.append(bully);
        builder.append("\n");
        builder.append("friendly:");
        builder.append(friendly);
        builder.append("\n");
        return builder.toString();
    }
    
    @Override
    public Spatial getParent(){
        return parent;
    }
}
