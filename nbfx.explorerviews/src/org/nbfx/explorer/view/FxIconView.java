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
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javax.swing.JPanel;
import org.nbfx.util.NBFxThreadUtilities;
import org.openide.nodes.AbstractNode;
import org.openide.nodes.Children;
import org.openide.nodes.Node;
import org.openide.util.Exceptions;

/**
 * @author Sven
 */
final public class FxIconView extends JPanel implements PropertyChangeListener {

    private final PropertyChangeSupport pcs = new PropertyChangeSupport(this);
    private DisplayShelf shelf = null;

    public FxIconView() {
        super(new BorderLayout());
        NBFxThreadUtilities.FX.runLater(new Runnable() {

            private final BorderPane borderPane = new BorderPane();

            @Override
            public void run() {
                NBFxThreadUtilities.FX.ensureThread();

                final JFXPanel panel = new JFXPanel();
                final TextField input = new TextField();

                panel.setScene(new Scene(borderPane));
                panel.getScene().getStylesheets().add("/org/nbfx/explorer/view/displayshelf.css");
                input.setPromptText("<put your folder here>");
                borderPane.setTop(input);

                NBFxThreadUtilities.SWING.runLater(new Runnable() {

                    @Override
                    public void run() {
                        FxIconView.this.add(panel, BorderLayout.CENTER);
                    }
                });

                input.setOnAction(new EventHandler<ActionEvent>() {

                    public void setImages(final URL... images) {
                        NBFxThreadUtilities.FX.ensureThread();

                        borderPane.getChildren().remove(shelf);
                        shelf = new DisplayShelf(images);
                        shelf.addListener(FxIconView.this);
                        borderPane.centerProperty().set(shelf);
                        pcs.firePropertyChange("ROOT", null, shelf.getShelfRootNode());
                    }

                    @Override
                    public void handle(final ActionEvent event) {
                        final List<URL> images = new ArrayList<URL>();

                        for (final File file : new File(input.getText()).listFiles(new FilenameFilter() {

                            @Override
                            public boolean accept(final File dir, final String name) {
                                final String n = name.toLowerCase();
                                return n.endsWith(".jpg") || n.endsWith(".jpeg") || n.endsWith(".png");
                            }
                        })) {
                            try {
                                images.add(file.toURI().toURL());
                            } catch (final MalformedURLException ex) {
                                Exceptions.printStackTrace(ex);
                            }
                        }

                        setImages(images.toArray(new URL[images.size()]));
                        borderPane.bottomProperty().set(new Label(new File(input.getText()).getAbsolutePath()));
                    }
                });
            }
        });
    }

    @Override
    public void propertyChange(final PropertyChangeEvent evt) {
        NBFxThreadUtilities.SWING.ensureThread();
        pcs.firePropertyChange(evt);
    }

    @Override
    public void addPropertyChangeListener(PropertyChangeListener pcl) {
        pcs.addPropertyChangeListener(pcl);
    }

    public Node getSelectedNode() {
        if (null != shelf) {
            return shelf.getSelectedNode();
        }
        
        return new AbstractNode(Children.LEAF);
    }
}
