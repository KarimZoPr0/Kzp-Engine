package components;

import org.joml.Vector2f;
import org.joml.Vector4f;
import renderer.Texture;
import se.Abdikarim.Component;

public class SpriteRenderer extends Component
{

    private Vector4f color;

    private Vector2f[] texCoords;

    private Texture texture;

    public SpriteRenderer(Vector4f color)
    {
        this.color = color;
        texture = null;
    }

    public SpriteRenderer(Texture texture)
    {
        this.texture = texture;
        color = new Vector4f( 1,1,1,1 );
    }
    @Override
    public void start( )
    {
    }

    @Override
    protected void update( float dt )
    {

    }

    public Vector4f getColor( )
    {
        return color;
    }

    public Texture getTexture( )
    {
        return texture;
    }

    public Vector2f[] getTexCoords( )
    {
        return new Vector2f[]{
                new Vector2f( 1,1 ),
                new Vector2f( 1,0 ),
                new Vector2f( 0,0 ),
                new Vector2f( 0,1 )
        };
    }
}
