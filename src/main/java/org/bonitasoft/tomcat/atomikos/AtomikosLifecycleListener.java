package org.bonitasoft.tomcat.atomikos;

import org.apache.catalina.Lifecycle;
import org.apache.catalina.LifecycleEvent;
import org.apache.catalina.LifecycleListener;

import com.atomikos.icatch.jta.UserTransactionManager;

/**
 * http://www.atomikos.com/Documentation/Tomcat7Integration35
 */
public class AtomikosLifecycleListener implements LifecycleListener {

    private UserTransactionManager utm;

    @Override
    public void lifecycleEvent(final LifecycleEvent event) {
        try {
            if (Lifecycle.START_EVENT.equals(event.getType())) {
                if (utm == null) {
                    utm = new UserTransactionManager();
                }
                utm.init();
            } else if (Lifecycle.AFTER_STOP_EVENT.equals(event.getType())) {
                if (utm != null) {
                    utm.close();
                }
            }
        } catch (final Exception e) {
            System.err.println(e.getMessage());
        }
    }
}
