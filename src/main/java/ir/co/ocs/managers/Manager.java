package ir.co.ocs.managers;

import ir.co.ocs.envoriments.NetworkChannel;
import ir.co.ocs.envoriments.State;
import ir.co.ocs.envoriments.exceptions.NetworkBindingException;
import ir.co.ocs.socketconfiguration.BaseTcpSocketConfiguration;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.ConcurrentHashMap;

@Log4j2
public abstract class Manager<T extends NetworkChannel> {
    private ConcurrentHashMap<String, T> services = new ConcurrentHashMap<>();


    public void addService(T... services) throws ManagersException {
        for (T service : services) {
            String identifier = service.getBaseTcpSocketConfiguration().getChannelIdentificationName();
            if (this.services.containsKey(identifier)) {
                throw new ManagersException("Server with identifier '" + identifier + "' already exists.");
            }
            this.services.put(identifier, service);
        }
    }

    public void remove(String... identifiers) throws ManagersException {
        for (String identifier : identifiers) {
            T service = services.get(identifier);
            if (service != null) {
                if (service.state() != State.STOPPED) {
                    throw new ManagersException("Server '" + identifier + "' is not stopped. Stop it before removal.");
                }
                service.dispose();
                services.remove(identifier);
                log.info("service '{}' removed.", identifier);
            } else {
                throw new ManagersException("service with identifier '" + identifier + "' not found.");
            }
        }
    }

    public T get(String identifier) throws ManagersException {
        return services.get(identifier);
    }

    public void shutdown() throws ManagersException {
        for (T service : services.values()) {
            try {
                service.stop();
                service.dispose();
            } catch (NetworkBindingException e) {
                log.error("Failed to shut down service: {}", service.getBaseTcpSocketConfiguration().getChannelIdentificationName(), e);
            }
        }
        services.clear();
        log.info("All services have been shut down.");
    }

    public ConcurrentHashMap<String, T> getServices() {

        return services;
    }


}
