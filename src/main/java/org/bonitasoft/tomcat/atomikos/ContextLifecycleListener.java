package org.bonitasoft.tomcat.atomikos;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

public class ContextLifecycleListener implements LifecycleListener {

    private String webappName = null;

    public void setWebappName(String name) {
        webappName = name;
    }

    @Override
    public void lifecycleEvent(LifecycleEvent event) {
        try {
            if (Lifecycle.START_EVENT.equals(event.getType())) {
                AtomikosLifecycleManager.getInstance().startWebApp(webappName);
            } else if (Lifecycle.STOP_EVENT.equals(event.getType())) {
                AtomikosLifecycleManager.getInstance().stopWebApp(webappName);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
