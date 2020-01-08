package org.restfeeds.server.spring;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import org.restfeeds.server.FeedItemRepository;
import org.restfeeds.server.NextLinkBuilder;
import org.restfeeds.server.RestFeedEndpoint;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration(proxyBeanMethods = false)
@ConditionalOnClass(RestFeedEndpoint.class)
@EnableConfigurationProperties(RestFeedServerProperties.class)
public class RestFeedServerAutoConfiguration {

  @Bean
  @ConditionalOnProperty(name = "restfeed.server.path")
  RestFeedEndpointController restFeedEndpointController(
      RestFeedEndpoint restFeedEndpoint, RestFeedServerProperties properties) {
    return new RestFeedEndpointController(
        restFeedEndpoint, properties.getFeed(), properties.getLimit());
  }

  @Bean
  @ConditionalOnMissingBean(NextLinkBuilder.class)
  CurrentRequestNextLinkBuilder nextLinkBuilder() {
    return new CurrentRequestNextLinkBuilder();
  }

  @Bean
  @ConditionalOnMissingBean(FeedItemMapper.class)
  FeedItemMapper feedItemMapper(NextLinkBuilder nextLinkBuilder) {
    return new FeedItemMapper(nextLinkBuilder);
  }

  @Bean
  @ConditionalOnClass(JdbcTemplate.class)
  @ConditionalOnMissingBean(FeedItemRepository.class)
  JdbcFeedItemRepository feedItemRepository(
      JdbcTemplate jdbcTemplate,
      FeedItemMapper feedItemMapper,
      RestFeedServerProperties properties) {
    return new JdbcFeedItemRepository(
        jdbcTemplate, feedItemMapper, properties.getJdbc().getTable());
  }

  @Bean
  @ConditionalOnMissingBean(RestFeedEndpoint.class)
  RestFeedEndpoint restFeedEndpoint(FeedItemRepository feedItemRepository) {
    return new RestFeedEndpoint(feedItemRepository);
  }

  @Configuration(proxyBeanMethods = false)
  @ConditionalOnClass(InMemoryUserDetailsManager.class)
  public static class RestFeedServerSecurityAutoConfiguration {

    @Bean
    @ConditionalOnProperty(name = "restfeed.server.credentials[0].username")
    UserDetailsService userDetailsService(RestFeedServerProperties properties) {
      List<UserDetails> users =
          properties.getCredentials().stream()
              .map(
                  credential ->
                      new User(
                          credential.getUsername(),
                          credential.getPassword(),
                          Collections.singletonList(new SimpleGrantedAuthority("USER"))))
              .collect(Collectors.toList());
      return new InMemoryUserDetailsManager(users);
    }
  }
}
