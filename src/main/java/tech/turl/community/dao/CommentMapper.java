package tech.turl.community.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import tech.turl.community.entity.Comment;

import java.util.List;

/**
 * @author zhengguohuang
 * @date 2021/03/17
 */
@Mapper
public interface CommentMapper {
    /**
     * 分页查询评论
     *
     * @param
     * @return
     */
    @Select({
            "select id, user_id, entity_type, entity_id, target_id, content, status, create_time",
            "from comment",
            "where status=0 and entity_type = #{entityType} and entity_id = #{entityId}",
            "order by create_time asc",
            "limit #{offset}, #{limit}"
    })
    /**
     * 查询评论
     * @param entityType 实体类型
     * @param entityId 实体Id
     * @param limit
     * @param offset
     * @return 一个分页
     */
    List<Comment> selectCommentsByEntity(@Param("entityType") int entityType, @Param("entityId") int entityId,
                                         @Param("offset") int offset, @Param("limit") int limit);

    /**
     * 查询评论总数
     *
     * @param entityType
     * @param entityId
     * @return
     */
    @Select({
            "select count(id)",
            "from comment",
            "where status=0 and entity_type = #{entityType} and entity_id = #{entityId}"
    })
    int selectCountByEntity(@Param("entityType") int entityType, @Param("entityId") int entityId);

}
