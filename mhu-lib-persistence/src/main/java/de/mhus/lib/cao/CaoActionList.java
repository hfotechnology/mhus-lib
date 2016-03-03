package de.mhus.lib.cao;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * The class will filter the actions and consider which one is more important. To fill it
 * correctly fill with the more special first and the more generic as last one.
 *
 * Use the method CaoFactory.fillWithActions() to fill it correctly. The method will first ask the drivers of the
 * elements to fill this list. The drivers are asking the CaoApplications and it will ask the CaoConnection by default
 * You can change this behavior by overwriting the method in it's extended classes.
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class CaoActionList implements Iterable<CaoAction>{

	private LinkedList<CaoAction> actions = new LinkedList<CaoAction>();
	private HashMap<String, CaoAction> index = new HashMap<String, CaoAction>();

	/**
	 * <p>add.</p>
	 *
	 * @param action a {@link de.mhus.lib.cao.CaoAction} object.
	 */
	public void add(CaoAction action) {
		String name = action.getName();

		if (index.containsKey(name)) {
			//TODO find the best one, maybe replace
			return;
		}

		actions.add(action);
		index.put(name, action);

	}

	/** {@inheritDoc} */
	@Override
	public Iterator<CaoAction> iterator() {
		return actions.iterator();
	}

	/**
	 * <p>getAction.</p>
	 *
	 * @param actionName a {@link java.lang.String} object.
	 * @return a {@link de.mhus.lib.cao.CaoAction} object.
	 */
	public CaoAction getAction(String actionName) {
		return index.get(actionName);
	}

}
