package tech.turl.community.entity;

import java.util.HashMap;
import java.util.Map;

/**
 * 用于封装消息的事件对象
 *
 * @author zhengguohuang
 * @date 2021/03/23
 */
public class Event {
    private String topic;
    /**
     * 触发事件的用户
     */
    private int userId;
    /**
     * 目标实体
     */
    private int entityType;
    private int entityId;
    /**
     * 目标实体的拥有者，如果目标实体是用户则entityId=entityUserId
     */
    private int entityUserId;
    /**
     * 封装其他数据，用于扩展
     */
    private Map<String, Object> data = new HashMap<>(16);

    public String getTopic() {
        return topic;
    }

    public Event setTopic(String topic) {
        this.topic = topic;
        return this;
    }

    public int getUserId() {
        return userId;
    }

    public Event setUserId(int userId) {
        this.userId = userId;
        return this;
    }

    public int getEntityType() {
        return entityType;
    }

    public Event setEntityType(int entityType) {
        this.entityType = entityType;
        return this;
    }

    public int getEntityId() {
        return entityId;
    }

    public Event setEntityId(int entityId) {
        this.entityId = entityId;
        return this;
    }

    public int getEntityUserId() {
        return entityUserId;
    }

    public Event setEntityUserId(int entityUserId) {
        this.entityUserId = entityUserId;
        return this;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public Event setData(String key, Object value) {
        this.data.put(key, value);
        return this;
    }
}
