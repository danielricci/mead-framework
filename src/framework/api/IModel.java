package framework.api;

import java.util.Map;
import java.util.UUID;

import framework.communication.internal.persistance.IXMLCodec;
import framework.communication.internal.signal.ISignalListener;
import framework.communication.internal.signal.SignalListenerContainer;
import framework.core.mvc.common.CommonProperties;

/**
 * This interface describes the general contract rules of all model type implementors 
 * 
 * @author Daniel Ricci {@literal <thedanny09@icloud.com>}
 *
 */
public interface IModel extends ISignalListener, IXMLCodec {

    /**
     * This event is used to indicate that the model listens to events that
     * need a reference to itself.
     */
    public static final String EVENT_PIPE_DATA = "EVENT_PIPE_DATA";

    /**
     * Gets the identifier associated with the model
     * 
     * @return The unique identifier of the object
     */
    public abstract UUID getUUID();
    
    /**
     * @return The name of the model
     */
    default public String getName() {
        return "";
    }
    
    public void refresh();
    
    public void refresh(String operationName);
    
    /**
     * Gets the model properties of the implementor of this interface
     * 
     * @return The model properties of the implementor
     */
    public CommonProperties getModelProperties();

    /**
     * Performs a copy of the specified model
     * 
     * @param model The model to copy
     */
    public void copyData(IModel model);

    public void addListener(ISignalListener... listeners);
    
    @Override default Map<String, SignalListenerContainer> getSignals() {
        return getModelProperties().getSignalListeners();
    }
}