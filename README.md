# restfeed-server-spring

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.restfeeds/restfeed-server-spring/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.restfeeds/restfeed-server-spring)

Spring Boot support for a [REST Feed](http://rest-feeds.org/) server endpoint.

## Getting Started 

Go to [start.spring.io](https://start.spring.io/#!type=maven-project&language=java&platformVersion=2.2.2.RELEASE&packaging=jar&jvmVersion=1.8&groupId=com.example&artifactId=restfeed-server-example&name=restfeed-server-example&description=Demo%20project%20for%20Spring%20Boot&packageName=com.example.restfeed-server-example&dependencies=web,jdbc,h2) and create an new application. Select these dependencies:

- Spring Web (to provide an HTTP endpoint)
- JDBC API (for database connectivity)

for testing, you might also want to add 

- H2 Database

Then add this library to your `pom.xml`:

```xml
    <dependency>
      <groupId>org.restfeeds</groupId>
      <artifactId>restfeed-server-spring</artifactId>
      <version>0.0.1</version>
    </dependency>
```

The [`RestFeedServerAutoConfiguration`](src/main/java/org/restfeeds/server/spring/RestFeedServerAutoConfiguration.java) adds all relevant beans.


Add these properties to your `application.properties`:

```properties
restfeed.server.feed=myfeed
restfeed.server.path=/myfeed
restfeed.server.limit=1000
restfeed.server.jdbc.table=feed
```

Next, make sure to have a valid schema for you database set up (use [Flyway](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto-use-a-higher-level-database-migration-tool) or the [schema.sql](https://docs.spring.io/spring-boot/docs/current/reference/htmlsingle/#howto-initialize-a-database-using-spring-jdbc) file):

```sql
create table feed
(
    position identity primary key,
    id       varchar(1024) not null,
    type     varchar(1024),
    resource varchar(1024),
    method   varchar(1024),
    timestamp timestamp,
    data      clob
);
```

and make sure your database is connected in your `application.properties`:

```properties
spring.datasource.url=jdbc:h2:mem:testdb
```

Finally, make sure that your application adds new feed items by calling the `FeedItemRepository#append` method.

```java
feedItemRepository.append(
    "myfeed",
    UUID.randomUUID().toString(),
    "application/vnd.org.example.resource",
    "/myresource/123",
    "PUT",
    Instant.now().toString(),
    data);
```

When you start the application, you can connect to http://localhost:8080/myfeed.

Find a fully working example at https://github.com/rest-feeds/rest-feed-server-example-spring-web.

## Security

Basic Auth is optionally supported.

Add the `spring-boot-starter-security` dependency to your `pom.xml`:

```xml
    <dependency>
      <groupId>org.springframework.boot</groupId>
      <artifactId>spring-boot-starter-security</artifactId>
    </dependency>
```

and specify a username and password in your `application.properties`:

```properties
# alice:secure123
restfeed.server.credentials[0].username=alice
restfeed.server.credentials[0].password={bcrypt}$2a$10$WWJ/p6BOga2R5TRb2LIy4OzlPNiwNM0/aikVKuQ74dKgs67xLIeGS
```

The password should be encoded, e. g. with [BCrypt](https://docs.spring.io/spring-security/site/docs/current/api/org/springframework/security/crypto/bcrypt/BCryptPasswordEncoder.html).



