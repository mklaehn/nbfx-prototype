package org.nbfx.util.property;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.CheckBoxBuilder;
import org.openide.nodes.Node.Property;

public class NBFxBooleanNodeProperty extends SimpleBooleanProperty implements NBFxNodeProperty<Boolean> {

    public NBFxBooleanNodeProperty(final Property<Boolean> nodeProperty) {
        super(nodeProperty, nodeProperty.getDisplayName());
        fireValueChangedEvent();
    }

    @Override
    public Boolean getValue() {
        return canRead()
                ? NBFxNodePropertyUtility.getValue(getNodeProperty())
                : null;
    }

    @Override
    public final void setValue(final Boolean newValue) {
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

    private Property<Boolean> getNodeProperty() {
        @SuppressWarnings("unchecked")
        final Property<Boolean> prop = (Property<Boolean>) getBean();
        return prop;
    }

    @Override
    public Node getRenderer() {
        if (canRead() || canWrite()) {
            final CheckBox checkBox = CheckBoxBuilder.create().
                    disable(!canWrite()).
                    selected((null == getValue()) ? false : getValue()).build();

            if (canWrite()) {
                checkBox.selectedProperty().addListener(new ChangeListener<Boolean>() {

                    @Override
                    public void changed(final ObservableValue<? extends Boolean> ov, final Boolean oldValue, final Boolean newValue) {
                        setValue(newValue);
                    }
                });
            }

            addListener(new ChangeListener<Boolean>() {

                @Override
                public void changed(final ObservableValue<? extends Boolean> observable, final Boolean oldValue, final Boolean newValue) {
                    checkBox.setSelected(Boolean.TRUE.equals(newValue));
                }
            });

            return checkBox;
        } else {
            return null;
        }
    }
}
