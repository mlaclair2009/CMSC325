package mygame;

import animations.AdvAnimationManagerControl;
import animations.CharacterInputAnimationAppState;
import characters.NavMeshNavigationControl;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import characters.AICharacterControl;
import characters.ChaseCamCharacter;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Cylinder;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Torus;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * CMSC 325 - Final Project
 * @author Matthew LaClair
 */

public class Main extends SimpleApplication {

    private BulletAppState bulletAppState;
    private Vector3f move = (new Vector3f(15f, 15f, 15f));
    private Vector3f normalGravity = new Vector3f(0, -9.81f, 0);
    private List <Spatial> AIAvoidObjects;
    private List<Spatial> enemies;
    public int levelTime = 300;
    public BitmapText timeText;
    public BitmapText hudText;
    public BitmapText instrText;
    
    public static void main(String[] args) {
        Main app = new Main();
        app.start();
    }

   @Override
    public void simpleInitApp() {
        bulletAppState = new BulletAppState();
        stateManager.attach(bulletAppState);
        getFlyByCamera().setMoveSpeed(45f);
        cam.setLocation(new Vector3f(20, 20, 20));
        cam.lookAt(new Vector3f(0,0,0), Vector3f.UNIT_Y);
//        flyCam.setEnabled(false);
        Node scene = setupWorld();
        DirectionalLight l = new DirectionalLight();
        rootNode.addLight(l);
        
        //Clear default screen information
        setDisplayStatView(false);
        setDisplayFps(false);
                
        //Add HUD with directions
        BitmapFont myFont = assetManager.loadFont("Interface/Fonts/SimSun-ExtB.fnt");
        instrText = new BitmapText(myFont, true);
        instrText.setColor(ColorRGBA.Blue);
        instrText.setSize(30);
        instrText.setText("Controls: WASD\nLeft Mouse Fires\nBox = 1pt, Ball = 2pt, Cylinder = 3pt, Ring = 4pt, AI = 5pt");
        instrText.setLocalTranslation(0,100, 0);
        guiNode.attachChild(instrText);
        
        //Timer HUD (updated by simpleUpdate)
        timeText = new BitmapText(myFont, true);
        timeText.setColor(ColorRGBA.Green);
        timeText.setSize(30);
        timeText.setLocalTranslation(1200,750, 0);
        guiNode.attachChild(timeText);
        
        
        hudText = new BitmapText(myFont, true);
        hudText.setColor(ColorRGBA.Red);
        hudText.setSize(60);
        hudText.setLocalTranslation(settings.getWidth() /2 , settings.getHeight() / 2 + hudText.getLineHeight(), 0f);
        guiNode.attachChild(hudText);
        
        //Create enemy objects & add to a list
        AIAvoidObjects = createObjects(scene);
        
        //Create AI enemy
        Node enemyAI = setupAIEnemy(scene);
        
        //Add all objects to enemy list for player
        enemies = new ArrayList <Spatial>(AIAvoidObjects);
        
        //Add enemyAI to a enemy list for player
        enemies.add(enemyAI);
        
////        //Create main player
//        Node player = createPlayerCharacter(scene);
//        ChaseCamera chaseCam = new ChaseCamera(cam, player, inputManager);
//        chaseCam.setSmoothMotion(true);
//        chaseCam.setDragToRotate(true);
//        chaseCam.setLookAtOffset(Vector3f.UNIT_Y.mult(6));
        
        //Create ball shooter
        ChaseCamCharacter.createBallShooter(this,rootNode,bulletAppState.getPhysicsSpace(), enemies);
        
        //Game timer
        simpleUpdate(0);       
    }
    
   //Create world
    private Node setupWorld(){
        Node scene = (Node) assetManager.loadModel("Scenes/newScene.j3o");
        
        rootNode.attachChild(scene);
  
        Spatial terrain = scene.getChild("terrain-newScene");
        terrain.addControl(new RigidBodyControl(0));
        
        //Add bounciness/remove friction/set gravity
        terrain.getControl(RigidBodyControl.class).setGravity(normalGravity);
        terrain.getControl(RigidBodyControl.class).setRestitution(.5f);
        terrain.getControl(RigidBodyControl.class).setFriction(1);
        bulletAppState.getPhysicsSpace().addAll(terrain);
             
        return scene;
    }

    
     private Node setupAIEnemy(Node scene){
        
        //Load model, attach to Enemy node
        Node aiEnemy = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");
        AICharacterControl physicsCharacter = new AICharacterControl(0.3f, 2.5f, 8f);
        aiEnemy.addControl(physicsCharacter);
        bulletAppState.getPhysicsSpace().add(physicsCharacter);
        aiEnemy.setLocalTranslation(0, 10, 0);
        aiEnemy.setLocalScale(2f);
        scene.attachChild(aiEnemy);
        NavMeshNavigationControl navMesh = new NavMeshNavigationControl((Node) scene);
        aiEnemy.addControl(navMesh);
        
        //Add list of objects to avoid
        aiEnemy.getControl(NavMeshNavigationControl.class).setObjectList(AIAvoidObjects);
        
        //Waypoint for enemy
        navMesh.moveTo(new Vector3f(-30, -50, 80));
        return aiEnemy;
     }
        
     public List <Spatial> createObjects(Node scene){
        
        //Material for spheres
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));
        
        //Add movable spheres/set all characteristics
        Sphere sphere1 = new Sphere(10, 10, 5f);
        Geometry ballGeometry1 = new Geometry("ball1", sphere1);
 
        Box box1 = new Box(4, 4, 4f);
        Geometry boxGeometry1 = new Geometry("box1", box1);
        
        Cylinder cylinder1 = new Cylinder(20, 50, 5, 5, true);
        Geometry cylinderGeometry1 = new Geometry("cylinder1", cylinder1);
        
        Torus torus1 = new Torus(64, 48, 1.0f, 3.0f);
        Geometry torusGeometry1 = new Geometry("torus1", torus1);
        
        ballGeometry1.setMaterial(material);
        ballGeometry1.setLocalTranslation(-15, 5, 5);
        ballGeometry1.addControl(new RigidBodyControl(.001f));
        ballGeometry1.getControl(RigidBodyControl.class).setRestitution(.5f);
        ballGeometry1.getControl(RigidBodyControl.class).setFriction(0);
        ballGeometry1.getControl(RigidBodyControl.class).setSleepingThresholds(0,0);
        ballGeometry1.getControl(RigidBodyControl.class).setLinearVelocity(move);
        scene.attachChild(ballGeometry1);
        bulletAppState.getPhysicsSpace().add(ballGeometry1);
        
        boxGeometry1.setMaterial(material);
        boxGeometry1.setLocalTranslation(15, 20, 30);
        boxGeometry1.addControl(new RigidBodyControl(.001f));
        boxGeometry1.getControl(RigidBodyControl.class).setRestitution(.5f);
        boxGeometry1.getControl(RigidBodyControl.class).setFriction(0);
        boxGeometry1.getControl(RigidBodyControl.class).setSleepingThresholds(0,0);
        boxGeometry1.getControl(RigidBodyControl.class).setLinearVelocity(move);
        scene.attachChild(boxGeometry1);
        bulletAppState.getPhysicsSpace().add(boxGeometry1); 
        
        cylinderGeometry1.setMaterial(material);
        cylinderGeometry1.setLocalTranslation(-40, 5, 0);
        cylinderGeometry1.addControl(new RigidBodyControl(.001f));
        cylinderGeometry1.getControl(RigidBodyControl.class).setRestitution(.5f);
        cylinderGeometry1.getControl(RigidBodyControl.class).setFriction(0);
        cylinderGeometry1.getControl(RigidBodyControl.class).setSleepingThresholds(0,0);
        cylinderGeometry1.getControl(RigidBodyControl.class).setLinearVelocity(move);
        scene.attachChild(cylinderGeometry1);
        bulletAppState.getPhysicsSpace().add(cylinderGeometry1);
        
        torusGeometry1.setMaterial(material);
        torusGeometry1.setLocalTranslation(-20, 30, -60);
        torusGeometry1.addControl(new RigidBodyControl(.001f));
        torusGeometry1.getControl(RigidBodyControl.class).setRestitution(.5f);
        torusGeometry1.getControl(RigidBodyControl.class).setFriction(0);
        torusGeometry1.getControl(RigidBodyControl.class).setSleepingThresholds(0,0);
        torusGeometry1.getControl(RigidBodyControl.class).setLinearVelocity(move);
        scene.attachChild(torusGeometry1);
        bulletAppState.getPhysicsSpace().add(torusGeometry1);
        
        //Create list
        List<Spatial> objects = new ArrayList<Spatial>();
              
        //Add objects to list of objects to be avoided by AI
        objects.add(ballGeometry1);
        objects.add(boxGeometry1);
        objects.add(cylinderGeometry1);
        objects.add(torusGeometry1);
        
        return objects;   
    }
     
     public Node createPlayerCharacter(Node scene) {  
        stateManager.attach(bulletAppState);
        Node playerNode = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");
        playerNode.setLocalTranslation(12f, 0, 8f);
        ChaseCamCharacter charControl = new ChaseCamCharacter(1f, 2.5f, 8f);
        charControl.setGravity(normalGravity);
        charControl.setCamera(cam);
        playerNode.addControl(charControl);
        bulletAppState.getPhysicsSpace().add(charControl);
        scene.attachChild(playerNode);
        
        CharacterInputAnimationAppState appState = new CharacterInputAnimationAppState();
        appState.addActionListener(charControl);
        appState.addAnalogListener(charControl);
        
        stateManager.attach(appState);
        inputManager.setCursorVisible(false);
        
        AdvAnimationManagerControl animControl = new AdvAnimationManagerControl("animations/resources/animations-jaime.properties");
        playerNode.addControl(animControl);
        appState.addActionListener(animControl);
        appState.addAnalogListener(animControl);
        
        return playerNode;
     }
     

      public void simpleUpdate(float tpf) {
  
        if (levelTime > 0){
            try{
                Thread.sleep(1000);
                levelTime -= 1; 
                timeText.setText("Timer: " + levelTime);
                hudText.setText("+");
        } catch (InterruptedException e) {
            
        }
      } else {
            
            timeText.setText("GAME OVER!!!!");
            instrText.setText("GAME OVER!!!!");
            hudText.setText("High Score: " + String.valueOf(displayHighScore()));
            rootNode.detachAllChildren();
        }     
    }

    public Integer displayHighScore() {
        try {
            FileReader fileReader = new FileReader(new File("highscore.txt"));
            BufferedReader reader = new BufferedReader(fileReader);
            String line = reader.readLine();
            return Integer.valueOf(line);
        } catch (FileNotFoundException e) {e.printStackTrace();
        } catch (IOException e) {e.printStackTrace();}
        return 0;
    }
    

    
    public void destroy(Spatial object){
            rootNode.detachChild(object);
    }
}




   