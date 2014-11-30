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
package org.nbfx.nodes.properties;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import org.openide.nodes.Node.Property;

public class NBFxEnumNodeProperty<E extends Enum<?>> extends SimpleObjectProperty<E> implements NBFxNodeProperty<E> {

    public NBFxEnumNodeProperty(final Property<E> nodeProperty) {
        super(nodeProperty, nodeProperty.getDisplayName());
        fireValueChangedEvent();
    }

    @Override
    public E getValue() {
        return canRead()
                ? NBFxNodePropertyUtility.getValue(getNodeProperty())
                : null;
    }

    @Override
    public final void setValue(final E newValue) {
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

    private Property<E> getNodeProperty() {
        @SuppressWarnings("unchecked")
        final Property<E> prop = (Property<E>) getBean();
        return prop;
    }

    @Override
    public Node getRenderer() {
        if (canRead() || canWrite()) {
            final ObservableList<E> enums;

            if (null != getValue()) {
                @SuppressWarnings("unchecked")
                final Class<E> enumClass = (Class<E>) getValue().getClass();
                 enums = FXCollections.observableArrayList(enumClass.getEnumConstants());
             } else if (getNodeProperty().getValueType().isEnum()) {
                 enums = FXCollections.observableArrayList(getNodeProperty().getValueType().getEnumConstants());
            } else {
                return null;
            }

            final ChoiceBox<E> choiceBox = new ChoiceBox<E>(enums);
            
            choiceBox.setDisable(!canWrite());

            if (canWrite()) {
                choiceBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<E>() {

                    @Override
                    public void changed(final ObservableValue<? extends E> observable, final E oldValue, final E newValue) {
                        setValue(newValue);
                    }
                });
            }

            addListener(new ChangeListener<E>() {

                @Override
                public void changed(ObservableValue<? extends E> observable, E oldValue, E newValue) {
                    choiceBox.getSelectionModel().select(newValue);
                }
            });

            return choiceBox;
        } else {
            return null;
        }
    }
}
