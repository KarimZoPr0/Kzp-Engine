package renderer;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader
{
    private int shaderProgramID;

    private String vertexSource;
    private String fragmentSource;
    private String filePath;

    public Shader(String filePath)
    {
        this.filePath = filePath;
        try {
            List<String> lines = Files.readAllLines( Path.of( filePath ));
            String source = String.join("\n", lines);

            String[] shaderParts = source.split("#type\\s+(vertex|fragment)");
            int partsCount = shaderParts.length;

            if (partsCount != 3) {
                throw new IOException("Error: Invalid shader file format");
            }

            // Assign shaders based on order
            if (source.indexOf("#type vertex") < source.indexOf("#type fragment")) {
                vertexSource = shaderParts[1].trim();
                fragmentSource = shaderParts[2].trim();
            } else {
                vertexSource = shaderParts[2].trim();
                fragmentSource = shaderParts[1].trim();
            }

        } catch (IOException e) {
            e.printStackTrace();
            assert false : "Error: Could not open file for shader: " + filePath;
        }

        System.out.println(vertexSource);
        System.out.println(fragmentSource);
    }

    public void compile()
    {
        int vertexID, fragmentID;
        // First load the vertex shader
        vertexID = glCreateShader(GL_VERTEX_SHADER);

        // Pass the shader source to the GPU
        glShaderSource( vertexID, vertexSource );

        // Compile the vertex source
        glCompileShader( vertexID );

        // Check for errors in compilation process
        int success = glGetShaderi( vertexID, GL_COMPILE_STATUS );
        if(success == GL_FALSE)
        {
            int len = glGetShaderi( vertexID, GL_INFO_LOG_LENGTH );
            System.out.println( "ERROR: " + filePath + "\n\tVertex shader compilation failed. " );
            System.out.println( glGetShaderInfoLog( vertexID, len ) );
            assert false : "";
        }


        // First load the fragment shader
        fragmentID = glCreateShader(GL_FRAGMENT_SHADER);

        // Pass the shader source to the GPU
        glShaderSource( fragmentID, fragmentSource );

        // Compile the fragment source
        glCompileShader( fragmentID );

        // Check for errors in compilation process
        success = glGetShaderi( fragmentID, GL_COMPILE_STATUS );
        if(success == GL_FALSE)
        {
            int len = glGetShaderi( fragmentID, GL_INFO_LOG_LENGTH );
            System.out.println( "ERROR: " + filePath + "\n\tFragment shader compilation failed. " );
            System.out.println( glGetShaderInfoLog( fragmentID, len ) );
            assert false : "";
        }

        // Link shaders
        shaderProgramID = glCreateProgram();
        glAttachShader( shaderProgramID, vertexID );
        glAttachShader( shaderProgramID, fragmentID );
        glLinkProgram( shaderProgramID );

        // Check for linking errors
        success = glGetProgrami( shaderProgramID, GL_LINK_STATUS );
        if(success == GL_FALSE)
        {
            int len = glGetProgrami( shaderProgramID, GL_INFO_LOG_LENGTH );
            System.out.println( "ERROR: " + filePath + "'\n\tLinking of shaders failed. " );
            System.out.println( glGetProgramInfoLog( shaderProgramID, len ) );
            assert false : "";
        }
    }

    public void use()
    {
        // Bind shader program
        glUseProgram( shaderProgramID );
    }

    public void detach()
    {
        glUseProgram( 0 );
    }
}
