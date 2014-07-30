package grid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Holds the contents and location of a single square in the grid.
 */
public class Square {

	// Maintain a thread-safe stack of GridObjects
	private List<GridObject> gridObjects = new ArrayList<GridObject>();
	public final GridPoint location;
	
	/**
	 * Simple constructor to make each SquareContents instance aware of its location
	 * @param location This square's location on the grid
	 */
	public Square(GridPoint location) {
		this.location = location;
	}
	
	public synchronized void add(GridObject gridObject) {
		gridObjects.add(gridObject);
	}

	/**
	 * Get the topmost (i.e. most recently added) GridObject that matches the given type.
	 * Returns null if there are no matching instances of GridObject in this square.
	 * @return The most recently added matching GridObject, or null if none exists.
	 */
	public synchronized GridObject get(Class c) {
		GridObject lastFound = null;
		Iterator<GridObject> i = gridObjects.iterator();
		while (i.hasNext()) {
			GridObject gridObject = i.next();
			if (gridObject.getClass() == c) {
				lastFound = gridObject;
			}
		}
		return lastFound;
	}
	
	public boolean has(Class c) {
		if (get(c) == null) return false;
		else return true;
	}
	
	/**
	 * Get the topmost (i.e. most recently added) GridObject that is a subclass of the given type.
	 * Returns null if there are no matching instances of GridObject in this square.
	 * @return The most recently added matching GridObject, or null if none exists.
	 */
	public synchronized GridObject getInstanceOf(Class c) {
		GridObject lastFound = null;
		Iterator<GridObject> i = gridObjects.iterator();
		while (i.hasNext()) {
			GridObject gridObject = i.next();
			if (c.isAssignableFrom(gridObject.getClass())) {
				lastFound = gridObject;
			}
		}
		return lastFound;
	}
	
	public boolean hasInstanceOf(Class c) {
		if (getInstanceOf(c) == null) return false;
		else return true;
	}
	
	/**
	 * Delete the given GridObject, if it is in this square.
	 * If the object is not found, does nothing.
	 * @param gridObject A GridObject in this square, to be deleted.
	 */
	public synchronized void remove(GridObject gridObject) {
		gridObjects.remove(gridObject);
	}
	
	/**
	 * Returns a count of the GridObjects currently occupying this square.
	 * @return the total number of objects in this square
	 */
	public synchronized int getCount() {
		return gridObjects.size();
	}
	
	/**
	 * Get an iterator to iterate through the contents in drawing order.
	 * @return an Iterator<GridObject> in bottom-up order.
	 */
	public Iterator<GridObject> iterator() {
		return gridObjects.iterator();
	}
}
