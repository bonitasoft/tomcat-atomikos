package org.bonitasoft.tomcat.atomikos;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

public class ContextLifecycleListener implements LifecycleListener {

    private String webappName = null;

    public void setWebappName(final String name) {
        webappName = name;
    }

    @Override
    public void lifecycleEvent(final LifecycleEvent event) {
        try {
            if (Lifecycle.START_EVENT.equals(event.getType())) {
                AtomikosLifecycleManager.getInstance().startWebApp(webappName);
            } else if (Lifecycle.AFTER_STOP_EVENT.equals(event.getType())) {
                AtomikosLifecycleManager.getInstance().stopWebApp(webappName);
            }
        } catch (final Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
