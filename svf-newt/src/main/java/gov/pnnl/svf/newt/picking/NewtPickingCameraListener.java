package gov.pnnl.svf.newt.picking;

import com.jogamp.nativewindow.util.Point;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import gov.pnnl.svf.event.CameraEventType;
import gov.pnnl.svf.event.PickingCameraEvent;
import gov.pnnl.svf.geometry.Rectangle;
import gov.pnnl.svf.newt.util.NewtCameraUtils;
import gov.pnnl.svf.picking.AbstractPickingCamera;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.EnumSet;
import java.util.Set;
import javax.swing.Timer;

/**
 * This camera builds a picking view for a scene. Actor's that have picking
 * support will be notified when clicked or double clicked in a camera view that
 * is attached to this picking camera.
 *
 * @author Arthur Bleeker
 *
 */
class NewtPickingCameraListener implements MouseListener, KeyListener {

    private static final int AREA_SIZE = 4;
    private static final Set<CameraEventType> MOVE = Collections.unmodifiableSet(EnumSet.of(CameraEventType.MOVE));
    protected final AbstractPickingCamera camera;
    protected final Timer timer;
    protected final TimerAction action;
    private int x = 0;
    private int y = 0;

    /**
     * Constructor
     *
     * @param camera
     */
    NewtPickingCameraListener(final AbstractPickingCamera camera) {
        super();
        this.camera = camera;
        action = new TimerAction();
        timer = new Timer(750, action);
        timer.setRepeats(false);
    }

    /**
     * Specifies the initial delay value.
     *
     * @param milliseconds the number of milliseconds to delay (after the cursor
     *                     has paused) before firing a hover event
     *
     * @see #getInitialDelay
     */
    public void setInitialDelay(final int milliseconds) {
        timer.setInitialDelay(milliseconds);
    }

    /**
     * Returns the initial delay value.
     *
     * @return an integer representing the initial delay value, in milliseconds
     *
     * @see #setInitialDelay
     */
    public int getInitialDelay() {
        return timer.getInitialDelay();
    }

    @Override
    public void keyPressed(final KeyEvent event) {
        final Point location = (Point) event.getSource();
        final Rectangle viewport = camera.getViewport();
        final Rectangle sceneViewport = camera.getScene().getViewport();
        if (!viewport.contains((int) location.getX(), sceneViewport.getHeight() - (int) location.getY())) {
            return;
        }
        // key down event
        final Set<CameraEventType> types = EnumSet.of(CameraEventType.DOWN);
        NewtCameraUtils.addButtonTypes(event, types);
        NewtCameraUtils.addModifierTypes(event, types);
        // process the pick
        camera.addEvent(new PickingCameraEvent(camera, (int) location.getX(), (int) location.getY(), event.getKeyChar(), types));
    }

    @Override
    public void keyReleased(final KeyEvent event) {
        final Point location = (Point) event.getSource();
        final Set<CameraEventType> types = EnumSet.noneOf(CameraEventType.class);
        NewtCameraUtils.addButtonTypes(event, types);
        NewtCameraUtils.addModifierTypes(event, types);
        // process the area pick
        final int w = Math.abs(x - (int) location.getX()) + 1;
        final int h = Math.abs(y - (int) location.getY()) + 1;
        if (w > AREA_SIZE || h > AREA_SIZE) {
            final Set<CameraEventType> areaTypes = EnumSet.copyOf(types);
            areaTypes.add(CameraEventType.AREA);
            camera.addEvent(new PickingCameraEvent(camera, Math.min(x, (int) location.getX()) + w / 2, Math.min(y, (int) location.getY()) + h / 2, w, h, event.getKeyChar(), areaTypes));
        }
        // process the pick
        camera.addEvent(new PickingCameraEvent(camera, (int) location.getX(), (int) location.getY(), event.getKeyChar(), types));
    }

    @Override
    public void mouseClicked(final MouseEvent evt) {
        // no operation
    }

    @Override
    public void mouseDragged(final MouseEvent evt) {
        // no operation
    }

    @Override
    public void mouseEntered(final MouseEvent evt) {
        x = evt.getX();
        y = evt.getY();
        action.startHoverTimer(evt);
    }

    @Override
    public void mouseExited(final MouseEvent evt) {
        action.stopHoverTimer();
    }

    @Override
    public void mouseMoved(final MouseEvent evt) {
        // filter move events here to reduce garbage
        if (camera.getPickTypes().contains(CameraEventType.MOVE)) {
            // create mouse moved currentEvent
            // we don't add button or modifier types to a move event
            final PickingCameraEvent pickEvent = new PickingCameraEvent(camera, evt.getX(), evt.getY(), evt.getClickCount(), MOVE);
            camera.addEvent(pickEvent);
        }
        action.startHoverTimer(evt);
    }

    @Override
    public void mousePressed(final MouseEvent evt) {
        x = evt.getX();
        y = evt.getY();
        // mouse down event
        final Set<CameraEventType> types = EnumSet.of(CameraEventType.DOWN);
        NewtCameraUtils.addButtonTypes(evt, types);
        NewtCameraUtils.addModifierTypes(evt, types);
        // process the pick
        camera.addEvent(new PickingCameraEvent(camera, evt.getX(), evt.getY(), evt.getClickCount(), types));
    }

    @Override
    public void mouseReleased(final MouseEvent evt) {
        final Set<CameraEventType> types = EnumSet.noneOf(CameraEventType.class);
        NewtCameraUtils.addButtonTypes(evt, types);
        NewtCameraUtils.addModifierTypes(evt, types);
        // click count
        if (Math.abs(x - evt.getX()) > camera.getDragSensitivity() || Math.abs(y - evt.getY()) > camera.getDragSensitivity()) {
            types.add(CameraEventType.DRAG);
        } else if (evt.getClickCount() == 1) {
            types.add(CameraEventType.SINGLE);
        } else if (evt.getClickCount() == 2) {
            types.add(CameraEventType.DOUBLE);
        } else {
            return;
        }
        // process the area pick
        final int w = Math.abs(x - evt.getX()) + 1;
        final int h = Math.abs(y - evt.getY()) + 1;
        if (w > AREA_SIZE || h > AREA_SIZE) {
            final Set<CameraEventType> areaTypes = EnumSet.copyOf(types);
            areaTypes.add(CameraEventType.AREA);
            camera.addEvent(new PickingCameraEvent(camera, Math.min(x, evt.getX()) + w / 2, Math.min(y, evt.getY()) + h / 2, w, h, evt.getClickCount(), areaTypes));
        }
        // process the pick
        camera.addEvent(new PickingCameraEvent(camera, evt.getX(), evt.getY(), evt.getClickCount(), types));
    }

    @Override
    public void mouseWheelMoved(final MouseEvent evt) {
        // down button
        final Rectangle viewport = camera.getViewport();
        final Rectangle sceneViewport = camera.getScene().getViewport();
        if (!viewport.contains(evt.getX(), sceneViewport.getHeight() - evt.getY())) {
            return;
        }
        x = evt.getX();
        y = evt.getY();
        action.stopHoverTimer();
        // up button
        final Set<CameraEventType> types = EnumSet.noneOf(CameraEventType.class);
        NewtCameraUtils.addButtonTypes(evt, types);
        NewtCameraUtils.addModifierTypes(evt, types);
        // process the pick
        camera.addEvent(new PickingCameraEvent(camera, evt.getX(), evt.getY(), Math.abs((int) Math.ceil(evt.getRotation()[1])), types));
    }

    private class TimerAction implements ActionListener {

        private MouseEvent event;

        private void startHoverTimer(final MouseEvent event) {
            this.event = event;
            timer.restart();
        }

        private void stopHoverTimer() {
            event = null;
            timer.stop();
        }

        @Override
        public void actionPerformed(final ActionEvent event) {
            if (this.event != null) {
                final Set<CameraEventType> types = EnumSet.noneOf(CameraEventType.class);
                types.add(CameraEventType.HOVER);
                NewtCameraUtils.addModifierTypes(this.event, types);
                camera.addEvent(new PickingCameraEvent(camera, this.event.getX(), this.event.getY(), 0, types));
            }
        }
    }
}
