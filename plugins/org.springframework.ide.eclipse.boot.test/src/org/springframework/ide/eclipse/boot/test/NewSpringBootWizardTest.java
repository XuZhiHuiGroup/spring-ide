/*******************************************************************************
 * Copyright (c) 2015 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *    Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.boot.test;

import java.io.File;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.springsource.ide.eclipse.commons.core.util.OsUtils;
import org.springsource.ide.eclipse.commons.tests.util.StsTestUtil;

import junit.framework.TestCase;

public class NewSpringBootWizardTest extends TestCase {

	private BootProjectTestHarness harness;

	@Override
	protected void setUp() throws Exception {
		StsTestUtil.cleanUpProjects();
		harness = new BootProjectTestHarness(ResourcesPlugin.getWorkspace());
	}

	public void test_STS_4260() throws Exception {
		if (OsUtils.isWindows()) {
			return;
		}
		//This test / bug is about unix file permissions so skip it
		// for windows.
		IProject project = harness.createBootProject("simple-boot");
		IFile mavenWrapper = project.getFile("mvnw");
		assertTrue(mavenWrapper.exists());
		assertExecutable(mavenWrapper);
	}

	private void assertExecutable(IFile eclipseFile) {
		File file = eclipseFile.getRawLocation().toFile();
		assertTrue(file.canExecute());
	}
}
