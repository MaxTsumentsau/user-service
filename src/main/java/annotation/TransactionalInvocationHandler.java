package annotation;

import config.SessionContext;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.FlushMode;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.sql.Connection;

@AllArgsConstructor
@Slf4j
public class TransactionalInvocationHandler implements InvocationHandler {

     private final Object target;
     private final SessionFactory sessionFactory;

     @Override
     public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
          Method implMethod = target.getClass().getMethod(method.getName(), method.getParameterTypes());

          if (!implMethod.isAnnotationPresent(HandmadeTransactional.class)) {
               return method.invoke(target, args);
          }

          log.info("Starting handmade transaction for method {}", method.getName());
          Session session = sessionFactory.openSession();
          //уровень изоляции
          Isolation isolation = implMethod.getAnnotation(HandmadeTransactional.class).isolation();

          log.info("Isolation level is set to: {} for method: {}", isolation.name(), method.getName());
          session.doWork(conn -> {
               switch (isolation) {
                    case READ_COMMITTED -> conn.setTransactionIsolation(Connection.TRANSACTION_READ_COMMITTED);
                    case REPEATABLE_READ -> conn.setTransactionIsolation(Connection.TRANSACTION_REPEATABLE_READ);
                    case SERIALIZABLE -> conn.setTransactionIsolation(Connection.TRANSACTION_SERIALIZABLE);
               }
          });

          boolean readOnly = implMethod.getAnnotation(HandmadeTransactional.class).readOnly();
          log.info("ReadOnly mode is set to: {} for method: {}", readOnly, method.getName());
          if (readOnly) {
               session.setDefaultReadOnly(true);
               session.setHibernateFlushMode(FlushMode.MANUAL);
          }

          SessionContext.set(session);
          Transaction tx = session.beginTransaction();

          try {
               Object result = method.invoke(target, args);
               tx.commit();
               if (result != null) {
                    log.info("Transaction commit for {}. Result: {}", method.getName(), result);
               } else {
                    log.info("Transaction commit for {} (void result)", method.getName());
               }

               return result;
          } catch (Throwable e) {
               tx.rollback();
               Throwable cause = e.getCause() != null ? e.getCause() : e;
               logErrorDetails(method, cause);
               throw cause;
          } finally {
               SessionContext.clear();
               session.close();
          }
     }

     private void logErrorDetails(Method method, Throwable cause) {
          log.error("Transaction rollback for method {}", method.getName());
          log.error("Cause: {}", cause.getMessage(), cause);
     }
}



