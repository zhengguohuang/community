package tech.turl.community.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tech.turl.community.entity.DiscussPost;

import java.util.List;

/**
 * @author zhengguohuang
 * @date 2021/03/17
 */
@Mapper
public interface DiscussPostMapper {
    /**
     * 分页查询帖子
     *
     * @param userId
     * @param offset
     * @param limit
     * @return
     */
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    /**
     * <p>@Param注解用于给定参数取别名</p>
     * 如果只有一个参数，并且在if里面使用，则必须加别名
     *
     * @param userId
     * @return
     */
    int selectDiscussPostRows (@Param("userId") int userId);

    /**
     * 插入一条帖子
     *
     * @param discussPost
     * @return
     */
    int insertDiscussPost(DiscussPost discussPost);

    /**
     * 通过id查找帖子
     *
     * @param id
     * @return
     */
    DiscussPost selectDiscussPostById(int id);
}
