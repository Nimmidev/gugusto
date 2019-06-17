package de.thu.gpro.gugusto.collision;

import de.thu.gpro.gugusto.game.level.Chunk;
import de.thu.gpro.gugusto.game.object.DynamicGameObject;
import de.thu.gpro.gugusto.game.object.GameObject;
import de.thu.gpro.gugusto.util.DebugInfo;
import de.thu.gpro.gugusto.util.Vector;
import de.thu.gpro.gugusto.game.Game;
import de.thu.gpro.gugusto.util.Size;

import java.util.List;

public class CollisionUtil {

    private CollisionUtil(){}

    /**
     * Check the dynamic GameObject against every StaticGameObject in the chunks provided
     * @param dynamicObj
     * @param chunks
     */
    public static void handleStaticCollisions(DynamicGameObject dynamicObj, List<Chunk> chunks) {
        for (Chunk chunk : chunks) {
            for (GameObject other : chunk.getBlocks()) {
                DebugInfo.checkedStaticCollisions++;
                if (CollisionUtil.isColliding(dynamicObj, other)) {
                    DebugInfo.occurredStaticCollisions++;
                    dynamicObj.collision(other);
                    other.collision(dynamicObj);
                }
            }
        }
    }

    /**
     * Checks for collisions between those GameObjects and calls the de.thu.gprog.gugusto.collision method of both de.thu.gprog.gugusto.collision partners if there is a de.thu.gprog.gugusto.collision
     * @param objs
     */
    public static void handleDynamicCollisions(List<DynamicGameObject> objs) {
        for (int i = 0; i < objs.size(); i++) {
            for (int j = 0; j < objs.size(); j++) {
                if (i < j) {
                    GameObject x = objs.get(i);
                    GameObject y = objs.get(j);
                    DebugInfo.checkedDynamicCollisions++;
                    if (CollisionUtil.isColliding(x, y)) {
                        DebugInfo.occurredDynamicCollisions++;
                        x.collision(y);
                        y.collision(x);
                    }
                }
            }
        }
    }

    public static boolean isColliding(GameObject object1, GameObject object2) {
        BoundingBox bb1 = object1.getBoundingBox();
        BoundingBox bb2 = object2.getBoundingBox();
        return isColliding(bb1, bb2);
    }

    public static boolean isColliding(BoundingBox bb1, BoundingBox bb2){
        BoundingBox.Type bbt1 = bb1.getType();
        BoundingBox.Type bbt2 = bb2.getType();

        if(bbt1 == BoundingBox.Type.RECTANGLE){
            if(bbt2 == BoundingBox.Type.RECTANGLE) return rectToRect(bb1, bb2);
            else if(bbt2 == BoundingBox.Type.CIRCLE) return rectToCircle(bb1, bb2);
        } else if(bbt1 == BoundingBox.Type.CIRCLE){
            if(bbt2 == BoundingBox.Type.RECTANGLE) return rectToRect(bb2, bb1);
            else if(bbt2 == BoundingBox.Type.CIRCLE) return circleToCircle(bb1, bb2);
        }

        return false;
    }

    public int isCollidingWithScreen(GameObject object){
        double nx, fx;
        double ny, fy;
        BoundingBox bb = object.getBoundingBox();
        Vector bbp = bb.getPosition();
        Size bbs = bb.getSize();

        if(bb.getType() == BoundingBox.Type.RECTANGLE){
            nx = bbp.getX();
            fx = bbp.getX() + bbs.getWidth();
            ny = bbp.getY();
            fy = bbp.getY() + bbs.getHeight();
        } else if(bb.getType() == BoundingBox.Type.CIRCLE){
            nx = bbp.getX() - bb.getRadius();
            fx = bbp.getX() + bb.getRadius();
            ny = bbp.getY() - bb.getRadius();
            fy = bbp.getY() + bb.getRadius();
        } else {
            return 0;
        }

        if(nx < 0 || fx > Game.WIDTH) return -1;
        if(ny < 0 || fy > Game.HEIGHT) return 1;

        return 0;
    }

    private static boolean rectToRect(BoundingBox bb1, BoundingBox bb2){
        Vector bbp1 = bb1.getPosition();
        Vector bbp2 = bb2.getPosition();
        Size bbs1 = bb1.getSize();
        Size bbs2 = bb2.getSize();

        return bbp1.getX() < bbp2.getX() + bbs2.getWidth() &&
                bbp1.getX() + bbs1.getWidth() > bbp2.getX() &&
                bbp1.getY() < bbp2.getY() + bbs2.getHeight() &&
                bbp1.getY() + bbs1.getHeight() > bbp2.getY();
    }

    private static boolean rectToCircle(BoundingBox bb1, BoundingBox bb2){
        // TODO
        return false;
    }

    private static boolean circleToCircle(BoundingBox bb1, BoundingBox bb2){
        double diffX = bb1.getPosition().getX() - bb2.getPosition().getX();
        double diffY = bb1.getPosition().getY() - bb2.getPosition().getY();
        double diff = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));

        return diff < bb1.getRadius() + bb2.getRadius();
    }

    public static boolean contains(Vector point, BoundingBox bb){
        if(bb.getType() == BoundingBox.Type.RECTANGLE) return isPointInRect(point, bb);
        else return isPointInCircle(point, bb);
    }

    public static boolean isPointInCircle(Vector point, BoundingBox bb){
        double diffX = point.getX() - bb.getPosition().getX();
        double diffY = point.getY() - bb.getPosition().getY();
        double diff = Math.sqrt(Math.pow(diffX, 2) + Math.pow(diffY, 2));

        return diff < bb.getRadius();
    }

    public static boolean isPointInRect(Vector point, BoundingBox bb){
        Vector position = bb.getPosition();
        Size size = bb.getSize();

        return point.getX() >= position.getX() &&
                point.getX() <= position.getX() + size.getWidth() &&
                point.getY() >= position.getY() &&
                point.getY() <= position.getY() + size.getHeight();
    }

}