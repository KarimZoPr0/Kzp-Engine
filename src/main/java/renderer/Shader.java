package renderer;

import org.joml.*;
import org.lwjgl.BufferUtils;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;

public class Shader
{
    private int shaderProgramID;
    private boolean beingUsed = false;
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
        if(!beingUsed)
        {
            // Bind shader program
            glUseProgram( shaderProgramID );
            beingUsed = true;
        }
    }

    public void detach()
    {
        glUseProgram( 0 );
        beingUsed = false;
    }

    public void uploadMat4f( String varName, Matrix4f mat4 )
    {
        int varLocation = glGetUniformLocation( shaderProgramID, varName );
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer( 16 );
        mat4.get( matBuffer );
        glUniformMatrix4fv( varLocation, false, matBuffer );
    }

    public void uploadMat3f( String varName, Matrix3f mat3 )
    {
        int varLocation = glGetUniformLocation( shaderProgramID, varName );
        use();
        FloatBuffer matBuffer = BufferUtils.createFloatBuffer( 9 );
        mat3.get( matBuffer );
        glUniformMatrix3fv( varLocation, false, matBuffer );
    }


    public void uploadVec4f( String varName, Vector4f vec )
    {
        int varLocation = glGetUniformLocation( shaderProgramID, varName );
        use();
        glUniform4f( varLocation, vec.x, vec.y, vec.z, vec.w);
    }

    public void uploadVec3f( String varName, Vector3f vec )
    {
        int varLocation = glGetUniformLocation( shaderProgramID, varName );
        use();
        glUniform3f( varLocation, vec.x, vec.y, vec.z);
    }

    public void uploadVec2f( String varName, Vector2f vec )
    {
        int varLocation = glGetUniformLocation( shaderProgramID, varName );
        use();
        glUniform2f( varLocation, vec.x, vec.y);
    }

    public void uploadFloat(String varName, float val)
    {
        int varLocation = glGetUniformLocation( shaderProgramID, varName );
        use();
        glUniform1f( varLocation, val );
    }

    public void uploadInt(String varName, int val)
    {
        int varLocation = glGetUniformLocation( shaderProgramID, varName );
        use();
        glUniform1i( varLocation, val );
    }

    public void uploadTexture(String varName, int slot)
    {
        int varLocation = glGetUniformLocation( shaderProgramID, varName );
        use();
        glUniform1i( varLocation, slot );
    }

    public void uploadIntArray(String varName, int[] array)
    {
        int varLocation = glGetUniformLocation( shaderProgramID, varName );
        use();
        glUniform1iv( varLocation, array );
    }
}
