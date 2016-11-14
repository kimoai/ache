package focusedCrawler.crawler.async;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.Assert.assertThat;

import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.apache.tika.metadata.Metadata;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;

import com.google.common.eventbus.EventBus;

import focusedCrawler.crawler.async.RobotsTxtHandler.RobotsData;
import focusedCrawler.crawler.crawlercommons.fetcher.FetchedResult;
import focusedCrawler.link.frontier.LinkRelevance;

public class RobotsTxtHandlerTest {

    @Test
    public void shouldParseLinksFromSitemapXml() throws Exception {
        // given
        EventBus linkStorageMock = Mockito.mock(EventBus.class);
        
        
        RobotsTxtHandler handler = new RobotsTxtHandler(linkStorageMock, "TestAgent");
        
        String url = "http://www.example.com/robots.txt";
        Path robotsFilePath = Paths.get(RobotsTxtHandler.class.getResource("sample-robots.txt").toURI());
        byte[] robotsContent = Files.readAllBytes(robotsFilePath);
        
        FetchedResult response = new FetchedResult(url, url, 1, new Metadata(), robotsContent, "text/plain", 1, null, url, 0, "127.0.0.1", 200, "OK");

        LinkRelevance link = new LinkRelevance(new URL(url), 1, LinkRelevance.Type.ROBOTS);
        
        // when
        handler.completed(link , response);
        
        // then
        ArgumentCaptor<RobotsData> argument = ArgumentCaptor.forClass(RobotsData.class);
        Mockito.verify(linkStorageMock).post(argument.capture());
        RobotsData robotsData = argument.getValue();
        
        assertThat(robotsData, is(notNullValue()));
        assertThat(robotsData.sitemapUrls.size(), is(2));
        assertThat(robotsData.sitemapUrls.get(0), is("http://www.example.com/example-sitemap/sitemap.xml"));
        assertThat(robotsData.sitemapUrls.get(1), is("http://www.example.com/example-sitemap/sitemap-news.xml"));
    }

}
