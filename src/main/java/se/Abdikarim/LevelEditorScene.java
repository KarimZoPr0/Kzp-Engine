package se.Abdikarim;

import org.lwjgl.BufferUtils;
import renderer.Shader;

import java.nio.FloatBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class LevelEditorScene extends Scene
{
    private final String vertexShaderSrc = "#version 330 core\n" +
            "\n" +
            "layout (location=0) in vec3 aPos;\n" +
            "layout (location=1) in vec4 aColor;\n" +
            "\n" +
            "out vec4 fColor;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    fColor = aColor;\n" +
            "    gl_Position = vec4(aPos, 1.0);\n" +
            "}";
    private final String fragmentShaderSrc = "#version 330 core\n" +
            "\n" +
            "in vec4 fColor;\n" +
            "\n" +
            "out vec4 color;\n" +
            "\n" +
            "void main()\n" +
            "{\n" +
            "    color = fColor;\n" +
            "}";

    private int vertexID, fragmentID, shaderProgram;

    private Shader defaultShader;

    private float[] vertexArray = {
             // position               // color
             0.5f,  -0.5f, 0.0f,       1.0f, 0.0f, 0.0f, 1.0f, // Bottom Right  0
             0.5f,   0.5f, 0.0f,       1.0f, 1.0f, 0.0f, 1.0f, // Top Right     1
            -0.5f,   0.5f, 0.0f,       0.0f, 1.0f, 0.0f, 1.0f, // Top left      2
            -0.5f,  -0.5f, 0.0f,       0.0f, 0.0f, 0.0f, 1.0f, // Bottom Left   3
    };

    // IMPORTANT: must be in counter-clockwise order
    private int[] elementArray = {
            0,1,2, // Top Right Triangle
            0,2,3, // Bottom Left Triangle
    };

    private int vaoID, vboID, eboID;

    public LevelEditorScene()
    {
    }

    @Override
    public void init( )
    {

        defaultShader = new Shader( "assets/shaders/default.glsl" );
        defaultShader.compile();

        // Generate VAO; VBO, and EBO buffer objects, and send to GPU
        vaoID = glGenVertexArrays();
        glBindVertexArray( vaoID );

        // Create a float buffer of vertices
        FloatBuffer vertexBuffer = BufferUtils.createFloatBuffer( vertexArray.length );
        vertexBuffer.put( vertexArray ).flip();

        // Create VBO upload the vertex buffer
        vboID = glGenBuffers();
        glBindBuffer( GL_ARRAY_BUFFER, vboID );
        glBufferData( GL_ARRAY_BUFFER, vertexBuffer, GL_STATIC_DRAW );

        // Create the indices and upload
        IntBuffer elementBuffer = BufferUtils.createIntBuffer( elementArray.length );
        elementBuffer.put( elementArray ).flip();

        eboID = glGenBuffers();
        glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, eboID );
        glBufferData( GL_ELEMENT_ARRAY_BUFFER, elementBuffer, GL_STATIC_DRAW );

        // Add the vertex attribute pointers
        int positionSize = 3;
        int colorSize = 4;
        int floatSizeBytes = 4;
        int vertexSizeBytes = (positionSize + colorSize ) * floatSizeBytes;

        glVertexAttribPointer( 0, positionSize, GL_FLOAT, false,  vertexSizeBytes, 0);
        glEnableVertexAttribArray( 0 );

        glVertexAttribPointer( 1, colorSize, GL_FLOAT, false, vertexSizeBytes,positionSize * floatSizeBytes );
        glEnableVertexAttribArray( 1 );
    }

    @Override
    public void update( float dt )
    {
        defaultShader.use();

        // Bind the VAO
        glBindVertexArray( vaoID );

        // Enable the vertex attribute pointers
        glEnableVertexAttribArray( 0 );
        glEnableVertexAttribArray( 1 );

        glDrawElements( GL_TRIANGLES, elementArray.length, GL_UNSIGNED_INT, 0 );

        // Unbind everything
        glDisableVertexAttribArray( 0 );
        glDisableVertexAttribArray( 1 );
        glBindVertexArray( 0 );

        defaultShader.detach();
    }
}
