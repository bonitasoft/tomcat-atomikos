package org.bonitasoft.tomcat.atomikos;

import java.util.Enumeration;
import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.Name;
import javax.naming.NamingException;
import javax.naming.RefAddr;
import javax.naming.Reference;
import javax.naming.spi.ObjectFactory;

import org.apache.naming.ResourceRef;
import org.apache.naming.factory.Constants;

import com.atomikos.beans.PropertyException;
import com.atomikos.beans.PropertyUtils;
import com.atomikos.jdbc.AtomikosDataSourceBean;
import com.atomikos.jdbc.AtomikosSQLException;

public class EnhancedTomcatAtomikosBeanFactory implements ObjectFactory
{

   @Override
public Object getObjectInstance(final Object obj, final Name name, final Context nameCtx, final Hashtable environment) throws NamingException
   {
      if (obj instanceof ResourceRef) { //see http://fogbugz.atomikos.com/default.asp?community.6.2947.0 for a fix for OpenEJB!
         try {
            final Reference ref = (Reference) obj;
            final String beanClassName = ref.getClassName();
            Class beanClass = null;
            final ClassLoader tcl = Thread.currentThread().getContextClassLoader();
            if (tcl != null) {
               try {
                  beanClass = tcl.loadClass(beanClassName);
               } catch (final ClassNotFoundException e) {
               }
            } else {
               try {
                  beanClass = Class.forName(beanClassName);
               } catch (final ClassNotFoundException e) {
                  e.printStackTrace();
               }
            }
            if (beanClass == null) {
               throw new NamingException("Class not found: " + beanClassName);
            }
            if (AtomikosDataSourceBean.class.isAssignableFrom(beanClass)) {
               return createDataSourceBean(ref, beanClass);
            } else {
               throw new NamingException("Class is neither an AtomikosDataSourceBean nor an AtomikosConnectionFactoryBean: " + beanClassName);
            }

         } catch (final Exception ex) {
            throw (NamingException) new NamingException("error creating AtomikosDataSourceBean").initCause(ex);
         }

      } else {
         return null;
      }
   }

   /**
    * create a DataSourceBean for a JDBC datasource
    *
    * @param ref
    * @param beanClass
    * @return
    * @throws InstantiationException
    * @throws IllegalAccessException
    * @throws PropertyException
    * @throws AtomikosSQLException
    */
   private Object createDataSourceBean(final Reference ref, final Class beanClass) throws InstantiationException, IllegalAccessException, PropertyException, AtomikosSQLException
   {
      final AtomikosDataSourceBean bean = (AtomikosDataSourceBean) beanClass.newInstance();

      int i = 0;
      final Enumeration en = ref.getAll();
      while (en.hasMoreElements()) {
         final RefAddr ra = (RefAddr) en.nextElement();
         final String propName = ra.getType();

         if (propName.equals(Constants.FACTORY) || propName.equals("singleton") || propName.equals("description") || propName.equals("scope") || propName.equals("auth")) {
            continue;
         }

         final String value = (String) ra.getContent();

         PropertyUtils.setProperty(bean, propName, value);

         i++;
      }

      bean.init();
      return bean;
   }
}
