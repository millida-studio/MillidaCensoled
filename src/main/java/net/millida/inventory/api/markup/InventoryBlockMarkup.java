package net.millida.inventory.api.markup;

public class InventoryBlockMarkup extends InventoryStandardMarkup {

    public InventoryBlockMarkup(int inventoryRows, int firstRowIndex, int tabSize) {
        super(inventoryRows);

        for (int rowIndex = firstRowIndex; rowIndex < inventoryRows; rowIndex++) {
            addHorizontalRow(rowIndex, tabSize);
        }
    }

    public InventoryBlockMarkup(int inventoryRows, int firstRowIndex) {
        this(inventoryRows, firstRowIndex, 1);
    }

    public InventoryBlockMarkup(int inventoryRows) {
        this(inventoryRows, 2, 1);
    }
}
