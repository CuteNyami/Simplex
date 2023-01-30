package tk.simplexclient.event;

import java.lang.reflect.InvocationTargetException;

public class Event {

    private boolean cancelled;

    public enum State {
        PRE("PRE", 0),
        POST("POST", 1);
        State(final String string, final int number) {}
    }

    public Event call() {

        this.cancelled = false;
        call(this);
        return this;
    }

    public boolean isCancelled() {

        return cancelled;
    }

    public void setCancelled(boolean cancelled) {

        this.cancelled = cancelled;
    }

    private static void call(final Event event) {

        final ArrayHelper<Data> dataList = EventManager.get(event.getClass());

        if (dataList != null) {
            for (final Data data : dataList) {

                try {
                    data.target.invoke(data.source, event);
                } catch (IllegalAccessException | InvocationTargetException e) {
                    // e.printStackTrace();
                }

            }
        }
    }

}
