package io.openliberty.tools.intellij.it.fixtures;

import com.intellij.remoterobot.fixtures.ComponentFixture;

import java.awt.*;

public class DragAndDropUtils {
    /**
     * Resizes a component by adjusting its bounds and dragging its bottom-right corner.
     *
     * @param fixture The component fixture
     * @param size    The new size for the component
     */
    public static void resizeBottomRight(DialogFixture fixture, Dimension size) {
        Rectangle boundsBeforeResize = new Rectangle(
                fixture.getLocationOnScreen().x,
                fixture.getLocationOnScreen().y,
                fixture.getRemoteComponent().getWidth(),
                fixture.getRemoteComponent().getHeight()
        );

        Point bottomRightCorner = new Point(
                boundsBeforeResize.x + boundsBeforeResize.width - 5,
                boundsBeforeResize.y + boundsBeforeResize.height - 5
        );

        Point shiftedBottomRight = shift(
                bottomRightCorner,
                size.width - boundsBeforeResize.width,
                size.height - boundsBeforeResize.height
        );

        dragAndDrop(fixture, bottomRightCorner, shiftedBottomRight);
    }

    public static void resizeTopLeft(DialogFixture fixture, Dimension size) {
        Rectangle boundsBeforeResize = new Rectangle(
                fixture.getLocationOnScreen().x,
                fixture.getLocationOnScreen().y,
                fixture.getRemoteComponent().getWidth(),
                fixture.getRemoteComponent().getHeight()
        );

        Point topLeftCorner = new Point(
                boundsBeforeResize.x + 5,
                boundsBeforeResize.y + 5
        );

        Point shiftedTopLeft = shift(
                topLeftCorner,
                boundsBeforeResize.width - size.width,
                boundsBeforeResize.height - size.height
        );

        dragAndDrop(fixture, topLeftCorner, shiftedTopLeft);
    }




    /**
     * Performs a drag-and-drop operation between two points.
     *
     * @param fixture   The component fixture
     * @param startPoint The starting point of the drag
     * @param endPoint   The ending point of the drag
     */
    public static void dragAndDrop(DialogFixture fixture, Point startPoint, Point endPoint) {
        fixture.getRemoteRobot().runJs(
                "const pointStart = new Point(" + startPoint.x + ", " + startPoint.y + ");" +
                        "const pointEnd = new Point(" + endPoint.x + ", " + endPoint.y + ");" +
                        "try {" +
                        "    robot.moveMouse(pointStart);" +
                        "    Thread.sleep(300);" +
                        "    robot.pressMouse(MouseButton.LEFT_BUTTON);" +
                        "    Thread.sleep(500);" +
                        "    robot.moveMouse(pointEnd);" +
                        "} finally {" +
                        "    Thread.sleep(500);" +
                        "    robot.releaseMouse(MouseButton.LEFT_BUTTON);" +
                        "}"
        );
    }

    /**
     * Shifts a point by the specified x and y values.
     *
     * @param point The original point
     * @param dx    The shift in the x direction
     * @param dy    The shift in the y direction
     * @return A new point shifted by the specified values
     */
    public static Point shift(Point point, int dx, int dy) {
        return new Point(point.x + dx, point.y + dy);
    }
}