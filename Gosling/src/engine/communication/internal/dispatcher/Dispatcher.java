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

package engine.communication.internal.dispatcher;

import java.util.concurrent.ConcurrentLinkedQueue;

import engine.util.event.ISignalReceiver;
import engine.util.event.SignalEvent;

public class Dispatcher<T extends ISignalReceiver> extends Thread {
	private volatile ConcurrentLinkedQueue<DispatcherMessage<T>> _messages = new ConcurrentLinkedQueue<>();		
	
	public void add(DispatcherMessage<T> message) {
		_messages.add(message);
	}
	
	@Override public void run() {
		while(true) {
			try {
				DispatcherMessage<T> message = _messages.poll();
				if(message != null) {
					message.resources.stream().forEach(
						z -> z.sendSignal(new SignalEvent(message.sender, message.operationName))
					);
				}
				Thread.sleep(220);						
			} catch (InterruptedException exception) {
				exception.printStackTrace();
			}
		}
	}
}
