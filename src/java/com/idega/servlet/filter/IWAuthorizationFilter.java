/*
 * $Id: IWAuthorizationFilter.java,v 1.16 2008/12/18 13:55:08 valdas Exp $ Created on 31.7.2004
 * in project com.idega.core
 * 
 * Copyright (C) 2004-2005 Idega Software hf. All Rights Reserved.
 * 
 * This software is the proprietary information of Idega hf. Use is subject to
 * license terms.
 */
package com.idega.servlet.filter;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.idega.core.accesscontrol.business.LoginBusinessBean;
import com.idega.core.view.ViewManager;
import com.idega.core.view.ViewNode;
import com.idega.idegaweb.IWUserContext;
import com.idega.idegaweb.IWUserContextImpl;
import com.idega.util.RequestUtil;
import com.idega.util.StringUtil;

/**
 * <p>
 * This servletFilter is by default mapped early in the filter chain in idegaWeb and 
 * checks if the user as sufficient access to a resource and blocks it if the user hasn't 
 * sufficent priviliges.<br/>
 * In some instances (when accessing the workspace) it redirects the user to the login page.
 * </p>
 * Last modified: $Date: 2008/12/18 13:55:08 $ by $Author: valdas $
 * 
 * @author <a href="mailto:tryggvil@idega.com">Tryggvi Larusson</a>
 * @version $Revision: 1.16 $
 */
public class IWAuthorizationFilter extends BaseFilter implements Filter {

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#init(javax.servlet.FilterConfig)
	 */
	public void init(FilterConfig arg0) throws ServletException {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see javax.servlet.Filter#doFilter(javax.servlet.ServletRequest, javax.servlet.ServletResponse, javax.servlet.FilterChain)
	 */
	public void doFilter(ServletRequest srequest, ServletResponse sresponse, FilterChain chain) throws IOException, ServletException {
		HttpServletRequest request = (HttpServletRequest)srequest;
		HttpServletResponse response = (HttpServletResponse)sresponse;

		LoginBusinessBean loginBusiness = getLoginBusiness(request);
		boolean isLoggedOn = loginBusiness.isLoggedOn(request);
		boolean hasPermission = getIfUserHasPermission(request);
		if(!hasPermission){
			if(getIfSendToLoginPage(request,response,isLoggedOn)){
				
				String requestedUri = request.getRequestURI();
				
				String newUrl = getNewLoginUri(request,requestedUri);
				response.sendRedirect(newUrl);
			}
			else{
				if (isLoggedOn) {
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
					return;
				}
				
				String redirectUri = RequestUtil.getRedirectUriByApplicationProperty(request, HttpServletResponse.SC_FORBIDDEN);
				if (StringUtil.isEmpty(redirectUri)) {
					//by default send a 403 error
					response.sendError(HttpServletResponse.SC_FORBIDDEN);
				}
				else {
					Logger.getLogger(this.getClass().getName()).warning("Found default page for error 403, redirecting to: " + redirectUri);
					response.sendRedirect(redirectUri);
				}
			}
		}
		else{
			boolean viewNodeExists = true;
			try{
				ViewManager.getInstance(getIWMainApplication(request)).getViewNodeForRequest(request);
				viewNodeExists=true;
			}
			catch(Exception e){
				e.printStackTrace();
				viewNodeExists=false;
			}
			
			if(!viewNodeExists){
				String redirectUri = RequestUtil.getRedirectUriByApplicationProperty(request, HttpServletResponse.SC_NOT_FOUND);
				if (StringUtil.isEmpty(redirectUri)) {
					response.sendError(HttpServletResponse.SC_NOT_FOUND);
					return;
				}
				
				Logger.getLogger(this.getClass().getName()).warning("Found default page for error 404, redirecting to: " + redirectUri);
				response.sendRedirect(redirectUri);
				return;
			}
			chain.doFilter(srequest,sresponse);
		}
		//chain.doFilter(srequest,sresponse);

	}
	
	protected boolean getIfUserHasPermission(HttpServletRequest request){
		//HttpServletRequest request = iwc.getRequest();
		/*HttpServletResponse response = iwc.getResponse();*/
		String uri = getURIMinusContextPath(request);
		if(uri.startsWith(NEW_WORKSPACE_URI_MINUSSLASH)){
			LoginBusinessBean loginBusiness = getLoginBusiness(request);
			if(!loginBusiness.isLoggedOn(request)){
				return false;
			}
			else{
				ViewManager vManager = ViewManager.getInstance(getIWMainApplication(request));
				ViewNode node = vManager.getViewNodeForRequest(request);
				IWUserContext iwuc = new IWUserContextImpl(request.getSession(),request.getSession().getServletContext());
				
				if(vManager.hasUserAccess(node,iwuc)){
					return true;
				}
				else{
					return false;
				}
			}
		}
		else if(uri.startsWith(PAGES_URI)){
			boolean pageAccess = getIWMainApplication(request).getAccessController().hasViewPermissionForPageURI(uri,request);
			return pageAccess;
		}
		return true;
	}
	
	/**
	 * Gets if it should redirect to the login page or send a 403 error
	 * @param request
	 * @param response
	 * @return
	 */
	protected boolean getIfSendToLoginPage(HttpServletRequest request,HttpServletResponse response,boolean isLoggedOn){
		String uri = getURIMinusContextPath(request);
		if(uri.startsWith(NEW_WORKSPACE_URI_MINUSSLASH)&&!isLoggedOn){
			return true;
		}
		else{
			//return false by default
			return false;
		}
	}


	/* (non-Javadoc)
	 * @see javax.servlet.Filter#destroy()
	 */
	public void destroy() {
		// TODO Auto-generated method stub

	}

}
