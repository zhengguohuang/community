package tech.turl.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import tech.turl.community.entity.Event;
import tech.turl.community.entity.User;
import tech.turl.community.event.EventProducer;
import tech.turl.community.service.LikeService;
import tech.turl.community.util.CommunityConstant;
import tech.turl.community.util.CommunityUtil;
import tech.turl.community.util.HostHolder;
import tech.turl.community.util.RedisKeyUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zhengguohuang
 * @date 2021/03/21
 */
@Controller
public class LikeController implements CommunityConstant {
    @Autowired private HostHolder hostHolder;

    @Autowired private LikeService likeService;

    @Autowired private EventProducer eventProducer;
    @Autowired private RedisTemplate redisTemplate;

    /**
     * 点赞
     *
     * @param entityType
     * @param entityId
     * @return
     */
    @PostMapping("/like")
    @ResponseBody
    public String like(int entityType, int entityId, int entityUserId, int postId) {
        User user = hostHolder.getUser();
        // 点赞
        likeService.like(user.getId(), entityType, entityId, entityUserId);
        // 数量
        long likeCount = likeService.findEntityLikeCount(entityType, entityId);
        // 状态
        int likeStatus = likeService.findEntityLikeStatus(user.getId(), entityType, entityId);
        // 返回的结果
        Map<String, Object> map = new HashMap<>(16);
        map.put("likeCount", likeCount);
        map.put("likeStatus", likeStatus);
        // 触发事件
        if (likeStatus == 1) {
            Event event =
                    new Event()
                            .setTopic(TOPIC_LIKE)
                            .setUserId(user.getId())
                            .setEntityType(entityType)
                            .setEntityId(entityId)
                            .setEntityUserId(entityUserId)
                            .setData("postId", postId);
            eventProducer.fireEvent(event);
        }
        if (entityType == ENTITY_TYPE_POST) {
            // 计算帖子的分数
            String redisKey = RedisKeyUtil.getPostScoreKey();
            redisTemplate.opsForSet().add(redisKey, postId);
        }
        return CommunityUtil.getJSONString(0, null, map);
    }
}
