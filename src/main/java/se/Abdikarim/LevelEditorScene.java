    package se.Abdikarim;

    import components.SpriteRenderer;
    import org.joml.Vector2f;
    import org.joml.Vector4f;
    import util.AssetPool;

    public class LevelEditorScene extends Scene
    {
        public LevelEditorScene()
        {

        }
        @Override
        public void init( )
        {
            camera = new Camera( new Vector2f( -250, 0 ) );

            GameObject obj1 = new GameObject( "Object 1", new Transform( new Vector2f( 100,100 ), new Vector2f( 256,256 ) ) );
            obj1.addComponent( new SpriteRenderer( AssetPool.getTexture( "assets/images/skull.jpg" ) ) );
            addGameObjectToScene( obj1 );

            GameObject obj2 = new GameObject( "Object 2", new Transform( new Vector2f( 400,400 ), new Vector2f( 256,256 ) ) );
            obj2.addComponent( new SpriteRenderer( AssetPool.getTexture( "assets/images/miniskull.png" ) ) );
            addGameObjectToScene( obj2 );

            loadResources();
        }

        private void loadResources( )
        {
            AssetPool.getShader( "assets/shaders/default.glsl" );
        }

        @Override
        public void update( float dt )
        {
            System.out.println( "FPS: " + (1.0f / dt) );
            for ( GameObject gameObject : gameObjects )
            {
                gameObject.update( dt );
            }

            renderer.render();
        }
    }
