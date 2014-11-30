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
package org.nbfx.util.property;

import java.lang.reflect.InvocationTargetException;
import org.openide.util.Exceptions;
import org.openide.util.Parameters;

public class NBFxNodePropertyUtility {

    private NBFxNodePropertyUtility() {
    }

    public static <T> T getValue(final org.openide.nodes.Node.Property<T> nodeProperty) {
        Parameters.notNull("nodeProperty", nodeProperty);

        if (nodeProperty.canRead()) {
            try {
                return nodeProperty.getValue();
            } catch (final IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (final InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        return null;
    }

    public static <T> boolean setValue(final org.openide.nodes.Node.Property<T> nodeProperty, final T v) {
        Parameters.notNull("nodeProperty", nodeProperty);

        if (nodeProperty.canWrite()) {
            try {
                nodeProperty.setValue(v);
                return true;
            } catch (IllegalAccessException ex) {
                Exceptions.printStackTrace(ex);
            } catch (IllegalArgumentException ex) {
                Exceptions.printStackTrace(ex);
            } catch (InvocationTargetException ex) {
                Exceptions.printStackTrace(ex);
            }
        }

        return false;
    }

    @SuppressWarnings("unchecked")
    public static <T> NBFxNodeProperty<?> createNBFxNodeProperty(final org.openide.nodes.Node.Property<T> nodeProperty) {
        Parameters.notNull("nodeProperty", nodeProperty);
        final Class<T> valueType = nodeProperty.getValueType();

        if (Boolean.class.isAssignableFrom(valueType)) {
            final org.openide.nodes.Node.Property<Boolean> prop = (org.openide.nodes.Node.Property<Boolean>) nodeProperty;
            return new NBFxBooleanNodeProperty(prop);
        } else if (Enum.class.isAssignableFrom(valueType)) {
            final org.openide.nodes.Node.Property<Enum<?>> prop = (org.openide.nodes.Node.Property<Enum<?>>) nodeProperty;
            @SuppressWarnings("rawtypes")
            final NBFxEnumNodeProperty nbFxEnumNodeProperty = new NBFxEnumNodeProperty(prop);
            return nbFxEnumNodeProperty;
        } else if (Short.class.isAssignableFrom(valueType)) {
            final org.openide.nodes.Node.Property<Short> prop = (org.openide.nodes.Node.Property<Short>) nodeProperty;
            return new NBFxShortNodeProperty(prop);
        } else if (Integer.class.isAssignableFrom(valueType)) {
            final org.openide.nodes.Node.Property<Integer> prop = (org.openide.nodes.Node.Property<Integer>) nodeProperty;
            return new NBFxIntegerNodeProperty(prop);
        } else if (Long.class.isAssignableFrom(valueType)) {
            final org.openide.nodes.Node.Property<Long> prop = (org.openide.nodes.Node.Property<Long>) nodeProperty;
            return new NBFxLongNodeProperty(prop);
        } else if (Float.class.isAssignableFrom(valueType)) {
            final org.openide.nodes.Node.Property<Float> prop = (org.openide.nodes.Node.Property<Float>) nodeProperty;
            return new NBFxFloatNodeProperty(prop);
        } else if (Double.class.isAssignableFrom(valueType)) {
            final org.openide.nodes.Node.Property<Double> prop = (org.openide.nodes.Node.Property<Double>) nodeProperty;
            return new NBFxDoubleNodeProperty(prop);
        } else if (String.class.isAssignableFrom(valueType)) {
            final org.openide.nodes.Node.Property<String> prop = (org.openide.nodes.Node.Property<String>) nodeProperty;
            return new NBFxStringNodeProperty(prop);
        } else {
            return new NBFxObjectNodeProperty<T>(nodeProperty);
        }
    }
}
