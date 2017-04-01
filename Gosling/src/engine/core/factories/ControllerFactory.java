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

package engine.core.factories;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import engine.api.IController;
import engine.api.IDestructor;
import engine.communication.internal.dispatcher.Dispatcher;
import engine.communication.internal.dispatcher.DispatcherMessage;
import engine.communication.internal.dispatcher.IDispatcher;

/**
 * This is a factory for the Controller core module. This factory will create all controller
 * related items for any given application as long as it implements the proper interface
 * 
 * @author Daniel Ricci <thedanny09@gmail.com>
 *
 */
public final class ControllerFactory implements IDestructor, IDispatcher<IController> {
	
	/**
	 * A message dispatcher used to communicate with controller 
	 */
	Dispatcher<IController> _dispatcher = new Dispatcher<>();
	
    /**
     * Contains the history of all the controllers ever created by this factory, organized 
     * by class name and mapping to the list of all those classes
     * 
     * Note: This list should be properly cleaned with respect to disposed
     * items within this list
     */
    //private final Map<String, Set<IController>> _history = new HashMap<>();
	private final Map<Class, Set<IController>> _history = new HashMap<>();
	
    /**
     * Contains the list of all exposed unique controllers created by this factory.  An exposed
     * controller is one where the controller reference is marked to be stored by this factory
     * so that it may be referenced by others during it's lifespan
     */
	private final Set<IController> _controllers = new HashSet<>(); 
	
	/**
	 * Singleton instance of this class
	 */
	private static ControllerFactory _instance;
	
	/**
	 * Constructs a new object of this class
	 */
	private ControllerFactory() {
		_dispatcher.start();
	}
	
	/**
	 * Returns the singleton reference 
	 * 
	 * @return The singleton reference
	 */
	public synchronized static ControllerFactory instance() {
		if(_instance == null) {
			_instance = new ControllerFactory();
		}	
		return _instance;
	}
	
	/**
	 * Adds a controller
	 * 
	 * @param controller The controller to add
	 * @param isShared If the controller should be added into the exposed cache
	 */
	private void Add(IController controller, boolean isShared) { 
	    Set<IController> controllers = _history.get(controller.getClass());
	    if(controllers == null) {
	        controllers = new HashSet<IController>();
	        _history.put(controller.getClass(), controllers);
	    }
	    controllers.add(controller);
	    
	    if(isShared) {
	        _controllers.add(controller);
	    }
	}
	
	/**
	 * Gets a particular controller without the side-effect of creation
	 * 
	 * @param controllerClass The class type to get
	 * 
	 * @return The specified class controller
	 */
	public <T extends IController> IController get(Class<T> controllerClass) {
		for(IController controller : _controllers) {
			if(controller.getClass() == controllerClass) {
				return controller;
			}
		}
		return null;
	}

	/**
	 * Gets the specified type of resource
	 * 
	 * @param controllerClass The controller class to get
	 * @param isShared If the factory should keep tabs of this class
	 * @param args The arguments to pass into the controller class
	 * @return A reference to the specified class
	 */
	public <T extends IController> IController get(Class<T> controllerClass, boolean isShared, Object...args) {
		System.out.println("Attempting to get " + controllerClass.getName());
		if(isShared) {
			for(IController item : _controllers) {
				if(item.getClass() == controllerClass) {
					return item;
				}
			}
		}
		
		// Get the list of arguments together
		Class<?>[] argsClass = new Class<?>[args.length];
		for(int i = 0; i < args.length; ++i) {
			argsClass[i] = args[i].getClass();
		}
		
		try {
		    T createdClass = controllerClass.getConstructor(argsClass).newInstance(args);
			Add(createdClass, isShared);
			return createdClass;
			
		} catch (Exception e) {
			e.printStackTrace();
		}
			
		return null;
	}
	
	@Override public void dispose() {
		for(IController controller : _controllers) {
			controller.dispose();
		}
		_instance = null;
	}

	@Override public <U extends IController> void BroadcastMessage(Object sender, String operationName, Class<U> type, Object... args) {
		Set<IController> controllers = _history.get(type);
		if(controllers == null) {
			_dispatcher.add(
				new DispatcherMessage<IController>(sender, operationName, null, Arrays.asList(args))
			);	
		}
	}

	@Override public void flush() {
	}

	public static boolean running() {
		return _instance != null;
	}
}