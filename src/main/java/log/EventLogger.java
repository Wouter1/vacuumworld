package log;

import java.io.PrintStream;
import java.text.NumberFormat;
import java.util.Calendar;

import actions.Action;
import grid.GridObject;
import grid.ModelListener;
import grid.ModelObject;

public abstract class EventLogger implements ModelListener {

	protected PrintStream stream;
	
	/**
	 * Override this constructor to initialise the 'stream' protected member variable.
	 * @param gridObject record events from this GridObject
	 */
	public EventLogger(GridObject gridObject) {
		gridObject.addListener(this);
	}

	public void eventFired(String eventName, ModelObject source) {
		// Filter out the GUI-only events
		if (eventName.equals(GridObject.PAINT)
				|| eventName.endsWith(Action.STEP_EVENT)) {
			return;
		}
		// Construct the generic parts of the log string
		StringBuffer sb = new StringBuffer();
		sb.append(timeStamp());
		sb.append(formatEventSource(source));
		sb.append(eventName);
		// Call to print has null check in case subclass has not initialised stream
		if (stream != null) stream.println(sb.toString());
	}
	
	protected String timeStamp() {
		Calendar now = Calendar.getInstance();
		NumberFormat nf = NumberFormat.getInstance();
		nf.setMinimumIntegerDigits(2);
		StringBuffer sb = new StringBuffer();
		sb.append('[');
		sb.append(nf.format(now.get(Calendar.HOUR_OF_DAY)));
		sb.append(':');
		sb.append(nf.format(now.get(Calendar.MINUTE)));
		sb.append(':');
		sb.append(nf.format(now.get(Calendar.SECOND)));
		sb.append('.');
		nf.setMinimumIntegerDigits(3);
		sb.append(nf.format(now.get(Calendar.MILLISECOND)));
		sb.append("] ");
		return sb.toString();
	}
	
	protected abstract String formatEventSource(ModelObject source);

}
