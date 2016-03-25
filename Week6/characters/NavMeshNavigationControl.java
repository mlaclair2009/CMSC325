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
 * CMSC 325 - Project 2
 * @author Matthew LaClair
 */

public class NavMeshNavigationControl extends AbstractControl {

    private PathfinderThread pathfinderThread;
    private Vector3f waypointPosition = null;
    private Vector3f sphere1Position = null;
    private Vector3f sphere2Position = null;
    private Vector3f sphere3Position = null;
    private Vector3f sphere4Position = null;
    private List <Spatial> enemies;

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
            
            //Get locations of AI, waypoint, & spheres
            Vector2f aiPosition = new Vector2f(spatialPosition.x, spatialPosition.z);
            Vector2f waypoint2D = new Vector2f(waypointPosition.x, waypointPosition.z);
            Vector2f ball1 = new Vector2f(sphere1Position.x,sphere1Position.z);
            Vector2f ball2 = new Vector2f(sphere2Position.x,sphere2Position.z);
            Vector2f ball3 = new Vector2f(sphere3Position.x,sphere3Position.z);
            Vector2f ball4 = new Vector2f(sphere4Position.x,sphere4Position.z);
            
            //Print sphere locations
            System.out.println("Sphere 1's location : " + ball1);
            System.out.println("Sphere 2's location : " + ball2);
            System.out.println("Sphere 3's location : " + ball3);
            System.out.println("Sphere 4's location : " + ball4);
            System.out.println("");
            
            //Get distance between AI, waypoint, & spheres
            float distanceTarget = aiPosition.distance(waypoint2D);
            float distanceBall1 = aiPosition.distance(ball1);
            float distanceBall2 = aiPosition.distance(ball2);
            float distanceBall3 = aiPosition.distance(ball3);
            float distanceBall4 = aiPosition.distance(ball4);
            
            //If waypoint is far, move towards it
            if(distanceTarget > 1f){
                Vector2f direction = waypoint2D.subtract(aiPosition);
                direction.mult(tpf);
                spatial.getControl(AICharacterControl.class).setViewDirection(new Vector3f(direction.x, 0, direction.y).normalize());
                spatial.getControl(AICharacterControl.class).onAction("MoveForward", true, 1); 
            
                //If any of the spheres are close, move away from them
                if (distanceBall1 < 8f){
                System.out.println("Avoiding ball 1");
                direction = ball1.add(aiPosition);
                direction.mult(tpf);
                spatial.getControl(AICharacterControl.class).setViewDirection(new Vector3f(direction.x, 0, direction.y).normalize());
                spatial.getControl(AICharacterControl.class).onAction("MoveForward", false, 1);
                spatial.getControl(AICharacterControl.class).onAction("MoveBackward", true, 1);   
                }
                 
                if (distanceBall2 < 8f){
                System.out.println("Avoiding ball 2");
                direction = ball2.add(aiPosition);
                direction.mult(tpf);
                spatial.getControl(AICharacterControl.class).setViewDirection(new Vector3f(direction.x, 0, direction.y).normalize());
                spatial.getControl(AICharacterControl.class).onAction("MoveForward", false, 1);
                spatial.getControl(AICharacterControl.class).onAction("MoveBackward", true, 1);    
                }
                    
                if (distanceBall3 < 8f){
                System.out.println("Avoiding ball 3");
                direction = ball3.add(aiPosition);
                direction.mult(tpf);
                spatial.getControl(AICharacterControl.class).setViewDirection(new Vector3f(direction.x, 0, direction.y).normalize());
                spatial.getControl(AICharacterControl.class).onAction("MoveForward", false, 1);
                spatial.getControl(AICharacterControl.class).onAction("MoveBackward", true, 1);    
                }
                    
                if (distanceBall4 < 8f){
                System.out.println("Avoiding ball 4");
                direction = ball4.add(aiPosition);
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
    
    public void setEnemyList(List<Spatial> input){
        enemies = input;
        sphere1Position = enemies.get(0).getWorldTranslation();
        sphere2Position = enemies.get(1).getWorldTranslation();
        sphere3Position = enemies.get(2).getWorldTranslation();
        sphere4Position = enemies.get(3).getWorldTranslation();
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
                    System.out.println("Thread sleeping...");
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
