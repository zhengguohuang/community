package tech.turl.community.dao.elasticsearch;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import tech.turl.community.entity.DiscussPost;

/**
 * @author zhengguohuang
 * @date 2021/03/25
 */
@Repository
public interface DiscussPostRepository extends ElasticsearchRepository<DiscussPost, Integer> {
}
