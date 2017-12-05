package org.dae.game.debug;

import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;
import org.dae.game.controls.IControlDebug;

/**
 * Attaches debugging information to the control (or the parent of the control). The debugging
 * information will be shown as a billboard.
 * @author Koen Samyn
 */
public class ControlDebug  {
    private IControlDebug control;
    private Spatial spatial;
    
    public ControlDebug(Control control)
    {
        if ( control instanceof IControlDebug){
            this.control = (IControlDebug)control;
            
        }
    }
}
