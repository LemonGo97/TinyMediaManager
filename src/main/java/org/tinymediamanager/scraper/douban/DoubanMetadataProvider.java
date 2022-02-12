package org.tinymediamanager.scraper.douban;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.core.movie.MovieSearchAndScrapeOptions;
import org.tinymediamanager.scraper.MediaMetadata;
import org.tinymediamanager.scraper.MediaProviderInfo;
import org.tinymediamanager.scraper.MediaSearchResult;
import org.tinymediamanager.scraper.exceptions.MissingIdException;
import org.tinymediamanager.scraper.exceptions.NothingFoundException;
import org.tinymediamanager.scraper.exceptions.ScrapeException;
import org.tinymediamanager.scraper.interfaces.IMovieMetadataProvider;

import java.util.SortedSet;

/**
 * The Class DoubanMetadataProvider. A meta data, artwork and trailer provider for the site movie.douban.com
 *
 * @author Manuel LemonGo97
 */
public class DoubanMetadataProvider implements IMovieMetadataProvider {

  private static final Logger LOGGER = LoggerFactory.getLogger(DoubanMetadataProvider.class);

  public static final String    ID           = "douban";
  static MediaProviderInfo providerInfo = createMediaProviderInfo();

  private static MediaProviderInfo createMediaProviderInfo() {
    MediaProviderInfo providerInfo = new MediaProviderInfo(ID, "豆瓣电影",
            "<html><h3>豆瓣电影</h3><br />豆瓣电影提供最新的电影介绍及评论包括上映影片的影讯查询及购票服务。你可以记录想看、在看和看过的电影电视剧,顺便打分、写影评。根据你的口味,豆瓣电影会推荐好电影给你。<br /></html>",
            DoubanMetadataProvider.class.getResource("/org/tinymediamanager/scraper/douban_logo.png"));
    providerInfo.getConfig().addText("doubanUserName", "", false);
    providerInfo.getConfig().addText("doubanPassword", "", false);
    providerInfo.getConfig().load();

    return providerInfo;
  }

  @Override
  public MediaProviderInfo getProviderInfo() {
    return providerInfo;
  }

  @Override
  public String getId() {
    return ID;
  }

  /**
   * 进行电影的搜索，如果在文件名中获取到年份，或者用户在界面手动配置了库中电影的年份，那么就会作为搜索条件进行搜索
   * 如果年份没有配置，那么默认 -1
   * @param options
   *          the options
   * @return
   * @throws ScrapeException
   */
  @Override
  public SortedSet<MediaSearchResult> search(MovieSearchAndScrapeOptions options) throws ScrapeException {
    LOGGER.info("=========获取搜索结果列表开始==========>");
    LOGGER.info("搜索条件：{}",options);
    LOGGER.info("=========获取搜索结果列表结束==========>");

    return new DoubanMovieMetadataProvider().search(options);
  }

  @Override
  public MediaMetadata getMetadata(MovieSearchAndScrapeOptions options) throws ScrapeException, MissingIdException, NothingFoundException {
    LOGGER.info("=========获取影片元数据开始==========>");
    LOGGER.info("搜索条件：{}",options);
    LOGGER.info("=========获取影片元数据开始==========>");
    return new DoubanMovieMetadataProvider().getMetadata(options);
  }
}
