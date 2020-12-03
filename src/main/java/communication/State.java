package communication;

public interface State {
    void call(Phone phone);
    void exit(Phone phone);
}

enum StateType {
    IDLE,
    RINGING,
    DIALING,
    TALKING,
    UNKNOWN,
}

class Idle implements State {

    @Override
    public void call(Phone phone) {
        phone.setCurrentState(StateType.DIALING);
        phone.callS();
    }

    @Override
    public void exit(Phone phone) {
        phone.repeat();
    }
}


class Ringing implements State {

    @Override
    public void call(Phone phone) {
        phone.setCurrentState(StateType.TALKING);
        phone.answer();
    }

    @Override
    public void exit(Phone phone) {
        phone.setCurrentState(StateType.IDLE);
        phone.disconnect();
    }
}

class Dialing implements State {

    @Override
    public void call(Phone phone) {
        phone.repeat();
    }

    @Override
    public void exit(Phone phone) {
        phone.setCurrentState(StateType.IDLE);
        phone.disconnect();
    }
}

class Talking implements State {

    @Override
    public void call(Phone phone) {
        phone.repeat();
    }

    @Override
    public void exit(Phone phone) {
        phone.setCurrentState(StateType.IDLE);
        phone.disconnect();
    }
}
