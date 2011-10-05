package org.nbfx.util.wrapper;

import java.beans.FeatureDescriptor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.nbfx.util.NBFxThreadUtilities;
import org.openide.util.Parameters;

public class FeatureDescriptorWrapper<T extends FeatureDescriptor> {

    private final StringProperty nameProperty;
    private final StringProperty displayNameProperty;
    private final StringProperty shortDescriptionProperty;
    private final T value;

    protected FeatureDescriptorWrapper(final T value) {
        NBFxThreadUtilities.FX.ensureThread();
        Parameters.notNull("t", value);

        nameProperty = new SimpleStringProperty(value.getName());
        nameProperty.addListener(new ChangeListener<String>() {

            @Override
            public void changed(final ObservableValue<? extends String> observableValue, final String oldValue, final String newValue) {
                value.setName(newValue);
            }
        });
        displayNameProperty = new SimpleStringProperty(value.getDisplayName());
        displayNameProperty.addListener(new ChangeListener<String>() {

            @Override
            public void changed(final ObservableValue<? extends String> observableValue, final String oldValue, final String newValue) {
                value.setDisplayName(newValue);
            }
        });
        shortDescriptionProperty = new SimpleStringProperty(value.getShortDescription());
        shortDescriptionProperty.addListener(new ChangeListener<String>() {

            @Override
            public void changed(final ObservableValue<? extends String> observableValue, final String oldValue, final String newValue) {
                value.setShortDescription(newValue);
            }
        });
        this.value = value;
    }

    public final T getValue() {
        return value;
    }

    public final StringProperty nameProperty() {
        return nameProperty;
    }

    public final StringProperty displayNameProperty() {
        return displayNameProperty;
    }

    public final StringProperty shortDescriptionProperty() {
        return shortDescriptionProperty;
    }
}
