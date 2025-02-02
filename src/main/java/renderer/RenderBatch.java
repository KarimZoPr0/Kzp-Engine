package renderer;

import components.SpriteRenderer;
import org.joml.Vector2f;
import org.joml.Vector4f;
import se.Abdikarim.Window;
import util.AssetPool;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL20.*;
import static org.lwjgl.opengl.GL30.glBindVertexArray;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class RenderBatch
{
    // Vertex
    // ====
    // Pos              Color                           tex coords      tex id
    // float, float,    float, float, float, float      float, float    float

    private final int POS_SIZE = 2;
    private final int COLOR_SIZE = 4;
    private final int TEX_COORDS_SIZE = 2;
    private final int TEX_ID_SIZE = 1;

    private final int POS_OFFSET = 0;
    private final int COLOR_OFFSET = POS_OFFSET + POS_SIZE * Float.BYTES ;
    private final int TEX_COORDS_OFFSET = COLOR_OFFSET + COLOR_SIZE * Float.BYTES;
    private final int TEX_ID_OFFSET = TEX_COORDS_OFFSET + TEX_COORDS_SIZE  * Float.BYTES;
    private final int VERTEX_SIZE = 9 ;
    private final int VERTEX_SIZE_BYTES = VERTEX_SIZE * Float.BYTES;

    private SpriteRenderer[] sprites;
    private int numSprites;
    private boolean hasRoom;
    private float[] vertices;
    private int[] texSlots = {0,1,2,3,4,5,6,7};
    private List<Texture> textures;
    private int vaoID, vboID;
    private int maxBatchSize;
    private Shader shader;

    public RenderBatch(int maxBatchSize)
    {

        shader = AssetPool.getShader( "assets/shaders/default.glsl" );
        shader.compile();
        sprites = new SpriteRenderer[maxBatchSize];
        this.maxBatchSize = maxBatchSize;

        // 4 vertices quads
        vertices = new float[maxBatchSize * 4 * VERTEX_SIZE];

        numSprites = 0;
        hasRoom = true;
        textures = new ArrayList<>(  );
    }

    public void start()
    {
        // Generate and bind a Vertex Array Object
        vaoID = glGenVertexArrays();
        glBindVertexArray( vaoID );

        // Allocate space for the vertices
        vboID = glGenBuffers();
        glBindBuffer( GL_ARRAY_BUFFER, vboID );
        glBufferData( GL_ARRAY_BUFFER,  vertices.length * Float.BYTES, GL_DYNAMIC_DRAW);

        // Create and upload the indices buffer
        int eboID = glGenBuffers();
        glBindBuffer( GL_ELEMENT_ARRAY_BUFFER, eboID );

        int[] indices = generateIndices();
        glBufferData( GL_ELEMENT_ARRAY_BUFFER,  indices, GL_STATIC_DRAW);

        // Enable the buffer attribute pointers
        glVertexAttribPointer( 0, POS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES,  POS_OFFSET);
        glEnableVertexAttribArray( 0 );

        glVertexAttribPointer( 1, COLOR_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, COLOR_OFFSET);
        glEnableVertexAttribArray( 1 );

        glVertexAttribPointer( 2,  TEX_COORDS_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_COORDS_OFFSET);
        glEnableVertexAttribArray( 2 );

        glVertexAttribPointer( 3, TEX_ID_SIZE, GL_FLOAT, false, VERTEX_SIZE_BYTES, TEX_ID_OFFSET );
        glEnableVertexAttribArray( 3 );

    }

    public void addSprite(SpriteRenderer spriteRenderer)
    {
        // Get index and add renderObject
        int index = numSprites;
        sprites[index] = spriteRenderer;
        numSprites++;

        if(spriteRenderer.getTexture() != null)
        {
            if(!textures.contains( spriteRenderer.getTexture() ))
            {
                textures.add( spriteRenderer.getTexture() );
            }
        }

        // Add properties to local vertices array
        loadVertexProperties(index);

        if(numSprites >= maxBatchSize)
        {
            hasRoom = false;
        }
    }

    private void loadVertexProperties( int index )
    {
        SpriteRenderer sprite = sprites[index];

        // Find offset within array (4 vertices per sprite)
        int offset = index * 4 * VERTEX_SIZE;

        Vector4f color = sprite.getColor();
        Vector2f[] texCoords = sprite.getTexCoords();

        int texID = 0;

        // [tex, tex, tex, tex, ...]
        if(sprite.getTexture() != null)
        {
            for ( int i = 0; i < textures.size( ); i++ )
            {
                if(textures.get( i ) == sprite.getTexture())
                {
                    texID = i + 1;
                    break;
                }
            }
        }


        // Add vertices with the appropriate properties
        float xAdd = 1.0f;
        float yAdd = 1.0f;

        // *    *
        // *    *
        for ( int i = 0; i < 4; i++ )
        {
            if(i == 1)
            {
                yAdd = 0.0f;
            }
            else if(i == 2)
            {
                xAdd = 0.0f;
            }
            else if(i == 3)
            {
                yAdd = 1.0f;
            }

            // Vertex
            // ====
            // Pos              Color
            // float, float,    float, float, float, float

            vertices[offset] = sprite.gameObject.transform.position.x + (xAdd * sprite.gameObject.transform.scale.x);
            vertices[offset + 1] = sprite.gameObject.transform.position.y + (yAdd * sprite.gameObject.transform.scale.y);

            // Load color
            vertices[offset + 2] = color.x;
            vertices[offset + 3] = color.y;
            vertices[offset + 4] = color.z;
            vertices[offset + 5] = color.w;

            // load texCoords
            vertices[offset + 6] = texCoords[i].x;
            vertices[offset + 7] = texCoords[i].y;


            // Load texture id
            vertices[offset + 8] = texID;


            offset += VERTEX_SIZE;
        }
    }

    public void render()
    {
        // For now, we will rebuffer all data every frame
        glBindBuffer( GL_ARRAY_BUFFER, vboID );
        glBufferSubData( GL_ARRAY_BUFFER, 0, vertices );

        // Use Shader
        shader.use();
        shader.uploadMat4f( "uProjection", Window.getScene().getCamera().getProjectionMatrix() );
        shader.uploadMat4f( "uView", Window.getScene().getCamera().getViewMatrix() );

        for ( int i = 0; i < textures.size( ); i++ )
        {
            glActiveTexture( GL_TEXTURE0 + i + 1);
            textures.get( i ).bind();
        }

        shader.uploadIntArray("uTextures", texSlots);

        glBindVertexArray( vaoID );
        glEnableVertexAttribArray( 0 );
        glEnableVertexAttribArray( 1 );

        glDrawElements( GL_TRIANGLES, 6 * numSprites, GL_UNSIGNED_INT, 0 );

        glDisableVertexAttribArray( 0 );
        glDisableVertexAttribArray( 1 );
        glBindVertexArray( 0 );

        for ( int i = 0; i < textures.size( ); i++ )
        {
            textures.get( i ).unbind();
        }

        shader.detach();
    }

    private int[] generateIndices( )
    {
        // 6 indices per quad (3 per triangle)
        int[] elements = new int[6 * maxBatchSize];

        for ( int i = 0; i < maxBatchSize; i++ )
        {
            loadElementIndices( elements, i );
        }
        return elements;
    }

    private void loadElementIndices( int[] elements, int index )
    {
        int offsetArrayIndex = 6 * index;
        int offset = 4 * index;

        // 3, 2, 0, 0, 2, 1       7, 6, 4, 4, 6, 5
        // Triangle 1
        elements[offsetArrayIndex] = 3 + offset;
        elements[offsetArrayIndex + 1] = 2 + offset;
        elements[offsetArrayIndex + 2] = 0 + offset;

        // Triangle 2
        elements[offsetArrayIndex + 3] = 0 + offset;
        elements[offsetArrayIndex + 4] = 2 + offset;
        elements[offsetArrayIndex + 5] = 1 + offset;

    }

    public boolean hasRoom( )
    {
        return hasRoom;
    }
}
