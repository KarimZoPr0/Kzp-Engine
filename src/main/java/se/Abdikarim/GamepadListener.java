package se.Abdikarim;

import org.lwjgl.glfw.GLFWGamepadState;

import static org.lwjgl.glfw.GLFW.GLFW_GAMEPAD_BUTTON_LAST;
import static org.lwjgl.glfw.GLFW.GLFW_PRESS;


public class GamepadListener
{
    private static GamepadListener instance;
    private GLFWGamepadState state = GLFWGamepadState.create();

    public static GamepadListener get()
    {
        if(instance == null)
        {
            GamepadListener.instance = new GamepadListener();
        }
        return GamepadListener.instance;
    }

    public static boolean isButtonPressed(int button)
    {
        return get().state.buttons(button) == GLFW_PRESS;
    }
}
