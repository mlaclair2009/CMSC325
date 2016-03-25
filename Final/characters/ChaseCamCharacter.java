package characters;

import com.jme3.app.Application;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.math.Triangle;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.queue.RenderQueue;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;
import com.jme3.texture.Texture;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

/**
 * CMSC 325 - Final Project
 * @author Matthew LaClair
 */

public class ChaseCamCharacter extends MyGameCharacterControl {
    
    private Camera cam;
    private Vector3f modelForwardDir;
    private Vector3f modelLeftDir;
    private float playerWidth = 0.1f;
    private boolean inCover;
    private boolean hasLowCover;
    private boolean hasHighCover;
    private float lowHeight = 0.5f;
    private float highHeight = 1.5f;
    private Node structures;
    private Application app;
    public static int score;
//    List<Spatial> targets = new ArrayList<Spatial>();
    
    public int getScore(){
        return score;
    }

    public ChaseCamCharacter(float radius, float height, float mass) {
        super(radius, height, mass);
    }
        
    @Override
    public void update(float tpf){
        super.update(tpf);
        if(!forward && !backward && !leftStrafe && !rightStrafe && !inCover){
            modelForwardDir = cam.getRotation().mult(Vector3f.UNIT_Z).multLocal(1, 0, 1);
            modelLeftDir = cam.getRotation().mult(Vector3f.UNIT_X);
        } else if(inCover){
            modelForwardDir = spatial.getWorldRotation().mult(Vector3f.UNIT_Z);
            modelLeftDir = spatial.getWorldRotation().mult(Vector3f.UNIT_X);
        }
        
        walkDirection.set(0, 0, 0);
        if (forward) {
            walkDirection.addLocal(modelForwardDir.mult(moveSpeed));
        } else if (backward) {
            walkDirection.addLocal(modelForwardDir.negate().multLocal(moveSpeed));
        }
        if (leftStrafe) {
            walkDirection.addLocal(modelLeftDir.mult(moveSpeed));
        } else if (rightStrafe) {
            walkDirection.addLocal(modelLeftDir.negate().multLocal(moveSpeed));
        }
        if(walkDirection.length() > 0){
            if(inCover){
                checkCover(spatial.getWorldTranslation().add(walkDirection.multLocal(0.2f).mult(0.1f)));
                if(!hasLowCover && !hasHighCover){
                    walkDirection.set(Vector3f.ZERO);
                }
            } else {
                viewDirection.set(walkDirection);
            }
        }
        
        setViewDirection(viewDirection.normalizeLocal());
        setWalkDirection(walkDirection);
    }
    
    @Override
    public void setCamera(Camera cam){
        this.cam = cam;
//        camNode = new CameraNode("CamNode", cam);
//        camNode.setControlDir(CameraControl.ControlDirection.SpatialToCamera);
//        head.attachChild(camNode);
//        camNode.setLocalTranslation(new Vector3f(0, 5, -5));
//        camNode.lookAt(head.getLocalTranslation(), Vector3f.UNIT_Y);
    }
    

    @Override
    public void onAction(String binding, boolean value, float tpf) {
        if(binding.equals("ToggleCover") && value){
            if(inCover){
                inCover = false;
            } else {
                checkCover(spatial.getWorldTranslation());
                if(hasLowCover || hasHighCover){
                    inCover = true;
                }
            }
        } else if(inCover){
            if (binding.equals("StrafeLeft")) {
                leftStrafe = value;
            } else if (binding.equals("StrafeRight")) {
                rightStrafe = value;
            }
        } else {
            if (binding.equals("StrafeLeft")) {
                leftStrafe = value;
            } else if (binding.equals("StrafeRight")) {
                rightStrafe = value;
            } else if (binding.equals("MoveForward")) {
                forward = value;
            } else if (binding.equals("MoveBackward")) {
                backward = value;
            } else if (binding.equals("Jump")) {
                jump();
            }
        }
    }
     
    public void setStructures(Node structures){
        this.structures = structures;
    }
    
    public void checkCover(Vector3f position){
        Ray ray = new Ray();
        ray.setDirection(viewDirection);
        ray.setLimit(0.8f);

        int lowCollisions = 0;
        int highCollisions = 0;
        
        CollisionResults collRes = new CollisionResults();
        
        Vector3f leftDir = spatial.getWorldRotation().getRotationColumn(0).mult(playerWidth);
        leftDir.setY(lowHeight);
        for(int i = -1; i < 2; i++){
            leftDir.multLocal(i, 1, i);
            ray.setOrigin(position.add(leftDir));
            structures.collideWith(ray, collRes);
            if(collRes.size() > 0){
                lowCollisions++;
            }
            collRes.clear();
        }
        
        if(lowCollisions == 3){
            leftDir.setY(highHeight);
            for(int i = -1; i < 2; i++){
                leftDir.multLocal(i, 1, i);
                ray.setOrigin(position.add(leftDir));
                structures.collideWith(ray, collRes);
                if(collRes.size() > 0){
                    highCollisions++;
                }
                collRes.clear();
            }
        
            ray.setOrigin(spatial.getWorldTranslation().add(0, 0.5f, 0));
            structures.collideWith(ray, collRes);

            Triangle t = new Triangle();
            collRes.getClosestCollision().getTriangle(t);
//            t.calculateNormal();
            viewDirection.set(t.getNormal().negate());//alignWithCover(t.getNormal());
            if(highCollisions == 3){
                hasHighCover = true;
            } else {
                hasLowCover = true;
            }
            
        } else {
            hasHighCover = false;
            hasLowCover = false;
        }
    }
    
//    //Add enemy list as targets for player
//    public void setTargets (List<Spatial> targets){
//        this.targets = targets;
//    }
    public void fire(final Application app) {   
       
        }
    
    public static void createBallShooter(final Application app, final Node rootNode, final PhysicsSpace space, final List<Spatial> targets) {
        ActionListener actionListener;
        actionListener = new ActionListener() {
    int box = 1;    
    int sphere = 2;
    int cylinder = 3;
    int torus = 4;
    int ai = 5;
    public int bFired = 0;
            


    public void onAction(String name, boolean keyPressed, float tpf) {
        Sphere bullet = new Sphere(5, 5, 2f, true, false);
        bullet.setTextureMode(Sphere.TextureMode.Projected);
        Material mat = new Material(app.getAssetManager(), "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key = new TextureKey("Textures/dirt.jpg");
        key.setGenerateMips(true);
        Texture tex2 = app.getAssetManager().loadTexture(key);
        mat.setTexture("ColorMap", tex2);
              

        if (name.equals("shoot") && !keyPressed) {
            Geometry bulletg = new Geometry("bullet", bullet);
            bulletg.setMaterial(mat);
             bulletg.setShadowMode(RenderQueue.ShadowMode.CastAndReceive);
             bulletg.setLocalTranslation(app.getCamera().getLocation());
             RigidBodyControl bulletControl = new RigidBodyControl(10);
             bulletg.addControl(bulletControl);
             bulletControl.setLinearVelocity(app.getCamera().getDirection().mult(100));
             bulletg.addControl(bulletControl);
             rootNode.attachChild(bulletg);
             space.add(bulletControl);
             bFired++;
             System.out.println("BULLETS FIRED: " + bFired);
     
      //Detect hits on targets/update score on console
     Ray r = new Ray(app.getCamera().getLocation(), app.getCamera().getDirection());
     CollisionResults collRes = new CollisionResults();
     for (Iterator<Spatial> it = targets.iterator(); it.hasNext();) {
         Spatial g = it.next();
         g.collideWith(r, collRes);
     }
     if(collRes.size() > 0){
         CollisionResult closest = collRes.getClosestCollision();
         String nameGeo = closest.getGeometry().getName();
         System.out.println("You hit: " + nameGeo );  
         if (nameGeo.equals("box1")){
             score += box;
             System.out.println("SCORE: " + score);
         }
         if (nameGeo.equals("ball1")){
             score += sphere;
             System.out.println("SCORE: " + score);
         }
         if (nameGeo.equals("cylinder1")){
             score += cylinder;
             System.out.println("SCORE: " + score);
         }
         if (nameGeo.equals("torus1")){
             score += torus;
             System.out.println("SCORE: " + score);
         }
        if (nameGeo.equals("JaimeGeom-geom-1")){
             score += ai;
             System.out.println("SCORE: " + score);
         }
        try {
            FileWriter writer = new FileWriter(new File("highscore.txt"),false);
            writer.write(score+System.getProperty("line.separator"));
            writer.close();
        } catch (IOException e) {e.printStackTrace();}
    }
     }
     
    
 }
};
        app.getInputManager().addMapping("shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
        app.getInputManager().addListener(actionListener, "shoot");
    }
    }

