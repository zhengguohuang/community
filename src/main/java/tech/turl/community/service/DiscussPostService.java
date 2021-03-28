package tech.turl.community.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.util.HtmlUtils;
import tech.turl.community.dao.DiscussPostMapper;
import tech.turl.community.entity.DiscussPost;
import tech.turl.community.util.SensitiveFilter;

import java.util.List;

/**
 * @author zhengguohuang
 * @date 2021/03/17
 */
@Service
public class DiscussPostService {
    @Autowired
    private DiscussPostMapper discussPostMapper;

    @Autowired
    private SensitiveFilter sensitiveFilter;

    public List<DiscussPost> findDiscussPosts(int userId, int offset, int limit){
        return discussPostMapper.selectDiscussPosts(userId, offset, limit);
    }

    public int findDiscussPostRows(int userId){
        return discussPostMapper.selectDiscussPostRows(userId);
    }

    /**
     * 添加帖子
     * 转义html标记
     * 过滤敏感词
     *
     * @param post
     * @return
     */
    public int addDiscussPost(DiscussPost post) {
        if (post == null) {
            throw new IllegalArgumentException("参数不能为空");
        }
        // 转义HTML标记
        post.setTitle(HtmlUtils.htmlEscape(post.getTitle()));
        post.setContent(HtmlUtils.htmlEscape(post.getContent()));

        // 过滤敏感词
        post.setTitle(sensitiveFilter.filter(post.getTitle()));
        post.setContent(sensitiveFilter.filter(post.getContent()));

        return discussPostMapper.insertDiscussPost(post);
    }

    /**
     * 通过id查询一个帖子
     */
    public DiscussPost findDiscussPostById(int id) {
        return discussPostMapper.selectDiscussPostById(id);
    }

    /**
     * 更新评论数量
     *
     * @param id
     * @param commentCount
     * @return
     */
    public int updateCommentCount(int id, int commentCount) {
        return discussPostMapper.updateCommentCount(id, commentCount);
    }

    /**
     * 更新帖子类型
     *
     * @param id
     * @param type
     * @return
     */
    public int updateType(int id, int type) {
        return discussPostMapper.updateType(id, type);
    }

    /**
     * 更新帖子状态
     *
     * @param id
     * @param status
     * @return
     */
    public int updateStatus(int id, int status) {
        return discussPostMapper.updateStatus(id, status);
    }
}
