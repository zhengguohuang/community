package tech.turl.community.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.access.AccessDeniedHandler;
import tech.turl.community.util.CommunityConstant;
import tech.turl.community.util.CommunityUtil;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author zhengguohuang
 * @date 2021/03/27
 */
@Configuration
public class SecurityConfig extends WebSecurityConfigurerAdapter implements CommunityConstant {
    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers("/resources/**");
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        // 授权
        http.authorizeRequests()
                .antMatchers(
                        "/user/setting",
                        "/user/upload",
                        "/user/forgetPassword",
                        "/discuss/add",
                        "/discuss/my",
                        "/comment/add/**",
                        "/letter/**",
                        "/notice/**",
                        "/like",
                        "/follow",
                        "/unfollow")
                .hasAnyAuthority(AUTHORITY_USER, AUTHORITY_ADMIN, AUTHORITY_MODERATOR)
                .antMatchers("/discuss/top", "/discuss/wonderful")
                .hasAnyAuthority(AUTHORITY_MODERATOR)
                .antMatchers("/discuss/delete", "/data/**", "/data/uv", "/data/dau", "/actuator/**")
                .hasAnyAuthority(AUTHORITY_ADMIN)
                .anyRequest()
                .permitAll()
                .and()
                .csrf()
                .disable();
        // 权限不够时的处理
        // 1.没有登录，这里有bug,登录后不刷新点赞，关注会显示没有登录
        // 2.权限不足
        http.exceptionHandling()
                .authenticationEntryPoint(
                        new AuthenticationEntryPoint() {
                            // 没有登录
                            @Override
                            public void commence(
                                    HttpServletRequest request,
                                    HttpServletResponse response,
                                    AuthenticationException e)
                                    throws IOException, ServletException {
                                String xRequestedWith = request.getHeader("x-requested-with");
                                if ("XMLHttpRequest".equals(xRequestedWith)) {
                                    response.setContentType("application/plain;charset=utf-8");
                                    PrintWriter writer = response.getWriter();
                                    writer.write(CommunityUtil.getJSONString(403, "你还没有登录哦!"));
                                } else {
                                    response.sendRedirect(request.getContextPath() + "/login");
                                }
                            }
                        })
                .accessDeniedHandler(
                        new AccessDeniedHandler() {
                            // 权限不足
                            @Override
                            public void handle(
                                    HttpServletRequest request,
                                    HttpServletResponse response,
                                    AccessDeniedException e)
                                    throws IOException, ServletException {
                                String xRequestedWith = request.getHeader("x-requested-with");
                                if ("XMLHttpRequest".equals(xRequestedWith)) {
                                    response.setContentType("application/plain;charset=utf-8");
                                    PrintWriter writer = response.getWriter();
                                    writer.write(CommunityUtil.getJSONString(403, "你没有访问此功能的权限!"));
                                } else {
                                    response.sendRedirect(request.getContextPath() + "/denied");
                                }
                            }
                        });

        // Security底层默认会拦截/logout请求,进行退出处理.
        // 覆盖它默认的逻辑,才能执行我们自己的退出代码.
        http.logout().logoutUrl("/securitylogout");
    }
}
