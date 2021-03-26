package tech.turl.community.service;

import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.stereotype.Service;
import tech.turl.community.dao.elasticsearch.DiscussPostRepository;
import tech.turl.community.entity.DiscussPost;

import java.util.ArrayList;
import java.util.List;

/**
 * @author zhengguohuang
 * @date 2021/03/26
 */
@Service
public class ElasticSearchService {
    @Autowired
    private DiscussPostRepository discussPostRepository;

    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;

    /**
     * 插入或修改帖子
     *
     * @param post
     */
    public void saveDiscussPost(DiscussPost post) {
        discussPostRepository.save(post);
    }

    /**
     * 删除帖子
     *
     * @param id
     */
    public void deleteDiscussPost(int id) {
        discussPostRepository.deleteById(id);
    }

    /**
     * 搜索并高亮分页显示
     *
     * @param keyword
     * @param current
     * @param limit
     * @return
     */
    public Page<DiscussPost> searchDiscussPost(String keyword, int current, int limit) {
        Pageable pageable = PageRequest.of(current, limit);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery(keyword, "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(pageable)
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();

        SearchHits<DiscussPost> searchHits = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
        if (searchHits.getTotalHits() <= 0) {
            return null;
        }
        List<DiscussPost> list = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : searchHits) {
            DiscussPost content = hit.getContent();
            DiscussPost post = new DiscussPost();
            BeanUtils.copyProperties(content, post);
            List<String> list1 = hit.getHighlightFields().get("title");
            if (list1 != null) {
                post.setTitle(list1.get(0));
            }
            List<String> list2 = hit.getHighlightFields().get("content");
            if (list2 != null) {
                post.setContent(list2.get(0));
            }
            list.add(post);
        }
        return new PageImpl<>(list, pageable, searchHits.getTotalHits());
    }

}
