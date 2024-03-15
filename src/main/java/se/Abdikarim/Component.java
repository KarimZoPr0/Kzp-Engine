package se.Abdikarim;

public abstract class Component
{
    public GameObject gameObject = null;

    public void start()
    {

    }
    protected abstract void update( float dt );
}
