package com.ecinema.models.dataaccess;

import com.ecinema.models.show.ShowRoom;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowRoomRepository extends JpaRepository<ShowRoom, Integer> {

//    public ShowRoom getShowRoomBy
}
