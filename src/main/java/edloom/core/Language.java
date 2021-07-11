package edloom.core;

import java.util.ArrayList;

public class Language extends ArrayList<String> {
    private final String name;

    public Language(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

}
