package ir.co.ocs.connection.envoriments;


import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

@Component
@Scope("prototype")
public class StateService {
    private State state;

    public State getState() {
        return state;
    }

    public void setState(State newState) {
        this.state = newState;
    }

    public void transitionTo(State newState) {
        this.state = newState;
    }
}
