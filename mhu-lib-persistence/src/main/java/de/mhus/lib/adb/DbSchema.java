package de.mhus.lib.adb;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import de.mhus.lib.adb.model.Feature;
import de.mhus.lib.adb.model.FeatureAccessManager;
import de.mhus.lib.adb.model.FeatureCut;
import de.mhus.lib.adb.model.Field;
import de.mhus.lib.adb.model.FieldPersistent;
import de.mhus.lib.adb.model.FieldVirtual;
import de.mhus.lib.adb.model.Table;
import de.mhus.lib.adb.model.TableAnnotations;
import de.mhus.lib.adb.model.TableDynamic;
import de.mhus.lib.core.MSystem;
import de.mhus.lib.core.directory.ResourceNode;
import de.mhus.lib.core.lang.MObject;
import de.mhus.lib.core.pojo.PojoAttribute;
import de.mhus.lib.core.service.UniqueId;
import de.mhus.lib.errors.MException;
import de.mhus.lib.sql.DbConnection;
import de.mhus.lib.sql.DbResult;

/**
 * Define the schema with a new instance of this class. It can handle and manipulate
 * all activities. It's also a factory for the loaded objects.
 * 
 * @author mikehummel
 *
 */
public abstract class DbSchema extends MObject {

	
	public abstract void findObjectTypes(List<Class<?>> list);
	protected String tablePrefix = "";
	private LinkedList<Class<?>> objectTypes;
	
	public final Class<?>[] getObjectTypes() {
		initObjectTypes();
		return objectTypes.toArray(new Class<?>[objectTypes.size()]);
	}
	
	/**
	 * This should be called after the manager is created.
	 * 
	 * @param manager
	 */
	public void doPostInit(DbManager manager) {
	}
	
	/**
	 * Overwrite this method to get the configuration object and initialize the schem. It should be
	 * called by the creator to initialize the schema before it is given to the manager.
	 * 
	 * @param config
	 */
	public void doInit(ResourceNode config) {
		
	}
	
	/**
	 * Masquerade the table names if needed. By default a tablePrefix is set for the table.
	 * 
	 * @param name
	 * @return
	 */
	public String getTableName(String name) {
		return tablePrefix + name;
	}
	

	/**
	 * Object factory to create different kinds of objects for one table.
	 * 
	 * @param clazz
	 * @param registryName
	 * @param ret could be null, return the default object
	 * @param manager
	 * @return
	 * @throws Exception
	 */
	public Object createObject(Class<?> clazz, String registryName, DbResult ret, DbManager manager, boolean isPersistent) throws Exception {
		Object object = manager.getActivator().createObject(clazz.getCanonicalName());
		if (object instanceof DbObject) {
			((DbObject)object).doInit(manager, registryName, isPersistent);
		}
		return object;
	}

	/**
	 * If no registryName is set in the manager this will ask the schema for the correct registryName.
	 * 
	 * @param object
	 * @param manager
	 * @return
	 */
	public Class<?> findClassForObject(Object object, DbManager manager) {
		initObjectTypes();
		if (object instanceof Class<?>) {
			for (Class<?> c : objectTypes)
				if (((Class<?>)object).isAssignableFrom(c)) return c;
		}
		
		for (Class<?> c : objectTypes)
			if (c.isInstance(object)) return c;
		return null;
	}

	protected synchronized void initObjectTypes() {
		if (objectTypes != null) return;
		objectTypes = new LinkedList<Class<?>>();
		findObjectTypes(objectTypes);
	}

	/**
	 * Return a new unique Id for a new entry in the table. Only used for auto_id fields with type long.
	 * The default implementation is not save !!!
	 * 
	 * @param table
	 * @param field
	 * @param obj
	 * @param name
	 * @param manager
	 * @return
	 */
	public long getUniqueId(Table table,Field field,Object obj, String name, DbManager manager) {
		return base(UniqueId.class).nextUniqueId();
	}

	/**
	 * Overwrite this to get the hook in the schema. By default it's delegated to the object.
	 * Remember to call the super.
	 */
	public void doPreCreate(Table table,Object object, DbConnection con, DbManager manager) {
		if (object instanceof DbObject) {
			((DbObject)object).doInit(manager, table.getRegistryName(), ((DbObject)object).isPersistent() );
			((DbObject)object).doPreCreate(con);
		}
	}

	/**
	 * Overwrite this to get the hook in the schema. By default it's delegated to the object.
	 * Remember to call the super.
	 */
	public void doPreSave(Table table,Object object, DbConnection con, DbManager manager) {
		if (object instanceof DbObject) {
			((DbObject)object).doPreSave(con);
		}
	}

	/**
	 * Return true if you want to store persistent information about the schema in the database.
	 * Use Manager.getSchemaProperties() to access the properties.
	 * Default value is true.
	 * @return
	 */
	public boolean hasPersistentInfo() {
		return true;
	}

	/**
	 * Return the name of the schema used for example for the schema property table. Default
	 * is the simple name of the class.
	 * 
	 * @return
	 */
	public String getSchemaName() {
		return getClass().getSimpleName();
	}

	/**
	 * Overwrite this if you want to provide default query attributes by default. Name mapping
	 * will provide all table and field names for the used db activities.
	 * 
	 * @param nameMapping
	 */
	public void doFillNameMapping(HashMap<String, Object> nameMapping) {
		
	}

	/**
	 * Overwrite this to get the hook in the schema. By default it's delegated to the object.
	 * Remember to call the super.
	 */
	public void doPreRemove(Table table, Object object, DbConnection con, DbManager dbManager) {
		if (object instanceof DbObject) {
			((DbObject)object).doPreRemove(con);
		}
	}

	/**
	 * Overwrite this to get the hook in the schema. By default it's delegated to the object.
	 * Remember to call the super.
	 */
	public void doPostLoad(Table table, Object object, DbConnection con, DbManager manager) {
		if (object instanceof DbObject) {
			((DbObject)object).doPostLoad(con);
		}
	}

	/**
	 * Overwrite this to get the hook in the schema. By default it's delegated to the object.
	 * Remember to call the super.
	 */
	public void doPostRemove(Table c, Object object, DbConnection con, DbManager dbManager) {
		if (object instanceof DbObject) {
			((DbObject)object).doPostRemove(con);
		}
	}

	/**
	 * Called if the schema property table is created. This allows the schema to add
	 * the default schema values to the properties set.
	 * 
	 * @param dbManager
	 */
	public void doInitProperties(DbManager dbManager) {
		
	}

	/**
	 * Overwrite this to validate the current database version and maybe migrate to a
	 * newer version.
	 * This only works if schema property is enabled.
	 * TODO Extend the default functionality to manage the versions.
	 * 
	 * @param dbManager
	 * @param currentVersion
	 * @throws MException
	 */
	public void doMigrate(DbManager dbManager, long currentVersion) throws MException {
		
	}

	/**
	 * If you provide access management return an access manager instance for the
	 * given table. This will most time be called one at initialization time.
	 * 
	 * @param c
	 * @return The manager or null
	 */
	public DbAccessManager getAccessManager(Table c) {
		return null;
	}
	
	@Override
	public String toString() {
		initObjectTypes();
		return MSystem.toString(this,getSchemaName(),objectTypes);
	}

	public Table createTable(DbManager manager, Class<?> clazz,String registryName, String tableName) {
		
		boolean isDynamic = true;
		try {
			clazz.asSubclass(DbDynamic.class);
		} catch (ClassCastException e) {
			isDynamic = false;
		}

		Table table = null;
		if (isDynamic)
			table = new TableDynamic();
		else
			table = new TableAnnotations();
		table.init(manager,clazz,registryName,tableName);
		return table;
	}

	public Feature createFeature(DbManager manager, Table table, String name) {
		
		try {
			Feature feature = null;
		
			name = name.trim().toLowerCase();
			
			if (name.equals("accesscontrol"))
				feature = new FeatureAccessManager();
			else
			if (name.equals("cut"))
				feature = new FeatureCut();
			
			if (feature != null)
				feature.init(manager,table);
			else
				log().t("feature not found",name);
			return feature;
		} catch (Exception e) {
			log().t("feature",name,e);
			return null;
		}
	}

	public Field createField(DbManager manager, Table table, boolean pk, boolean virtual, PojoAttribute<?> attribute, ResourceNode attr,DbDynamic.Field dynamicField) throws MException {

		Field field = null;
		if (virtual)
			field = new FieldVirtual( table, pk, attribute, attr );
		else
			field = new FieldPersistent( manager, table, pk, attribute, attr, dynamicField );

		return field;
	}
	
}