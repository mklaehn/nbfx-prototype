package org.nbfx.browser;

import java.awt.BorderLayout;
import java.net.URL;
import org.openide.awt.HtmlBrowser;
import org.openide.windows.TopComponent;
import org.openide.windows.WindowManager;

/**
 * @author sven
 */
public class NBFxUrlDisplayer extends HtmlBrowser.URLDisplayer {

    @Override
    public void showURL(URL url) {
        final TopComponent tc = new TopComponent();

        tc.setLayout(new BorderLayout());
        tc.add(new NBFxUrlDisplayPanel(url), this);
        tc.setDisplayName(url.toString());
        
        WindowManager.getDefault().findMode("editor").dockInto(tc);
        
        tc.open();
    }
}
