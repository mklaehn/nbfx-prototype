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
package org.nbfx.explorer.view;

import java.awt.BorderLayout;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.FilenameFilter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import javafx.embed.swing.JFXPanel;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javax.swing.JPanel;
import org.nbfx.util.NBFxPanelBuilder;
import org.nbfx.util.NBFxPanelCreator;
import org.nbfx.util.NBFxThreadUtilities;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;

/**
 * @author Sven
 */
final public class FxIconView extends JPanel implements PropertyChangeListener {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private DisplayShelf shelf = null;

    public FxIconView() {
        super(new BorderLayout());
        NBFxThreadUtilities.SWING.ensureThread();

        final TextField input = new TextField();
        input.setPromptText("<put your folder here>");
        
        final BorderPane borderPane = new BorderPane();
        borderPane.setTop(input);

        final JFXPanel panel = NBFxPanelBuilder.create()
                .root(borderPane)
                .additionalStyle("/org/nbfx/explorer/view/displayshelf.css")
                .build();
        
        add(panel, BorderLayout.CENTER);
        input.setOnAction(new InputHandler(this, borderPane, input));
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        NBFxThreadUtilities.SWING.ensureThread();
        pcs.firePropertyChange(evt);
    }

    @Override
    public void addPropertyChangeListener(final PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }

    public Node getSelectedNode() {
        if (null != shelf) {
            return shelf.getSelectedNode();
        }

        return new AbstractNode(Children.LEAF);
    }

    private static class InputHandler implements EventHandler<ActionEvent> {

        private final FxIconView view;
        private final TextField input;
        private final BorderPane borderPane;

        public InputHandler(final FxIconView view, final BorderPane borderPane, final TextField input) {
            Parameters.notNull("view", view);
            Parameters.notNull("borderPane", borderPane);
            Parameters.notNull("input", input);
            this.view = view;
            this.borderPane = borderPane;
            this.input = input;
        }

        private void setImages(final URL... images) {
            NBFxThreadUtilities.FX.ensureThread();

            borderPane.getChildren().remove(view.shelf);
            view.shelf = new DisplayShelf(images);
            view.shelf.addListener(view);
            borderPane.centerProperty().set(view.shelf);
            view.pcs.firePropertyChange("ROOT", null, view.shelf.getShelfRootNode());
        }

        @Override
        public void handle(final ActionEvent event) {
            final List<URL> images = new ArrayList<URL>();
            final File[] files = new File(input.getText()).listFiles(new FilenameFilter() {

                @Override
                public boolean accept(final File dir, final String name) {
                    final String n = name.toLowerCase();
                    return n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png");
                }
            });

            if (null != files) {
                for (final File file : files) {
                    try {
                        images.add(file.toURI().toURL());
                    } catch (final MalformedURLException ex) {
                        Exceptions.printStackTrace(ex);
                    }
                }
            }

            setImages(images.toArray(new URL[images.size()]));
            borderPane.bottomProperty().set(new Label(new File(input.getText()).getAbsolutePath()));
        }
    }
}
