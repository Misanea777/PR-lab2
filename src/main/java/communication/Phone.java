package communication;

import protocol.application.ControlType;

import java.util.HashMap;

public class Phone {
    private Server server;
    private State currentState;
    private HashMap<StateType, State> states = new HashMap<StateType, State>();

    public Phone(Server server) {
        this.server = server;

        states.put(StateType.IDLE, new Idle());
        states.put(StateType.RINGING, new Ringing());
        states.put(StateType.DIALING, new Dialing());
        states.put(StateType.TALKING, new Talking());

        this.currentState = states.get(StateType.IDLE);
    }

    public StateType getCurrentState() {
        for (StateType stateType : states.keySet()) {
            if(states.get(stateType) == currentState) return stateType;
        }
        return StateType.UNKNOWN;
    }

    public void setCurrentState(StateType stateType) {
        currentState = states.get(stateType);
    }


    public void call() {
        currentState.call(this);
    }
    public void exit() {
        currentState.exit(this);
    }

    public void ignore() {

    }
    public void repeat() {
    }

    public void callS() {
        System.out.println("Calling...");
        server.sendCommandToServ(ControlType.NOTIFY_CALL);
    }
    public void talking() {    System.out.println("Call Connected...");    }
    public void ringing() {
        System.out.println("Call is coming...");
        if((currentState == states.get(StateType.DIALING)) || (currentState == states.get(StateType.TALKING))) return;
        setCurrentState(StateType.RINGING);
        server.sendCommand(ControlType.NOTIFY_CALL);
    }
    public void answer() {
        System.out.println("Answering...");
        server.sendCommandToServ(ControlType.NOTIFY_ACCEPT_CALL);
    }
    public void disconnect() {
        System.out.println("Disconnected/Idle...");
        server.sendCommandToServ(ControlType.EXIT);
    }
}
