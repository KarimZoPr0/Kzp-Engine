package se.Abdikarim;

import java.util.ArrayList;
import java.util.List;

public class GameObject
{
    private String name;
    private List<Component> components;

    public Transform transform;

    public GameObject(String name, Transform transform)
    {
        this.name = name;
        this.transform = transform;
        components = new ArrayList<>(  );
    }

    public GameObject( String name )
    {
        this(name, new Transform(  ));
    }

    public <T extends Component> T getComponent(Class<T> componentClass)
    {
        for ( Component component : components )
        {
            if(componentClass.isAssignableFrom( component.getClass() ))
            {
                try
                {
                    return componentClass.cast( component );
                }catch ( ClassCastException e )
                {
                    e.printStackTrace();
                    assert false : "Error: Casting component";
                }
            }
        }
        return null;
    }

    public <T extends Component> void removeComponent(Class<T> componentClass)
    {
        for ( Component component : components )
        {
            if(componentClass.isAssignableFrom( component.getClass() ))
            {
                components.remove( component );
                return;
            }
        }
    }

    public void addComponent(Component component)
    {
        components.add( component );
        component.gameObject = this;
    }

    public void update(float dt)
    {
        for ( Component component : components )
        {
            component.update( dt );
        }
    }

    public void start()
    {
        for ( Component component : components )
        {
            component.start();
        }
    }
}
