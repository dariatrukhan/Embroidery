import java.awt.Color;
import java.util.Stack;

public class DrawController {
    private int rowsCount = 17;
    private int colsCount = 17;
    private int[][] grid = new int[rowsCount][colsCount];

    private Color selectedColor = new Color(193, 91, 91);
    private boolean EraserActive = false;
    private boolean HorActive = false;
    private boolean VerActive = false;

    private boolean RepeatActive = false;
    private int repeatStep = 1;
    private double scaleFactor = 1.0;
    private boolean isPipetteActive = false;

    private final Stack<int[][]> undoStack = new Stack<>();
    private final Stack<int[][]> redoStack = new Stack<>();

    public DrawController() {
        clearGrid();
    }

    private int[][] copyGrid(int[][] original) {
        int[][] copy = new int[original.length][original[0].length];
        for (int i = 0; i < original.length; i++) {
            System.arraycopy(original[i], 0, copy[i], 0, original[i].length);
        }
        return copy;
    }

    public void saveStateToUndo() {
        undoStack.push(copyGrid(this.grid));
        redoStack.clear();
    }

    public boolean undo() {
        if (!undoStack.isEmpty()) {
            redoStack.push(copyGrid(this.grid));
            int[][] previousGrid = undoStack.pop();
            this.grid = previousGrid;
            this.rowsCount = previousGrid.length;
            this.colsCount = previousGrid[0].length;
            return true;
        }
        return false;
    }

    public boolean redo() {
        if (!redoStack.isEmpty()) {
            undoStack.push(copyGrid(this.grid));
            int[][] nextGrid = redoStack.pop();
            this.grid = nextGrid;
            this.rowsCount = nextGrid.length;
            this.colsCount = nextGrid[0].length;
            return true;
        }
        return false;
    }
    public void changeGridSize(int newRows, int newCols) {
        saveStateToUndo();
        this.rowsCount = newRows;
        this.colsCount = newCols;
        this.grid = new int[rowsCount][colsCount];
        clearGrid();
    }

    public void clearGrid() {
        if (grid != null) {
            saveStateToUndo();
        }
        for (int row = 0; row < rowsCount; row++) {
            for (int col = 0; col < colsCount; col++) {
                grid[row][col] = 0;
            }
        }
    }

    public double getScaleFactor() { return scaleFactor; }

    public void zoomIn() {
        if (scaleFactor < 3.0) {
            scaleFactor += 0.1;
        }
    }

    public void zoomOut() {
        if (scaleFactor > 0.5) {
            scaleFactor -= 0.1;
        }
    }

    public void handleDraw(int mouseX, int mouseY, int offsetX, int offsetY, int fixedCanvasSize) {
        if (isPipetteActive) {
            return;
        }

        int maxDimension = Math.max(rowsCount, colsCount);

        double exactCellSize = ((double) fixedCanvasSize / maxDimension) * scaleFactor;

        int col = (int) ((mouseX - offsetX) / exactCellSize);
        int row = (int) ((mouseY - offsetY) / exactCellSize);

        if (row >= 0 && row < rowsCount && col >= 0 && col < colsCount) {
            int valueToSet = EraserActive ? 0 : selectedColor.getRGB();

            grid[row][col] = valueToSet;

            if (HorActive) grid[row][colsCount - 1 - col] = valueToSet;
            if (VerActive) grid[rowsCount - 1 - row][col] = valueToSet;
            if (HorActive && VerActive) grid[rowsCount - 1 - row][colsCount - 1 - col] = valueToSet;

            if (RepeatActive) {
                int startRow = row % repeatStep;
                int startCol = col % repeatStep;

                for (int r = startRow; r < rowsCount; r += repeatStep) {
                    for (int c = startCol; c < colsCount; c += repeatStep) {
                        grid[r][c] = valueToSet;
                        if (HorActive) grid[r][colsCount - 1 - c] = valueToSet;
                        if (VerActive) grid[rowsCount - 1 - r][c] = valueToSet;
                        if (HorActive && VerActive) grid[rowsCount - 1 - r][colsCount - 1 - c] = valueToSet;
                    }
                }
            }
        }
    }

    public void duplicateVertically() {
        if (rowsCount * 2 > 69) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Неможливо дублювати! Висота перевищить ліміт (68 клітинок).",
                    "Перевищено ліміт",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        saveStateToUndo();

        int[][] tempGrid = new int[rowsCount * 2][colsCount];
        for (int i = 0; i < rowsCount; i++) {
            for (int j = 0; j < colsCount; j++) {
                tempGrid[i][j] = grid[i][j];
                tempGrid[i + rowsCount][j] = grid[i][j];
            }
        }
        this.rowsCount *= 2;
        this.grid = tempGrid;
    }

    public void duplicateHorizontally() {
        if (colsCount * 2 > 69) {
            javax.swing.JOptionPane.showMessageDialog(null,
                    "Неможливо дублювати! Ширина перевищить ліміт (68 клітинок).",
                    "Перевищено ліміт",
                    javax.swing.JOptionPane.WARNING_MESSAGE);
            return;
        }
        saveStateToUndo();

        int[][] tempGrid = new int[rowsCount][colsCount * 2];
        for (int i = 0; i < rowsCount; i++) {
            for (int j = 0; j < colsCount; j++) {
                tempGrid[i][j] = grid[i][j];
                tempGrid[i][j + colsCount] = grid[i][j];
            }
        }
        this.colsCount *= 2;
        this.grid = tempGrid;
    }

    public int[][] getGrid() { return grid; }
    public void setGrid(int[][] newGrid) {
        this.grid = newGrid;
        this.rowsCount = newGrid.length;
        this.colsCount = newGrid[0].length;
    }
    public boolean isGridEmpty() {
        for (int r = 0; r < rowsCount; r++) {
            for (int c = 0; c < colsCount; c++) {
                if (grid[r][c] != 0) return false;
            }
        }
        return true;
    }
    public int getRowsCount() { return rowsCount; }
    public int getColsCount() { return colsCount; }
    public Color getSelectedColor() { return selectedColor; }
    public void setSelectedColor(Color color) { this.selectedColor = color; }
    public boolean EraserActive() { return EraserActive; }
    public void setEraserActive(boolean active) { EraserActive = active; }
    public void setHorActive(boolean active) { HorActive = active; }
    public void setVerActive(boolean active) { VerActive = active; }
    public void setRepeatActive(boolean active) { this.RepeatActive = active; }
    public void setRepeatStep(int step) { this.repeatStep = step; }
    public void setGridCount(int patternGridSize) { this.rowsCount = patternGridSize; this.colsCount = patternGridSize; }
    public void setPipetteActive(boolean active) { this.isPipetteActive = active; }
}
