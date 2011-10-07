package org.nbfx.explorer.view.table;

import java.awt.BorderLayout;
import javafx.embed.swing.JFXPanelBuilder;
import javafx.scene.SceneBuilder;
import javax.swing.JComponent;
import javax.swing.JPanel;
import org.nbfx.explorer.view.table.NBFxTableView.TableColumnDefinition;
import org.nbfx.util.NBFxThreadUtilities;
import org.openide.explorer.ExplorerManager;

public class NBFxTableViewComponent extends JPanel {

    private final NBFxTableView view = new NBFxTableView();

    public NBFxTableViewComponent() {
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
                        NBFxTableViewComponent.this.add(component, BorderLayout.CENTER);
                    }
                });
            }
        });
    }

    public void setColumns(final TableColumnDefinition<?>... tcds) {
        view.setColumns(tcds);
    }

    public final void setTableMenuButtonVisible(final boolean visible) {
        view.setTableMenuButtonVisible(visible);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        view.setExplorerManager(ExplorerManager.find(this));
    }
}
