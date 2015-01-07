package de.mhus.lib.test.adb.model;

import java.util.HashMap;
import java.util.List;

import de.mhus.lib.adb.DbAccessManager;
import de.mhus.lib.adb.DbManager;
import de.mhus.lib.adb.DbSchema;
import de.mhus.lib.adb.model.Table;
import de.mhus.lib.errors.AccessDeniedException;
import de.mhus.lib.errors.MException;
import de.mhus.lib.sql.DbConnection;

public class BookStoreSchema extends DbSchema {

	@Override
	public void doFillNameMapping(HashMap<String, Object> nameMapping) {
		super.doFillNameMapping(nameMapping);
		System.out.println(nameMapping);
	}

	public void doMigrate(DbManager dbManager, long currentVersion) throws MException {
		if (currentVersion == 0) {
			Person p = new Person();
			p.setName("Hausmeister Krause");
			dbManager.createObject(p);
			dbManager.getSchemaProperties().set(DbManager.DATABASE_VERSION, "1");
		}
	}

	@Override
	public DbAccessManager getAccessManager(Table c) {
		if (c.getClazz() == Finances.class) {
			return new DbAccessManager() {

				@Override
				public void hasAccess(DbManager manager, Table c,
						DbConnection con, Object object, int right)
						throws AccessDeniedException {
					
					Finances f = (Finances) object;

					String conf = f.getConfidential();
					if (conf != null) {
						if (right == DbManager.R_UPDATE && conf.indexOf("write") >= 0 )
							throw new AccessDeniedException("access denied");
	
						if (right == DbManager.R_REMOVE && conf.indexOf("remove") >= 0 )
							throw new AccessDeniedException("access denied");
					}
					// set new acl if needed
					if (f.getNewConfidential() != null)
						f.setConfidential(f.getNewConfidential());
				}

				@Override
				public void hasReadAccess(DbManager dbManager, Table table,
						DbConnection con, de.mhus.lib.sql.DbResult ret) throws AccessDeniedException {
					
					try {
						String conf = ret.getString( dbManager.getNameMapping().get("db.finances.confidential").toString() );
						if ( conf != null && conf.indexOf("read") >= 0 ) {
							throw new AccessDeniedException("access denied");
						}
					} catch (Exception e) {
//						e.printStackTrace();
						throw new AccessDeniedException(e);
					}
				}
				
			};
		}
		return null;
	}

	@Override
	public void findObjectTypes(List<Class<?>> list) {
		list.add(Book.class);
		list.add(Person.class);
		list.add(Store.class);
		list.add(Finances.class);
		list.add(Regal.class);
	}

}