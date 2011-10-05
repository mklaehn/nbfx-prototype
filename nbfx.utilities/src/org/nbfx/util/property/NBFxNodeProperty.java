package org.nbfx.util.property;

import javafx.beans.property.Property;
import javafx.scene.Node;

public interface NBFxNodeProperty<T> extends Property<T> {

    boolean canRead();

    boolean canWrite();

    Node getRenderer();
}
