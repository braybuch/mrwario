package components;

import coal.Component;

public class SpriteRenderer extends Component{
    private boolean firstTime = false;
    @Override
    public void start() {
        System.out.println("I am starting");
    }

    @Override
    public void update(Float deltaTime) {
        if (!firstTime) {
            System.out.println("I am updating");
            firstTime = true;
        }
    }
}
