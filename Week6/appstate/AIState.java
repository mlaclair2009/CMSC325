package appstate;

import AI.AIControl_SM;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;

/**
 * CMSC 325 - Project 2
 * @author Matthew LaClair
 */

public abstract class AIState extends AbstractControl{
    
    protected AIControl_SM aiControl;
    
    public abstract void stateEnter();
    
    public abstract void stateExit();

    @Override
    public void setSpatial(Spatial spatial) {
        super.setSpatial(spatial);
        
        aiControl = this.spatial.getControl(AIControl_SM.class);
    }
    
    

    @Override
    public void setEnabled(boolean enabled) {
        if(enabled && !this.enabled){
            stateEnter();
        }else if(!enabled && this.enabled){
            stateExit();
        }
        this.enabled = enabled;
        System.out.println("State " + this + " " + enabled);
    }
    
    
}
