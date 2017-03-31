package com.pandamedia.filters;

import com.pandamedia.beans.UserActionBean;
import java.io.IOException;
import java.util.logging.Logger;
import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author Erika Bourque
 */
@WebFilter(filterName = "ShopFilter", urlPatterns = {"/shop/*"})
public class ShopFilter implements Filter{
    private static final Logger LOG = Logger.getLogger("ShopFilter.class");
    private ServletContext context;
    
    @Inject
    private UserActionBean uab;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        context = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        context.log("In the filer");
        
        // Making sure that logged in user is not a manager
        if ((uab.isLogin()) && (uab.getCurrUser().getIsManager() == 1))
        {
            context.log("User is a manager. id = " + uab.getCurrUser().getId());
            String contextPath = ((HttpServletRequest) request)
                    .getContextPath();
            // TODO: change this to new management location once moved
            ((HttpServletResponse) response).sendRedirect(contextPath
                    + "/userconnection/login.xhtml");
            context.log(contextPath + "/userconnection/login.xhtml");            
        }
        else
        {
            context.log("User is not logged in or is not a manager.");
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // Nothing to do here
    }
    
}