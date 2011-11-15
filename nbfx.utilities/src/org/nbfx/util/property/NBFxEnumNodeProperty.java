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
package org.nbfx.util.property;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ChoiceBoxBuilder;
import org.openide.nodes.Node.Property;

public class NBFxEnumNodeProperty extends SimpleObjectProperty<Enum<?>> implements NBFxNodeProperty<Enum<?>> {

    public NBFxEnumNodeProperty(final Property<Enum<?>> nodeProperty) {
        super(nodeProperty, nodeProperty.getDisplayName());
        fireValueChangedEvent();
    }

    @Override
    public Enum<?> getValue() {
        return canRead()
                ? NBFxNodePropertyUtility.getValue(getNodeProperty())
                : null;
    }

    @Override
    public final void setValue(final Enum<?> newValue) {
        if (canWrite() && NBFxNodePropertyUtility.setValue(getNodeProperty(), newValue)) {
            fireValueChangedEvent();
        }
    }

    @Override
    public final boolean canRead() {
        return getNodeProperty().canRead();
    }

    @Override
    public final boolean canWrite() {
        return getNodeProperty().canWrite();
    }

    private Property<Enum<?>> getNodeProperty() {
        @SuppressWarnings("unchecked")
        final Property<Enum<?>> prop = (Property<Enum<?>>) getBean();
        return prop;
    }

    @Override
    public Node getRenderer() {
        if (canRead() || canWrite()) {
            final ObservableList<Enum<?>> enums;

            if (null != getValue()) {
                @SuppressWarnings("unchecked")
                final Class<? extends Enum<?>> enumClass = (Class<? extends Enum<?>>) getValue().getClass();
                enums = FXCollections.observableArrayList(enumClass.getEnumConstants());
            } else if (getNodeProperty().getValueType().isEnum()) {
                enums = FXCollections.observableArrayList(getNodeProperty().getValueType().getEnumConstants());
            } else {
                return null;
            }

            final ChoiceBox<Enum<?>> choiceBox = ChoiceBoxBuilder.<Enum<?>>create().disable(!canWrite()).items(enums).build();

            if (canWrite()) {
                choiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Enum<?>>() {

                    @Override
                    public void changed(final ObservableValue<? extends Enum<?>> observable, final Enum<?> oldValue, final Enum<?> newValue) {
                        setValue(newValue);
                    }
                });
            }

            addListener(new ChangeListener<Enum<?>>() {

                @Override
                public void changed(ObservableValue<? extends Enum<?>> observable, Enum<?> oldValue, Enum<?> newValue) {
                    choiceBox.getSelectionModel().select(newValue);
                }
            });

            return choiceBox;
        } else {
            return null;
        }
    }
}
