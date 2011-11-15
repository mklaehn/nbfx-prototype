package org.nbfx.explorer.view.list;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.nbfx.util.NBFxPanelCreator;
import org.openide.explorer.ExplorerManager;

public class NBFxListViewComponent extends JPanel {

    private final NBFxListView view = new NBFxListView();

    public NBFxListViewComponent() {
        super(new BorderLayout());
        add(NBFxPanelCreator.create(view), BorderLayout.CENTER);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        view.setExplorerManager(ExplorerManager.find(this));
    }
}
