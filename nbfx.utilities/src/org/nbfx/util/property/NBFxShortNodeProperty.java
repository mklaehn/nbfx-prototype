package org.nbfx.util.property;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import org.openide.nodes.Node.Property;

public class NBFxShortNodeProperty extends SimpleIntegerProperty implements NBFxNodeProperty<Number> {

    public NBFxShortNodeProperty(final Property<Short> nodeProperty) {
        super(nodeProperty, nodeProperty.getDisplayName());
        fireValueChangedEvent();
    }

    @Override
    public Integer getValue() {
        final Short shortValue = canRead()
                ? NBFxNodePropertyUtility.getValue(getNodeProperty())
                : null;

        return (null == shortValue)
                ? null
                : shortValue.intValue();
    }

    @Override
    public final void setValue(final Number newValue) {
        if (canWrite() && NBFxNodePropertyUtility.setValue(getNodeProperty(), (null == newValue)
                ? null
                : newValue.shortValue())) {
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

    private Property<Short> getNodeProperty() {
        @SuppressWarnings("unchecked")
        final Property<Short> prop = (Property<Short>) getBean();
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
                            setValue(Short.valueOf(newValue));
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
