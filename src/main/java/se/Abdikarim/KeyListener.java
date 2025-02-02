package se.Abdikarim;

import static org.lwjgl.glfw.GLFW.*;

public class KeyListener
{
    private static KeyListener instance;
    private final boolean[] keyPressed = new boolean[GLFW_KEY_LAST];

    public static KeyListener get()
    {
        if(instance == null)
        {
            KeyListener.instance = new KeyListener();
        }
        return KeyListener.instance;
    }

    private KeyListener()
    {

    }

    public static void KeyCallback(long window, int key, int scancode, int action, int mods)
    {
        if(action == GLFW_PRESS )
        {
            get( ).keyPressed[ key ] = true;
        }
        else if(action == GLFW_RELEASE)
        {
            get().keyPressed[key] = false;
        }
    }

    public static boolean isKeyPressed(int keyCode)
    {
        return get( ).keyPressed[ keyCode ];
    }
}
