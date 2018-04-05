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
import java.util.logging.Level;

import engine.communication.internal.signal.ISignalListener;
import engine.communication.internal.signal.arguments.AbstractEventArgs;
import engine.utils.logging.Tracelog;

/**
 * Factory that can communicate between signal types
 * 
 * @author Daniel Ricci {@literal <thedanny09@gmail.com>}
 *
 * @param <T> Any type extending from the ISignalListener interface
 */
public abstract class AbstractSignalFactory<T extends ISignalListener> extends AbstractFactory {

    /**
     * The cache holds items that have been pushed into the factory but that do not
     * currently have any association with anything. They remain dormant until there is 
     * a request for a resource of it's type, then it is used in place of creating another
     * resource.  
     * 
     * Note: These items will take precedence over the creation of new items first.
     */
    protected final Map<Class, Queue<T>> _cache = new HashMap<>();

    /**
     * The mappings of all signals that have been declared and marked as private
     */
    protected final Map<Class, List<T>> _privateSignals = new HashMap<>();

    /**
     * The list of all signals that have been declared and marked as public
     */
    protected final List<T> _publicSignals = new ArrayList<>();

    /**
     * Returns a flag indicating if the specified public signal class exists
     *   
     * @param signalClass The class of the resource to verify
     * @param <U> A type extending The class template type 
     *  
     * @return TRUE if the specified signal class exists, FALSE otherwise
     */
    public final <U extends T> boolean exists(Class<U> signalClass) {
        return get(signalClass) != null;
    }

    /**
     * Gets the specified concrete signal class based on the type provided
     * 
     * Note: This will return the singular publicly shared entity (if it exists)
     * 
     * @param signalClass The class of the resource to get
     * @param <U> A type extending The class template type 
     * 
     * @return The concrete class of the specified type
     */
    public final <U extends T> U get(Class<U> signalClass) {
        for(T resource : _publicSignals) {
            if(resource.getClass().equals(signalClass)) {
                return (U)resource;
            }
        }
        return null;
    }

    /**
     * Gets the list of signals associated to the specified signal class type
     * 
     * Note: This will return the private resources associated to the factory
     * 
     * @param signalClass The signal class type
     * @param <U> A type extending The class template type 
     * 
     * @return The list of private signals of the specified type
     */
     public final <U extends T> List<U> getAll(Class<U> signalClass) {
        List<T> signals = _privateSignals.get(signalClass);
        return signals == null ? new ArrayList() : new ArrayList(signals);
     }

    /**
     * Helper method to add the created resource for retrieval
     *  
     * @param resource The resource that was created to be added
     * @param isShared If the resource should be available publicly
     * @param <U> A type extending The class template type
     * 
     * @return A resource based on the specified type
     */
    public <U extends T> U add(U resource, boolean isShared) {

        // Get the list of classes based on the type of controller 
        // that is being added
        List resources = _privateSignals.get(resource.getClass());

        // If there is no entry then create a new entry and add to it
        if(resources == null) {

            // Create a new list and populate it with the specified resource
            resources = new ArrayList<>();
            resources.add(resource);

            // Add the new entry into the history structure
            // for future reference
            _privateSignals.put(resource.getClass(), resources);
        }

        // There was in fact an entry which means the resource 
        // needs to be added into the list if it does not already
        // exist
        else if(!resources.contains(resource)) {
            resources.add(resource);
        }

        // If the resource is marked to be shared then it is
        // stored in a separate list 
        if(isShared) {
            _publicSignals.add(resource);
        }

        return resource;
    }

    /**
     * Removes the specified resource from this factory.  This will remove
     * the reference from both the history list and shared list.
     * 
     * @param resource The resource to remove
     * @param <U> A type extending The class template type
     */
    public final <U extends T> void remove(U resource) {
        List<T> history = _privateSignals.get(resource.getClass());
        if(history != null) {

            // Attempt to remove the reference from the history
            if(history.remove(resource)) {
                Tracelog.log(Level.INFO, false, "Successfully removed " + resource.getClass().toString() + " from the history within the " + this.getClass().toString() + " factory");

                // If there are no more items in the list then clean out the key as well
                if(history.size() == 0) {
                    _privateSignals.remove(resource.getClass());
                }
            }
        }

        // If the reference also exists in the shared space then remove it from there as well.
        if(_publicSignals.remove(resource)) {
            Tracelog.log(Level.INFO, false, "Successfully removed " + resource.getClass().toString() + " from the resources within the " + this.getClass().toString() + " factory");
        }
    }

    /**
     * Gets the total count of resources from the factory
     * 
     * Note: This goes through the history list therefore this will
     * include everything that was ever created, minus whatever resources
     * have been destroyed (nullified)
     *  
     * @return The total number of current resources within the history structure
     */
    protected final int getTotalResourcesCount() {

        int count = 0;

        // For every list within the history structure
        // get the total number of entries
        for(List list : _privateSignals.values()) {
            count += list.size();
        }

        return count;
    }

    /**
     * Gets the number of resources
     * 
     * @param classType The resource class type
     * @param <U> A type extending The class template type 
     *
     * @return The number of queued resources
     */
    public final <U extends T> int getQueuedResourcesCount(Class<U> classType) {
        Queue<T> resources = _cache.get(classType);
        return resources == null ? 0 : resources.size();
    }

    public final <U extends T> void queueResource(U resource) {
        // Get the list of queue'd resources based on the resource class
        Queue<T> cachedResources = _cache.get(resource.getClass());

        // If there is no entry
        if(cachedResources == null) {

            // Create a new entry
            cachedResources = new LinkedList<>();

            // Insert into the cache the resourceClass and the cached resources empty list
            _cache.put(resource.getClass(), cachedResources);
        }

        // Populate the list of items using the reference
        cachedResources.add(resource);
    }

    /**
     * Sends out a signal to a group of the specified types in a multi-cast fashion
     * 
     * @param signalClass The type of class to send the event to
     * @param signalEvent The event to pass in, this is a signal event or one of its derived types
     * @param <U> A type extending The class template type
     * @param <V> A type extending The class SignalEvent
     * 
     */
    public final <U extends T, V extends AbstractEventArgs> void multicastSignalListeners(Class<U> signalClass, V signalEvent) {
        List<T> resources = _privateSignals.get(signalClass);
        if(resources != null) {
            Object source = signalEvent.getSource();
            for(T resource : resources) {
                // Send out a unicast signal to every resource, although 
                // horribly inefficient the way it is being done right now
                if(!source.equals(resource)) {
                    resource.sendSignalEvent(signalEvent);
                }
            }
        }
    }

    @Override protected boolean hasEntities() {
        return !_privateSignals.isEmpty() || !_cache.isEmpty();
    }

    @Override public boolean clear() {
        
        _privateSignals.clear();
        _publicSignals.clear();
        _cache.clear();

        return true;
    }
}