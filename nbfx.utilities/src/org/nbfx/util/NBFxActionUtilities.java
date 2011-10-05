package org.nbfx.util;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.CheckMenuItemBuilder;
import javafx.scene.control.MenuBuilder;
import javafx.scene.control.MenuItem;
import javafx.scene.control.MenuItemBuilder;
import javafx.scene.control.RadioMenuItemBuilder;
import javafx.scene.control.SeparatorMenuItem;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.SwingUtilities;
import org.openide.awt.DynamicMenuContent;
import org.openide.util.ContextAwareAction;
import org.openide.util.Exceptions;
import org.openide.util.Lookup;
import org.openide.util.actions.Presenter;
import org.openide.util.lookup.ServiceProvider;

public class NBFxActionUtilities {

    private NBFxActionUtilities() {
    }

    public static MenuItem[] convertMenuItems(final Lookup lookup, final Object[] objects) {
        if ((null == objects) || (0 == objects.length)) {
            return new MenuItem[0];
        }

        final List<MenuItem> menuItems = new ArrayList<MenuItem>(objects.length);
        boolean separator = false;
        boolean itemAdded = false;

        for (final Object object : objects) {
            final Object o = ((null != lookup) && (object instanceof ContextAwareAction))
                    ? ContextAwareAction.class.cast(object).createContextAwareInstance(lookup)
                    : object;

            if (null == o) {
                if (itemAdded) {
                    separator = true;
                }
            } else {
                for (final MenuItem menuItem : convertMenuItem(o)) {
                    if (null == menuItem) {
                        if (itemAdded) {
                            separator = true;
                        }
                    } else {
                        if (separator) {
                            menuItems.add(new SeparatorMenuItem());
                        }
                        separator = false;
                    }

                    menuItems.add(menuItem);
                    itemAdded = true;
                }
            }
        }

        return menuItems.toArray(new MenuItem[menuItems.size()]);
    }

    public static interface JMenuItemConverter {

        MenuItem convertJMenuItem(final JMenuItem menuItem);
    }

    private static List<MenuItem> convertMenuItem(final Object object) {
        if (object instanceof DynamicMenuContent) {
            final JComponent[] jComponents = DynamicMenuContent.class.cast(object).getMenuPresenters();

            if ((null == jComponents) || (0 == jComponents.length)) {
                return Collections.<MenuItem>emptyList();
            }

            final List<MenuItem> menuItems = new ArrayList<MenuItem>();

            for (final JComponent jComponent : jComponents) {
                menuItems.addAll(convertMenuItemImpl(jComponent));
            }

            return menuItems;
        } else {
            return convertMenuItemImpl(object);
        }
    }

    private static List<MenuItem> convertMenuItemImpl(final Object object) {
        if (object instanceof Presenter.Popup) {
            return convertMenuItem(Presenter.Popup.class.cast(object).getPopupPresenter());
        } else if (object instanceof Action) {
            final Action action = Action.class.cast(object);

            final String name = (null == action.getValue(Action.SHORT_DESCRIPTION))
                    ? String.class.cast(action.getValue(Action.NAME))
                    : String.class.cast(action.getValue(Action.SHORT_DESCRIPTION));
            final String id = String.class.cast(action.getValue(Action.ACTION_COMMAND_KEY));

            return Collections.singletonList(MenuItemBuilder.create().
                    id(id).
                    text(name).
                    disable(!action.isEnabled()).
                    onAction(new ActionEventHandler(action, action, id)).
                    build());
        } else if (object instanceof JMenuItem) {
            final JMenuItem jmi = JMenuItem.class.cast(object);

            if (!jmi.isVisible()) {
                return null;
            }

            for (final JMenuItemConverter converter : Lookup.getDefault().lookupAll(JMenuItemConverter.class)) {
                try {
                    final MenuItem menuItem = converter.convertJMenuItem(jmi);

                    if (null != menuItem) {
                        return Collections.singletonList(menuItem);
                    }
                } catch (final Throwable throwable) {
                    Exceptions.printStackTrace(throwable);
                }
            }
        } else {
            System.out.println("ignored " + object);
        }

        return null;
    }

    @ServiceProvider(service = JMenuItemConverter.class)
    public static final class DefaultJMenuItemConverter implements JMenuItemConverter {

        @Override
        public MenuItem convertJMenuItem(final JMenuItem jmi) {
            final String name = jmi.getText();
            final String id = jmi.getActionCommand();
            final MenuItemBuilder<?> mib;

            if (jmi instanceof JMenu) {
                mib = MenuBuilder.create();
            } else if (jmi instanceof JCheckBoxMenuItem) {
                mib = CheckMenuItemBuilder.create().selected(JCheckBoxMenuItem.class.cast(jmi).isSelected());
            } else if (jmi instanceof JRadioButtonMenuItem) {
                mib = RadioMenuItemBuilder.create().selected(JRadioButtonMenuItem.class.cast(jmi).isSelected());
            } else {
                mib = MenuItemBuilder.create();
            }

            return mib.text(name).
                    id(id).
                    onAction(new ActionEventHandler(new AbstractButtonActionMediator(jmi), jmi, id)).
                    build();
        }
    }

    private static class AbstractButtonActionMediator implements ActionListener {

        private final AbstractButton ab;

        public AbstractButtonActionMediator(AbstractButton ab) {
            this.ab = ab;
        }

        @Override
        public void actionPerformed(final java.awt.event.ActionEvent e) {
            ab.doClick();
        }
    }

    private static class ActionEventHandler implements Runnable, EventHandler<ActionEvent> {

        private final ActionListener actionListener;
        private final Object source;
        private final String actionCommand;

        public ActionEventHandler(final ActionListener actionListener, final Object source, final String actionCommand) {
            this.actionListener = actionListener;
            this.source = source;
            this.actionCommand = actionCommand;
        }

        @Override
        public void run() {
            if (SwingUtilities.isEventDispatchThread()) {
                actionListener.actionPerformed(new java.awt.event.ActionEvent(
                        source,
                        java.awt.event.ActionEvent.ACTION_PERFORMED,
                        actionCommand,
                        System.currentTimeMillis(),
                        0));
            } else {
                NBFxThreadUtilities.SWING.runLater(this);
            }
        }

        @Override
        public void handle(final ActionEvent event) {
            NBFxThreadUtilities.SWING.runLater(this);
        }
    }
}
