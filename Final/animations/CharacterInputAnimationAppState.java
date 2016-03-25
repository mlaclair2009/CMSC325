
package animations;


import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.input.ChaseCamera;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
//import chapter02.control.ChaseCamCharacter;
import java.util.ArrayList;
import java.util.List;

/**
 * CMSC 325 - Final Project
 * @author Matthew LaClair
 */

public class CharacterInputAnimationAppState extends AbstractAppState implements AnalogListener, ActionListener{
    
    private Application app;
    private InputManager inputManager;
    private float sensitivity = 100;
    
    private List<ActionListener> actionListeners = new ArrayList<ActionListener>();
    private List<AnalogListener> analogListeners = new ArrayList<AnalogListener>();
    
    /**
     * 
     */
    private ChaseCamera chaseCam;
    /**
     * 
     */

    public enum InputMapping{
        LeanLeft,
        LeanRight,
        LeanFree,
        ToggleCover,
        RotateLeft,
        RotateRight,
        LookUp,
        LookDown,
        StrafeLeft,
        StrafeRight,
        MoveForward,
        MoveBackward,
        Fire,
        Jump;
    }
    
    private String[] mappingNames = new String[]{InputMapping.Fire.name(), InputMapping.LeanFree.name(), InputMapping.LeanLeft.name(), InputMapping.LeanRight.name(),InputMapping.ToggleCover.name(), InputMapping.RotateLeft.name(), InputMapping.RotateRight.name(), InputMapping.LookUp.name(), InputMapping.LookDown.name(), InputMapping.StrafeLeft.name(),
        InputMapping.StrafeRight.name(), InputMapping.MoveForward.name(), InputMapping.MoveBackward.name(), ChaseCamera.ChaseCamUp, ChaseCamera.ChaseCamDown, ChaseCamera.ChaseCamMoveLeft, ChaseCamera.ChaseCamMoveRight, InputMapping.Jump.name()};
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = app;
        this.inputManager = app.getInputManager();
        addInputMappings();
     }
    
    private void addInputMappings(){
        inputManager.addMapping(InputMapping.LeanFree.name(), new KeyTrigger(KeyInput.KEY_V));
        inputManager.addMapping(InputMapping.LeanLeft.name(), new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping(InputMapping.LeanRight.name(), new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping(InputMapping.StrafeLeft.name(), new KeyTrigger(KeyInput.KEY_A));
        inputManager.addMapping(InputMapping.StrafeRight.name(), new KeyTrigger(KeyInput.KEY_D));
        inputManager.addMapping(InputMapping.MoveForward.name(), new KeyTrigger(KeyInput.KEY_W));
        inputManager.addMapping(InputMapping.MoveBackward.name(), new KeyTrigger(KeyInput.KEY_S));
        inputManager.addMapping(InputMapping.Jump.name(), new KeyTrigger(KeyInput.KEY_SPACE));
        inputManager.addMapping(InputMapping.Fire.name(), new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        inputManager.addListener(this, mappingNames);
        
    }
    
   

    @Override
    public void cleanup() {
        super.cleanup();
        for (InputMapping i : InputMapping.values()) {
            if (inputManager.hasMapping(i.name())) {
                inputManager.deleteMapping(i.name());
            }
        }
        inputManager.removeListener(this);
    }
    
    public void onAnalog(String name, float value, float tpf) {
        for(AnalogListener analogListener: analogListeners){
            analogListener.onAnalog(name, value * sensitivity, tpf);
        }
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        for(ActionListener actionListener: actionListeners){
            actionListener.onAction(name, isPressed, tpf);
        }
    }
    
    public void setChaseCamera(ChaseCamera cam){
        this.chaseCam = cam;
    }
  
    public void addActionListener(ActionListener listener){
        actionListeners.add(listener);
    }
    
    public void addAnalogListener(AnalogListener listener){
        analogListeners.add(listener);
    }
    
    public void removeActionListener(ActionListener listener){
        actionListeners.remove(listener);
    }
    
    public void removeAnalogListener(AnalogListener listener){
        analogListeners.remove(listener);
    }
}
