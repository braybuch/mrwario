package coal;

import org.joml.Vector2f;

/**
 * This class represents a position and scale
 */
public class Transform {
    /** the 2D position of the object */
    public Vector2f position;
    /** the scale of the obejct */
    public Vector2f scale;
    /** the rotation of the object */
    public float rotation = 0.0f;

    /**
     * Construct default transform
     */
    public Transform(){
        init(new Vector2f(), new Vector2f());
    }

    /**
     * construct transform of default size
     *
     * @param position the 2d vector of position
     */
    public Transform(Vector2f position){
        init(position, new Vector2f());
    }

    /**
     * Construct a complete transform
     *
     * @param position the 2d vector of position
     * @param scale the 2d vector of scale
     */
    public Transform(Vector2f position, Vector2f scale){
        init(position, scale);
    }

    /**
     * Initialize fields of this transform
     *
     * @param position the 2d vector of position
     * @param scale the 2d vector of scale
     */
    public void init(Vector2f position, Vector2f scale){
        this.position = position;
        this.scale = scale;
    }

    /**
     * Return a duplicate of this object
     *
     * @return the duplicate
     */
    public Transform copy(){
        return new Transform(new Vector2f(position), new Vector2f(scale));
    }

    /**
     * Assign duplicate values to the passed transfrom
     *
     * @param other the transfrom to reinitialize
     */
    public void copy(Transform other){
        other.position.set(this.position);
        other.scale.set(this.scale);
    }

    @Override
    public boolean equals(Object o){
        if (o ==null) return false;
        if (!(o instanceof Transform t)) return false;
        return t.position.equals(this.position) && t.scale.equals(this.scale);
    }
}
