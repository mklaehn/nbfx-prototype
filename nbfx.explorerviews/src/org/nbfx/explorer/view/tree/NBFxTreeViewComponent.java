package org.nbfx.explorer.view.tree;

import java.awt.BorderLayout;
import javax.swing.JPanel;
import org.nbfx.util.NBFxPanelCreator;
import org.openide.explorer.ExplorerManager;

public class NBFxTreeViewComponent extends JPanel {

    private final NBFxTreeView view = new NBFxTreeView();

    public NBFxTreeViewComponent() {
        super(new BorderLayout());
        add(NBFxPanelCreator.create(view), BorderLayout.CENTER);
    }

    @Override
    public void addNotify() {
        super.addNotify();
        view.setExplorerManager(ExplorerManager.find(this));
    }
}
