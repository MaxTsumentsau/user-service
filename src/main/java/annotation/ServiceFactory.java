package annotation;

import lombok.AllArgsConstructor;
import org.hibernate.SessionFactory;

import java.lang.reflect.Proxy;

@AllArgsConstructor
public class ServiceFactory {

     private final SessionFactory sessionFactory;

     @SuppressWarnings("unchecked")
     public <T> T create(Class<T> serviceInterface, T implementation) {

          return (T) Proxy.newProxyInstance(
                  serviceInterface.getClassLoader(),
                  new Class[]{serviceInterface},
                  new TransactionalInvocationHandler(implementation, sessionFactory)
          );
     }
}

