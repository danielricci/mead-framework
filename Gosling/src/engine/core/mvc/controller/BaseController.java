/**
* Daniel Ricci <thedanny09@gmail.com>
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

package engine.core.mvc.controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

import engine.api.IController;
import engine.api.IView;
import engine.core.factories.ViewFactory;
import engine.core.mvc.view.BaseView;

/**
 * This class holds base controller implementation functionality for all controllers
 * 
 * @author Daniel Ricci <thedanny09@gmail.com>
 */
public abstract class BaseController implements IController  {
		
	/**
	 * The view assigned to the controller
	 */
	private BaseView _view;
		
	public BaseController() {
	}
	
	public <T extends BaseView> BaseController(T view) {
		this();
		setView(view);
	}

	public <T extends BaseView> BaseController(Class<T> viewClass, boolean shared) {
		this();
		setView(ViewFactory.instance().get(viewClass, shared, this));
	}
		
	protected final <T extends IView> T getView(Class<T> viewClass) {
		return (T)_view;
	}
	
	protected final <T extends BaseView> void setView(T view) {
		_view = view;
	}
	
	@Override public Map<String, ActionListener> getRegisteredOperations() {
		return null;
	}
	
	@Override public final void executeRegisteredOperation(Object sender, String operation) {		
		Map<String, ActionListener> operations = getRegisteredOperations();
		ActionListener event;
		if(operations != null && (event = operations.get(operation)) != null) {
			event.actionPerformed(new ActionEvent(sender, 0, null));	
		}
	}
	
	@Override public void dispose() {	
	}
}