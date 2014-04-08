package org.bonitasoft.tomcat.atomikos;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.atomikos.jdbc.AtomikosDataSourceBean;

public class AtomikosLifecycleManager
{

    private static AtomikosLifecycleManager manager = null;

    private String webappName = null;

    final private ConcurrentMap<String, List<Object>> atomikosObjectMap = new ConcurrentHashMap<String, List<Object>>();

    private AtomikosLifecycleManager() {
    }

    public synchronized static AtomikosLifecycleManager getInstance() {
        if (manager == null) {
            manager = new AtomikosLifecycleManager();
        }

        return (manager);
    }

    public void startWebApp(String name) {
        webappName = name;

        atomikosObjectMap.put(name, new ArrayList<Object>());
    }

    public String getWebappName() {
        return (webappName);
    }

    public void addResource(Object obj) {
        if (atomikosObjectMap.containsKey(webappName)) {
            atomikosObjectMap.get(webappName).add(obj);
        }
    }

    public void stopWebApp(String name) {
        if (atomikosObjectMap.containsKey(name)) {
            List<Object> list = atomikosObjectMap.get(name);
            for (Object obj : list) {
                /*
                 * CS: for JMS use only?
                 * if (obj instanceof AtomikosConnectionFactoryBean) {
                 * ((AtomikosConnectionFactoryBean) obj).close();
                 * } else
                 */if (obj instanceof AtomikosDataSourceBean) {
                    ((AtomikosDataSourceBean) obj).close();
                }
            }
            list.clear();
        }
    }
}
