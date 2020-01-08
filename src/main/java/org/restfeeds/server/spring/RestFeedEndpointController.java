package org.restfeeds.server.spring;

import java.util.List;
import org.restfeeds.server.FeedItem;
import org.restfeeds.server.RestFeedEndpoint;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${restfeed.server.path}")
public class RestFeedEndpointController {

  static final Logger log = LoggerFactory.getLogger(RestFeedEndpointController.class);

  private final RestFeedEndpoint restFeedEndpoint;
  private final String feed;
  private final int limit;

  public RestFeedEndpointController(RestFeedEndpoint restFeedEndpoint, String feed, int limit) {
    this.restFeedEndpoint = restFeedEndpoint;
    this.feed = feed;
    this.limit = limit;
  }

  @GetMapping
  public List<FeedItem> getFeedItems(
      @RequestParam(value = "offset", defaultValue = "0") long offset) {
    log.debug("GET feed {} with offset {}", feed, offset);
    List<FeedItem> items = restFeedEndpoint.fetch(feed, offset, limit);
    log.debug("GET feed {} with offset {} returned {} items", feed, offset, items.size());
    return items;
  }
}
