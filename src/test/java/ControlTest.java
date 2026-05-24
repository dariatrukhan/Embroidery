import org.junit.jupiter.api.*;
import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

public class ControlTest {
    private DrawController controller;

    @BeforeEach // дефолтний розмір 17х17
    public void setUp() {
        controller = new DrawController();
    }

    @Test // перевірка дефолтних розмірів
    public void testInitialGridSize() {
        assertEquals(17, controller.getRowsCount(), "рядків - 17");
        assertEquals(17, controller.getColsCount(), "стовпців - 17");
    }

    @Test // перевірка нової сітки на пустоту (заповнена нулями)
    public void testInitialGridIsEmpty() {
        assertTrue(controller.isGridEmpty(), "нова сітка повинна бути чистою");
    }

    @Test  // створення будь-якої форми полотна
    public void testChangeGridSizeToRectangle() {
        controller.changeGridSize(25, 40);
        assertEquals(25, controller.getRowsCount(), "рядків - 25");
        assertEquals(40, controller.getColsCount(), "стовпців - 40");
        assertTrue(controller.isGridEmpty(), "полотно має бути чистим");
    }

    @Test // замальовування клітинки при handleDraw
    public void testHandleDrawFillsCell() {
        controller.setSelectedColor(Color.RED);
        controller.handleDraw(5, 5, 0, 0, 510);
        int[][] grid = controller.getGrid();
        assertEquals(Color.RED.getRGB(), grid[0][0], "клітинка - червона");
    }

    @Test // Undo
    public void testUndoRestoresPreviousState() {
        controller.setSelectedColor(Color.BLACK);
        controller.saveStateToUndo();
        controller.handleDraw(5, 5, 0, 0, 510);
        assertFalse(controller.isGridEmpty(), "полотно не порожнє");
        boolean undoResult = controller.undo();
        assertTrue(undoResult);
        assertTrue(controller.isGridEmpty(), "після Undo полотно стає чистим");
    }

    @Test // Redo
    public void testRedoRestoresCanceledAction() {
        controller.setSelectedColor(Color.BLACK);
        controller.saveStateToUndo();
        controller.handleDraw(5, 5, 0, 0, 510);
        controller.undo();
        assertTrue(controller.isGridEmpty());
        boolean redoResult = controller.redo();
        assertTrue(redoResult);
        assertEquals(Color.BLACK.getRGB(), controller.getGrid()[0][0], "Redo повертає чорну точку");
    }
}
