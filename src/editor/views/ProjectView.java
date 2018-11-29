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

package editor.views;

import java.awt.Color;

import javax.swing.JScrollPane;

import framework.communication.internal.signal.arguments.EventArgs;
import framework.core.factories.AbstractFactory;
import framework.core.factories.AbstractSignalFactory;
import framework.core.factories.ControllerFactory;
import framework.core.factories.ModelFactory;
import framework.core.mvc.view.ScrollView;
import framework.core.mvc.view.layout.DraggableLayout;
import framework.core.system.Application;

import editor.controllers.ProjectController;
import editor.models.ProjectModel;

/**
 * The main window view is the outer most shell that wraps everything
 * 
 * @author {@literal Daniel Ricci <thedanny09@icloud.com>}
 *
 */
public class ProjectView extends ScrollView {

    /**
     * Creates a new instance of this class type
     */
    public ProjectView(String projectName) {
        setLayout(new DraggableLayout());
        Application.instance.setContentPane(new JScrollPane(this));
setBackground(Color.ORANGE);
        // Create the tile model that this view will be populated from
        ProjectModel model = AbstractFactory.getFactory(ModelFactory.class).add(
                new ProjectModel(projectName),
                true
                );
        model.addListeners(this);

        getViewProperties().setEntity(
                AbstractSignalFactory.getFactory(ControllerFactory.class).add(new ProjectController(model), true)
                );
    }

    @Override public void render() {
        Application.instance.validate();
        setVisible(true);
    }

    @Override public void update(EventArgs event) {
        super.update(event);

        if(event.getSource() instanceof ProjectModel) {
            ProjectModel model = (ProjectModel) event.getSource();
            Application.instance.setTitle(Application.instance.getTitle() + " - " + model.getName());
        }

        repaint();
    }
}