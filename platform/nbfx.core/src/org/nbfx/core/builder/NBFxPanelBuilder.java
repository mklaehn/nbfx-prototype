/**
 * This file is part of the NBFx.
 *
 * NBFx is free software: you can redistribute it and/or modify it under the
 * terms of the GNU General Public License as published by the Free Software
 * Foundation in version 2 of the License only.
 *
 * NBFx is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with
 * NBFx. If not, see <http://www.gnu.org/licenses/>.
 *
 * The NBFx project designates this particular file as subject to the
 * "Classpath" exception as provided by the NBFx Project in the GPL Version 2
 * section of the License file that accompanied this code.
 */
package org.nbfx.core.builder;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.embed.swing.JFXPanel;
import javafx.embed.swing.JFXPanelBuilder;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.SceneBuilder;
import javafx.scene.paint.Paint;
import org.nbfx.core.util.NBFxUtilities;

/**
 * @author martin
 */
public final class NBFxPanelBuilder {

    private static final Logger LOG = Logger.getLogger(NBFxPanelBuilder.class.getName());
    private final Map<Key, Object> values = new EnumMap<Key, Object>(Key.class);

    private NBFxPanelBuilder() {
    }

    public static NBFxPanelBuilder create() {
        return new NBFxPanelBuilder();
    }

    public NBFxPanelBuilder fill(final Paint fill) {
        return setValue(Key.FILL, fill);
    }

    public NBFxPanelBuilder root(final Parent root) {
        return setValue(Key.ROOT, root);
    }

    public NBFxPanelBuilder additionalStyle(final String additionalStyle) {
        return setValue(Key.ADDITIONAL_STYLES, additionalStyle);
    }

    private NBFxPanelBuilder setValue(final Key key, final Object value) {
        if (null != value) {
            if (key.isList()) {
                final Object object = values.get(key);
                final List<Object> list;

                if (object instanceof List) {
                    @SuppressWarnings("unchecked")
                    final List<Object> l = (List<Object>) object;
                    list = l;
                } else {
                    list = new ArrayList<Object>();
                    values.put(key, list);
                }

                list.add(value);
            } else {
                values.put(key, value);
            }
        }

        return this;
    }

    public JFXPanel build() {
        NBFxUtilities.SWING.ensureThread();

        return JFXPanelBuilder.create().
                scene(NBFxUtilities.FX.get(new Callable<Scene>() {

            @Override
            public Scene call() throws Exception {
                final SceneBuilder<?> builder = SceneBuilder.create();

                for (final Map.Entry<Key, Object> entry : values.entrySet()) {
                    switch (entry.getKey()) {
                        case FILL:
                            builder.fill(Paint.class.cast(entry.getValue()));
                            break;
                        case ROOT:
                            builder.root(Parent.class.cast(entry.getValue()));
                            break;
                        case ADDITIONAL_STYLES:
                            // handled after the creation
                            break;
                        default:
                            LOG.log(Level.INFO, "Key {0} not supported!", entry.getKey());
                            break;
                    }
                }

                final Scene s = builder.build();

                if (values.get(Key.ADDITIONAL_STYLES) instanceof List) {
                    @SuppressWarnings("unchecked")
                    final List<String> list = (List<String>) values.get(Key.ADDITIONAL_STYLES);

                    s.getStylesheets().addAll(list);
                }

                return s;
            }
        })).build();
    }

    private static enum Key {

        FILL,
        ROOT,
        ADDITIONAL_STYLES {

            @Override
            public boolean isList() {
                return true;
            }
        };

        public boolean isList() {
            return false;
        }
    }
}
