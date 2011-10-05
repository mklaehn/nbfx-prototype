package org.nbfx.explorer.view.list;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ListChangeListener.Change;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.MultipleSelectionModel;
import javafx.scene.image.ImageView;
import javafx.util.Callback;
import org.nbfx.explorer.view.NodeView;
import org.nbfx.util.NBFxThreadUtilities;
import org.nbfx.util.wrapper.NodeWrapper;
import org.openide.nodes.Node;

public class NBFxListView extends NodeView<NodeWrapper> {

    private final ListView<NodeWrapper> listView = new ListView<>();

    public NBFxListView() {
        setCenter(listView);
        listView.setCellFactory(new Callback<ListView<NodeWrapper>, ListCell<NodeWrapper>>() {

            @Override
            public ListCell<NodeWrapper> call(final ListView<NodeWrapper> param) {
                NBFxThreadUtilities.FX.ensureThread();
                final ListCell<NodeWrapper> cell = new ListCell<NodeWrapper>();

                cell.itemProperty().addListener(new ChangeListener<NodeWrapper>() {

                    @Override
                    public void changed(final ObservableValue<? extends NodeWrapper> observable, final NodeWrapper oldValue, final NodeWrapper newValue) {
                        NBFxThreadUtilities.FX.ensureThread();

                        if (null != oldValue) {
                            cell.textProperty().unbind();
                            cell.graphicProperty().unbind();
                            cell.contextMenuProperty().unbind();
                        }

                        if (null != newValue) {
                            final ImageView imageView = new ImageView();

                            imageView.imageProperty().bind(newValue.nodeIconProperty());
                            cell.textProperty().bind(newValue.displayNameProperty());
                            cell.contextMenuProperty().bind(newValue.contextMenuProperty());
                            cell.setGraphic(imageView);
                        }
                    }
                });

                return cell;
            }
        });

        final ChangeListener<MultipleSelectionModel<NodeWrapper>> selModelListener = new ChangeListener<MultipleSelectionModel<NodeWrapper>>() {

            @Override
            public void changed(final ObservableValue<? extends MultipleSelectionModel<NodeWrapper>> observable, final MultipleSelectionModel<NodeWrapper> oldValue, final MultipleSelectionModel<NodeWrapper> newValue) {
                if (null != oldValue) {
                    oldValue.selectedItemProperty().removeListener(getSelectionListener());
                }

                if (null != newValue) {
                    newValue.selectedItemProperty().addListener(getSelectionListener());
                }

                getSelectionListener().changed(null, null, newValue.getSelectedItem());
            }
        };
        final ListChangeListener<? super Node> selectionListener = new ListChangeListener<Node>() {

            @Override
            public void onChanged(final Change<? extends Node> c) {
                final ObservableList<? extends Node> list = c.getList();

                listView.getItems().setAll(getRepresentations(list.toArray(new Node[list.size()])));
            }
        };


        listView.selectionModelProperty().addListener(selModelListener);
        selModelListener.changed(null, null, listView.getSelectionModel());

        setRepresentationFactory(new Callback<NodeWrapper, NodeWrapper>() {

            @Override
            public NodeWrapper call(final NodeWrapper param) {
                return param;
            }
        });
        rootNodeProperty().addListener(new ChangeListener<NodeWrapper>() {

            @Override
            public void changed(final ObservableValue<? extends NodeWrapper> observable, final NodeWrapper oldValue, final NodeWrapper newValue) {
                if (null != oldValue) {
                    oldValue.childNodes().removeListener(selectionListener);
                }

                if (null != newValue) {
                    newValue.childNodes().addListener(selectionListener);
                    newValue.addNotify();
                }
            }
        });
    }

    @Override
    protected Node getNode(final NodeWrapper t) {
        return (null == t)
                ? null
                : t.getValue();
    }
}
