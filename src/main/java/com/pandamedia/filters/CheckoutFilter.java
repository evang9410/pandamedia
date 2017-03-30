package com.pandamedia.filters;

import com.pandamedia.beans.ShoppingCart;
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
@WebFilter(filterName = "CheckoutFilter", urlPatterns = {"/clientsecure/purchase/*"})
public class CheckoutFilter implements Filter{
    private static final Logger LOG = Logger.getLogger("CheckoutFilter.class");
    private ServletContext context;
    
    @Inject
    private ShoppingCart cart;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        context = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        context.log("In the filer");
        
        // Making sure cart is not empty
        if (cart.getIsCartEmpty())
        {
            context.log("Cart is empty.");
            String contextPath = ((HttpServletRequest) request)
                    .getContextPath();
            ((HttpServletResponse) response).sendRedirect(contextPath
                    + "/cart.xhtml");
            context.log(contextPath + "/cart.xhtml");
        }
        else
        {
            context.log("Cart is not empty.");
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // Nothing to do here
    }    
}