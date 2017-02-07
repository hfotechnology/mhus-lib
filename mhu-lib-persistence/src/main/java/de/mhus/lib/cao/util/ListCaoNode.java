package de.mhus.lib.cao.util;

import java.io.InputStream;
import java.net.URL;
import java.util.Collection;

import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoWritableElement;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.util.EmptyList;
import de.mhus.lib.errors.MException;

public class ListCaoNode extends CaoNode {

	private static final long serialVersionUID = 1L;
	private String name;
	private Collection<CaoNode> list;

	public ListCaoNode(String name, Collection<CaoNode> list) {
		super(null, null);
		this.name = name;
		this.list = list;
	}

	@Override
	public void clear() {
		list.clear();
	}

	@Override
	public CaoWritableElement getWritableNode() throws MException {
		return null;
	}

	@Override
	public String getId() {
		return null;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public boolean isNode() {
		return true;
	}

	@Override
	public String getPath() {
		return null;
	}

	@Override
	public Collection<String> getPaths() {
		return null;
	}

	@Override
	public void reload() throws MException {
	}

	@Override
	public boolean isValid() {
		return true;
	}

	@Override
	public Collection<String> getPropertyKeys() {
		return new EmptyList<>();
	}

	@Override
	public CaoNode getNode(String key) {
		return null;
	}

	@Override
	public Collection<CaoNode> getNodes() {
		return list;
	}

	@Override
	public Collection<CaoNode> getNodes(String key) {
		return new EmptyList<>();
	}

	@Override
	public Collection<String> getNodeKeys() {
		return new EmptyList<>();
	}

	@Override
	public InputStream getInputStream(String rendition) {
		return null;
	}

	@Override
	public Collection<String> getRenditions() {
		return new EmptyList<>();
	}

	@Override
	public IProperties getRenditionProperties(String rendition) {
		return null;
	}

	@Override
	public URL getUrl() {
		return null;
	}

	@Override
	public boolean hasContent() {
		return false;
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

}
