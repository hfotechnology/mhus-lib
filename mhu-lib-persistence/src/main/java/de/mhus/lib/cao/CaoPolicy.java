package de.mhus.lib.cao;

import java.io.InputStream;
import java.net.URL;
import java.util.LinkedList;
import java.util.Set;

import de.mhus.lib.cao.CaoMetaDefinition.TYPE;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.errors.MException;

public class CaoPolicy extends CaoNode {
	
	public static final String READ = "read";
	public static final String WRITE = "write";
	public static final String POLICIES = "policies";
	public static final String PRINCIPAL = "principal";

	public static final String CATEGORY_POLICY = "policy";
	public static final String CATEGORY_RIGHT = "right";

	protected boolean writable;
	protected boolean readable;
	protected CaoNode element;
	protected CaoPolicy proxy = null;
	protected CaoMetadata meta;
	
	public CaoPolicy(CaoNode element, boolean readable, boolean writable) throws CaoException {
		super(element);
		this.element = element;
		this.readable = readable;
		this.writable = writable;
		meta = new CaoMetadata(getConnection().getDriver());
		fillMetaData(meta.definition);
	}
	
	/**
	 * Overwrite this function to append or set your own right definitions.
	 * 
	 * @param definition
	 */
	protected void fillMetaData(LinkedList<CaoMetaDefinition> definition) {
		definition.add(new CaoMetaDefinition(meta,READ,TYPE.BOOLEAN,null,0,CATEGORY_RIGHT) );
		definition.add(new CaoMetaDefinition(meta,WRITE,TYPE.BOOLEAN,null,0,CATEGORY_RIGHT) );
		definition.add(new CaoMetaDefinition(meta,POLICIES,TYPE.LIST,null,0,CATEGORY_POLICY) );
		definition.add(new CaoMetaDefinition(meta,PRINCIPAL,TYPE.ELEMENT,null,0) );
	}
	
	public boolean isWritable() {
		if (proxy != null) return proxy.isWritable();
		return getBoolean(WRITE, writable);
	}
	
	public boolean isReadable() {
		if (proxy != null) return proxy.isReadable();
		return getBoolean(READ, readable);
	}

	@Override
	public boolean isNode() {
		if (proxy != null) return proxy.isNode();
		return false;
	}

	@Override
	public String getId() throws CaoException {
		if (proxy != null) return proxy.getId();
		return null;
	}

	@Override
	public String getName() throws MException {
		if (proxy != null) return proxy.getName();
		return null;
	}

	@Override
	public CaoMetadata getMetadata() {
		if (proxy != null) return proxy.getMetadata();
		return meta;
	}

	@Override
	public CaoNode getParent() {
		if (proxy != null) return proxy.getParent();
		return null;
	}

//	@Override
//	public String getString(String name) throws CaoException {
//		if (proxy != null) return proxy.getString(name);
//		if (name.equals(PRINCIPAL)) {
//			return getConnection().getCurrentUser().getName();
//		}
//		return MCast.toString(getBoolean(name, false));
//	}
	
	@Override
	public boolean getBoolean(String name, boolean def) {
		if (proxy != null) return proxy.getBoolean(name, def);
		if (READ.equals(name))  return readable;
		if (WRITE.equals(name)) return writable;
		return def;
	}

//	@Override
//	public CaoList getList(String name, CaoAccess access, String... attributes)
//			throws CaoException {
//		if (proxy != null) return proxy.getList(name, access, attributes);
//		
//		if (POLICIES.equals(name)) {
//			return getPoliciesList(attributes);
//		}
//		
//		throw new CaoNotFoundException(this,"list",name);
//	}

	/**
	 * Overwrite this function to return another policy informations.
	 * 
	 * @param attributes
	 * @return
	 * @throws CaoException 
	 */
	protected CaoList getPoliciesList(String[] attributes) throws CaoException {
		// Returns a list with the current user
		CaoList list = new CaoList(this);
		list.add(this);
		return list;
	}

//	@Override
//	public Object getObject(String name, String... attributes)
//			throws CaoException {
//		if (proxy != null) return proxy.getObject(name, attributes);
//		
//		if (name.equals(PRINCIPAL)) {
//			return getConnection().getCurrentUser();
//		}
//		
//		throw new CaoNotSupportedException();
//	}

	@Override
	public CaoWritableElement getWritableNode() throws MException {
		if (proxy != null) return proxy.getWritableNode();
		throw new CaoNotSupportedException();
	}

	@Override
	public void reload() throws CaoException {
		proxy = element.getAccessPolicy();
	}

	@Override
	public boolean isValid() {
		return element.isValid();
	}

	@Override
	public Set<String> keys() {
		return element.keys();
	}

	@Override
	public String[] getPropertyKeys() {
		return new String[0];
	}

	@Override
	public ResourceNode getNode(String key) {
		return null;
	}

	@Override
	public ResourceNode[] getNodes() {
		return new ResourceNode[0];
	}

	@Override
	public ResourceNode[] getNodes(String key) {
		return new ResourceNode[0];
	}

	@Override
	public String[] getNodeKeys() {
		return new String[0];
	}

	@Override
	public InputStream getInputStream(String key) {
		return null;
	}

	@Override
	public Object getProperty(String name) {
		return null;
	}

	@Override
	public boolean isProperty(String name) {
		return false;
	}

	@Override
	public void removeProperty(String key) {
		
	}

	@Override
	public void setProperty(String key, Object value) {
		
	}

	@Override
	public URL getUrl() {
		return null;
	}

	@Override
	public boolean isValide() {
		return true;
	}

	@Override
	public boolean hasContent() {
		return false;
	}
	
}