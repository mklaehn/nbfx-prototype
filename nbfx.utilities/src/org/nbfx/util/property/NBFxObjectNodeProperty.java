package org.nbfx.util.property;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.LabelBuilder;
import org.openide.nodes.Node.Property;

public class NBFxObjectNodeProperty<T> extends SimpleObjectProperty<T> implements NBFxNodeProperty<T> {

    public NBFxObjectNodeProperty(final Property<T> nodeProperty) {
        super(nodeProperty, nodeProperty.getDisplayName());
        fireValueChangedEvent();
    }

    @Override
    public T getValue() {
        return canRead()
                ? NBFxNodePropertyUtility.getValue(getNodeProperty())
                : null;
    }

    @Override
    public final void setValue(final T newValue) {
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

    private Property<T> getNodeProperty() {
        @SuppressWarnings("unchecked")
        final Property<T> prop = (Property<T>) getBean();
        return prop;
    }

    @Override
    public Node getRenderer() {
        if (canRead() || canWrite()) {
            final Label label = LabelBuilder.create().
                    disable(true).
                    text((null == getValue()) ? null : getValue().toString()).
                    build();

            addListener(new ChangeListener<T>() {

                @Override
                public void changed(final ObservableValue<? extends T> observable, final T oldValue, final T newValue) {
                    label.setText((null == newValue) ? null : newValue.toString());
                }
            });

            return label;
        } else {
            return null;
        }
    }
}
