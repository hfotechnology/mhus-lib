package de.mhus.lib.adb.model;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.core.pojo.PojoAttribute;
import de.mhus.lib.sql.DbResult;

public class FieldVirtual extends Field {

	public FieldVirtual(Table table, boolean isPrimary, PojoAttribute<?> attribute, ResourceNode attr2) {
		this.attribute = (PojoAttribute<Object>) attribute;
		this.nameOrg = attribute.getName();
		this.table = table;
		this.name = nameOrg.toLowerCase();
		this.createName = name.toLowerCase();
		this.methodName = name;
		this.isPrimary = isPrimary;
		this.attr = attr2;
	}

//	public FieldVirtual(Table table, de.mhus.lib.adb.DbDynamic.Field f) {
//		methodName = f.getName();
//		this.table = table;
//		this.nameOrg = methodName;
//		this.name = methodName.toLowerCase();
//		this.createName = methodName.toLowerCase();
//		this.isPrimary = f.isPrimaryKey();
//		this.ret = f.getReturnType();
//		this.attr = f.getAttributes();
//		this.dynamicField = f;
//	}

	@Override
	public void prepareCreate(Object obj)
			throws IllegalArgumentException, IllegalAccessException,
			InvocationTargetException {
	}

	@Override
	public Object getFromTarget(Object obj)
			throws Exception {
		return get(obj);
	}

	@Override
	public void setToTarget(DbResult res, Object obj) throws Exception {
	}

	@Override
	public boolean changed(DbResult res, Object obj) throws Exception {
		return false;
	}
	
	@Override
	public void fillNameMapping(HashMap<String, Object> nameMapping) {
	}

	@Override
	public boolean isPersistent() {
		return false;
	}
}