package AI;

import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.control.AbstractControl;

/**
 * CMSC 325 - Homework 4
 * @author Matthew LaClair
 */

public class SoundEmitterControl extends AbstractControl{

    private Vector3f lastPosition = new Vector3f();
    private float noiseEmitted = 0f;
    private float maxSpeed = 25f;
    
    @Override
    protected void controlUpdate(float tpf) {
        float movementSpeed = lastPosition.distance(spatial.getWorldTranslation()) / tpf;
        if(movementSpeed > 0f){
            movementSpeed = Math.min(movementSpeed, maxSpeed);
            noiseEmitted = movementSpeed / maxSpeed;
        } else {
            noiseEmitted = 0f;
        }
        lastPosition.set(spatial.getWorldTranslation());
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }
    
    public float getNoiseEmitted(){
        return noiseEmitted;
    }
}
