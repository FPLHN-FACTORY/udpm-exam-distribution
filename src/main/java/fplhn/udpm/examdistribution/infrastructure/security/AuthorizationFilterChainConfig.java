package fplhn.udpm.examdistribution.infrastructure.security;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.infrastructure.constant.Role;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.CustomAccessDeniedHandler;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.CustomUnAuthorizeHandler;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.OAuth2AuthenticationFailureHandler;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.OAuth2AuthenticationSuccessHandler;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.user.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthorizationFilterChainConfig {

    private static final Logger log = LoggerFactory.getLogger(AuthorizationFilterChainConfig.class);
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private final CustomUnAuthorizeHandler customUnAuthorizeHandler;

    private final CustomOAuth2UserService customOAuth2UserService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(AbstractHttpConfigurer::disable);
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.authorizeHttpRequests(authorization -> {
            authorization.requestMatchers(MappingConstants.HEAD_OFFICE + "/**").hasAuthority(Role.BAN_DAO_TAO.name());
            authorization.requestMatchers("/giang-vien" + "/**").hasAuthority("GIANG_VIEN");
            authorization.requestMatchers("/sinh-vien" + "/**").hasAuthority("SINH_VIEN");
            authorization.anyRequest().permitAll();
        });
        httpSecurity.oauth2Login(oauth2 -> {
            oauth2.loginPage("/"); //khi người dùng chưa được xác thực, nếu người dùng cố gắng truy cập vào 1 route được bảo vệ thì nó sẽ bắn lại route "/" (nếu đã handle 401 rồi thì nó sẽ không hoạt động).
            oauth2.userInfoEndpoint(userInfoEndpointConfig -> {
                userInfoEndpointConfig.userService(customOAuth2UserService); //lấy thông tin người dùng ở đây để thực hiện author.
            });
            oauth2.successHandler(oAuth2AuthenticationSuccessHandler); //xử lý khi đăng nhập thành công.
            oauth2.failureHandler(oAuth2AuthenticationFailureHandler); //xử lý khi đăng nhập thất bại.
        });
        httpSecurity.logout(logout -> {
            logout.logoutUrl(MappingConstants.REDIRECT_AUTHENTICATION_LOGOUT); //khi bấm vào đây thì thông tin của người dùng hiện tại được dùng để author sẽ bị clear.
            logout.logoutSuccessUrl("/").permitAll(); // sau khi bị clear thì sẽ tự động chuyển tới trang "/".
        });
        httpSecurity.exceptionHandling(exception -> {
            exception.accessDeniedHandler(customAccessDeniedHandler); //xử lý 403
            exception.authenticationEntryPoint(customUnAuthorizeHandler); // xử lý 401
        });

        return httpSecurity.build();
    }

}
