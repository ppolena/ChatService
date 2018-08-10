package chat.onair.repository;

import chat.onair.entity.Channel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;

@RepositoryRestResource(collectionResourceRel = "channels", path = "channels")
public interface ChannelRepository extends JpaRepository<Channel, String> {

    @RestResource(path = "find-by-channel-name")
    Channel findByChannelName(@Param("name") String name);
}
