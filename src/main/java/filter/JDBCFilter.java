package filter;

import conn.ConnectionUtils;
import java.io.IOException;
import java.sql.Connection;
import java.util.Collection;
import java.util.Map;

import utils.MyUtils;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
@WebFilter(filterName = "jdbcFilter", urlPatterns = { "/*" })
public class JDBCFilter implements Filter {

    public JDBCFilter() {
    }

    @Override
    public void init(FilterConfig fConfig) throws ServletException {
    }

    @Override
    public void destroy() {
    }

    private boolean needJDBC(HttpServletRequest request) {
        System.out.println("JDBC Filter");

        String servletPath = request.getServletPath();
        String pathInfo = request.getPathInfo();

        String urlPattern = servletPath;

        if (pathInfo != null) {
            urlPattern = servletPath + "/*";
        }

        ServletContext servletContext; // Lấy ServletContext từ HttpServletRequest
        servletContext = request.getServletContext();
        Map<String, ? extends ServletRegistration> servletRegistrations = servletContext.getServletRegistrations();

        Collection<? extends ServletRegistration> values = servletRegistrations.values();
        for (ServletRegistration sr : values) {
            Collection<String> mappings = sr.getMappings();
            if (mappings.contains(urlPattern)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;

        if (this.needJDBC(req)) {

            System.out.println("Open Connection for: " + req.getServletPath());

            Connection conn = null;
            try {
                conn = ConnectionUtils.getConnection();
                conn.setAutoCommit(false);

                MyUtils.storeConnection(request, conn);

                chain.doFilter(request, response);

                conn.commit();
            } catch (Exception e) {
                e.printStackTrace();
                ConnectionUtils.rollbackQuietly(conn);
                throw new ServletException("Database operation error: " + e.getMessage(), e);
            } finally {
                ConnectionUtils.closeQuietly(conn);
            }
        } else {
            chain.doFilter(request, response);
        }
    }
}
