package de.mhus.lib.core.cast;

import de.mhus.lib.core.logging.Log;
import de.mhus.lib.core.util.ObjectContainer;

/**
 * <p>ObjectToInteger class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 */
public class ObjectToInteger implements Caster<Object,Integer> {

	private final static Log log = Log.getLog(ObjectToInteger.class);
	
	/** {@inheritDoc} */
	@Override
	public Class<? extends Integer> getToClass() {
		return Integer.class;
	}

	/** {@inheritDoc} */
	@Override
	public Class<? extends Object> getFromClass() {
		return Object.class;
	}

	/** {@inheritDoc} */
	@Override
	public Integer cast(Object in, Integer def) {
		ObjectContainer<Integer> ret = new ObjectContainer<>(def);
		toInt(in, 0, ret);
		return ret.getObject();
	}

	/**
	 * <p>toInt.</p>
	 *
	 * @param in a {@link java.lang.Object} object.
	 * @param def a int.
	 * @param ret a {@link de.mhus.lib.core.util.ObjectContainer} object.
	 * @return a int.
	 */
	public int toInt(Object in, int def, ObjectContainer<Integer> ret) {
		if (in == null) return def;
		if (in instanceof Integer) {
			if (ret != null) ret.setObject((Integer)in);
			return ((Integer)in).intValue();
		}
		if (in instanceof Number) {
			int r = ((Number)in).intValue();
			if (ret != null) ret.setObject(r);
			return r;
		}

		String _in = String.valueOf(in);
		try {
			if (_in.startsWith("0x") || _in.startsWith("-0x") || _in.startsWith("+0x")) {
				int start = 2;
				if (_in.startsWith("-")) start = 3;
				int out = 0;
				for (int i = start; i < _in.length(); i++) {
					int s = -1;
					char c = _in.charAt(i);
					if (c >= '0' && c <= '9')
						s = c - '0';
					else if (c >= 'a' && c <= 'f')
						s = c - 'a' + 10;
					else if (c >= 'A' && c <= 'F')
						s = c - 'A' + 10;

					if (s == -1)
						throw new NumberFormatException(_in);
					out = out * 16 + s;
				}
				if (_in.startsWith("-")) out = -out;
				if (ret != null) ret.setObject(out);
				return out;
			}
			
			int r = Integer.parseInt(_in);
			if (ret != null) ret.setObject(r);
			return r;
		} catch (Throwable e) {
			log.t(_in, e.toString());
			return def;
		}
	}
}
