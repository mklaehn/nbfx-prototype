package org.nbfx.propsheet;

import java.awt.BorderLayout;
import java.util.Collection;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.JFXPanelBuilder;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import javafx.scene.control.ScrollPane;
import javafx.scene.paint.Color;
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
    private final ScrollPane propertiesScrollPane = new ScrollPane();
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

        NBFxThreadUtilities.FX.runLater(new Runnable() {

            @Override
            public void run() {
                final Scene scene = SceneBuilder.create().
                        fill(Color.BLACK).
                        root(propertiesScrollPane).
                        stylesheets("/org/nbfx/propsheet/propsheet.css").
                        build();
                final JFXPanel panel = JFXPanelBuilder.create().
                        scene(scene).
                        build();

                NBFxThreadUtilities.SWING.runLater(new Runnable() {

                    @Override
                    public void run() {
                        add(panel, BorderLayout.CENTER);
                    }
                });
            }
        });
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
