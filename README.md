# tinyMediaManager 定制版使用说明

**免责声明：此定制版仅供学习交流所用，请勿用于商业及非法用途，不提供任何付费服务，作者也不会获取任何收益，如有需要请支持正版！！,任何用户修改代码打包造成的问题，本仓库作者不承担任何责任，如产生法律纠纷与本人无关**

## 初衷

2021 年 家里攒了台 NAS ，先后搭建了 jellyfin、 plex、 emby（正在使用）

在建设自己的海报墙时，从各位大神处获知了 tinyMediaManager 这款神器，但是这款软件并不支持豆瓣电影的刮削，最主要的是演员表全部都是英文名称，部分中国演员、导演获取不到图片，于是萌生了给 tmm 添加一个 豆瓣刮削器的想法

## 原理

通过爬虫进行自动化操作：

1. 搜索相关电影
2. 展示搜索结果到 tmm 界面
3. 获取影片详细信息
4. 生成 NFO，并下载相关图片（如海报、预告片）

等待开发的只有前三点，第四点 tmm 会自己生成 nfo 文件，下载相关资源

## 难点

豆瓣电影使用了比较繁杂的反爬技术，对于爬虫而言，不是件简单请求接口或获取 html 解析的事情（尤其在电脑端）

### 解决思路

- 通过 selenium 爬取渲染完成后的页面，并进行相关数据解析，获取 （缺点见后文）
- 解密相关前端加密算法，直接获取相关数据（缺点是加密算法一更新直接 GG）
- 请求移动端的豆瓣网站，进行数据获取（豆瓣移动端并未进行复杂加密，但是什么时候移动端也不能直接爬虫，谁也不清楚）

其中 关于 selenium 爬取又分为两个思路

- 打包 webdriver、浏览器二进制文件 到 tmm 安装包中（但是 tmm 兼容三个平台（windows、macos、linux）这样多平台的兼容性要进行适配，tmm 安装包过大，并且每次爬虫进行更新都需要更新整个tmm 等问题）
- webdriver、浏览器二进制文件 外置，由用户进行相关下载，在 tmm 设置界面进行配置（对于小白来说，上手难度过大）
- 爬虫服务外置，将 webdriver、浏览器二进制文件等 新建立一个项目进行维护，用户只需在 tmm 设置界面配置这个独立服务的地址即可（对于小白来说，上手难度也不小） ，但是这种方式的优点是将爬虫与 tmm 解耦，之后如果豆瓣电影更新反爬机制，只需更新外置服务即可

## 其他

以下网站均可获取电影信息,其他网站各位可补充到 issue 中

- [时光网](http://movie.mtime.com/) 可 JSON 获取接口
- [猫眼电影](https://www.maoyan.com/) 可 HTML 获取接口
- [1905电影网](https://www.1905.com/) 可 HTML 获取接口

****


## 以下为 tmm 官方仓库 README


# [tinyMediaManager][1]

tinyMediaManager (https://www.tinymediamanager.org) full featured media manager to organize and clean up your media library. It is designed to allow you to create/view/edit the metadata, artwork and file structure for your media files used by Kodi (formerly XBMC), Plex, MediaPortal, Emby, Jellyfin and other compatible media center software. As a Java application it is truly cross-platform and will run on Windows, Linux and MacOS (and possibly more).

## [Features][4]

- Automatic updates
- GUI and command line interfaces
- Metadata scrapers for IMDb, TheMovieDb, TVDb, OFDb, Moviemeter, Trakt and more
- Artwork downloaders for TheMovieDb, TVDb and FanArt.tv
- Trailer downloads from TheMovieDb and HD-Trailers.net
- Subtitles downloaded from OpenSubtitles.org
- Manually edit any metadata fields with ease
- Automatic file renaming according to any user-defined format
- Powerful search features with custom filters and sorting
- Saves everything in .nfo files automatically recognized by Kodi and most other media centers
- Technical metadata like codecs, duration and resolution extracted from each media file
- Group movies into sets with special artwork common to all movies in it
- Import TV show collections no matter the file organization style used

## [Release][5]

You can always find the latest release [here][5]. [Pre-release builds][6] and [nightly builds][7] are also available, all of which automatically update within their release channel. You need at least Java Runtime Environment 8 to run tinyMediaManager, which you can get [here][8]. Linux users can download [OpenJDK][9] from the package manager (apt/dnf/yum/pacman) of their distribution too.

## [Changelog][10]

Each release's major improvements are announced on our [project blog][11] or you can view the full ChangeLog [here][12].

## [Screenshots][13]

[![movies01](https://www.tinymediamanager.org/images/screenshots/thumbs/v3/movies/movies01-thumb.png)](https://www.tinymediamanager.org/images/screenshots/v3/movies/movies01.png) [![movies04](https://www.tinymediamanager.org/images/screenshots/thumbs/v3/movies/movies04-thumb.png)](https://www.tinymediamanager.org/images/screenshots/v3/movies/movies04.png)

[![movies08](https://www.tinymediamanager.org/images/screenshots/thumbs/v3/movies/movies08-thumb.png)](https://www.tinymediamanager.org/images/screenshots/v3/movies/movies08.png) [![movies14](https://www.tinymediamanager.org/images/screenshots/thumbs/v3/movies/movies14-thumb.png)](https://www.tinymediamanager.org/images/screenshots/v3/movies/movies14.png)

[![tvshows01](https://www.tinymediamanager.org/images/screenshots/thumbs/v3/tvshows/tvshows01-thumb.png)](https://www.tinymediamanager.org/images/screenshots/v3/tvshows/tvshows01.png) [![tvshows02](https://www.tinymediamanager.org/images/screenshots/thumbs/v3/tvshows/tvshows02-thumb.png)](https://www.tinymediamanager.org/images/screenshots/v3/tvshows/tvshows02.png)

The complete gallery of screenshots can be viewed on our website [here][13].

## [Contributing][14]

Please read our [Contributors' Guide][14] and be sure to base your pull requests against our **devel** branch.

## Building from source

tinyMediaManager is compiled using Apache's build automation tool, [Maven][15]. Check that you have it installed (and git, of course) before attempting a build.

1. Clone this repository to your computer

   ```bash
   git clone https://gitlab.com/tinyMediaManager/tinyMediaManager.git
   ```

1. Build using maven

   ```bash
   mvn package
   ```

After that you will find the packaged build in the folder `dist`

[1]: https://www.tinymediamanager.org
[4]: https://www.tinymediamanager.org/features/
[5]: https://www.tinymediamanager.org/download/
[6]: https://www.tinymediamanager.org/download/prerelease
[7]: https://www.tinymediamanager.org/download/nightly-build
[8]: https://www.java.com/en/download/manual.jsp
[9]: https://openjdk.java.net/install/
[10]: /changelog.txt
[11]: https://www.tinymediamanager.org/blog/
[12]: https://www.tinymediamanager.org/changelog/
[13]: https://www.tinymediamanager.org/screenshots/
[14]: /CONTRIBUTING.md
[15]: https://maven.apache.org/
