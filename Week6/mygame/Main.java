package mygame;

import characters.NavMeshNavigationControl;
import com.jme3.app.SimpleApplication;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.DirectionalLight;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import characters.AICharacterControl;
import com.jme3.material.Material;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Sphere;
import java.util.ArrayList;
import java.util.List;

/**
 * CMSC 325 - Project 2
 * @author Matthew LaClair
 */

public class Main extends SimpleApplication {

    private BulletAppState bulletAppState;
    private Vector3f move = (new Vector3f(15f, 15f, 15f));
    
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
        Node scene = setupWorld();
        
        DirectionalLight l = new DirectionalLight();
        rootNode.addLight(l);
        setupCharacter(scene);
    }
    
   //Create world
    private Node setupWorld(){
        Node scene = (Node) assetManager.loadModel("Scenes/newScene.j3o");
        
        rootNode.attachChild(scene);
  
        Spatial terrain = scene.getChild("terrain-newScene");
        terrain.addControl(new RigidBodyControl(0));
        
        //Add bounciness/remove friction
        terrain.getControl(RigidBodyControl.class).setRestitution(.5f);
        terrain.getControl(RigidBodyControl.class).setFriction(0);
        bulletAppState.getPhysicsSpace().addAll(terrain);
        
             
        return scene;
    }

     private void setupCharacter(Node scene){
        //Material for spheres
        Material material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));
        
        //Load model, attach to character node
        Node aiCharacter = (Node) assetManager.loadModel("Models/Jaime/Jaime.j3o");
        AICharacterControl physicsCharacter = new AICharacterControl(0.3f, 2.5f, 8f);
        aiCharacter.addControl(physicsCharacter);
        bulletAppState.getPhysicsSpace().add(physicsCharacter);
        aiCharacter.setLocalTranslation(0, 10, 0);
        aiCharacter.setLocalScale(2f);
        scene.attachChild(aiCharacter);
        NavMeshNavigationControl navMesh = new NavMeshNavigationControl((Node) scene);
        aiCharacter.addControl(navMesh);
        
        //Waypoint for character
        navMesh.moveTo(new Vector3f(80, 50, 60));
        
        //Add movable spheres/set all characteristics
        Sphere sphere1 = new Sphere(10, 10, 5f);
        Geometry ballGeometry1 = new Geometry("ball1", sphere1);
 
        Sphere sphere2 = new Sphere(10, 10, 5f);
        Geometry ballGeometry2 = new Geometry("ball2", sphere2);
        
        Sphere sphere3 = new Sphere(10, 10, 5f);
        Geometry ballGeometry3 = new Geometry("ball3", sphere3);
        
        Sphere sphere4 = new Sphere(10, 10, 5f);
        Geometry ballGeometry4 = new Geometry("ball4", sphere4);
        
        ballGeometry1.setMaterial(material);
        ballGeometry1.setLocalTranslation(5, 5, 5);
        ballGeometry1.addControl(new RigidBodyControl(.001f));
        ballGeometry1.getControl(RigidBodyControl.class).setRestitution(.5f);
        ballGeometry1.getControl(RigidBodyControl.class).setFriction(0);
        ballGeometry1.getControl(RigidBodyControl.class).setSleepingThresholds(0,0);
        ballGeometry1.getControl(RigidBodyControl.class).setLinearVelocity(move);
        rootNode.attachChild(ballGeometry1);
        bulletAppState.getPhysicsSpace().add(ballGeometry1);
        
        ballGeometry2.setMaterial(material);
        ballGeometry2.setLocalTranslation(5, 5, 15);
        ballGeometry2.addControl(new RigidBodyControl(.001f));
        ballGeometry2.getControl(RigidBodyControl.class).setRestitution(.5f);
        ballGeometry2.getControl(RigidBodyControl.class).setFriction(0);
        ballGeometry2.getControl(RigidBodyControl.class).setSleepingThresholds(0,0);
        ballGeometry2.getControl(RigidBodyControl.class).setLinearVelocity(move);
        rootNode.attachChild(ballGeometry2);
        bulletAppState.getPhysicsSpace().add(ballGeometry2); 
        
        ballGeometry3.setMaterial(material);
        ballGeometry3.setLocalTranslation(40, 5, 20);
        ballGeometry3.addControl(new RigidBodyControl(.001f));
        ballGeometry3.getControl(RigidBodyControl.class).setRestitution(.5f);
        ballGeometry3.getControl(RigidBodyControl.class).setFriction(0);
        ballGeometry3.getControl(RigidBodyControl.class).setSleepingThresholds(0,0);
        ballGeometry3.getControl(RigidBodyControl.class).setLinearVelocity(move);
        rootNode.attachChild(ballGeometry3);
        bulletAppState.getPhysicsSpace().add(ballGeometry3);
        
        ballGeometry4.setMaterial(material);
        ballGeometry4.setLocalTranslation(80, 5, -60);
        ballGeometry4.addControl(new RigidBodyControl(.001f));
        ballGeometry4.getControl(RigidBodyControl.class).setRestitution(.5f);
        ballGeometry4.getControl(RigidBodyControl.class).setFriction(0);
        ballGeometry4.getControl(RigidBodyControl.class).setSleepingThresholds(0,0);
        ballGeometry4.getControl(RigidBodyControl.class).setLinearVelocity(move);
        rootNode.attachChild(ballGeometry4);
        bulletAppState.getPhysicsSpace().add(ballGeometry4);
        
        //Enemy list created
        List<Spatial> enemies = new ArrayList<Spatial>();
        
        //Set Spheres as enemies
        enemies.add(ballGeometry1);
        enemies.add(ballGeometry2);
        enemies.add(ballGeometry3);
        enemies.add(ballGeometry4);
        aiCharacter.getControl(NavMeshNavigationControl.class).setEnemyList(enemies);
                
    }
}
