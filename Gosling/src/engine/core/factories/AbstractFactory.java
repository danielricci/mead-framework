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
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import engine.api.IDestructor;

/**
 * Top-most factory class that defines functionality for persisting/creating/storing objects
 * 
 * @author Daniel Ricci {@literal <thedanny09@gmail.com>}
 *
 * @param <T> Any type
 */
public abstract class AbstractFactory<T extends Object> implements IDestructor {
	
	/**
	 * The list of factories that have been constructed and that are still active 
	 */
	static final List<AbstractFactory> FACTORIES = new ArrayList<>();
	
	/**
	 * The history of all factory resources that have been created
	 */
	final Map<Class, List<T>> _history = new HashMap<>();
	
	/**
	 * The cache holds items that have been pushed into the factory but that do not
	 * currently have any association.  These items will take precedence over the creation
	 * of new items first.
	 */
	final Map<Class, Queue<T>> _cache = new HashMap<>();
	
	/**
	 * The queue of resources that are publicly available
	 */
	final List<T> _resources = new ArrayList<>();
	
	/**
	 * Indicates if this factory persists throughout the lifetime of the application
	 */
	private boolean _isPersistent;
	
	/**
	 * Resets the factory
	 */
	public static final void reset() {
		for(int i = FACTORIES.size() - 1; i >= 0; --i) {
			AbstractFactory factory = FACTORIES.get(i);
			if(!factory._isPersistent)
			{
				factory.flush();
				FACTORIES.remove(i);
			}
		}
	}
	
	/**
	 * Sets if this factory is persistent
	 * 
	 * @param isPersistent A flag if the factory is persistent
	 */
	public final void setPersitent(boolean isPersistent) {
		_isPersistent = isPersistent;
	}
	
	/**
	 * Gets if the factory is a persistent factory
	 * 
	 * @return TRUE if the factory is persistent, FALSE if it is not persistent
	 */
	public final boolean getIsPersistent() {
		return _isPersistent;
	}
	
	/**
	 * Checks if there is a factory that is running
	 * 
	 * Note: For a factory to run there must have been something created
	 *       in the factory, and it cannot be a factory that is non-persistent
	 * 
	 * @return Flag indicating if the factory is running
	 */
	public static final boolean isRunning()
	{
		// Go through the list of factories
		for(AbstractFactory factory : FACTORIES) {
			
			// 1. Factories that cannot persist can be removed
			// therefore their existence means that they are running
			//
			// 2. Even though a factory exists, if its not actually being used
			//    then it shouldn't necessarily count as "running"
			if(!factory._isPersistent && factory._history.size() > 0) {
				return true;
			}
		}

		return false;
	}
	
	/**
	 * Retrieves the specified factory in question. 
	 * 
	 * If the factory does not exist then there will be an attempt 
	 * to create the factory and store a reference to it within 
	 * this construct. 
	 * 
	 * Future calls to this method will return a reference to the 
	 * created factory.
	 * 
	 * @param factoryClass The class to lookup in the 
	 * 		  list of already created factories
	 * 
	 * @param <T> A type extending AbstractFactory
	 * 
	 * @return The factory of the specified concrete type
	 */
	public static final <T extends AbstractFactory> T getFactory(Class<T> factoryClass) {
		// Verify if the factory has already been created, if so then
		// return that instance
		for(AbstractFactory factory : FACTORIES) {
			if(factory.getClass() == factoryClass) {
				return (T) factory;
			}
		}
		
		// If execution gets to here, then it is assumed that 
		// the factory being asked for has not been created yet 
		T factory = null;
		
		try {	
			// Call the default constructor for the factory creation
			factory = factoryClass.getConstructor().newInstance();
			
			// add the factory to the factories list so that 
			// the reference is returned next time
			FACTORIES.add(factory);
		} 
		catch (Exception exception) {
			exception.printStackTrace();
		}	
		
		// Return the newly created factory reference
		return factory;
	}
	
	/**
	 * Attempts to the a cached resource 
	 * 
	 * @param resourceClass The class of the cached resource
	 * @param <U> A type extending The class template type
	 * 
	 * @return A cached resource if it exists
	 */
	private <U extends T> U getCachedResource(Class<U> resourceClass) {
		
		// Get the cache list based on the specified resource class
		Queue<T> cachedResources = _cache.get(resourceClass);
		
		T resource = null;
		
		// If the cached resources exists 
		if(cachedResources != null) {
				
			// If there are items to get
			if(cachedResources.size() > 0) {
				
				// Remove the item from the queue and get its reference
				resource = cachedResources.remove();
			
				// Logging information
				System.out.println("Info: Queue'd item " + resourceClass.getCanonicalName() + " being used, items left in queue = " + cachedResources.size());				
			}
			
			// If there are no more entries then just remove the key, it is no longer needed
			if(cachedResources.isEmpty()) {
				System.out.print("Info: No more entries in cache for " + resourceClass.getCanonicalName() + ", removing associated key");
				
				// Remove the no longer needed entry
				_cache.remove(resourceClass);			
			} 		
		}
		
		return resource != null ? (U)resource : null;
	}
	
	/**
	 * Helper method to add the created resource for retrieval
	 *  
	 * @param resource The resource that was created to be added
	 * @param isShared If the resource should be available publicly
	 * @param <U> A type extending The class template type
	 * 
	 */
	private final <U extends T> void add(U resource, boolean isShared) {
		
		// Get the list of classes based on the type of controller 
		// that is being added
	    List resources = _history.get(resource.getClass());
	    
	    // If there is no entry then create a new entry and add to it
	    if(resources == null) {
	    	
	    	// Create a new list and populate it with the specified resource
	        resources = new ArrayList<>();
	        resources.add(resource);
	        
	        // Add the new entry into the history structure
	        // for future reference
	        _history.put(resource.getClass(), resources);
	    }
	    // There was in fact an entry which means the resource 
	    // needs to be added into the list if it does not already
	    // exist
	    else if(!resources.contains(resource)){
	    	resources.add(resource);
	    }
	    
	    // If the resource is marked to be shared then it is
	    // stored in a separate list 
	    if(isShared) {
	        _resources.add(resource);
	    }
	}
	
	/**
	 * Gets the specified concrete class based on the type provided
	 * 
	 * @param resourceClass The class of the resource to get
	 * @param <U> A type extending The class template type 
	 * 
	 * @return The concrete class of the specified type
	 */
	public final <U extends T> U get(Class<U> resourceClass) {
		
		// Go through all the resources and try to find
		// the concrete class with the same resource class 
		for(T resource : _resources) {
			if(resource.getClass() == resourceClass) {
				return (U)resource;
			}
		}
		
		return null;
	}
	
	/**
	 * Gets the specified concrete class based on type provided. 
	 * Note: This will create the resource if it does not 
     *       already exist. 
	 * 
	 * @param resourceClass The class of the resource to get
	 * @param isShared If the resource should be shared when created
	 * @param resourceParameters The arguments to be passed into 
	 * 							 the resource class
	 * @param <U> A type extending The class template type
	 * 
	 * @return The created or already created resource
	 */
	public <U extends T> U get(Class<U> resourceClass, boolean isShared, Object... resourceParameters) {
		
		// Attempt to get a cached resource, this has priority over
		// every other resource
		U cachedResource = getCachedResource(resourceClass);
		
		// Make sure it exists
		if(cachedResource != null) {
			
			// Add it to our factory 
			add(cachedResource, isShared);
			
			// Return the resource
			return cachedResource;
		}
		
		// If the shared flag is set then make sure it doesn't 
		// already exist before creating it
		if(isShared) {

			// Try to get the resource to see if it already exists
			U resource = get(resourceClass);
			if(resource != null) {
				return resource;
			}
		}

		// Construct the list of arguments that were provided
		// Note: This is used to pass into the proper constructor
		Class[] argsClass = new Class[resourceParameters.length];
		for(int i = 0; i < resourceParameters.length; ++i) {
			argsClass[i] = resourceParameters[i].getClass();
		}
		
		// Create a fresh instance of the class specified
		U createdClass = null;
		
		try {
			// Attempt to create the class by getting the constructor with the 
			// same number of arguments as the list of resourceParameters passed in
		    createdClass = resourceClass.getConstructor(argsClass).newInstance(resourceParameters);
		    
		    // Add the newly created resource into the list of 
		    // created resources so that it can be referenced 
		    // later on
			add(createdClass, isShared);
			
		} catch (Exception exception) {
			exception.printStackTrace();
		}
			
		return createdClass;
	}
	
	/**
	 * Removes the specified resource from this factory.  This will remove
	 * the reference from both the history list and shared list.
	 * 
	 * @param resource The resource to remove
	 * @param <U> A type extending The class template type
	 * 
	 */
	public final <U extends T> void remove(U resource) {
		
		// Get the list of resources based on the class type
		List<T> resources = _history.get(resource.getClass());
		if(resources != null) {
			
			// Attempt to remove the reference from the history
			resources.remove(resource);
		}
		
		// If the reference also exists in the shared space then remove it
		// from there as well.
		_resources.remove(resource);
	}
	
	/**
	 * Queues the specified resources into the factory
	 * 
	 * @param resourceClass The class type of the resource
	 * @param resources The list of resources
	 * @param <U> A type extending The class template type
	 */
	public final <U extends T> void queueResources(Class<U> resourceClass, List<U> resources) {
		for(U resource : resources) {
			queueResources(resourceClass, resource);
		}
	}
	
	/**
	 * Queues the specified resource into the factory
	 * 
	 * @param resourceClass The class type of the resource
	 * @param resources The resource to queue
	 * @param <U> A type extending The class template type
	 */
	public final <U extends T> void queueResources(Class<U> resourceClass, U resource) {
		
		// Get the list of queue'd resources based on the resource class
		Queue<T> cachedResources = _cache.get(resourceClass);
		
		// If there is no entry
		if(cachedResources == null) {
			
			// Create a new entry
			cachedResources = new LinkedList<>();
			
			// Insert into the cache the resourceClass and the cached resources empty list
			_cache.put(resourceClass, cachedResources);
		}
		
		// Populate the list of items using the reference
		cachedResources.add(resource);
	}
	/**
	 * Queue's the list of specified resources, and pushes them into the factory.
	 * 
	 * Note: Each resource is not shared.
	 * 
	 * @param resourceClass The class type of the resource
	 * @param resources The resource data to queue
	 * @param <U> A type extending The class template type
	 */
	public <U extends T> void queueResourcesPush(Class<U> resourceClass, List<U> resources) {
		
		// Queue the specified resource
		queueResources(resourceClass, resources);
		
		for(int i = 0, size = resources.size(); i < size; ++i) {
			// Call 'get' on the resource, effectively forcing it to 
			// be added into the queue.
			// Note: This is done to follow the same conventions as the 'get' method, its good practice
			if(this.get(resourceClass, false) == null) {
				System.out.print("Error: Could not fetch one of the queued resources");
			}		
		}
	}
	
	/**
	 * Gets the total number of queue'd resources of the specified class type
	 * 
	 * @param resourceClass The resource class type
	 * @param <U> A type extending The class template type
	 * 
	 * @return The total number of resources of the specified class type
	 */
	public final <U extends T> int getQueuedResourceCount(Class<U> resourceClass) {
		// Get the list of queue'd resources based on the resource class
		Queue<T> cachedResources = _cache.get(resourceClass);
		
		// Return the total number of resources of the specified class type
		return cachedResources == null ? 0 : cachedResources.size();
	}
	
	/**
	 * Gets the total count of resources from the factory
	 * 
	 * Note: This goes through the history list therefore this will
	 * include everything that was ever created, minus whatever resources
	 * have been destroyed (nullified)
	 *  
	 * @return The total number of current resources within the
	 *         history structure
	 */
	protected final int getTotalResourcesCount() {
		
		int count = 0;
		
		// For every list within the history structure
		// get the total number of entries
		for(List list : _history.values()) {
			count += list.size();
		}
		
		return count;
	}
		
	@Override public void flush() {
		_resources.clear();
		_history.clear();	
		_cache.clear();
	}	
}