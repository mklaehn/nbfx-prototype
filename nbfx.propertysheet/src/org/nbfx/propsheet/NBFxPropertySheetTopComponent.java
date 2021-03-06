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
package org.nbfx.propsheet;

import java.awt.BorderLayout;
import java.util.Collection;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
import org.nbfx.util.NBFxPanelBuilder;
import org.nbfx.util.NBFxThreadUtilities;
import org.openide.awt.ActionID;
import org.openide.awt.ActionReference;
import org.openide.nodes.Node;
import org.openide.util.Lookup;
import org.openide.util.LookupEvent;
import org.openide.util.LookupListener;
import org.openide.util.NbBundle;
import org.openide.util.RequestProcessor;
import org.openide.util.Utilities;
import org.openide.windows.TopComponent;

@TopComponent.Description(preferredID = "NBFxPropSheetTopComponent", persistenceType = TopComponent.PERSISTENCE_ALWAYS)
@TopComponent.Registration(mode = "properties", openAtStartup = false)
@ActionID(category = "Window", id = "org.nbfx.propsheet.NBFxPropSheetTopComponent")
@ActionReference(path = "Menu/Window/NBFx")
@TopComponent.OpenActionRegistration(displayName = "#CTL_NBFxPropSheetAction", preferredID = "NBFxPropSheetTopComponent")
@NbBundle.Messages({
    "CTL_NBFxPropSheetAction=NBFxPropSheet",
    "CTL_NBFxPropSheetTopComponent=NBFxPropSheet Window",
    "HINT_NBFxPropSheetTopComponent=This is a NBFxPropSheet window"
})
public final class NBFxPropertySheetTopComponent extends TopComponent {

    private static final RequestProcessor RP = new RequestProcessor(NBFxPropertySheetTopComponent.class.getSimpleName(), 2);
    private final Lookup.Result<Node> lookupResult = Utilities.actionsGlobalContext().lookupResult(Node.class);
    private final ScrollPane propertiesScrollPane;
    private final LookupListener lookupListener = new LookupListener() {

        @Override
        public void resultChanged(final LookupEvent le) {
            if (null == lookupResult) {
                return;
            }

            final Collection<? extends Node> nodes = lookupResult.allInstances();

            if (nodes.isEmpty()) {
                return;
            }

            final Node node = nodes.iterator().next();

            RP.post(new Runnable() {
                private final long createTime = System.currentTimeMillis();

                @Override
                public void run() {
                    final long startTime = System.currentTimeMillis();
                    final PropertySheetView sheet = PropertySheetView.create(node);

                    NBFxThreadUtilities.FX.runLater(new Runnable() {

                        @Override
                        public void run() {
                            propertiesScrollPane.setContent(sheet);
                        }
                    });
                    final long endTime = System.currentTimeMillis();
                    System.out.println("(" + createTime + "-" + startTime + "-" + endTime);
                }
            });
        }
    };

    public NBFxPropertySheetTopComponent() {
        setLayout(new BorderLayout());
        setName(Bundle.CTL_NBFxPropSheetTopComponent());
        setToolTipText(Bundle.HINT_NBFxPropSheetTopComponent());

        try {
            propertiesScrollPane = NBFxThreadUtilities.FX.post(new Callable<ScrollPane>() {
                
                @Override
                public ScrollPane call() throws Exception {
                    return new ScrollPane();
                }
            }).get();
        } catch (InterruptedException ex) {
            throw new RuntimeException("view is required", ex);
        } catch (ExecutionException ex) {
            throw new RuntimeException("view is required", ex);
        }

        add(NBFxPanelBuilder.create()
                .root(propertiesScrollPane)
                .fill(Color.BLACK)
                .additionalStyle("/org/nbfx/propsheet/propsheet.css").build(), BorderLayout.CENTER);
    }

    @Override
    public void componentOpened() {
        lookupResult.addLookupListener(lookupListener);
    }

    @Override
    public void componentClosed() {
        lookupResult.removeLookupListener(lookupListener);
    }
}
