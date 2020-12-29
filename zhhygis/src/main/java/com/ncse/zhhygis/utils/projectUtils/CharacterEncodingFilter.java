package com.ncse.zhhygis.utils.projectUtils;

import org.jboss.logging.Logger;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * ClassName:  [功能名称]
 * Description:  [字符编码过滤器]
 * Date:  2018/11/22 14 52
 *
 * @author Xugn
 * @version 1.0.0
 */
@WebFilter(filterName = "CharacterEncodingFilter",urlPatterns = "/*")
public class CharacterEncodingFilter implements Filter{

    private final Logger log = Logger.getLogger(this.getClass());

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");
        filterChain.doFilter(request , response);
    }
    @Override
    public void destroy() {

    }
}
