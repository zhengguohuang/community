package tech.turl.community.controller.interceptor;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import tech.turl.community.annotation.LoginRequired;
import tech.turl.community.util.HostHolder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Method;

/**
 * @author zhengguohuang
 * @date 2021/03/16
 */
// @Component
@Deprecated
public class LoginRequiredInterceptor implements HandlerInterceptor {
    @Autowired private HostHolder hostHolder;

    @Override
    public boolean preHandle(
            HttpServletRequest request, HttpServletResponse response, Object handler)
            throws Exception {
        if (handler instanceof HandlerMethod) {
            HandlerMethod handlerMethod = (HandlerMethod) handler;
            Method method = handlerMethod.getMethod();
            LoginRequired loginRequired = method.getAnnotation(LoginRequired.class);
            if (loginRequired != null && hostHolder.getUser() == null) {
                response.sendRedirect(request.getContextPath() + "/login");
                System.out.println("----------------------拦截---------------------");
                return false;
            }
        }
        return true;
    }
}
