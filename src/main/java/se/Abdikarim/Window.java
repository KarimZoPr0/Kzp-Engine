package se.Abdikarim;

import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import imgui.ImGui;
import imgui.flag.ImGuiConfigFlags;
import imgui.gl3.ImGuiImplGl3;
import imgui.glfw.ImGuiImplGlfw;
import org.lwjgl.Version;
import org.lwjgl.glfw.Callbacks;
import org.lwjgl.glfw.GLFW;
import org.lwjgl.glfw.GLFWErrorCallback;
import org.lwjgl.opengl.GL;

public class Window
{
    private final ImGuiImplGlfw imGuiGlfw = new ImGuiImplGlfw();
    private final ImGuiImplGl3 imGuiGl3 = new ImGuiImplGl3();
    private final String glslVersion = null;

    private final ImGuiLayer imGuiLayer = new ImGuiLayer();

    public float r, g, b, a;
    private final int width;
    private final int height;
    private final String title;
    private long glfwWindow;
    private static Window window = null;

    private static Scene currentScene = null;

    private Window()
    {
        width = 1920;
        height = 1000;
        title = "Kzp-Engine";

        r = g = b = 0;
        a = 1;
    }

    public static void changeScene(int newScene)
    {
        switch ( newScene )
        {
            case 0:
                currentScene = new LevelEditorScene();
                currentScene.init();
                currentScene.start();
                break;
            case 1:
                currentScene = new LevelScene();
                currentScene.init();
                currentScene.start();
                break;
            default:
                assert false: "Unknown scene: " + newScene;
        }
    }

    public static Window get()
    {
        if(window == null)
        {
            Window.window = new Window();
        }
        return Window.window;
    }

    public static Scene getScene( )
    {
        return get().currentScene;
    }

    public void run()
    {
        System.out.println("Hello LWJGL " + Version.getVersion() + "!");

        init();
        loop();

        // Free the window callbacks and destroy the window
        glfwFreeCallbacks(glfwWindow);
        glfwDestroyWindow(glfwWindow);

        // Terminate GLFW and free the error callback
        glfwTerminate();
        glfwSetErrorCallback(null).free();
    }

    public void init()
    {
        // Setup error callback
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW
        if(!glfwInit())
        {
            throw new IllegalStateException( "Unable to initialize GLFW" );
        }

        // Configure GLFW
        glfwDefaultWindowHints();
        glfwWindowHint( GLFW_VISIBLE, GLFW_FALSE );
        glfwWindowHint( GLFW_RESIZABLE, GLFW_TRUE );
        glfwWindowHint( GLFW_MAXIMIZED, GLFW_TRUE );

        // Create the window
        glfwWindow = glfwCreateWindow( width, height, title, NULL, NULL );
        if(glfwWindow == NULL)
        {
            throw new IllegalStateException( "Failed to create the GLFW window." );
        }

        // Set callbacks
        glfwSetCursorPosCallback( glfwWindow, MouseListener::mousePosCallback );
        glfwSetMouseButtonCallback( glfwWindow, MouseListener::mouseButtonCallback );
        glfwSetScrollCallback( glfwWindow, MouseListener::mouseScrollCallback );
        glfwSetKeyCallback( glfwWindow, KeyListener::KeyCallback );


        // Make the OpenGL context current
        glfwMakeContextCurrent( glfwWindow );

        // Enable v-sync
        glfwSwapInterval( 1 );

        // Make the window visible
        glfwShowWindow( glfwWindow );

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities( );

        ImGui.createContext();
        ImGui.getIO().addConfigFlags( ImGuiConfigFlags.ViewportsEnable);
        ImGui.getIO().setFontGlobalScale( 2f );
        imGuiGlfw.init( glfwWindow, true );
        imGuiGl3.init(glslVersion);

        changeScene( 0 );
    }

    public void destroyImGui()
    {
        imGuiGl3.dispose();
        imGuiGlfw.dispose();
        ImGui.destroyContext();
        Callbacks.glfwFreeCallbacks( glfwWindow );
        glfwDestroyWindow( glfwWindow );
        glfwTerminate();
    }

    public void loop()
    {
        float beginTime = ( float ) glfwGetTime();
        float endTime;
        float dt = -1.0f;

        while ( !glfwWindowShouldClose( glfwWindow ) )
        {
            // Poll events
            glfwPollEvents();

            glClearColor(r, g, b, a);
            glClear( GL_COLOR_BUFFER_BIT );

            imGuiGlfw.newFrame();
            ImGui.newFrame();

            imGuiLayer.imgui();

            ImGui.render();
            imGuiGl3.renderDrawData( ImGui.getDrawData() );


            if (ImGui.getIO().hasConfigFlags( ImGuiConfigFlags.ViewportsEnable)) {
                final long backupWindowPtr = org.lwjgl.glfw.GLFW.glfwGetCurrentContext();
                ImGui.updatePlatformWindows();
                ImGui.renderPlatformWindowsDefault();
                org.lwjgl.glfw.GLFW.glfwMakeContextCurrent(backupWindowPtr);
            }

            if(dt >= 0)
            {
                currentScene.update( dt );
            }

            glfwSwapBuffers( glfwWindow );

            endTime = ( float ) glfwGetTime();
            dt = endTime - beginTime;
            beginTime = endTime;
        }

        destroyImGui();
    }
}
