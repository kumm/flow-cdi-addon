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
    public TemplateData getTemplateContent(Class<? extends PolymerTemplate<?>> clazz, String tag) {
        try {
            Document document = Jsoup.parse(content,
                    StandardCharsets.UTF_8.name(), "");
            Element element = document.getElementsByTag("dom-module").get(0);
            return new TemplateData("dummy", element);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
}
