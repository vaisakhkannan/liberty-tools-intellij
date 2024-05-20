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
package io.openliberty.tools.intellij.lsp4mp4ij.psi.core.jaxrs;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiMethod;
import io.openliberty.tools.intellij.lsp4mp4ij.psi.core.utils.IPsiUtils;
import org.eclipse.lsp4j.CodeLens;
import org.eclipse.lsp4j.Command;
import org.eclipse.lsp4j.Position;
import org.eclipse.lsp4j.Range;

import java.util.Collections;

import static io.openliberty.tools.intellij.lsp4mp4ij.psi.core.utils.AnnotationUtils.getFirstAnnotation;
import static io.openliberty.tools.intellij.lsp4mp4ij.psi.core.utils.AnnotationUtils.getAnnotationMemberValue;
import static io.openliberty.tools.intellij.lsp4mp4ij.psi.core.utils.AnnotationUtils.hasAnyAnnotation;
import static io.openliberty.tools.intellij.lsp4mp4ij.psi.core.jaxrs.JaxRsConstants.HTTP_METHOD_ANNOTATIONS;
import static io.openliberty.tools.intellij.lsp4mp4ij.psi.core.jaxrs.JaxRsConstants.JAVAX_WS_RS_APPLICATIONPATH_ANNOTATION;
import static io.openliberty.tools.intellij.lsp4mp4ij.psi.core.jaxrs.JaxRsConstants.JAVAX_WS_RS_GET_ANNOTATION;
import static io.openliberty.tools.intellij.lsp4mp4ij.psi.core.jaxrs.JaxRsConstants.JAVAX_WS_RS_PATH_ANNOTATION;
import static io.openliberty.tools.intellij.lsp4mp4ij.psi.core.jaxrs.JaxRsConstants.JAKARTA_WS_RS_APPLICATIONPATH_ANNOTATION;
import static io.openliberty.tools.intellij.lsp4mp4ij.psi.core.jaxrs.JaxRsConstants.JAKARTA_WS_RS_GET_ANNOTATION;
import static io.openliberty.tools.intellij.lsp4mp4ij.psi.core.jaxrs.JaxRsConstants.JAKARTA_WS_RS_PATH_ANNOTATION;
import static io.openliberty.tools.intellij.lsp4mp4ij.psi.core.jaxrs.JaxRsConstants.PATH_VALUE;


/**
 * JAX-RS utilities.
 *
 * @author Angelo ZERR
 *
 */
public class JaxRsUtils {

	private JaxRsUtils() {

	}

	/**
	 * Returns the value of the JAX-RS/Jakarta Path annotation and null otherwise..
	 *
	 * @param annotatable the annotatable that might be annotated with the
	 *                    JAX-RS/Jakarta Path annotation
	 * @return the value of the JAX-RS/Jakarta Path annotation and null otherwise..
	 */
	public static String getJaxRsPathValue(PsiElement annotatable) {
		PsiAnnotation annotationPath = getFirstAnnotation(annotatable, JAVAX_WS_RS_PATH_ANNOTATION,
				JAKARTA_WS_RS_PATH_ANNOTATION);
		if (annotationPath == null) {
			return null;
		}
		return getAnnotationMemberValue(annotationPath, PATH_VALUE);
	}

	/**
	 * Returns the value of the JAX-RS/Jakarta ApplicationPath annotation and null
	 * otherwise.
	 *
	 * @param annotatable the annotatable that might be annotated with the
	 *                    JAX-RS/Jakarta ApplicationPath annotation
	 * @return the value of the JAX-RS/Jakarta ApplicationPath annotation and null
	 *         otherwise.
	 */
	public static String getJaxRsApplicationPathValue(PsiElement annotatable) {
		PsiAnnotation annotationApplicationPath = getFirstAnnotation(annotatable, JAVAX_WS_RS_APPLICATIONPATH_ANNOTATION,
				JAKARTA_WS_RS_APPLICATIONPATH_ANNOTATION);
		if (annotationApplicationPath == null) {
			return null;
		}
		return getAnnotationMemberValue(annotationApplicationPath, PATH_VALUE);
	}

	/**
	 * Returns true if the given method has @GET annotation and false otherwise.
	 *
	 * @param method the method.
	 * @return true if the given method has @GET annotation and false otherwise.
	 */
	public static boolean isClickableJaxRsRequestMethod(PsiMethod method) {
		return hasAnyAnnotation(method, JAVAX_WS_RS_GET_ANNOTATION, JAKARTA_WS_RS_GET_ANNOTATION);
	}

	/**
	 * Returns true if the given method
	 * has @GET, @POST, @PUT, @DELETE, @HEAD, @OPTIONS, or @PATCH annotation
	 * and false otherwise.
	 *
	 * @param method the method.
	 * @return true if the given method
	 *         has @GET, @POST, @PUT, @DELETE, @HEAD, @OPTIONS, or @PATCH annotation and
	 *         false otherwise.
	 */
	public static boolean isJaxRsRequestMethod(PsiMethod method) {
		return hasAnyAnnotation(method, HTTP_METHOD_ANNOTATIONS);
	}

	/**
	 * Create URL CodeLens.
	 *
	 * @param baseURL          the base URL.
	 * @param rootPath         the JAX-RS path value.
	 * @param openURICommandId the open URI command and null otherwise.
	 * @param method           the method.
	 * @param utils            the JDT utilities.
	 * @return the code lens and null otherwise.
	 */
	public static CodeLens createURLCodeLens(String baseURL, String rootPath, String openURICommandId, PsiMethod method,
			IPsiUtils utils) {
		CodeLens lens = createURLCodeLens(method, utils, true);
		if (lens != null) {
			String pathValue = getJaxRsPathValue(method);
			String url = buildURL(baseURL, rootPath, pathValue);
			lens.setCommand(
					new Command(url, openURICommandId != null ? openURICommandId : "", Collections.singletonList(url)));
		}
		return lens;
	}

	public static CodeLens createURLCodeLens(PsiMethod method, IPsiUtils utils, boolean shouldHaveAnnotation) {
		PsiAnnotation[] annotations = method.getAnnotations();
		if ((annotations == null || annotations.length < 1)) {
			if (shouldHaveAnnotation) {
				return null;
			}
			CodeLens lens = new CodeLens();
			TextRange r = method.getModifierList().getTextRange();
			final Range range = utils.toRange(method, r.getStartOffset(), r.getLength());
			Position codeLensPosition = new Position(range.getEnd().getLine(), range.getStart().getCharacter());
			range.setStart(codeLensPosition);
			range.setEnd(codeLensPosition);
			lens.setRange(range);
			return lens;

		}
		TextRange r = annotations[annotations.length - 1].getTextRange();

		CodeLens lens = new CodeLens();
		final Range range = utils.toRange(method, r.getStartOffset(), r.getLength());
		// Increment line number for code lens to appear on the line right after the last annotation
		// align with the start of the last annotation (see https://github.com/redhat-developer/intellij-quarkus/issues/795)
		Position codeLensPosition = new Position(range.getEnd().getLine() + 1, range.getStart().getCharacter());
		range.setStart(codeLensPosition);
		range.setEnd(codeLensPosition);
		lens.setRange(range);
		return lens;
	}

	public static String buildURL(String... paths) {
		StringBuilder url = new StringBuilder();
		for (String path : paths) {
			if (path != null && !path.isEmpty()) {
				if (url.length() > 0 && path.charAt(0) == '/') {
					path = path.substring(1, path.length());
				}

				if (url.length() > 0 && url.charAt(url.length() - 1) != '/') {
					url.append('/');
				}
				url.append(path);
			}
		}
		return url.toString();
	}

	/**
	 * Returns an HttpMethod given the FQN of a JAX-RS or Jakarta RESTful
	 * annotation, nor null if the FQN doesn't match any HttpMethod.
	 *
	 * @param annotationFQN the FQN of the annotation to convert into a HttpMethod
	 * @return an HttpMethod given the FQN of a JAX-RS or Jakarta RESTful
	 *         annotation, nor null if the FQN doesn't match any HttpMethod
	 */
	public static HttpMethod getHttpMethodForAnnotation(String annotationFQN) {
		switch (annotationFQN) {
			case JaxRsConstants.JAKARTA_WS_RS_GET_ANNOTATION:
			case JaxRsConstants.JAVAX_WS_RS_GET_ANNOTATION:
				return HttpMethod.GET;
			case JaxRsConstants.JAKARTA_WS_RS_HEAD_ANNOTATION:
			case JaxRsConstants.JAVAX_WS_RS_HEAD_ANNOTATION:
				return HttpMethod.HEAD;
			case JaxRsConstants.JAKARTA_WS_RS_POST_ANNOTATION:
			case JaxRsConstants.JAVAX_WS_RS_POST_ANNOTATION:
				return HttpMethod.POST;
			case JaxRsConstants.JAKARTA_WS_RS_PUT_ANNOTATION:
			case JaxRsConstants.JAVAX_WS_RS_PUT_ANNOTATION:
				return HttpMethod.PUT;
			case JaxRsConstants.JAKARTA_WS_RS_DELETE_ANNOTATION:
			case JaxRsConstants.JAVAX_WS_RS_DELETE_ANNOTATION:
				return HttpMethod.DELETE;
			case JaxRsConstants.JAKARTA_WS_RS_PATCH_ANNOTATION:
			case JaxRsConstants.JAVAX_WS_RS_PATCH_ANNOTATION:
				return HttpMethod.PATCH;
			default:
				return null;
		}
	}
}
