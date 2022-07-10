package com.uncertainty;

import com.badlogic.gdx.InputAdapter;
import com.badlogic.gdx.graphics.Camera;
import com.badlogic.gdx.math.Intersector;
import com.badlogic.gdx.math.Plane;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.collision.Ray;

import static com.uncertainty.UncertaintyGame.MAX_DEPTH;
import static com.uncertainty.UncertaintyGame.MIN_DEPTH;

public class CameraController extends InputAdapter {

    final Plane xyPlane = new Plane(new Vector3(0,0,1),0);
    final Vector3 curr = new Vector3();
    final Vector3 last = new Vector3(-1,-1,-1);
    final Vector3 delta = new Vector3();
    Vector3 selected = new Vector3(-1, -1, -1);
    Vector3 cursorIntersectionIso = new Vector3(-1,-1,-1);
    Vector3 cursorIntersectionXY = new Vector3(-1,-1,-1);

    private Camera camera;

    public CameraController(Camera camera){
        this.camera = camera;
    }

    public boolean touchDragged(int x, int y, int pointer){
        Ray pickRay = camera.getPickRay(x,y);
        Intersector.intersectRayPlane(pickRay, xyPlane, curr);
        System.out.println("intersection: ");
        System.out.println(curr);

        if(!(last.x == -1 && last.y == -1 && last.z == -1)){
            pickRay = camera.getPickRay(last.x, last.y);
            Intersector.intersectRayPlane(pickRay,xyPlane, delta);
            delta.sub(curr);
            camera.position.add(delta.x, delta.y, delta.z);
        }
        last.set(x,y,0);
        return false;
    }

    public boolean touchUp(int x, int y, int pointer, int button){
        last.set(-1,-1,-1);
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        Vector3 isoPoint = new Vector3();
        Ray pickRay = camera.getPickRay(screenX, screenY);
        Intersector.intersectRayPlane(pickRay,xyPlane, isoPoint);
        this.cursorIntersectionIso = isoPoint;
        this.cursorIntersectionXY = UncertaintyGame.isoToXY(isoPoint);

        return false;
    }

    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {

//        Vector3 isoPoint = new Vector3();
//        Ray pickRay = camera.getPickRay(screenX, screenY);
//        Intersector.intersectRayPlane(pickRay, xyPlane, isoPoint);
//        System.out.println("isoPoint:" + isoPoint);
//
//        Vector3 xyPoint = UncertaintyGame.isoToXY(isoPoint);
//        System.out.println("xyPoint:" + xyPoint);
//        selected = isoPoint;

        /*
         * TODO: select a tile
         */

        return false;
    }

    @Override
    public boolean scrolled(int amount) {

        int newDepth = UncertaintyGame.currentDepth + amount;

        if(newDepth > MAX_DEPTH-1){
            UncertaintyGame.currentDepth = MAX_DEPTH-1;
            return false;
        }

        if(newDepth < MIN_DEPTH){
            UncertaintyGame.currentDepth = MIN_DEPTH;
            return false;
        }

        UncertaintyGame.currentDepth = newDepth;
        return false;

    }

    public Vector3 getSelected(){
        return selected;
    }

    public Vector3 getCursorIntersectionIso(){return cursorIntersectionIso;}

    public Vector3 getCursorIntersectionXY(){return cursorIntersectionXY;}

}
