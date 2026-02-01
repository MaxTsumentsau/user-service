package config;

import org.hibernate.Session;

public class SessionContext {

     private static final ThreadLocal<Session> holder = new ThreadLocal<>();

     public static void set(Session session) {
          holder.set(session);
     }

     public static Session get() {
          return holder.get();
     }

     public static void clear() {
          holder.remove();
     }
}

