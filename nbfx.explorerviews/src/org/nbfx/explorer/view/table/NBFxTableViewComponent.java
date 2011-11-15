package org.nbfx.explorer.view.table;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.nbfx.explorer.view.table.NBFxTableView.TableColumnDefinition;
import org.nbfx.util.NBFxPanelCreator;
import org.nbfx.util.NBFxThreadUtilities;
import org.openide.explorer.ExplorerManager;

public class NBFxTableViewComponent extends JPanel {

    private final NBFxTableView view = new NBFxTableView();

    public NBFxTableViewComponent() {
        super(new BorderLayout());
        add(NBFxPanelCreator.create(view), BorderLayout.CENTER);
    }

    public void setColumns(final TableColumnDefinition<?>... tcds) {
        NBFxThreadUtilities.FX.runLater(new Runnable() {

            @Override
            public void run() {
                view.setColumns(tcds);
            }
        });
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
