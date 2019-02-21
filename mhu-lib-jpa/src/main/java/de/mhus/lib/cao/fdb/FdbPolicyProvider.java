/**
 * Copyright 2018 Mike Hummel
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mhus.lib.cao.fdb;

import de.mhus.lib.cao.CaoAspectFactory;
import de.mhus.lib.cao.CaoCore;
import de.mhus.lib.cao.CaoNode;
import de.mhus.lib.cao.CaoPolicy;
import de.mhus.lib.cao.util.MutableActionList;
import de.mhus.lib.errors.MException;

public class FdbPolicyProvider implements CaoAspectFactory<CaoPolicy> {

	private CaoCore core;

	@Override
	public CaoPolicy getAspectFor(CaoNode node) {
		try {
			return new CaoPolicy(core, node, true, node.isEditable());
		} catch (MException e) {
		}
		return null;
	}

	@Override
	public void doInitialize(CaoCore core, MutableActionList actionList) {
		this.core = core;
	}

}
