package de.mhus.lib.core.util;

import java.io.IOException;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.node.ObjectNode;

import de.mhus.lib.core.MActivator;
import de.mhus.lib.core.MJson;
import de.mhus.lib.core.MSingleton;
import de.mhus.lib.core.pojo.MPojo;
import de.mhus.lib.errors.NotFoundException;

/**
 * <p>TableRow class.</p>
 *
 * @author mikehummel
 * @version $Id: $Id
 * @since 3.2.9
 */
public class TableRow implements Serializable {

	private static final long serialVersionUID = 1L;
	LinkedList<Object> data = new LinkedList<>();
	private Table table;
	
	/**
	 * <p>Getter for the field <code>data</code>.</p>
	 *
	 * @return a {@link java.util.List} object.
	 */
	public List<Object> getData() {
		return data;
	}
	
	private void writeObject(java.io.ObjectOutputStream out)
		     throws IOException {
		out.writeInt(data.size());
		for (Object d : data) {
			// write data
			if (d == null || d instanceof Serializable) {
				// via java default
				out.writeInt(0);
				out.writeObject(d);
			} else {
				// via pojo
				out.writeInt(1);
				ObjectNode to = MJson.createObjectNode();
				MPojo.pojoToJson(d, to);
				out.writeUTF(d.getClass().getCanonicalName());
				out.writeUTF(MJson.toString(to));
			}
		}
	}
	
	private void readObject(java.io.ObjectInputStream in)
		     throws IOException, ClassNotFoundException {
		int size = in.readInt();
		// data.clear(); Create new one because of concurrent modifications
		data = new LinkedList<>();
		for (int i = 0; i < size; i++) {
			int code = in.readInt();
			if (code == 0) {
				Object d = in.readObject();
				data.add(d);
			} else
			if (code == 1) {
				String clazzName = in.readUTF();
				Object obj;
				try {
					obj = MSingleton.get().base().lookup(MActivator.class).createObject(clazzName);
				} catch (Exception e) {
					throw new IOException(e);
				}
				String jsonString = in.readUTF();
				JsonNode json = MJson.load(jsonString);
				MPojo.jsonToPojo(json, obj);
				data.add(obj);
			}
		}
		
	}

	/**
	 * <p>appendData.</p>
	 *
	 * @param d a {@link java.lang.Object} object.
	 */
	public void appendData(Object ... d) {
		for (Object o : d)
			data.add(o);
	}
	
	/**
	 * <p>Setter for the field <code>data</code>.</p>
	 *
	 * @param d a {@link java.lang.Object} object.
	 */
	public void setData(Object ... d) {
		data.clear();
		for (Object o : d)
			data.add(o);
	}

	/**
	 * <p>Setter for the field <code>table</code>.</p>
	 *
	 * @param table a {@link de.mhus.lib.core.util.Table} object.
	 */
	public void setTable(Table table) {
		this.table = table;
	}
	
	/**
	 * <p>get.</p>
	 *
	 * @param index a int.
	 * @return a {@link java.lang.Object} object.
	 */
	public Object get(int index) {
		if (index < 0 || index >= data.size())
			throw new NotFoundException("column index not found",index);
		return data.get(index);
	}
	
	/**
	 * <p>get.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @return a {@link java.lang.Object} object.
	 */
	public Object get(String name) {
		int index = table.getColumnIndex(name);
		if (index == -1)
			throw new NotFoundException("column not found",name);
		return get(index);
	}

	/**
	 * <p>get.</p>
	 *
	 * @param name a {@link java.lang.String} object.
	 * @param def a T object.
	 * @param <T> a T object.
	 * @return a T object.
	 */
	@SuppressWarnings("unchecked")
	public <T> T get(String name, T def) {
		int index = table.getColumnIndex(name);
		if (index == -1) return def;
		Object val = get(index);
		if (val == null) return def;
		return (T)val;
	}

}
