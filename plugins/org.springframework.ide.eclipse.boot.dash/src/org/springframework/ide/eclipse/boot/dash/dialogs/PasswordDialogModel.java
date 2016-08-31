/*******************************************************************************
 * Copyright (c) 2016 Pivotal, Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Pivotal, Inc. - initial API and implementation
 *******************************************************************************/
package org.springframework.ide.eclipse.boot.dash.dialogs;

import org.eclipse.core.runtime.Assert;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.springframework.ide.eclipse.boot.dash.cloudfoundry.client.CFCredentials;
import org.springframework.ide.eclipse.editor.support.util.StringUtil;
import org.springsource.ide.eclipse.commons.livexp.core.LiveExpression;
import org.springsource.ide.eclipse.commons.livexp.core.LiveVariable;
import org.springsource.ide.eclipse.commons.livexp.core.ValidationResult;
import org.springsource.ide.eclipse.commons.livexp.ui.Ilabelable;
import org.springsource.ide.eclipse.commons.livexp.ui.OkButtonHandler;

import org.springsource.ide.eclipse.commons.livexp.core.Validator;

/**
 * Password dialog model. Provides ability to specify password and whether it
 * needs to be stored.
 *
 * @author Alex Boyko
 *
 */
public class PasswordDialogModel implements OkButtonHandler {

	public static enum StoreCredentialsMode implements Ilabelable {

		STORE_PASSWORD {
			@Override
			public CFCredentials createCredentials(String storedString) {
				return CFCredentials.fromPassword(storedString);
			}

			@Override
			public String credentialsToString(CFCredentials credentials) {
				String password = credentials.getPassword();
				Assert.isLegal(password!=null, "Password is not set");
				return password;
			}

			@Override
			public String getLabel() {
				return "Store Password";
			}
		},

		STORE_TOKEN {
			@Override
			public CFCredentials createCredentials(String storedString) {
				return CFCredentials.fromRefreshToken(storedString);
			}

			@Override
			public String credentialsToString(CFCredentials credentials) {
				String token = credentials.getRefreshToken();
				return token;
			}
			@Override
			public String getLabel() {
				return "Store OAuth Token";
			}
		},

		STORE_NOTHING {
			@Override
			public CFCredentials createCredentials(String storedString) {
				return null;
			}

			@Override
			public String credentialsToString(CFCredentials credentials) {
				return null;
			}

			@Override
			public String getLabel() {
				return "Do NOT Store";
			}
		};

		/**
		 * Create credentials from a previously stored string.
		 */
		public abstract CFCredentials createCredentials(String storedString);

		/**
		 * Convert credentials to a String that can be stored and later retrieved to
		 * recreate the credentials.
		 */
		public abstract String credentialsToString(CFCredentials credentials);
	}

	final private String fUser;
	final private String fTargetId;
	final private LiveVariable<String> fPasswordVar;
	final private LiveVariable<StoreCredentialsMode> fStoreVar;
	private boolean okButtonPressed = false;
	private Validator passwordValidator;

	public PasswordDialogModel(String user, String targetId, String password, StoreCredentialsMode secureStore) {
		super();
		fUser = user;
		fTargetId = targetId;
		fPasswordVar = new LiveVariable<>(password);
		fStoreVar = new LiveVariable<>(secureStore);
	}

	public PasswordDialogModel(String user, String targetId, StoreCredentialsMode secureStore) {
		this(user, targetId, null, secureStore);
	}

	public String getUser() {
		return fUser;
	}

	public String getTargetId() {
		return fTargetId;
	}

	public LiveVariable<String> getPasswordVar() {
		return fPasswordVar;
	}

	public LiveVariable<StoreCredentialsMode> getStoreVar() {
		return fStoreVar;
	}

	public boolean isOk() {
		return okButtonPressed;
	}

	@Override
	public void performOk() throws Exception {
		okButtonPressed = true;
	}

	public LiveExpression<ValidationResult> getPasswordValidator() {
		if (passwordValidator==null) {
			passwordValidator = new Validator() {
				{
					dependsOn(fPasswordVar);
				}
				@Override
				protected ValidationResult compute() {
					String pw = fPasswordVar.getValue();
					if (!StringUtil.hasText(pw)) {
						return ValidationResult.error("Password can not be empty");
					}
					return ValidationResult.info("Please press 'OK' to set the password.");
				}
			};
		}
		return passwordValidator;
	}

}
