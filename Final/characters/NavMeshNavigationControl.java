package characters;

import com.jme3.ai.navmesh.NavMesh;
import com.jme3.ai.navmesh.NavMeshPathfinder;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * CMSC 325 - Final Project
 * @author Matthew LaClair
 */

public class NavMeshNavigationControl extends AbstractControl {

    private PathfinderThread pathfinderThread;
    private Vector3f waypointPosition = null;
    private Vector3f sphere1Position = null;
    private Vector3f box1Position = null;
    private Vector3f cylinder1Position = null;
    private Vector3f torus1Position = null;
    private List <Spatial> objects;

    public NavMeshNavigationControl(Node world) {

        Mesh mesh = ((Geometry) world.getChild("NavMesh")).getMesh();
        NavMesh navMesh = new NavMesh(mesh);

        pathfinderThread = new PathfinderThread(navMesh);
        pathfinderThread.start();
    }

    @Override
    protected void controlUpdate(float tpf) {
        Vector3f spatialPosition = spatial.getWorldTranslation();
        if(waypointPosition != null){
            
            //Get locations of AI, waypoint, & objects
            Vector2f aiPosition = new Vector2f(spatialPosition.x, spatialPosition.z);
            Vector2f waypoint2D = new Vector2f(waypointPosition.x, waypointPosition.z);
            Vector2f ball1 = new Vector2f(sphere1Position.x,sphere1Position.z);
            Vector2f box1 = new Vector2f(box1Position.x,box1Position.z);
            Vector2f cylinder1 = new Vector2f(cylinder1Position.x,cylinder1Position.z);
            Vector2f torus1 = new Vector2f(torus1Position.x,torus1Position.z);
            
            //Get distance between AI, waypoint, & objects
            float distanceTarget = aiPosition.distance(waypoint2D);
            float distanceBall1 = aiPosition.distance(ball1);
            float distanceBox1 = aiPosition.distance(box1);
            float distanceCylinder1 = aiPosition.distance(cylinder1);
            float distanceTorus1 = aiPosition.distance(torus1);
            
            //If waypoint is far, move towards it
            if(distanceTarget > 1f){
                Vector2f direction = waypoint2D.subtract(aiPosition);
                direction.mult(tpf);
                spatial.getControl(AICharacterControl.class).setViewDirection(new Vector3f(direction.x, 0, direction.y).normalize());
                spatial.getControl(AICharacterControl.class).onAction("MoveForward", true, 1); 
            
                //If any of the objects are close, move away from them
                if (distanceBall1 < 8f){
                System.out.println("Avoiding ball 1");
                direction = ball1.add(aiPosition);
                direction.mult(tpf);
                spatial.getControl(AICharacterControl.class).setViewDirection(new Vector3f(direction.x, 0, direction.y).normalize());
                spatial.getControl(AICharacterControl.class).onAction("MoveForward", false, 1);
                spatial.getControl(AICharacterControl.class).onAction("MoveBackward", true, 1);   
                }
                 
                if (distanceBox1 < 8f){
                System.out.println("Avoiding box 1");
                direction = box1.add(aiPosition);
                direction.mult(tpf);
                spatial.getControl(AICharacterControl.class).setViewDirection(new Vector3f(direction.x, 0, direction.y).normalize());
                spatial.getControl(AICharacterControl.class).onAction("MoveForward", false, 1);
                spatial.getControl(AICharacterControl.class).onAction("MoveBackward", true, 1);    
                }
                    
                if (distanceCylinder1 < 8f){
                System.out.println("Avoiding cylinder 1");
                direction = cylinder1.add(aiPosition);
                direction.mult(tpf);
                spatial.getControl(AICharacterControl.class).setViewDirection(new Vector3f(direction.x, 0, direction.y).normalize());
                spatial.getControl(AICharacterControl.class).onAction("MoveForward", false, 1);
                spatial.getControl(AICharacterControl.class).onAction("MoveBackward", true, 1);    
                }
                    
                if (distanceTorus1 < 8f){
                System.out.println("Avoiding ball 4");
                direction = torus1.add(aiPosition);
                direction.mult(tpf);
                spatial.getControl(AICharacterControl.class).setViewDirection(new Vector3f(direction.x, 0, direction.y).normalize());
                spatial.getControl(AICharacterControl.class).onAction("MoveForward", false, 1);
                spatial.getControl(AICharacterControl.class).onAction("MoveBackward", true, 1);    
                }
            } else {
                waypointPosition = null;
            }
            
        } else if (!pathfinderThread.isPathfinding() && pathfinderThread.pathfinder.getNextWaypoint() != null && !pathfinderThread.pathfinder.isAtGoalWaypoint() ){
            pathfinderThread.pathfinder.goToNextWaypoint();
            System.out.println("Moving to next way point...");
            waypointPosition = new Vector3f(pathfinderThread.pathfinder.getWaypointPosition());
        } else {
            spatial.getControl(AICharacterControl.class).onAction("MoveForward", false, 1);
            spatial.getControl(AICharacterControl.class).onAction("MoveBackward", false, 1);
            System.out.println("I made it!");
            moveTo(new Vector3f(80,30,-60));
            }
    }

    @Override
    protected void controlRender(RenderManager rm, ViewPort vp) {
    }

    public void moveTo(Vector3f target) {
        pathfinderThread.setTarget(target);
    }
    
    //Get object locations and set in list to avoid
    public void setObjectList(List<Spatial> input){
        objects = input;
        sphere1Position = objects.get(0).getWorldTranslation();
        box1Position = objects.get(1).getWorldTranslation();
        cylinder1Position = objects.get(2).getWorldTranslation();
        torus1Position = objects.get(3).getWorldTranslation();
    }

    
    private class PathfinderThread extends Thread {

        private Vector3f target;
        private NavMeshPathfinder pathfinder;
        private boolean pathfinding;
        private boolean running = true;

        public PathfinderThread(NavMesh navMesh) {
            pathfinder = new NavMeshPathfinder(navMesh);
            this.setDaemon(true);
        }

        @Override
        public void run() {
            while(running){
                if (target != null) {
                    pathfinding = true;
                    pathfinder.setPosition(getSpatial().getWorldTranslation());
                    boolean success = pathfinder.computePath(target);
                    System.out.println(success);
                    if (success) {
                        target = null;
                    }
                    pathfinding = false;
                }
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException ex) {
                    Logger.getLogger(NavMeshNavigationControl.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

        public void setTarget(Vector3f target) {
            this.target = target;
        }
        
        public boolean isPathfinding(){
            return pathfinding;
        }
        
    };
    
}
