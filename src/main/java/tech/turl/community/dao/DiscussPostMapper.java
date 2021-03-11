package tech.turl.community.dao;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import tech.turl.community.entity.DiscussPost;

import java.util.List;

@Mapper
public interface DiscussPostMapper {
    List<DiscussPost> selectDiscussPosts(@Param("userId") int userId, @Param("offset") int offset, @Param("limit") int limit);

    // @Param注解用于给定参数取别名，
    // 如果只有一个参数，并且在<if>里面使用，则必须加别名
    int selectDiscussPostRows (@Param("userId") int userId);
}
