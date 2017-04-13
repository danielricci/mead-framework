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

import engine.api.IView;

/**
 * Factory for creating and working with IView types
 * 
 * @author Daniel Ricci <thedanny09@gmail.com>
 *
 */
public final class ViewFactory extends AbstractFactory<IView> {

	@Override public <U extends IView> U get(Class<U> resourceClass, boolean isShared, Object... resourceParameters) {
		
		// Get the total number of resources currently in the factory
		int numOfElements = getTotalResourcesCount();		

		// Call the super functionality to get a reference to the desired view
		// Note: If the view doesn't exist then this will create a new entry
		U view = super.get(resourceClass, isShared, resourceParameters);
		
		// Compare the current number of resources to see if this was newly created
		// and if it was then call initialize components so that this procedure is
		// automated and will not need to be called by the programmer 
		if(numOfElements != getTotalResourcesCount()) {
			view.initializeComponents();
		}
		
		// Return the referenced resource
		return view;
	}
}