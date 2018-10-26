package com.example.it.components;

import com.adobe.cq.testing.client.ComponentClient;
import com.adobe.cq.testing.client.components.AbstractComponent;

public class NewsletterRegistration extends AbstractComponent {

    public static final String RESOURCE_TYPE = "custom/components/newsletter";

    public NewsletterRegistration(ComponentClient client, String pagePath, String location, String nameHint) {
        super(client, pagePath, location, nameHint);
    }

    @Override
    public String getResourceType() {
        return RESOURCE_TYPE;
    }
}
