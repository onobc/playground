package com.example.actuator.security;

import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.endpoint.annotation.Endpoint;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.RequestMatcher;

@Configuration(proxyBeanMethods = false)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        RequestMatcher loggersGet = EndpointRequestExtended.to(HttpMethod.GET, "loggers");
        RequestMatcher loggersPost = EndpointRequestExtended.to(HttpMethod.POST, "loggers");

        // NOTE: I disabled this to allow POST from local curl w/o ssl (did not want to mess w/ it)
        http.csrf().ignoringRequestMatchers(loggersPost);

        http.authorizeRequests()
                .requestMatchers(EndpointRequest.toLinks()).permitAll()
                .requestMatchers(loggersGet).permitAll()
                .requestMatchers(loggersPost).authenticated()
                .requestMatchers(EndpointRequest.to("health", "info", "bindings")).permitAll()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).authenticated()
                .requestMatchers(EndpointRequest.toAnyEndpoint()).authenticated()
                .and().formLogin()
                .and().httpBasic();
    }

    /**
     * Extends {@link EndpointRequest} to allow HTTP methods to be specified on the request matcher.
     */
    static class EndpointRequestExtended {

        /**
         * Returns a matcher that includes the specified {@link Endpoint actuator endpoints} and http method.
         * For example: <pre class="code">
         * EndpointRequest.to("loggers", HttpMethod.POST)
         * </pre>
         * @param httpMethod the http method to include
         * @param endpoints the endpoints to include
         * @return the configured {@link RequestMatcher}
         */
        static RequestMatcher to(HttpMethod httpMethod, String... endpoints) {
            final EndpointRequest.EndpointRequestMatcher matcher = EndpointRequest.to(endpoints);
            return (request) -> {
                if (!httpMethod.toString().equals(request.getMethod())) {
                    return false;
                }
                return matcher.matches(request);
            };
        }
    }
}
