//package org.nbfx.explorer.view.icon;
//
//import javafx.geometry.HPos;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.geometry.VPos;
//import javafx.scene.control.Label;
//import javafx.scene.image.ImageView;
//import javafx.scene.image.ImageViewBuilder;
//import javafx.scene.layout.BorderPane;
//import javafx.scene.layout.GridPane;
//import org.efx.explorer.view.NodeView;
//import org.efx.explorer.view.icon.NBFxIconView.IconEntry;
//import org.efx.util.ObjectConverter;
//import org.efx.util.wrapper.NodeWrapper;
//import org.openide.nodes.Node;
//
//public final class NBFxIconView extends NodeView<IconEntry> {
//
//    private final ObjectConverter<Node, IconEntry> converter = new ObjectConverter<Node, IconEntry>() {
//
//        @Override
//        protected IconEntry convert(Node input) {
//            return (null == input)
//                    ? null
//                    : new IconEntry(new NodeWrapper(input));
//        }
//    };
//
//    public NBFxIconView() {
//    }
//
//    @Override
//    public void setRootNodeImpl(final IconEntry root) {
//        root.getNodeWrapper().addNotify();
//        updateItems(root.getNodeWrapper());
//    }
//
//    @Override
//    protected Node getNode(IconEntry t) {
//        return (null == t)
//                ? null
//                : t.getNodeWrapper().getValue();
//    }
//
//    private void updateItems(final NodeWrapper nodeWrapper) {
//        if (null == nodeWrapper) {
//            getChildren().clear();
//        } else {
//            getChildren().setAll(converter.getConverted(nodeWrapper.childNodes()));
//        }
//    }
//
//    public static class IconEntry extends BorderPane {
//
//        private final Label label = new Label();
//        private final ImageView imageView = ImageViewBuilder.create().fitWidth(32d).fitWidth(32d).build();
//        private final NodeWrapper nodeWrapper;
//
//        public IconEntry(final NodeWrapper nodeWrapper) {
//            this.nodeWrapper = nodeWrapper;
//
//            label.textProperty().bind(nodeWrapper.displayNameProperty());
//            imageView.imageProperty().bind(nodeWrapper.nodeIconProperty());
//
//            setBottom(label);
//            final GridPane gridPane = new GridPane();
//
//            gridPane.add(imageView, 0, 0);
//            gridPane.alignmentProperty().set(Pos.CENTER);
//            GridPane.setHalignment(imageView, HPos.CENTER);
//            GridPane.setValignment(imageView, VPos.CENTER);
//            GridPane.setMargin(imageView, new Insets(3d));
//            gridPane.autosize();
//
//            setCenter(gridPane);
//        }
//
//        public NodeWrapper getNodeWrapper() {
//            return nodeWrapper;
//        }
//    }
////    private final PropertyChangeListener emListener = new PropertyChangeListener() {
////
////        @Override
////        public void propertyChange(final PropertyChangeEvent pce) {
////            if (ExplorerManager.PROP_ROOT_CONTEXT.equals(pce.getPropertyName())
////                    && (pce.getNewValue() instanceof Node)) {
////                FxUtilities.FX.runLater(new FXSetRootRunnable(NBFxIconView.this, Node.class.cast(pce.getNewValue())));
////            }
////        }
////    };
////    private final ChangeListener<TreeItem<Node>> selectionListener = new ChangeListener<TreeItem<Node>>() {
////
////        @Override
////        public void changed(final ObservableValue<? extends TreeItem<Node>> observable, final TreeItem<Node> oldValue, final TreeItem<Node> newValue) {
////            if (null != explorerManager) {
////                final Node[] nodes = ((null == newValue) || (null == newValue.getValue()))
////                        ? new Node[0]
////                        : new Node[]{newValue.getValue()};
////
////                FxUtilities.SWING.runLater(new Runnable() {
////
////                    @Override
////                    public void run() {
////                        try {
////                            explorerManager.setSelectedNodes(nodes);
////                        } catch (final PropertyVetoException ex) {
////                            Exceptions.printStackTrace(ex);
////                        }
////                    }
////                });
////            }
////        }
////    };
////    private ExplorerManager explorerManager;
////
////    public NBFxIconView() {
////        setCellFactory(new Callback<TreeView<Node>, TreeCell<Node>>() {
////
////            @Override
////            public TreeCell<Node> call(final TreeView<Node> param) {
////                FxUtilities.FX.ensureThread();
////                final TreeCell<Node> cell = new TreeCell<Node>();
////
////                cell.treeItemProperty().addListener(new ChangeListener<TreeItem<Node>>() {
////
////                    @Override
////                    public void changed(final ObservableValue<? extends TreeItem<Node>> observable, final TreeItem<Node> oldValue, final TreeItem<Node> newValue) {
////                        FxUtilities.FX.ensureThread();
////
////                        if (null != oldValue) {
////                            cell.textProperty().unbind();
////                            cell.graphicProperty().unbind();
////                            cell.contextMenuProperty().unbind();
////                        }
////
////                        if (null != newValue) {
////                            if (newValue instanceof TreeNodeItem) {
////                                cell.textProperty().bind(TreeNodeItem.class.cast(newValue).textProperty());
////                                cell.contextMenuProperty().bind(TreeNodeItem.class.cast(cell.getTreeItem()).contextMenuProperty());
////                            } else {
////                                cell.textProperty().set(newValue.getValue().getDisplayName());
////                            }
////
////                            cell.graphicProperty().bind(newValue.graphicProperty());
////                        }
////                    }
////                });
////
////                if (cell.getTreeItem() instanceof TreeNodeItem) {
////                    cell.contextMenuProperty().bind(TreeNodeItem.class.cast(cell.getTreeItem()).contextMenuProperty());
////                }
////
////                return cell;
////            }
////        });
////
////        editingItemProperty().addListener(new ChangeListener<TreeItem<Node>>() {
////
////            @Override
////            public void changed(ObservableValue<? extends TreeItem<Node>> observable, TreeItem<Node> oldValue, TreeItem<Node> newValue) {
////                System.out.println("newValue: " + newValue);
////            }
////        });
////
////        ChangeListener<MultipleSelectionModel<TreeItem<Node>>> selModelListener = new ChangeListener<MultipleSelectionModel<TreeItem<Node>>>() {
////
////            @Override
////            public void changed(final ObservableValue<? extends MultipleSelectionModel<TreeItem<Node>>> observable, final MultipleSelectionModel<TreeItem<Node>> oldValue, final MultipleSelectionModel<TreeItem<Node>> newValue) {
////                if (null != oldValue) {
////                    oldValue.selectedItemProperty().removeListener(selectionListener);
////                }
////
////                if (null != newValue) {
////                    newValue.selectedItemProperty().addListener(selectionListener);
////                }
////
////                selectionListener.changed(null, null, newValue.getSelectedItem());
////            }
////        };
////
////        selectionModelProperty().addListener(selModelListener);
////        selModelListener.changed(null, null, getSelectionModel());
////    }
////
////    public ExplorerManager getExplorerManager() {
////        return explorerManager;
////    }
////
////    public void setExplorerManager(final ExplorerManager explorerManager) {
////        if (null != this.explorerManager) {
////            this.explorerManager.removePropertyChangeListener(emListener);
////        }
////
////        this.explorerManager = explorerManager;
////
////        if (null != this.explorerManager) {
////            this.explorerManager.addPropertyChangeListener(emListener);
////
////            FxUtilities.FX.runLater(new FXSetRootRunnable(NBFxIconView.this, explorerManager.getRootContext()));
////        }
////    }
////
////    private static class FXSetRootRunnable implements Runnable {
////
////        private final TreeView<Node> treeView;
////        private final Node node;
////
////        public FXSetRootRunnable(final TreeView<Node> treeView, final Node node) {
////            this.treeView = treeView;
////            this.node = node;
////        }
////
////        @Override
////        public void run() {
////            treeView.setRoot(new TreeNodeItem(node));
////        }
////    }
////
////    private static class TreeNodeItem extends TreeItem<Node> {
////
////        private static final NodeWrapper WAIT_NODE_WRAPPER = new NodeWrapper(createWaitNode());
////        private final Map<Node, TreeNodeItem> childNodeMap = new WeakHashMap<Node, TreeNodeItem>();
////        private final NodeWrapper nodeWrapper;
////        private final ImageView imageView = new ImageView();
////
////        public TreeNodeItem(final Node node) {
////            this(new NodeWrapper(node));
////        }
////
////        public TreeNodeItem(final NodeWrapper nodeWrapper) {
////            super(nodeWrapper.getValue());
////            FxUtilities.FX.ensureThread();
////
////            this.nodeWrapper = nodeWrapper;
////
////            setGraphic(imageView);
////
////            // expanded && image
////            expandedProperty().addListener(new ChangeListener<Boolean>() {
////
////                @Override
////                public void changed(final ObservableValue<? extends Boolean> observableValue, final Boolean oldValue, final Boolean newValue) {
////                    FxUtilities.FX.ensureThread();
////
////                    nodeWrapper.addNotify();
////                    updateIcon(newValue);
////                }
////            });
////
////            updateIcon(isExpanded());
////
////            // children & leaf
////            nodeWrapper.childNodes().addListener(new ListChangeListener<Node>() {
////
////                @Override
////                public void onChanged(final ListChangeListener.Change<? extends Node> change) {
////                    FxUtilities.FX.ensureThread();
////
////                    if (isExpanded() || isLeaf()) {
////                        TreeNodeItem.this.getChildren().setAll(getTreeItems(change.getList()));
////                    }
////                }
////            });
////
////            if (!getValue().isLeaf()) {
////                getChildren().setAll(new TreeNodeItem(WAIT_NODE_WRAPPER));
////            }
////
////        }
////
////        public StringProperty textProperty() {
////            return nodeWrapper.displayNameProperty();
////        }
////
////        public ReadOnlyObjectProperty<ContextMenu> contextMenuProperty() {
////            return nodeWrapper.contextMenuProperty();
////        }
////
////        private void updateIcon(final Boolean isExpanded) {
////            FxUtilities.FX.ensureThread();
////            imageView.imageProperty().unbind();
////
////            if (leafProperty().get() || Boolean.FALSE.equals(isExpanded)) {
////                imageView.imageProperty().bind(nodeWrapper.nodeIconProperty());
////            } else if (Boolean.TRUE.equals(isExpanded)) {
////                imageView.imageProperty().bind(nodeWrapper.nodeIconOpenedProperty());
////            }
////        }
////
////        private List<TreeNodeItem> getTreeItems(final List<? extends Node> childNodes) {
////            final List<TreeNodeItem> treeItems = new ArrayList<TreeNodeItem>(childNodes.size());
////
////            synchronized (childNodeMap) {
////                if ((null == childNodes) || childNodes.isEmpty()) {
////                    childNodeMap.clear();
////                    return treeItems;
////                }
////
////                for (final Node node : childNodes) {
////                    TreeNodeItem treeItem = childNodeMap.get(node);
////
////                    if (null == treeItem) {
////                        treeItem = new TreeNodeItem(node);
////                        childNodeMap.put(node, treeItem);
////                    }
////
////                    treeItems.add(treeItem);
////                }
////            }
////
////            return treeItems;
////        }
////
////        @Override
////        public boolean equals(final Object o) {
////            return (o instanceof TreeNodeItem)
////                    && getValue().equals(TreeNodeItem.class.cast(o).getValue());
////        }
////
////        @Override
////        public int hashCode() {
////            int hash = 37;
////            hash = 67 * hash + getValue().hashCode();
////            return hash;
////        }
////
////        private static Node createWaitNode() {
////            final AbstractNode an = new AbstractNode(Children.LEAF) {
////
////                @Override
////                public Action[] getActions(boolean context) {
////                    return new Action[0];
////                }
////            };
////
////            an.setDisplayName(NbBundle.getMessage(ChildFactory.class, "LBL_WAIT")); //NOI18N
////            an.setIconBaseWithExtension("org/openide/nodes/wait.gif"); //NOI18N
////
////            return an;
////        }
////    }
//}
