package se.Abdikarim;

import renderer.RenderBatch;
import renderer.Renderer;

import java.util.ArrayList;
import java.util.List;

public abstract class Scene
{
    protected Renderer renderer = new Renderer();
    protected Camera camera;
    private boolean isRunning = false;

    protected List<GameObject> gameObjects = new ArrayList<>(  );

    public Scene()
    {

    }

    public void init( )
    {

    }

    public void start()
    {
        for ( GameObject gameObject : gameObjects )
        {
            gameObject.start();
            renderer.add( gameObject );
        }

        isRunning = true;
    }

    public void addGameObjectToScene(GameObject gameObject)
    {
        if(!isRunning)
        {
            gameObjects.add( gameObject );
        }
        else
        {
            gameObjects.add( gameObject );
            gameObject.start();
            renderer.add( gameObject );
        }
    }

    public abstract void update(float dt);

    public Camera getCamera( )
    {
        return camera;
    }
}
