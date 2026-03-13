package top.aliangmu.bookkeepingapp.config;

import cn.dev33.satoken.fun.strategy.SaCorsHandleFunction;
import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.router.SaRouter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class Interceptor implements WebMvcConfigurer {

    /**
     * Sa-token 拦截器
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/bookkeeping/user/login")
                .excludePathPatterns("/bookkeeping/admin/api/login");
    }

    @Bean
    public SaCorsHandleFunction corsHandleFunction() {
        return (req, res, sto) -> {
            // 获取请求 Origin，只允许特定域名
            String origin = req.getHeader("Origin");
            if (origin != null && (
                    origin.equals("http://localhost:5173") ||                   // 开发环境
                            origin.equals("https://aliangmux.online") ||        // 生产环境
                            origin.equals("https://www.aliangmux.online")
            )) {
                res.setHeader("Access-Control-Allow-Origin", origin);
            }

            res.setHeader("Access-Control-Allow-Methods", "POST, GET, OPTIONS, DELETE, PUT, PATCH");
            res.setHeader("Access-Control-Max-Age", "3600");
            res.setHeader("Access-Control-Allow-Headers", "*");
            res.setHeader("Access-Control-Allow-Credentials", "true");  // 如果需要携带 cookie/token

            SaRouter.match(SaHttpMethod.OPTIONS).free(r -> {}).back();
        };
    }
}
