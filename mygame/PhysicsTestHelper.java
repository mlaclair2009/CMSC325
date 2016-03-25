package mygame;

import com.jme3.app.Application;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Plane;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
import com.jme3.scene.shape.Sphere.TextureMode;
import com.jme3.texture.Texture;

/**
 * test
 * @author Matthew LaClair
 * CMSC 325 - Project 1
 * 
 */
public class PhysicsTestHelper {
    /**
     * creates a simple physics test world with a floor, an obstacle and some test boxes
     * @param rootNode
     * @param assetManager
     * @param space
     */
    public static void createPhysics (Node rootNode, AssetManager assetManager, PhysicsSpace space) {
        
        InputAppState appStateTargets = new InputAppState();
        Material material1 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material1.setTexture("ColorMap", assetManager.loadTexture("Textures/Terrain/Pond/Pond.jpg"));
        
        Material material2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material2.setTexture("ColorMap", assetManager.loadTexture("Textures/Terrain/Rock2/rock.jpg"));
        
        Material material3 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material3.setTexture("ColorMap", assetManager.loadTexture("Textures/Terrain/BrickWall/BrickWall.jpg"));
        
        Material material4 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material4.setTexture("ColorMap", assetManager.loadTexture("Textures/Terrain/splat/dirt.jpg"));

        Box floorBox = new Box(1000, 0, 1000);
        Geometry floorGeometry = new Geometry("Floor", floorBox);
        floorGeometry.setMaterial(material1);
        floorGeometry.setLocalTranslation(0, 0, 0);
        floorGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(floorGeometry);
        space.add(floorGeometry);

        //movable boxes
        for (int i = 0; i < 20; i++) {
            Box box = new Box(0.25f, 1, 0.25f);
            Geometry boxGeometry = new Geometry("Box", box);
            boxGeometry.setMaterial(material2);
            boxGeometry.setLocalTranslation(i*2, 1, -3);
            //RigidBodyControl automatically uses box collision shapes when attached to single geometry with box mesh
            boxGeometry.addControl(new RigidBodyControl(2));
            rootNode.attachChild(boxGeometry);
            space.add(boxGeometry);
            appStateTargets.targets.add(i, boxGeometry);
        }
        
        //movable boxes
        for (int i = 10; i < 20; i++) {
            Box box = new Box(0.5f, 1, 0.5f);
            Geometry boxGeometry = new Geometry("Box", box);
            boxGeometry.setMaterial(material3);
            boxGeometry.setLocalTranslation(i*-1, 1, -10);
            //RigidBodyControl automatically uses box collision shapes when attached to single geometry with box mesh
            boxGeometry.addControl(new RigidBodyControl(2));
            rootNode.attachChild(boxGeometry);
            space.add(boxGeometry);
        }
        
        //movable spheres
        for (int i = 5; i < 20; i++) {
            Sphere sphere = new Sphere(20, 20, .5f);
            Geometry ballGeometry = new Geometry("ball", sphere);
            ballGeometry.setMaterial(material3);
            ballGeometry.setLocalTranslation(i*2, 4, 5);
            //RigidBodyControl automatically uses Sphere collision shapes when attached to single geometry with sphere mesh
            ballGeometry.addControl(new RigidBodyControl(.001f));
            ballGeometry.getControl(RigidBodyControl.class).setRestitution(1);
            rootNode.attachChild(ballGeometry);
            space.add(ballGeometry);
        }
        
        //movable spheres
        for (int i = 5; i < 20; i++) {
            Sphere sphere = new Sphere(30, 30, 5f);
            Geometry ballGeometry = new Geometry("ball", sphere);
            ballGeometry.setMaterial(material3);
            ballGeometry.setLocalTranslation(i*10, 5, i*5);
            //RigidBodyControl automatically uses Sphere collision shapes when attached to single geometry with sphere mesh
            ballGeometry.addControl(new RigidBodyControl(.001f));
            ballGeometry.getControl(RigidBodyControl.class).setRestitution(1);
            rootNode.attachChild(ballGeometry);
            space.add(ballGeometry);
        }

        //immovable sphere with mesh collision shape
        for (int i = 10; i < 20; i++) {
             Sphere sphere = new Sphere(8, 8, 1);
             Geometry sphereGeometry = new Geometry("Sphere", sphere);
             sphereGeometry.setMaterial(material4);
             sphereGeometry.setLocalTranslation(45, 2, i*3);
             sphereGeometry.addControl(new RigidBodyControl(new MeshCollisionShape(sphere), 0));
             rootNode.attachChild(sphereGeometry);
             space.add(sphereGeometry);
        }
    }
    



    /**
     * creates the necessary inputlistener and action to shoot balls from teh camera
     * @param app
     * @param rootNode
     * @param space
     */
    public static void createBallShooter(final Application app, final Node rootNode, final PhysicsSpace space) {
        ActionListener actionListener = new ActionListener() {

            public void onAction(String name, boolean keyPressed, float tpf) {
                Sphere bullet = new Sphere(50, 50, 2f, true, false);
                bullet.setTextureMode(TextureMode.Projected);
                Material mat2 = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
                TextureKey key2 = new TextureKey("Textures/Terrain/splat/grass.jpg");
                key2.setGenerateMips(true);
                Texture tex2 = app.getAssetManager().loadTexture(key2);
                mat2.setTexture("ColorMap", tex2);
                if (name.equals("shoot") && !keyPressed) {
                    Geometry bulletg = new Geometry("bullet", bullet);
                    bulletg.setMaterial(mat2);
                    bulletg.setShadowMode(ShadowMode.CastAndReceive);
                    bulletg.setLocalTranslation(app.getCamera().getLocation());
                    RigidBodyControl bulletControl = new RigidBodyControl(10);
                    bulletg.addControl(bulletControl);
                    bulletControl.setLinearVelocity(app.getCamera().getDirection().mult(70));
                    bulletg.addControl(bulletControl);
                    rootNode.attachChild(bulletg);
                    space.add(bulletControl);
                }
            }
        };
        app.getInputManager().addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        app.getInputManager().addListener(actionListener, "shoot");
    }
}
    

