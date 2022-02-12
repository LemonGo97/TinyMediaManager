package org.tinymediamanager.scraper.douban;

import com.uwetrottmann.tmdb2.entities.Movie;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tinymediamanager.core.movie.MovieSearchAndScrapeOptions;
import org.tinymediamanager.scraper.MediaMetadata;
import org.tinymediamanager.scraper.MediaSearchResult;
import org.tinymediamanager.scraper.entities.MediaType;
import org.tinymediamanager.scraper.exceptions.ScrapeException;
import org.tinymediamanager.scraper.tmdb.TmdbMetadataProvider;

import java.util.SortedSet;
import java.util.TreeSet;

import static org.tinymediamanager.scraper.douban.DoubanMetadataProvider.providerInfo;

/**
 *
 * 列表搜索【HTML】（手机端网页搜索）：https://m.douban.com/search/?query={URL编码后的搜索词}&type=1002
 *
 * 列表搜索【JSON】（搜索建议框）：https://movie.douban.com/j/subject_suggest?q={URL编码后的搜索词}
 *
 * [
 *     {
 *         "episode":"",
 *         "img":"影片图片（URL全路径）",
 *         "title":"影片名",
 *         "url":"影片详情页URL",
 *         "type":"movie",
 *         "year":"影片年份",
 *         "sub_title":"影片名（副标题）",
 *         "id":"豆瓣 ID"
 *     }
 * ]
 *
 * 演员表【HTML】：https://movie.douban.com/subject/{影片ID}/celebrities
 */
class DoubanMovieMetadataProvider {
    private static final Logger LOGGER = LoggerFactory.getLogger(DoubanMovieMetadataProvider.class);

    /**
     * 列表搜索（搜索建议框）：https://movie.douban.com/j/subject_suggest?q={URL编码后的搜索词}
     *
     * [
     *     {
     *         "episode":"",
     *         "img":"影片图片（URL全路径）",
     *         "title":"影片名",
     *         "url":"影片详情页URL",
     *         "type":"movie",
     *         "year":"影片年份",
     *         "sub_title":"影片名（副标题）",
     *         "id":"豆瓣 ID"
     *     }
     * ]
     *
     * @param options
     * @return
     * @throws ScrapeException
     */
    SortedSet<MediaSearchResult> search(MovieSearchAndScrapeOptions options) throws ScrapeException {
        LOGGER.debug("search(): {}", options);
        // TODO 获取搜索结果数据
        SortedSet<MediaSearchResult> results = new TreeSet<>();
        results.add(cover(options));
        return results;
    }

    /**
     * 元数据获取
     * options 参数中 ids属性获取 providerInfo.getId() 为 key 的 value 值 这个值就是豆瓣电影ID
     * @param options
     * @return
     */
    MediaMetadata getMetadata(MovieSearchAndScrapeOptions options) {
        LOGGER.debug("getMetadata(): {}", options);
        // TODO 获取电影元数据
        return morphMovieToMediaMetadata(options);
    }

    private MediaSearchResult cover(MovieSearchAndScrapeOptions options){
        MediaSearchResult searchResult = new MediaSearchResult(providerInfo.getId(), MediaType.MOVIE);
        // TODO 获取到搜索结果数据后各种 Setter
        // id
        searchResult.setId("24987018");
        // 影片名
        searchResult.setTitle("赤道");
        // 概述
        searchResult.setOverview("概述");
        // 原始标题
        searchResult.setOriginalTitle("赤道");
        // 原始语言
        searchResult.setOriginalLanguage("原始语言");
        // 封面图URL
        searchResult.setPosterUrl("https://img1.doubanio.com/view/photo/s_ratio_poster/public/p2238375159.jpg");
        // 年份
        searchResult.setYear(2015);
        // 评分
        searchResult.setScore(6.5f);
        searchResult.calculateScore(options);
        return searchResult;
    }

    private MediaMetadata morphMovieToMediaMetadata(MovieSearchAndScrapeOptions options) {
        MediaMetadata mediaMetadata = new MediaMetadata(providerInfo.getId());
        // TODO 获取到电影元数据后各种 Setter
        return mediaMetadata;
    }
}
