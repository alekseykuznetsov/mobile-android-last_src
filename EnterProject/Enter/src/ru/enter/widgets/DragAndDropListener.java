package ru.enter.widgets;

public interface DragAndDropListener {
	public void onStartDrag(int positionInGrid);
    public void onDrag(int positionInGrid, int x, int y);
    public void onStopDrag(int positionInGrid, int x, int y);
    public void onClick(int positionInGrid);
}
