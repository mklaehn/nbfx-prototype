package org.nbfx.util.property;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFieldBuilder;
import org.openide.nodes.Node.Property;

public class NBFxStringNodeProperty extends SimpleStringProperty implements NBFxNodeProperty<String> {

    public NBFxStringNodeProperty(final Property<String> nodeProperty) {
        super(nodeProperty, nodeProperty.getDisplayName());
        fireValueChangedEvent();
    }

    @Override
    public final String getValue() {
        return canRead()
                ? NBFxNodePropertyUtility.getValue(getNodeProperty())
                : null;
    }

    @Override
    public final void setValue(final String newValue) {
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

    private Property<String> getNodeProperty() {
        @SuppressWarnings("unchecked")
        final Property<String> prop = (Property<String>) getBean();
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
                        setValue(newValue);
                    }
                });
            }

            addListener(new ChangeListener<String>() {

                @Override
                public void changed(final ObservableValue<? extends String> observable, final String oldValue, final String newValue) {
                    textField.setText((null == newValue) ? "" : newValue);
                }
            });

            return textField;
        } else {
            return null;
        }
    }
}
