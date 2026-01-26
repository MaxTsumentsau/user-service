package listener;

import lombok.extern.slf4j.Slf4j;
import org.hibernate.event.spi.*;
import org.hibernate.persister.entity.EntityPersister;

@Slf4j
public class UserCrudListener implements PostInsertEventListener, PostUpdateEventListener,
        PostDeleteEventListener, PostLoadEventListener {
     @Override
     public void onPostDelete(PostDeleteEvent postDeleteEvent) {
          Object entity = postDeleteEvent.getEntity();
          log.info("Deleted entity: {}", entity);
     }

     @Override
     public void onPostInsert(PostInsertEvent postInsertEvent) {
          log.info("Inserted entity: {}", postInsertEvent.getEntity());
     }

     @Override
     public void onPostLoad(PostLoadEvent postLoadEvent) {
          log.info("Loaded entity: {}", postLoadEvent.getEntity());
     }

     @Override
     public void onPostUpdate(PostUpdateEvent postUpdateEvent) {
          Object newEntity = postUpdateEvent.getEntity();
          log.info("Updated  entity: {}", newEntity);
     }

     @Override
     public boolean requiresPostCommitHandling(EntityPersister entityPersister) {
          return false;
     }
}
