package com.wcs.vaadin.flow.cdi.polymertemplate;

import com.vaadin.flow.component.polymertemplate.PolymerTemplate;
import com.vaadin.flow.component.polymertemplate.TemplateParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class TestTemplateParser implements TemplateParser {
    final InputStream content;

    public TestTemplateParser(InputStream content) {
        this.content = content;
    }

    @Override
    public Element getTemplateContent(Class<? extends PolymerTemplate<?>> clazz, String tag) {
        try {
            Document document = Jsoup.parse(content,
                    StandardCharsets.UTF_8.name(), "");
            return document.getElementsByTag("dom-module").get(0);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
