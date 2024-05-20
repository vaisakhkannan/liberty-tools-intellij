/*******************************************************************************
* Copyright (c) 2020 Red Hat Inc. and others.
*
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v. 2.0 which is available at
* http://www.eclipse.org/legal/epl-2.0, or the Apache License, Version 2.0
* which is available at https://www.apache.org/licenses/LICENSE-2.0.
*
* SPDX-License-Identifier: EPL-2.0 OR Apache-2.0
*
* Contributors:
*     Red Hat Inc. - initial API and implementation
*******************************************************************************/
package io.openliberty.tools.intellij.lsp4mp4ij.psi.internal.restclient.java;

import io.openliberty.tools.intellij.lsp4mp4ij.psi.core.MicroProfileConfigConstants;
import io.openliberty.tools.intellij.lsp4mp4ij.psi.core.java.codeaction.InsertAnnotationMissingQuickFix;
import io.openliberty.tools.intellij.lsp4mp4ij.psi.core.java.codeaction.JavaCodeActionContext;
import io.openliberty.tools.intellij.lsp4mp4ij.psi.core.utils.PsiTypeUtils;
import org.eclipse.lsp4j.CodeAction;
import org.eclipse.lsp4j.Diagnostic;
import org.eclipse.lsp4mp.commons.codeaction.MicroProfileCodeActionId;

import java.util.List;

/**
 * QuickFix for fixing
 * {@link MicroProfileRestClientErrorCode#InjectAnnotationMissing} error by
 * providing several code actions:
 *
 * <ul>
 * <li>Insert @Inject annotation and the proper import.</li>
 * </ul>
 *
 * @author Angelo ZERR
 *
 */
public class InjectAnnotationMissingQuickFix extends InsertAnnotationMissingQuickFix {

	public InjectAnnotationMissingQuickFix() {
		super(MicroProfileConfigConstants.INJECT_JAKARTA_ANNOTATION, MicroProfileConfigConstants.INJECT_JAVAX_ANNOTATION);
	}

	@Override
	public String getParticipantId() {
		return InjectAnnotationMissingQuickFix.class.getName();
	}

	@Override
	protected void insertAnnotations(Diagnostic diagnostic, JavaCodeActionContext context, List<CodeAction> codeActions) {
		String[] annotations = getAnnotations();
		for (String annotation : annotations) {
			if (PsiTypeUtils.findType(context.getJavaProject(), annotation) != null) {
				insertAnnotation(diagnostic, context, codeActions, annotation);
				return;
			}
		}
	}

	@Override
	protected MicroProfileCodeActionId getCodeActionId() {
		return MicroProfileCodeActionId.InsertInjectAnnotation;
	}
}
