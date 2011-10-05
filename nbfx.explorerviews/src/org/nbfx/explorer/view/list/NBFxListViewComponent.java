package org.nbfx.explorer.view.list;

import java.awt.BorderLayout;
import javafx.embed.swing.JFXPanelBuilder;
import javafx.scene.SceneBuilder;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.nbfx.util.NBFxThreadUtilities;
import org.openide.explorer.ExplorerManager;

public class NBFxListViewComponent extends JPanel {

    private final NBFxListView view = new NBFxListView();

    public NBFxListViewComponent() {
        super(new BorderLayout());

        NBFxThreadUtilities.FX.runLater(new Runnable() {

            @Override
            public void run() {
                final JComponent component = JFXPanelBuilder.create().
                        scene(SceneBuilder.create().root(view).build()).
                        build();

                NBFxThreadUtilities.SWING.runLater(new Runnable() {

                    @Override
                    public void run() {
                        NBFxListViewComponent.this.add(component, BorderLayout.CENTER);
                    }
                });
            }
        });
    }

    @Override
    public void addNotify() {
        super.addNotify();
        view.setExplorerManager(ExplorerManager.find(this));
    }
}
