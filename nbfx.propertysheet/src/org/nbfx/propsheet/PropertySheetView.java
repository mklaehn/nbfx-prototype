package org.nbfx.propsheet;

import java.util.List;
import java.util.Map;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Priority;
import org.nbfx.util.property.NBFxNodeProperty;
import org.nbfx.util.wrapper.NodeWrapper;

/**
 * PropertySheetView for editing groups of bean properties
 */
public class PropertySheetView extends GridPane {

    public static PropertySheetView create(final org.openide.nodes.Node node) {
        if (null == node) {
            return new PropertySheetView(null);
        } else {
            return create(node.getPropertySets());
        }
    }

    public static PropertySheetView create(final org.openide.nodes.Node.PropertySet[] propertySets) {
        return new PropertySheetView(NodeWrapper.getNodeProperties(propertySets));
    }

    private PropertySheetView(final Map<String, List<NBFxNodeProperty<?>>> groups) {
        getStyleClass().add("property-sheet");

        int row = 0;

        if (null != groups) {
            for (final Map.Entry<String, List<NBFxNodeProperty<?>>> entry : groups.entrySet()) {
                if ((null == entry.getValue()) || entry.getValue().isEmpty()) {
                    continue;
                }

                final Label sheetTitle = LabelBuilder.create().text(entry.getKey()).build();

                sheetTitle.getStyleClass().add("property-sheet-header");

                addNode(sheetTitle,
                        0,
                        row,
                        2,
                        1,
                        new Insets(0 == row ? 0d : 5d, 0d, 5d, 0d),
                        null,
                        Priority.ALWAYS);

                row++;

                for (final NBFxNodeProperty<?> nbfnp : entry.getValue()) {
                    addNode(LabelBuilder.create().text(nbfnp.getName()).alignment(Pos.CENTER_RIGHT).build(),
                            0,
                            row,
                            1,
                            1,
                            new Insets(2d, 2d, 2d, 2d),
                            HPos.LEFT,
                            null);
                    addNode(nbfnp.getRenderer(),
                            1,
                            row,
                            1,
                            1,
                            new Insets(2d, 2d, 2d, 2d),
                            null,
                            Priority.ALWAYS);

                    row++;
                }
            }
        }
    }

    private void addNode(final Node node, final int column, final int row, final int colspan, final int rowspan, final Insets insets, final HPos hPos, final Priority priority) {
        if (null != hPos) {
            setHalignment(node, hPos);
        }

        if (null != insets) {
            setMargin(node, insets);
        }

        if (null != priority) {
            setHgrow(node, priority);
        }

        add(node, column, row, colspan, rowspan);
    }
}
