package org.nbfx.explorer.view.tree;

import java.awt.BorderLayout;
import javafx.embed.swing.JFXPanelBuilder;
import javafx.scene.SceneBuilder;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.nbfx.util.NBFxThreadUtilities;
import org.openide.explorer.ExplorerManager;

public class NBFxTreeViewComponent extends JPanel {

    private final NBFxTreeView view = new NBFxTreeView();

    public NBFxTreeViewComponent() {
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
                        NBFxTreeViewComponent.this.add(component, BorderLayout.CENTER);
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
