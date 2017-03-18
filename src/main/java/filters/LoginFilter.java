package filters;

import java.io.IOException;
import java.util.logging.Logger;
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
import persistence.entities.ShopUser;

/**
 *
 * @author Erika Bourque
 */
@WebFilter(filterName = "LoginFilter", urlPatterns = {"/secure/*", "/faces/secure/*"})
public class LoginFilter implements Filter{
    private static final Logger LOG = Logger.getLogger("LoginFilter.class");
    private ServletContext context;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        context = filterConfig.getServletContext();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        context.log("In the filer");        
        // Getting the shop user from session
        ShopUser user = (ShopUser) ((HttpServletRequest) request)
                .getSession().getAttribute("user");
        
        // Making sure user is not null or not persisted to db
        if ((user == null) || (user.getId() == 0))
        {
            context.log("User not logged in.");
            String contextPath = ((HttpServletRequest) request)
                    .getContextPath();
            ((HttpServletResponse) response).sendRedirect(contextPath
                    + "/registration.xhtml");
        }
        else
        {
            context.log("User is logged in.  id = " + user.getId());
            chain.doFilter(request, response);
        }
    }

    @Override
    public void destroy() {
        // Nothing to do here
    }
    
}
