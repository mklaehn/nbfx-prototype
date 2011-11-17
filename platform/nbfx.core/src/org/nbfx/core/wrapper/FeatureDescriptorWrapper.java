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
package org.nbfx.core.wrapper;

import java.beans.FeatureDescriptor;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.nbfx.core.util.NBFxUtilities;
import org.openide.util.Parameters;

public class FeatureDescriptorWrapper<T extends FeatureDescriptor> {

    private final StringProperty nameProperty;
    private final StringProperty displayNameProperty;
    private final StringProperty shortDescriptionProperty;
    private final T value;

    protected FeatureDescriptorWrapper(final T value) {
        NBFxUtilities.FX.ensureThread();
        Parameters.notNull("value", value);

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
