import java.awt.Color;

public class DrawController {
    private int rowsCount = 17;
    private int colsCount = 17;
    private int[][] grid = new int[rowsCount][colsCount];

    private Color selectedColor = new Color(193, 91, 91);
    private boolean EraserActive = false;
    private boolean HorActive = false;
    private boolean VerActive = false;

    private boolean RepeatActive = false;
    private int repeatStep = 5;

    public DrawController() {
        clearGrid();
    }

    public void changeGridSize(int newSize, int width) {
        this.rowsCount = newSize;
        this.colsCount = newSize;
        this.grid = new int[rowsCount][colsCount];
        clearGrid();
    }

    public void clearGrid() {
        for (int row = 0; row < rowsCount; row++) {
            for (int col = 0; col < colsCount; col++) {
                grid[row][col] = 0;
            }
        }
    }

    public void handleDraw(int mouseX, int mouseY, int offsetX, int offsetY, int fixedCanvasSize) {
        int maxDimension = Math.max(rowsCount, colsCount);
        double exactCellSize = (double) fixedCanvasSize / maxDimension;

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
    public int getRowsCount() { return rowsCount; }
    public int getColsCount() { return colsCount; }
    public Color getSelectedColor() { return selectedColor; }
    public void setSelectedColor(Color color) { this.selectedColor = color; }
    public boolean EraserActive() { return EraserActive; }
    public void setEraserActive(boolean active) { EraserActive = active; }
    public void setHorActive(boolean active) { HorActive = active; }
    public void setVerActive(boolean active) { VerActive = active; }
    public void setRepeatActive(boolean active) { this.RepeatActive = active; }
    public int getRepeatStep() { return repeatStep; }
    public void setRepeatStep(int step) { this.repeatStep = step; }
    public void setGridCount(int patternGridSize) { this.rowsCount = patternGridSize; this.colsCount = patternGridSize; }
}
