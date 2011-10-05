package org.nbfx.util.property;

import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import org.openide.nodes.Node.Property;

public class NBFxFloatNodeProperty extends SimpleFloatProperty implements NBFxNodeProperty<Number> {

    public NBFxFloatNodeProperty(final Property<Float> nodeProperty) {
        super(nodeProperty, nodeProperty.getDisplayName());
        fireValueChangedEvent();
    }

    @Override
    public Float getValue() {
        return canRead()
                ? NBFxNodePropertyUtility.getValue(getNodeProperty())
                : null;
    }

    @Override
    public final void setValue(final Number newValue) {
        if (canWrite() && NBFxNodePropertyUtility.setValue(getNodeProperty(), (null == newValue)
                ? null
                : newValue.floatValue())) {
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

    private Property<Float> getNodeProperty() {
        @SuppressWarnings("unchecked")
        final Property<Float> prop = (Property<Float>) getBean();
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
                            setValue(Float.valueOf(newValue));
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
