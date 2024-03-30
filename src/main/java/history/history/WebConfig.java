package history.history;

import history.history.member.web.interceptor.LogCheckInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LogCheckInterceptor())
                .order(1)

                .addPathPatterns("/**")
                .excludePathPatterns("/", "/members/add", "/login", "/css/**", "/*.ico", "/error",
                        "/logout", "/forgot-password", "/new-password",
                        "/free", "/donation", "/admin/login", "/admin/logout", "/tem",
                        "/my-page/imagesV3/{boardId}", "/my-page/imagesV2/{memberId}",
                        "/my-page/images/{boardId}", "/my-page/imageV4/{image}");
    }

}


