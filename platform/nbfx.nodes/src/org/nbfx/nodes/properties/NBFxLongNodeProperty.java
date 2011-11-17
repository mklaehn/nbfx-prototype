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

import javafx.beans.property.SimpleLongProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import org.openide.nodes.Node.Property;

public class NBFxLongNodeProperty extends SimpleLongProperty implements NBFxNodeProperty<Number> {

    public NBFxLongNodeProperty(final Property<Long> nodeProperty) {
        super(nodeProperty, nodeProperty.getDisplayName());
        fireValueChangedEvent();
    }

    @Override
    public Long getValue() {
        return canRead()
                ? NBFxNodePropertyUtility.getValue(getNodeProperty())
                : null;
    }

    @Override
    public final void setValue(final Number newValue) {
        if (canWrite() && NBFxNodePropertyUtility.setValue(getNodeProperty(), (null == newValue)
                ? null
                : newValue.longValue())) {
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

    private Property<Long> getNodeProperty() {
        @SuppressWarnings("unchecked")
        final Property<Long> prop = (Property<Long>) getBean();
        return prop;
    }

    @Override
    public Node getRenderer() {
        if (canRead() || canWrite()) {
            final TextField textField = TextFieldBuilder.create().
                    editable(canWrite()).
                    text((null == getValue()) ? "" : getValue().toString()).
                    build();

            if (canWrite()) {
                textField.textProperty().addListener(new ChangeListener<String>() {

                    @Override
                    public void changed(final ObservableValue<? extends String> ov, final String oldValue, final String newValue) {
                        try {
                            setValue(Long.valueOf(newValue));
                        } catch (final NumberFormatException e) {
                        }
                    }
                });
            }

            addListener(new ChangeListener<Number>() {

                @Override
                public void changed(final ObservableValue<? extends Number> observable, final Number oldValue, final Number newValue) {
                    textField.setText((null == newValue) ? "" : newValue.toString());
                }
            });

            return textField;
        } else {
            return null;
        }
    }
}
