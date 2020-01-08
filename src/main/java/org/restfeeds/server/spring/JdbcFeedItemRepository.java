package org.restfeeds.server.spring;

import java.util.List;
import org.restfeeds.server.FeedItem;
import org.restfeeds.server.FeedItemRepository;
import org.springframework.jdbc.core.JdbcTemplate;

public class JdbcFeedItemRepository implements FeedItemRepository {

  private final JdbcTemplate jdbcTemplate;
  private final FeedItemMapper feedItemMapper;
  private final String table;

  public JdbcFeedItemRepository(
      JdbcTemplate jdbcTemplate, FeedItemMapper feedItemMapper, String table) {
    this.jdbcTemplate = jdbcTemplate;
    this.feedItemMapper = feedItemMapper;
    this.table = table;
  }

  @Override
  public void append(
      String feed,
      String id,
      String type,
      String resource,
      String method,
      String timestamp,
      Object data) {

    String dataAsString = DataSerializer.toString(data);

    String sql =
        String.format(
            "insert into %s (id, type, resource, method, timestamp, data) values (?, ?, ?, ?, ?, ?)",
            table);
    jdbcTemplate.update(sql, id, type, resource, method, timestamp, dataAsString);
  }

  @Override
  public List<FeedItem> findByFeedPositionGreaterThanEqual(String feed, long position, int limit) {
    String sql = String.format("select * from %s where position >= ? limit ?", table);
    return jdbcTemplate.query(sql, new Object[] {position, limit}, feedItemMapper);
  }

  public void deleteAll() {
    jdbcTemplate.update(String.format("delete from %s", table));
  }
}
