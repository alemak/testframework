package com.netaporter.test.utils.assertion.html;

import com.gargoylesoftware.htmlunit.StringWebResponse;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HTMLParser;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.hamcrest.Matcher;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: J.Christian@net-a-porter.com
 * Date: 07/05/2013
 * Time: 12:27
 * To change this template use File | Settings | File Templates.
 */
public class HtmlTextXPathMatcher extends BaseMatcher<String> {

    public static Matcher<String> htmlContainsTextAtXPath(String xpath, String text) {
        return new HtmlTextXPathMatcher(xpath, text);
    }

    public static Matcher<String> mustachedHtmlHasXPath(String xpath, Map<String, Object> handlebarsNameValues) {
        return new HtmlTextXPathMatcher(xpath, null, true, handlebarsNameValues);
    }

    public static Matcher<String> mustachedHtmlContainsTextAtXPath(String xpath, String text, Map<String, Object> handlebarsNameValues) {
        return new HtmlTextXPathMatcher(xpath, text, true, handlebarsNameValues);
    }



    public HtmlTextXPathMatcher(String xpath, String text) {
        this(xpath, text, false, null);
    }


    public HtmlTextXPathMatcher(String xpath, String text, boolean handlebars, Map<String, Object> handlebarsNameValues) {
        this.xpath = xpath;
        this.text = text;
        this.handlebars = handlebars;
        this.handlebarsNameValues = handlebarsNameValues;
    }

    private String xpath;
    private String text;

    private boolean handlebars;
    private Map<String, Object> handlebarsNameValues;

    private String assertError;

    @Override
    public boolean matches(Object o) {
        String src = (String)o;
        HtmlElement element = null;
        try {
            element = getElement(getHTML(src), xpath);
        } catch (AssertionError e) {
            assertError = e.getMessage();
        }

        if (element != null) {
            if (text == null) {
                // assume mustachedHtmlHasXPath() was called, so nothing to match against
                return true;
            }

            return element.getTextContent().indexOf(text) != -1;
        }

        return false;
    }

    private String getHTML(String src) {
        String html;
        if (handlebars) {
            Handlebars handlebars = new Handlebars();
            try {
                Template template = handlebars.compileInline(src);
                html = template.apply(handlebarsNameValues);

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            html = src;
        }
        return html;
    }

    @Override
    public void describeTo(Description description) {
        description.appendText("text to contain '" + text + "' at xpath: " + xpath);
        if (assertError != null) {
            description.appendText("\n\tAssertion fail: " + assertError);
        }
    }

    private HtmlElement getElement(String html, String xPath) throws AssertionError  {
        try {
            StringWebResponse response = new StringWebResponse(html, new URL("http://dummy-url-not-used")); // Dummy URL
            WebClient client = new WebClient();
            client.getOptions().setJavaScriptEnabled(false);
            HtmlPage page = HTMLParser.parseHtml(response, client.getCurrentWindow());

            List<?> elements = page.getByXPath(xPath);

            if ((elements == null) || (elements.size() != 1) || (! (elements.get(0) instanceof HtmlElement))) {
                throw new AssertionError("Expected to match a single element with xpath: " + xPath + ", but actual results: " + elements);
            }
            return (HtmlElement)elements.get(0);

        } catch (IOException e) {
            // Not expecting IO errors as we're just processing HTML strings
            throw new RuntimeException(e);
        }
    }

}
