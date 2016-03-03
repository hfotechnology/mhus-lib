package de.mhus.lib.form.ui;

import de.mhus.lib.core.definition.IDefAttribute;
import de.mhus.lib.errors.MException;
import de.mhus.lib.form.definition.FmDefaultSources;
import de.mhus.lib.form.definition.FmElement;
import de.mhus.lib.form.definition.FmNls;

/**
 * <p>FmCheckbox class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class FmCheckbox extends FmElement {

	/** Constant <code>TYPE_CHECKBOX="checkbox"</code> */
	public static final String TYPE_CHECKBOX = "checkbox";

	/**
	 * <p>Constructor for FmCheckbox.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param title a {@link java.lang.String} object.
	 * @param description a {@link java.lang.String} object.
	 * @since 3.2.9
	 */
	public FmCheckbox(String name, String title, String description) {
		this(name, new FmNls(title, description), new FmDefaultSources());
	}

	/**
	 * <p>Constructor for FmCheckbox.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param definitions a {@link de.mhus.lib.core.definition.IDefAttribute} object.
	 */
	public FmCheckbox(String name, IDefAttribute ... definitions) {
		super(name, definitions);
		setString(FmElement.TYPE,TYPE_CHECKBOX);
	}
	
	/**
	 * <p>defaultValue.</p>
	 *
	 * @param in a boolean.
	 * @return a {@link de.mhus.lib.form.ui.FmCheckbox} object.
	 * @throws de.mhus.lib.errors.MException if any.
	 */
	public FmCheckbox defaultValue(boolean in) throws MException {
		setBoolean(FmElement.DEFAULT, in);
		return this;
	}
	
}
