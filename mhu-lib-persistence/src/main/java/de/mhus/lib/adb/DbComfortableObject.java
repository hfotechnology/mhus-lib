package de.mhus.lib.adb;

import de.mhus.lib.core.lang.MObject;
import de.mhus.lib.errors.MException;
import de.mhus.lib.sql.DbConnection;

/**
 * A comfortable abstract object base to make the life of the developer fresh and
 * handy. Use this, if possible as base of a database object. You will love it.
 * 
 * @author mikehummel
 *
 */
public class DbComfortableObject extends MObject implements DbObject {

	protected DbManager manager;
	protected boolean persistent = false;
	protected String registryName;
	protected DbConnection con;

	public boolean isAdbManaged() {
		return manager != null;
	}
	/**
	 * Save changes.
	 * 
	 * @throws MException
	 */
	public void save() throws MException {
		if (isAdbManaged() && !persistent)
			create(manager);
		else
			manager.saveObject(con, registryName,this);
	}
	
	/**
	 * Save changed is not jet managed it will create the object
	 * 
	 * @param manager
	 * @throws MException 
	 */
	public void save(DbManager manager) throws MException {
		if (isAdbManaged() && persistent)
			save();
		else
			create(manager);
	}
	
	/**
	 * Save the object if the object differs from database.
	 * 
	 * @return true if the object was saved.
	 * @throws MException
	 */
	public boolean saveChanged() throws MException {
		if (isAdbManaged() && !persistent) {
			create(manager);
			return true;
		} else
		if (manager.objectChanged(this)) {
			manager.saveObject(con, registryName,this);
			return true;
		}
		return false;
	}

	/**
	 * Reload from database.
	 * 
	 * @throws MException
	 */
	public void reload() throws MException {
		manager.reloadObject(con, registryName, this);
	}
	
	/**
	 * Remove in database.
	 * 
	 * @throws MException
	 */
	public void remove() throws MException {
		if (isAdbManaged()) {
			manager.removeObject(con, registryName,this);
			persistent = false;
		}
	}
	
	/**
	 * Create this new object in the database.
	 * 
	 * @param manager
	 * @throws MException
	 */
	public void create(DbManager manager) throws MException {
		manager.createObject(con, this);
		persistent = true;
	}
	
	/**
	 * Overwrite to get the hook.
	 */
	@Override
	public void doPreCreate(DbConnection con) {
	}

	/**
	 * Overwrite to get the hook.
	 */
	@Override
	public void doPreSave(DbConnection con) {
	}

	/**
	 * Overwrite to get the hook.But remember, it could be called more times. It's only
	 * to store the data.
	 */
	@Override
	public void doInit(DbManager manager, String registryName, boolean isPersistent) {
		this.manager = manager;
		this.registryName = registryName;
		persistent = isPersistent;
	}

	public void setDbManager(DbManager manager) {
		this.manager = manager;
	}
	
	public boolean isPersistent() {
		return persistent;
	}

	/**
	 * Overwrite to get the hook.
	 */
	@Override
	public void doPreRemove(DbConnection con) {
	}

	/**
	 * Overwrite to get the hook.
	 */
	@Override
	public void doPostLoad(DbConnection con) {
	}

	/**
	 * Overwrite to get the hook.
	 */
	@Override
	public void doPostRemove(DbConnection con) {
	}

	public DbManager getDbManager() {
		return manager;
	}

}