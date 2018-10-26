package com.example.it;

import com.adobe.cq.testing.client.CQClient;
import com.adobe.cq.testing.client.ComponentClient;
import com.adobe.cq.testing.client.ReplicationClient;
import com.adobe.cq.testing.junit.rules.CQClassRule;
import com.adobe.cq.testing.junit.rules.CQRule;
import com.adobe.cq.testing.junit.rules.Page;
import com.example.it.components.NewsletterRegistration;
import org.apache.http.HttpStatus;
import org.apache.sling.testing.clients.ClientException;
import org.apache.sling.testing.clients.util.poller.Polling;
import org.apache.sling.testing.junit.rules.instance.ExistingInstance;
import org.apache.sling.testing.junit.rules.instance.Instance;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ChangeComponentIT {

    private static final Logger LOG = LoggerFactory.getLogger(ChangeComponentIT.class);

    private static final String PAGE_PATH = "/content/path/to/specific/page";  // TODO set your content page
    private static final String COMPONENT_PATH = "/jcr:content/par/path/to/content";  // TODO set your component path

    @ClassRule
    public static CQClassRule classRule = new CQClassRule();

    @ClassRule
    public static Instance authorRule = new ExistingInstance().withRunMode("author");

    @ClassRule
    public static Instance publishRule = new ExistingInstance().withRunMode("publish");

    @Rule
    public CQRule cqRule = new CQRule();

    @Rule
    public Page page = new Page(authorRule) {
        @Override
        protected String initialTemplatePath() {
            return "/apps/path/to/template";  // TODO set your template
        }

        @Override
        protected String initialParentPath() {
            return "/content/path/to/root";  // TODO set your content root page
        }

        @Override
        protected void prepare() throws ClientException {
        }
    };

    @Before
    public void setup() throws ClientException {
        adminAuthor = authorRule.getAdminClient(CQClient.class);
        componentClient = adminAuthor.adaptTo(ComponentClient.class);
        componentClient.registerComponent(NewsletterRegistration.RESOURCE_TYPE, NewsletterRegistration.class);

        anonymousPublish = publishRule.getClient(CQClient.class, null, null);
    }

    private CQClient adminAuthor;
    private CQClient anonymousPublish;
    private ComponentClient componentClient;

    @Test
    public void testAuthorPath() throws ClientException {
        adminAuthor = authorRule.getAdminClient(CQClient.class);
        adminAuthor.doGet(PAGE_PATH, HttpStatus.SC_OK);
    }

    @Test
    public void testChangeComponent() throws ClientException, InterruptedException, TimeoutException {
        NewsletterRegistration nrComponent = componentClient.getComponent(PAGE_PATH + COMPONENT_PATH, NewsletterRegistration.class);
        LOG.info("component title is: {}", nrComponent.getProperty("componenttitle"));

        // Change the title
        final String newTitle = "newsletter registration " + UUID.randomUUID();
        LOG.info("setting new title: {}", newTitle);
        nrComponent.setProperty("componenttitle", newTitle);
        nrComponent.save(HttpStatus.SC_OK);

        // Publish the changes
        adminAuthor.adaptTo(ReplicationClient.class).activate(PAGE_PATH, HttpStatus.SC_OK);

        // Wait for the replication and check the new title is present in the page
        new Polling() {
            @Override
            public Boolean call() throws Exception {
                try {
                    anonymousPublish.doGet(PAGE_PATH + ".html", HttpStatus.SC_OK).checkContentContains(newTitle);
                } catch (Exception e) {
                    LOG.info(e.getMessage());
                    throw e;
                }
                return true;
            }

        }.poll(TimeUnit.SECONDS.toMillis(30), 100);
    }
}
