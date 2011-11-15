/**
 * This file is part of the NBFx.
 *
 * NBFx is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation in version 2 of the License only.
 *
 * NBFx is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with NBFx. If not, see <http://www.gnu.org/licenses/>.
 *
 * The NBFx project designates this particular file as subject to the
 * "Classpath" exception as provided by the NBFx Project in the GPL Version 2 section
 * of the License file that accompanied this code.
 */
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
