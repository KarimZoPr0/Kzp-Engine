package se.Abdikarim;

import org.joml.Vector2f;

public class Transform
{
    public Vector2f position;
    public Vector2f scale;

    public Transform(Vector2f position, Vector2f scale)
    {
        this.position = position;
        this.scale = scale;
    }

    public Transform()
    {
       this(new Vector2f(  ), new Vector2f(  ));
    }

    public Transform(Vector2f position)
    {
        this(position, new Vector2f(  ));
    }
}
