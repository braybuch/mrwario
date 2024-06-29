package components;

import org.joml.Vector3f;

/**
 * This component provides physics to a game object
 */
public class Rigidbody extends Component {
    /** the type of collider to use */
    private final int colliderType = 0;
    /** the amount of friction for this object */
    private final float friction = 0.8f;
    /** the velocity of this object */
    public Vector3f velocity = new Vector3f(0, 0.5f, 0);
}
