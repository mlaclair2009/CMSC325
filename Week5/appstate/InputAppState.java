package appstate;

import characters.MyGameCharacterControl;
import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Ray;
import com.jme3.scene.Geometry;
import java.util.ArrayList;
import java.util.List;

/**
 * CMSC 325 - Homework 4
 * @author Matthew LaClair
 */
public class InputAppState extends AbstractAppState implements AnalogListener, ActionListener {
    
     private Application app;
    private InputManager inputManager;
    private MyGameCharacterControl character; //The Custom Character Control
    private float sensitivity = 5000;

    List<Geometry> targets = new ArrayList<Geometry>();
    
    public enum InputMapping{
        
        LeanLeft,
        LeanRight,
        LeanFree,
        RotateLeft,
        RotateRight,
        LookUp,
        LookDown,
        StrafeLeft,
        StrafeRight,
        MoveForward,
        MoveBackward,
        Fire;
    }
    
    private String[] mappingNames = new String[]{InputAppState.InputMapping.LeanFree.name(), InputAppState.InputMapping.LeanLeft.name(), InputAppState.InputMapping.LeanRight.name(), InputAppState.InputMapping.RotateLeft.name(), InputAppState.InputMapping.RotateRight.name(), InputAppState.InputMapping.LookUp.name(), InputAppState.InputMapping.LookDown.name(), InputAppState.InputMapping.StrafeLeft.name(), InputAppState.InputMapping.StrafeRight.name(), InputAppState.InputMapping.MoveForward.name(), InputAppState.InputMapping.MoveBackward.name()};
    
    
    @Override
    public void initialize(AppStateManager stateManager, Application app) {
        super.initialize(stateManager, app);
        this.app = app;
        this.inputManager = app.getInputManager();
        addInputMappings();
    }
    
    private void addInputMappings(){
        inputManager.addMapping(InputAppState.InputMapping.LeanFree.name(), new KeyTrigger(KeyInput.KEY_V));
        inputManager.addMapping(InputAppState.InputMapping.LeanLeft.name(), new KeyTrigger(KeyInput.KEY_Q));
        inputManager.addMapping(InputAppState.InputMapping.LeanRight.name(), new KeyTrigger(KeyInput.KEY_E));
        inputManager.addMapping(InputAppState.InputMapping.RotateLeft.name(), new MouseAxisTrigger(MouseInput.AXIS_X, true));
        inputManager.addMapping(InputAppState.InputMapping.RotateRight.name(), new MouseAxisTrigger(MouseInput.AXIS_X, false));
        inputManager.addMapping(InputAppState.InputMapping.LookUp.name(), new MouseAxisTrigger(MouseInput.AXIS_Y, false));
        inputManager.addMapping(InputAppState.InputMapping.LookDown.name(), new MouseAxisTrigger(MouseInput.AXIS_Y, true));
        inputManager.addMapping(InputAppState.InputMapping.StrafeLeft.name(), new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping(InputAppState.InputMapping.StrafeRight.name(), new KeyTrigger(KeyInput.KEY_L));
        inputManager.addMapping(InputAppState.InputMapping.MoveForward.name(), new KeyTrigger(KeyInput.KEY_I));
        inputManager.addMapping(InputAppState.InputMapping.MoveBackward.name(), new KeyTrigger(KeyInput.KEY_K));
        inputManager.addListener(this, mappingNames);
        
        inputManager.addMapping(InputAppState.InputMapping.Fire.name(), new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
    }
    
    @Override
    public void cleanup() {
        super.cleanup();
        for (InputAppState.InputMapping i : InputAppState.InputMapping.values()) {
            if (inputManager.hasMapping(i.name())) {
                inputManager.deleteMapping(i.name());
            }
        }
        inputManager.removeListener(this);
    }
    
    public void onAnalog(String action, float value, float tpf) {
        if(character != null){
            character.onAnalog(action, value * sensitivity, tpf);
        }
    }

    public void onAction(String name, boolean isPressed, float tpf) {
        if(character != null){
            
           if (name.equals("Fire")) {
               if (isPressed && character.getCooldown() == 0f){
                   fire();
               }
           } else {
               character.onAction(name, isPressed, tpf);
           }
        }
    }
    
    public void setCharacter(MyGameCharacterControl character){
        this.character = character;
    }
    
    public void setTargets(List<Geometry> targets){
        this.targets = targets;
    }
    
    public void fire(){
        if(character != null){
            Ray r = new Ray(app.getCamera().getLocation(), app.getCamera().getDirection());
            
            CollisionResults collRes = new CollisionResults();
            for(Geometry g: targets){
                g.collideWith(r, collRes);
            }
            if(collRes.size() > 0){
                System.out.println("hit");
            }
            character.onFire();
        }
    }
}
