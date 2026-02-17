package com.max2ba.user_service.listener;

import com.max2ba.user_service.entity.User;
import jakarta.persistence.PostLoad;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class UserCrudListener {

     @PostPersist
     public void afterInsert(User user) {
          log.info("EntityListener. Inserted entity: {}", user);
     }

     @PostUpdate
     public void afterUpdate(User user) {
          log.info("EntityListener. Updated entity: {}", user);
     }

     @PostRemove
     public void afterDelete(User user) {
          log.info("EntityListener. Deleted entity: {}", user);
     }

     @PostLoad
     public void afterLoad(User user) {
          log.info("EntityListener. Loaded entity: {}", user);
     }
}
