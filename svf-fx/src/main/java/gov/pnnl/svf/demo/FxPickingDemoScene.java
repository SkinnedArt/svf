package gov.pnnl.svf.demo;

import gov.pnnl.svf.scene.FxAbstractScene;
import javax.media.opengl.GL2;

/**
 *
 * @author Arthur Bleeker
 */
public class FxPickingDemoScene extends FxAbstractScene {

    /**
     * Constructor
     */
    public FxPickingDemoScene() {
        super();
    }

    @Override
    public void initialize(final GL2 gl) {
        // no operation
    }

    @Override
    public void load() {
        // load the scene
        // TODO when JavaFX is more mature move demo to svf-demo
        //        new PickingDemoLoader().load(this);
    }
}