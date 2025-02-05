package framework.core.navigation;

import java.awt.Component;
import java.util.ArrayList;
import java.util.logging.Level;

import javax.swing.JComponent;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;

import framework.utils.logging.Tracelog;

/**
 * A builder class for easily creating UI menus
 * 
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 *
 */
public final class MenuBuilder {

    /**
     * The host of this builder
     */
    private final JComponent _host;

    /**
     * The root component of this menu system
     */
    private JComponent _root;

    /**
     * The list of components in chronologically added order
     */
    private ArrayList<JComponent> _components = new ArrayList();

    /**
     * Constructs a new instance of this class type
     *
     * @param host The host component of this menu
     */
    private MenuBuilder(JComponent host) {
        _host = host;
    }

    /**
     * Adds an option builder to this option builder
     * 
     * @param builder The option builder to associate to this option builder's item
     * 
     * @return A reference to this option builder
     */
    public MenuBuilder addBuilder(MenuBuilder builder) {
        builder._root = _root;
        _components.addAll(builder._components);

        return this;
    }

    /**
     * Adds a menu with the specified text to the currently active menu
     * 
     * @param text The text to set as the menu title
     * 
     * @return A reference to the menu builder
     */
    public MenuBuilder addMenu(String text) {
        AbstractMenuContainer component = new AbstractMenuContainer(text, _root == null ? _host : _root);

        if(_components.isEmpty() && _root == null) {
            _root = component.getComponent();
        }
        else {
            _components.add(component.getComponent());    
        }

        return this;
    }
    
    /**
     * Adds a menu with the specified text to the currently active menu
     * 
     * @param text The text to set as the menu title
     * @param mnemonic The mnemonic to associate to the menu 
     * 
     * @return A reference to the menu builder
     */
    public MenuBuilder addMenu(String text, int mnemonic)
    {
        AbstractMenuContainer component = new AbstractMenuContainer(text, _root == null ? _host : _root);
        component.setMnemonic(mnemonic);
        if(_components.isEmpty() && _root == null) {
            _root = component.getComponent();
        }
        else {
            _components.add(component.getComponent());    
        }

        return this;
    }
    
    /**
     * Adds the specified menu item type to the currently active menu
     *
     * @param classType The class type
     * @param <T> An object that extends AbstractMenu
     * 
     * @return A reference to the menu builder
     */
    public <T extends AbstractMenu> MenuBuilder addMenu(Class<T> classType) {
        try {
            T baseComponent = classType.getConstructor(JComponent.class).newInstance(_root == null ? _host : _root);
            if(_components.isEmpty() && _root == null) {
                _root = baseComponent.getComponent();
            }
            else {
                _components.add(baseComponent.getComponent());    
            }
        } 
        catch (Exception exception) {
            Tracelog.log(Level.SEVERE, true, exception);
        }

        return this;
    }

    /**
     * Adds a new item of the specified component to the builder
     * 
     * @param component The type of component to construct
     * @param <T> A type extending an AbstractOption class
     * 
     * @return A reference to this builder
     */
    public <T extends AbstractMenuItem> MenuBuilder addMenuItem(Class<T> component) {
        try {
            T baseComponent = component.getConstructor(JComponent.class).newInstance(_root == null ? _host : _root);
            if(_components.isEmpty() && _root == null) {
                _root = baseComponent.getComponent();
            }
            else {
                _components.add(baseComponent.getComponent());    
            }
        } 
        catch (Exception exception) {
            Tracelog.log(Level.SEVERE, true, exception);
        }

        return this;
    }
    
    public <T extends AbstractMenuItem> MenuBuilder addMenuItem(T clazz) {
        try {
            if(_components.isEmpty() && _root == null) {
                _root = clazz.getComponent();
            }
            else {
                _components.add(clazz.getComponent());    
            }
        } 
        catch (Exception exception) {
            Tracelog.log(Level.SEVERE, true, exception);
        }

        return this;
    }

    /**
     * Adds a separator to the list of options
     * 
     * @param <T> A type extending the class AbstractOption
     * 
     * @return A reference to this option builder
     */
    public <T extends AbstractMenu> MenuBuilder addSeparator() {
        if(_root != null) {
            T component = (T) _root.getClientProperty(_root);
            component.addSeperator();
        }

        return this;
    }

    /**
     * Sets the root of this builder
     * 
     * @param root The root of this builder
     * 
     * @return A reference to this builder
     */
    public MenuBuilder root(JComponent root) {
        _root = root;
        return this;
    }

    /**
     * Resets the specified menu bar. The resetting of each portion of the menu is implementation
     * specific per menu item for example, therefore the state of the said menu should be back
     * in the 'original' starting state that it would normally be in
     * 
     * @param jMenuBar The menu bar to reset
     */
    public static void reset(JMenuBar jMenuBar) {
        for(Component component : jMenuBar.getComponents()) {
            if(component instanceof JMenu) {
                JMenu menu = (JMenu)component;
                Object obj = menu.getClientProperty(menu);
                if(obj instanceof AbstractMenuContainer) {
                    AbstractMenuContainer menuComponent = (AbstractMenuContainer) obj;
                    menuComponent.reset();
                }
            }
        }
    }

    /**
     * Builder entry-point
     *  
     * @param host The root component of this menu system
     * 
     * @return A reference to this builder
     */
    public static MenuBuilder start(JComponent host) {
        return new MenuBuilder(host);
    }

    /**
     * Builder entry-point
     * 
     * @param host The root component of this menu system
     * 
     * @return A reference to this builder
     */
    public static MenuBuilder start(MenuBuilder host) {
        return start(host._root);
    }

    /**
     * Searches for the specified class type within the specified menu bar
     * 
     * @param jMenuBar The JMenu bar to perform the search
     * @param classType The type of class to look for within the menu
     * @param <U> A type which is of type AbstractMenuItem
     * @param <T> A type extending The class template type
     * 
     * @return The first entry found of the specified class type
     */
    public static <U extends T, T extends AbstractMenuItem> U search(JMenuBar jMenuBar, Class<U> classType) {
        for(Component component : jMenuBar.getComponents()) {
            if(component instanceof JMenu) {
                JMenu menu = (JMenu)component;
                for(Component menuComponent : menu.getMenuComponents()) {
                    if(menuComponent instanceof JMenuItem) {
                        Object obj = ((JMenuItem)menuComponent).getClientProperty(menuComponent);
                        if(obj.getClass() == classType) {
                            return (U)obj;
                        }
                    }
                }
            }
        }

        return null;
    }
}