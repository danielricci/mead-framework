/**
 * Daniel Ricci <thedanny09@icloud.com>
 *
 * Permission is hereby granted, free of charge, to any person
 * obtaining a copy of this software and associated documentation
 * files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge,
 * publish, distribute, sublicense, and/or sell copies of the Software,
 * and to permit persons to whom the Software is furnished to do so, subject
 * to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package framework.core.navigation;

import java.awt.event.ActionEvent;
import java.util.EventObject;
import java.util.HashMap;
import java.util.Map;

import javax.swing.AbstractAction;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import framework.core.factories.AbstractFactory;

/**
 * Defines the abstract implementation for the menu item. A menu item is similar
 * in nature to a JMenuItem
 * 
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 *
 */
public abstract class AbstractMenuItem extends AbstractMenu {

    private static final Map<String, ButtonGroup> _groupings = new HashMap();

    /**
     * Constructs a new instance of this class type
     * 
     * @param component The component to associate to this option item
     * @param parent The parent component associated to this option item
     */
    protected AbstractMenuItem(JComponent component, JComponent parent) {
        super(component, parent);

        // Initialize the groupings
        initGrouping();
    }	

    /**
     * Gets the group name that this menu item is associated to
     * 
     * @return The group name
     */
    protected String getGroupName() {
        return null;
    }
    
    /**
     * Defines a method for handling an execution of the option
     * 
     * @param actionEvent The action event associated to the call of this method
     */
    protected abstract void onExecute(ActionEvent actionEvent);

    protected void onEntered(EventObject event) {
    }
    
    protected void onExited(EventObject event) {
    }
    
    /**
     * Initializes the grouping of the menu item if any
     * 
     * @return A button group associated to the specified group name
     */
    private void initGrouping() {

        // Get the name of the group
        String groupName = getGroupName();

        // If the name is not specified or is empty then do not
        // proceed any further
        if(groupName == null || groupName.trim().isEmpty()) {
            return;
        }

        // Attempt to get a button grouping from the cache
        ButtonGroup group = _groupings.get(groupName);

        // If none exists for the group name then create one and
        // add an entry to it
        if(group == null) {
            group = new ButtonGroup();
            _groupings.put(groupName, group);
        }

        // Associate this menu item to the specified grouping
        group.add(this.getComponent(AbstractButton.class));
    }

    @Override protected final void onInitialize() {
        
        JMenuItem item = super.getComponent(JMenuItem.class);
        
        // Listen to events when the focus changes on menu items
        item.addChangeListener(new ChangeListener() {
            @Override public void stateChanged(ChangeEvent event) {
                
                if (event.getSource() instanceof JMenuItem) {
                    
                    // As a convenience functionality, only fire onEntered/onExited after the factory
                    // has been used. This is to avoid issues related to trying to access factory related
                    // operations prematurely.
                    JMenuItem eventItem = (JMenuItem) event.getSource();
                    if(AbstractFactory.isRunning())
                    {
                        // Focus In
                        if (eventItem.isSelected() || eventItem.isArmed()) {
                            onEntered(event);
                        }
                        // Focus Out
                        else
                        {
                            onExited(event);
                        }
                    }
                }
            }
        });
        
        item.addActionListener(new AbstractAction(super.toString()) {
            @Override public void actionPerformed(ActionEvent event) {
                if(isEnabled()) {
                    onExecute(event);
                } 
            }
        });
    }
}