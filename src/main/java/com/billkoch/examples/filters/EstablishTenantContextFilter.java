/*
 * Copyright 2016 Bill Koch
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
package com.billkoch.examples.filters;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;

import com.billkoch.examples.tenant.management.TenantContext;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@WebFilter(urlPatterns = "/api/*")
public class EstablishTenantContextFilter implements Filter {

	private static final String TENANT_HEADER = "x-tenant-id";

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest httpRequest = (HttpServletRequest) request;

		logHeaders(httpRequest);
		TenantContext.set(httpRequest.getHeader(TENANT_HEADER));
		chain.doFilter(request, response);
	}

	private void logHeaders(HttpServletRequest request) {
		Collections.list(request.getHeaderNames()).forEach(headerName -> log.debug("HTTP header {}: {}", headerName, request.getHeader(headerName)));
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {}

	@Override
	public void destroy() {}
}
