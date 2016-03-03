package de.mhus.lib.annotations.base;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * <p>Bind class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Bind {

	Class<?> name() default Class.class;
	
}
