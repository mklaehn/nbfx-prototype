package org.nbfx.browser;

import org.openide.awt.HtmlBrowser;
import org.openide.awt.HtmlBrowser.Impl;

public class NBFxBrowser implements HtmlBrowser.Factory {

    @Override
    public Impl createHtmlBrowserImpl() {
        return new NBFxHtmlBrowserImpl();
    }
}
