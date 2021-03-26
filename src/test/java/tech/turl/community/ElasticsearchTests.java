package tech.turl.community;

import org.apache.commons.lang3.StringUtils;
import org.elasticsearch.index.query.Operator;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.fetch.subphase.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchRestTemplate;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.data.elasticsearch.core.query.NativeSearchQuery;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.test.context.ContextConfiguration;
import tech.turl.community.dao.DiscussPostMapper;
import tech.turl.community.dao.elasticsearch.DiscussPostRepository;
import tech.turl.community.entity.DiscussPost;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author zhengguohuang
 * @date 2021/03/25
 */
@SpringBootTest
@ContextConfiguration(classes = CommunityApplication.class)
public class ElasticsearchTests {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private DiscussPostRepository discussPostRepository;


    @Autowired
    private ElasticsearchRestTemplate elasticsearchRestTemplate;


    @Test
    public void testInsert() {
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(241));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(242));
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(243));
    }

    @Test
    public void testUpdate() {
        discussPostRepository.save(discussPostMapper.selectDiscussPostById(231));
        DiscussPost post = discussPostMapper.selectDiscussPostById(231);
        post.setContent("我是新人，使劲灌水");
        discussPostRepository.save(post);
    }

    /**
     * 一次性插入多个
     */
    @Test
    public void testInsertList() {
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(101, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(102, 0, 100));
        discussPostRepository.saveAll(discussPostMapper.selectDiscussPosts(103, 0, 100));
    }

    @Test
    public void testDelete() {
        discussPostRepository.deleteById(231);
        // discussPostRepository.deleteAll();
    }

    @Test
    public void testSearchByRepository() {
        Pageable pageable = PageRequest.of(0, 10);
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.multiMatchQuery("互联网寒冬", "title", "content"))
                .withSort(SortBuilders.fieldSort("type").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("score").order(SortOrder.DESC))
                .withSort(SortBuilders.fieldSort("createTime").order(SortOrder.DESC))
                .withPageable(pageable)
                .withHighlightFields(
                        new HighlightBuilder.Field("title").preTags("<em>").postTags("</em>"),
                        new HighlightBuilder.Field("content").preTags("<em>").postTags("</em>")
                ).build();
        // 底层获取了高亮显示的值，但是没有返回
        // Page<DiscussPost> page = discussPostRepository.search(searchQuery);

        SearchHits<DiscussPost> searchHits = elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
        if (searchHits.getTotalHits() <= 0) {
            return;
        }
        List<DiscussPost> list = new ArrayList<>();
        for (SearchHit<DiscussPost> hit : searchHits) {
            DiscussPost content = hit.getContent();
            DiscussPost post = new DiscussPost();
            BeanUtils.copyProperties(content, post);
            // 处理高亮
//            Map<String, List<String>> highlightFields = hit.getHighlightFields();
//            for (Map.Entry<String, List<String>> stringHighlightFieldEntry : highlightFields.entrySet()) {
//                String key = stringHighlightFieldEntry.getKey();
//                if(StringUtils.equals(key, "title")){
//                    List<String> fragments = stringHighlightFieldEntry.getValue();
//                    StringBuilder sb = new StringBuilder();
//                    for (String fragment : fragments) {
//                        sb.append(fragment);
//                    }
//                    post.setTitle(sb.toString());
//                }
//                if(StringUtils.equals(key, "content")){
//                    List<String> fragments = stringHighlightFieldEntry.getValue();
//                    StringBuilder sb = new StringBuilder();
//                    for (String fragment : fragments) {
//                        sb.append(fragment);
//                    }
//                    post.setContent(sb.toString());
//                }
//            }
            // 处理高亮
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
//        List<DiscussPost> searchDiscussPost = searchHits.stream().map(SearchHit::getContent).collect(Collectors.toList());
        Page<DiscussPost> page = new PageImpl<>(list, pageable, searchHits.getTotalHits());

        System.out.println(page.getTotalElements());
        System.out.println(page.getTotalPages());
        System.out.println(page.getNumber());
        System.out.println(page.getSize());
        for (DiscussPost post : page) {
            System.out.println(post);
        }
    }


    @Test
    public void test() {
        search("互联网", 1);
    }

    private static final Integer ROWS = 10;

    public void search(String keyWord, Integer page) {
        List<DiscussPost> list = new ArrayList();
        Pageable pageable = PageRequest.of(page - 1, ROWS); // 设置分页参数
        NativeSearchQuery searchQuery = new NativeSearchQueryBuilder()
                .withQuery(QueryBuilders.matchQuery("title", keyWord).operator(Operator.AND)) // match查询
                .withPageable(pageable).withHighlightBuilder(getHighlightBuilder("title")) // 设置高亮
                .build();
        SearchHits<DiscussPost> searchHits = this.elasticsearchRestTemplate.search(searchQuery, DiscussPost.class);
        for (SearchHit<DiscussPost> searchHit : searchHits) { // 获取搜索到的数据

            DiscussPost content = searchHit.getContent();
            DiscussPost discussPost = new DiscussPost();
            BeanUtils.copyProperties(content, discussPost);

            // 处理高亮
            Map<String, List<String>> highlightFields = searchHit.getHighlightFields();
            for (Map.Entry<String, List<String>> stringHighlightFieldEntry : highlightFields.entrySet()) {
                String key = stringHighlightFieldEntry.getKey();
                if (StringUtils.equals(key, "title")) {
                    List<String> fragments = stringHighlightFieldEntry.getValue();
                    StringBuilder sb = new StringBuilder();
                    for (String fragment : fragments) {
                        sb.append(fragment);
                    }
                    discussPost.setTitle(sb.toString());
                }

            }
            list.add(discussPost);
        }
        Page<DiscussPost> all = new PageImpl<>(list, pageable, searchHits.getTotalHits());
        System.out.println(all.getTotalElements());
        System.out.println(all.getTotalPages());
        System.out.println(all.getNumber());
        System.out.println(all.getSize());
        for (DiscussPost post : all) {
            System.out.println(post);
        }
    }

    // 设置高亮字段
    private HighlightBuilder getHighlightBuilder(String... fields) {
        // 高亮条件
        HighlightBuilder highlightBuilder = new HighlightBuilder(); // 生成高亮查询器
        for (String field : fields) {
            highlightBuilder.field(field);// 高亮查询字段
        }
        highlightBuilder.requireFieldMatch(false); // 如果要多个字段高亮,这项要为false
        highlightBuilder.preTags("<span style=\"color:red\">"); // 高亮设置
        highlightBuilder.postTags("</span>");
        // 下面这两项,如果你要高亮如文字内容等有很多字的字段,必须配置,不然会导致高亮不全,文章内容缺失等
        highlightBuilder.fragmentSize(800000); // 最大高亮分片数
        highlightBuilder.numOfFragments(0); // 从第一个分片获取高亮片段

        return highlightBuilder;
    }


}
