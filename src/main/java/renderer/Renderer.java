package renderer;

import components.SpriteRenderer;
import se.Abdikarim.GameObject;

import java.util.ArrayList;
import java.util.List;

public class Renderer
{
    private final int MAX_BATCH_SIZE = 1000;
    private List<RenderBatch> batches;

    public Renderer()
    {
        batches = new ArrayList<>(  );
    }

    public void add( GameObject go )
    {
        SpriteRenderer spriteRenderer = go.getComponent( SpriteRenderer.class );
        if(spriteRenderer != null)
        {
            add( spriteRenderer );
        }
    }

    private void add(SpriteRenderer sprite)
    {
        boolean added = false;
        for ( RenderBatch batch : batches )
        {
            if(batch.hasRoom())
            {
                batch.addSprite( sprite );
                added = true;
                break;
            }
        }

        if(!added)
        {
            RenderBatch newBatch = new RenderBatch( MAX_BATCH_SIZE );
            newBatch.start();
            batches.add( newBatch );
            newBatch.addSprite( sprite );
        }
    }

    public void render()
    {
        for ( RenderBatch batch : batches )
        {
            batch.render();
        }
    }
}
