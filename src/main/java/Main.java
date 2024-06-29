import coal.Window;

/**
 * Begin window and hand off
 */
public class Main {
    public static void main(String[] args) {
        Window window = Window.get();
        window.run();
    }
}
