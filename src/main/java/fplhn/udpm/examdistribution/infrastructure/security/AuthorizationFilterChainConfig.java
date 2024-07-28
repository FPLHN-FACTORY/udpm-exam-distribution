package fplhn.udpm.examdistribution.infrastructure.security;

import fplhn.udpm.examdistribution.infrastructure.constant.MappingConstants;
import fplhn.udpm.examdistribution.infrastructure.constant.Role;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.CustomAccessDeniedHandler;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.CustomLogoutHandler;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.CustomUnAuthorizeHandler;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.OAuth2AuthenticationFailureHandler;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.OAuth2AuthenticationSuccessHandler;
import fplhn.udpm.examdistribution.infrastructure.security.oauth2.user.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static fplhn.udpm.examdistribution.utils.Helper.appendWildcard;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class AuthorizationFilterChainConfig {

    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;

    private final CustomAccessDeniedHandler customAccessDeniedHandler;

    private final CustomUnAuthorizeHandler customUnAuthorizeHandler;

    private final CustomLogoutHandler customLogoutHandler;

    private final CustomOAuth2UserService customOAuth2UserService;

    @Value("${allowed.origin}")
    public String ALLOWED_ORIGIN;

    @Bean
    CorsConfigurationSource corsConfigurationSource() {
        final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        source.registerCorsConfiguration("/**", config.applyPermitDefaultValues());
        config.setAllowedHeaders(List.of("Cache-Control", "Content-Type", "*"));
        config.setAllowedOrigins(List.of(ALLOWED_ORIGIN));
        config.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "PUT", "OPTIONS", "PATCH", "DELETE"));
        config.setAllowCredentials(true);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(httpSecurityCorsConfigurer -> {
            httpSecurityCorsConfigurer.configurationSource(corsConfigurationSource());
        });
        httpSecurity.csrf(AbstractHttpConfigurer::disable);
        httpSecurity.authorizeHttpRequests(authorization -> {
            authorization.requestMatchers(
                            appendWildcard(MappingConstants.HEAD_OFFICE))
                    .hasAuthority(Role.BAN_DAO_TAO.name());
            authorization.requestMatchers(
                            appendWildcard(MappingConstants.TEACHER))
                    .hasAuthority(Role.GIANG_VIEN.name());
            authorization.requestMatchers(
                            appendWildcard(MappingConstants.STUDENT))
                    .hasAuthority(Role.SINH_VIEN.name());
            authorization.requestMatchers(
                            appendWildcard(MappingConstants.HEAD_DEPARTMENT))
                    .hasAuthority(Role.CHU_NHIEM_BO_MON.name());
            authorization.requestMatchers(
                            appendWildcard(MappingConstants.HEAD_SUBJECT))
                    .hasAuthority(Role.TRUONG_MON.name());
            authorization.requestMatchers(
                    "/static/**",
                    "/css/**",
                    "/js/**",
                    "/assets/**",
                    "/webjars/**",
                    "/favicon.ico",
                    "/error",
                    "/plugins/**",
                    "/version"
            ).permitAll();
            authorization.anyRequest().permitAll();
        });
        httpSecurity.oauth2Login(oauth2 -> {
            oauth2.loginPage("/");
            oauth2.userInfoEndpoint(userInfoEndpointConfig -> {
                userInfoEndpointConfig.userService(customOAuth2UserService);
            });
            oauth2.successHandler(oAuth2AuthenticationSuccessHandler);
            oauth2.failureHandler(oAuth2AuthenticationFailureHandler);
        });
        httpSecurity.logout(logout -> {
            logout.logoutUrl(MappingConstants.REDIRECT_AUTHENTICATION_LOGOUT);
            logout.logoutSuccessUrl("/").permitAll();
            logout.addLogoutHandler(customLogoutHandler);
        });
        httpSecurity.exceptionHandling(exception -> {
            exception.accessDeniedHandler(customAccessDeniedHandler);
            exception.authenticationEntryPoint(customUnAuthorizeHandler);
        });

        return httpSecurity.build();
    }

}
