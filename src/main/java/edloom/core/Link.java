package edloom.core;

import java.io.Serializable;

public class Link implements Serializable {
    private final String name, url;

    public Link(String name, String url) {
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }


    public String getUrl() {
        return url;
    }

}
