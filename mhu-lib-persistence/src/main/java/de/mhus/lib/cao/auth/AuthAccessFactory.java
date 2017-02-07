package de.mhus.lib.cao.auth;

import de.mhus.lib.cao.CaoAspect;
import de.mhus.lib.cao.CaoAspectFactory;
import de.mhus.lib.cao.CaoCore;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.util.MutableActionList;

public class AuthAccessFactory implements CaoAspectFactory<AuthAccess> {

	private AuthCore con;

	public AuthAccessFactory(AuthCore con) {
		this.con = con;
	}

	@Override
	public AuthAccess getAspectFor(CaoNode node) {
		AuthNode n = (AuthNode)node;
		synchronized (n) {
			if (n.access != null) return n.access;
			n.access = new Access(con,node);
			return n.access;
		}
	}

	private static class Access implements AuthAccess {

		private AuthCore con;
		private CaoNode node;

		public Access(AuthCore con, CaoNode node) {
			this.con = con;
			this.node = node;
		}

		@Override
		public boolean hasReadAccess() {
			return con.hasReadAccess(node);
		}

		@Override
		public boolean hasWriteAccess() {
			return con.hasWriteAccess(node);
		}

		@Override
		public boolean hasContentAccess(String rendition) {
			return con.hasContentAccess(node, rendition);
		}

		@Override
		public boolean hasAspectAccess(Class<? extends CaoAspect> ifc) {
			return con.hasAspectAccess(node, ifc);
		}


	}

	@Override
	public void doInitialize(CaoCore caoConnection, MutableActionList actionList) {
	}
	
}
