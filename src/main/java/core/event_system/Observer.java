package core.event_system;

import pixel_pioneer.GameObject;

public interface Observer {

    void onNotify(GameObject gameObject, Event event);
}
