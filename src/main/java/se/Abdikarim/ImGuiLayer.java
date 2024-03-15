package se.Abdikarim;

import imgui.ImGui;
import org.joml.Vector2f;

public class ImGuiLayer
{
    private boolean showText = false;


    public void imgui(  )
    {

        ImGui.begin( "Cool window" );

        if ( ImGui.button( "I am a button" ) )
        {
            showText = true;
        }


        if(showText)
        {
            ImGui.text( "You clicked a button" );
            ImGui.sameLine();
            if(ImGui.button( "Stop showing text" ))
            {
                showText = false;
            }
        }

        // Slider for camera X position
        float[] cameraPosX = new float[] { Window.getScene().camera.position.x };
        if (ImGui.sliderFloat("Camera X: ", cameraPosX, 1180.0f, -1180.0f)) {
            Window.getScene().camera.position.x = cameraPosX[0];
        }

        // Slider for camera X position
        float[] cameraPosY = new float[] { Window.getScene().camera.position.y };
        if (ImGui.sliderFloat("Camera Y: ", cameraPosY, 1180.0f, -1180.0f)) {
            Window.getScene().camera.position.y = cameraPosY[0];
        }

        ImGui.end( );
    }
}
