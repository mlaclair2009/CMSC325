package mygame;

import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

/**
 * test
 * @author Matthew LaClair
 * CMSC 325 - Project 1
 * 
 */
public class Main extends SimpleApplication {
    
    protected BulletAppState bulletAppState;
    private Vector3f normalGravity = new Vector3f(0, -9.8f, 0);
    private Vector3f defCam = new Vector3f(10,5,15);

    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

    @Override
    //Runs applicatoin
    public void simpleInitApp() {
   
        //Creates car scene
        Spatial scene = assetManager.loadModel("Scenes/Scene.j3o");
        rootNode.attachChild(scene);
        
        //Set default cam location
        cam.setLocation(defCam);
      
        //Allows for the use of Physics simulation
        bulletAppState = new BulletAppState(); 
        stateManager.attach(bulletAppState);
        
        //Allows for viewing of mesh/geometry for troubleshooting
       //bulletAppState.setDebugEnabled(true);
        
        //Lighting
        AmbientLight alight = new AmbientLight();
        alight.setColor(ColorRGBA.White);
        rootNode.addLight(alight);
        DirectionalLight dlight = new DirectionalLight();
        dlight.setColor(ColorRGBA.White);
        dlight.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
        rootNode.addLight(dlight);
        
        //Create the Physics World based on the Helper class
        PhysicsTestHelper.createPhysics (rootNode, assetManager, bulletAppState.getPhysicsSpace());
        
        //Add the Player1 to the world and use the customer character and input control classes
        Node playerNode1 = (Node)assetManager.loadModel("Models/Sinbad/Sinbad.mesh.xml");
        MyGameCharacterControl charControl1 = new MyGameCharacterControl(8f, 30f, 80);
        charControl1.setCamera(cam);
        playerNode1.addControl(charControl1);
        charControl1.setGravity(normalGravity);
        bulletAppState.getPhysicsSpace().add(charControl1);
        InputAppState appState1 = new InputAppState();
        appState1.setCharacter(charControl1);
        stateManager.attach(appState1);
        rootNode.attachChild(playerNode1);
        charControl1.warp(new Vector3f(15, 10,0));

        
        //Add the Player2 to the world and use the customer character and input control classes
        Node playerNode2 = (Node)assetManager.loadModel("Models/Oto/Oto.mesh.xml");
        MyGameCharacterControl charControl2 = new MyGameCharacterControl(5, 10, 80);
        charControl2.setCamera(cam);
        playerNode2.addControl(charControl2);
        charControl2.setGravity(normalGravity);
        bulletAppState.getPhysicsSpace().add(charControl2);  
        InputAppState appState2 = new InputAppState();
        appState2.setCharacter(charControl2);
        stateManager.attach(appState2);
        rootNode.attachChild(playerNode2);
        charControl2.warp(new Vector3f(30, 10,15));
        
        //Add the "bullets" to the scene to allow the player to shoot the balls
        PhysicsTestHelper.createBallShooter(this,rootNode,bulletAppState.getPhysicsSpace());
        
        //Add a custom font and text to the scene
        BitmapFont myFont = assetManager.loadFont("Interface/Fonts/SimSun-ExtB.fnt");
        BitmapText hudText = new BitmapText(myFont, true);
        hudText.setText("Here we Go !!!!!\n\n\t\t+");
        hudText.setColor(ColorRGBA.White);
        hudText.setSize(30);
        
        //Set the text in the middle of the screen
        hudText.setLocalTranslation(settings.getWidth() /2 , settings.getHeight() / 2 + hudText.getLineHeight(), 0f); //Positions text to middle of screen
        guiNode.attachChild(hudText);
        
        
        //Creates cartoon border & fog filters
        FilterPostProcessor processor1 = (FilterPostProcessor) assetManager.loadAsset ("Effects/Filter1.j3f");
        viewPort.addProcessor(processor1);
        
    }
}
