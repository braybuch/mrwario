package components;

import coal.Component;

public class FontRenderer extends Component {

    @Override
    public void start() {
        if (gameObject.getComponents(SpriteRenderer.class) != null){
            System.out.println("Found font renderer");
        }
    }

    @Override
    public void update(Float deltaTime) {

    }
}
