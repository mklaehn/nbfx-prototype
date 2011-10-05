package org.nbfx.propsheet;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
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
import org.nbfx.util.property.NBFxNodePropertyUtility;
import org.openide.nodes.Node.PropertySet;

/**
 * PropertySheetView for editing groups of bean properties
 */
public class PropertySheetView extends GridPane {

    public static PropertySheetView create(final org.openide.nodes.Node node) {
        return (null == node)
                ? create(new org.openide.nodes.Node.PropertySet[0])
                : create(node.getPropertySets());
    }

    public static PropertySheetView create(final org.openide.nodes.Node.PropertySet[] propertySets) {
        if ((null == propertySets) || (0 == propertySets.length)) {
            return new PropertySheetView(Collections.<String, List<NBFxNodeProperty<?>>>emptyMap());
        }

        final Map<String, List<NBFxNodeProperty<?>>> groups = new LinkedHashMap<String, List<NBFxNodeProperty<?>>>();

        for (final PropertySet propertySet : propertySets) {
            final List<NBFxNodeProperty<?>> properties = new ArrayList<NBFxNodeProperty<?>>(propertySet.getProperties().length);

            for (org.openide.nodes.Node.Property<?> nodeProperty : propertySet.getProperties()) {
                properties.add(NBFxNodePropertyUtility.createNBFxNodeProperty(nodeProperty));
            }

            groups.put(propertySet.getDisplayName(), properties);
        }

        return new PropertySheetView(groups);
    }

    private PropertySheetView(final Map<String, List<NBFxNodeProperty<?>>> groups) {
        getStyleClass().add("property-sheet");

        int row = 0;

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
