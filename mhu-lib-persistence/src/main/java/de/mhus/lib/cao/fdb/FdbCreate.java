package de.mhus.lib.cao.fdb;

import java.io.File;
import java.util.Map.Entry;

import de.mhus.lib.cao.CaoAction;
import de.mhus.lib.cao.CaoException;
import de.mhus.lib.cao.CaoList;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoWritableElement;
import de.mhus.lib.cao.action.CaoConfiguration;
import de.mhus.lib.cao.action.CreateConfiguration;
import de.mhus.lib.cao.aspect.Changes;
import de.mhus.lib.core.IProperties;
import de.mhus.lib.core.strategy.Monitor;
import de.mhus.lib.core.strategy.NotSuccessful;
import de.mhus.lib.core.strategy.OperationResult;
import de.mhus.lib.core.strategy.Successful;

public class FdbCreate extends CaoAction {

	@Override
	public String getName() {
		return CREATE;
	}

	@Override
	public CaoConfiguration createConfiguration(CaoList list, IProperties configuration) throws CaoException {
		return new CreateConfiguration(null, list, null);
	}

	@Override
	public boolean canExecute(CaoConfiguration configuration) {
		try {
			return configuration.getList().size() == 1 
					&& 
					configuration.getList().get(0) instanceof FdbNode 
					&&
					! new File( ((FdbNode)configuration.getList().get(0)).getFile(), configuration.getProperties().getString(CreateConfiguration.NAME) ).exists()
					;
		} catch (Throwable t) {
			log().d(t);
			return false;
		}
	}

	@Override
	public OperationResult doExecuteInternal(CaoConfiguration configuration, Monitor monitor) throws CaoException {
		if (!canExecute(configuration)) return new NotSuccessful(getName(), "can't execute", -1);

		try {
			FdbNode parent = (FdbNode)configuration.getList().get(0);
			
			File nextFile = new File(parent.getFile(), configuration.getProperties().getString(CreateConfiguration.NAME) );
			nextFile.mkdir();
			((FdbCore)core).indexFile(nextFile);
			
			CaoNode nextNode = parent.getNode(nextFile.getName());
			CaoWritableElement nextWrite = nextNode.getWritableNode();
			
			for (Entry<String, Object> entry : configuration.getProperties().entrySet()) {
				if (!entry.getKey().startsWith("_"))
					nextWrite.put(entry.getKey(), entry.getValue());
			}
			nextWrite.getUpdateAction().doExecute(monitor);
			nextNode.reload();
			
			Changes change = nextNode.adaptTo(Changes.class);
			if (change != null) change.created();

			return new Successful(getName(),"ok",0,nextNode);
		} catch (Throwable t) {
			log().d(t);
			return new NotSuccessful(getName(),t.toString(),-1);
		}
	}

}
