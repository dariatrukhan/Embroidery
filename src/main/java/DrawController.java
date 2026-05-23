import java.awt.Color;

public class DrawController {
    private int gridCount = 17;
    private int[][] grid = new int[gridCount][gridCount];

    private Color selectedColor = new Color(193, 91, 91);
    private boolean EraserActive = false;
    private boolean HorActive = false;
    private boolean VerActive = false;

    public DrawController() {
        clearGrid();
    }

    public void changeGridSize(int newSize) {
        this.gridCount = newSize;
        this.grid = new int[gridCount][gridCount];
        clearGrid();
    }

    public void clearGrid() {
        for (int row = 0; row < gridCount; row++) {
            for (int col = 0; col < gridCount; col++) {
                grid[row][col] = 0;
            }
        }
    }

    public void handleDraw(int mouseX, int mouseY, int offsetX, int offsetY, int fixedCanvasSize) {
        if (mouseX >= offsetX && mouseX < offsetX + fixedCanvasSize &&
                mouseY >= offsetY && mouseY < offsetY + fixedCanvasSize) {

            double exactCellSize = (double) fixedCanvasSize / gridCount;

            int col = (int) ((mouseX - offsetX) / exactCellSize);
            int row = (int) ((mouseY - offsetY) / exactCellSize);

            if (col >= gridCount) col = gridCount - 1;
            if (row >= gridCount) row = gridCount - 1;

            if (col >= 0 && row >= 0) {
                int valueToSet = EraserActive ? 0 : selectedColor.getRGB();
                grid[row][col] = valueToSet;

                if (HorActive) grid[row][gridCount - 1 - col] = valueToSet;
                if (VerActive) grid[gridCount - 1 - row][col] = valueToSet;
                if (HorActive && VerActive) grid[gridCount - 1 - row][gridCount - 1 - col] = valueToSet;
            }
        }
    }

    public int[][] getGrid() { return grid; }
    public void setGrid(int[][] newGrid) { this.grid = newGrid; this.gridCount = newGrid.length; }
    public int getGridCount() { return gridCount; }
    public Color getSelectedColor() { return selectedColor; }
    public void setSelectedColor(Color color) { this.selectedColor = color; }
    public boolean EraserActive() { return EraserActive; }
    public void setEraserActive(boolean active) { EraserActive = active; }
    public void setHorActive(boolean active) { HorActive = active; }
    public void setVerActive(boolean active) { VerActive = active; }
}
