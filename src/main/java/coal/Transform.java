package coal;

import org.joml.Vector2f;

public class Transform {
    public Vector2f position;
    public Vector2f scale;

    public String toString(){
        return position.x + " " + position.y + ", " + scale.x + " " + scale.y;
    }

    public Transform(){
        init(new Vector2f(), new Vector2f());
    }

    public Transform(Vector2f position){
        init(position, new Vector2f());
    }

    public Transform(Vector2f position, Vector2f scale){
        init(position, scale);
    }

    public void init(Vector2f position, Vector2f scale){
        this.position = position;
        this.scale = scale;
    }

    public Transform copy(){
        return new Transform(new Vector2f(position), new Vector2f(scale));
    }

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
