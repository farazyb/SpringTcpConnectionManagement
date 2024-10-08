package ir.co.ocs.envoriments;

public interface LifeCycle {
    public void start();

    public void restart();

    public void stop();
    public void stopAndRemove();
}
