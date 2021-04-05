package tech.turl.community.controller;

import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.util.HtmlUtils;
import tech.turl.community.entity.Message;
import tech.turl.community.entity.Page;
import tech.turl.community.entity.User;
import tech.turl.community.service.MessageService;
import tech.turl.community.service.UserService;
import tech.turl.community.util.CommunityConstant;
import tech.turl.community.util.CommunityUtil;
import tech.turl.community.util.HostHolder;

import java.util.*;

/**
 * @author zhengguohuang
 * @date 2021/03/19
 */
@Controller
public class MessageController implements CommunityConstant {
    @Autowired private MessageService messageService;

    @Autowired private HostHolder hostHolder;

    @Autowired private UserService userService;

    @GetMapping("/letter/list")
    public String getLetterList(Model model, Page page) {

        User user = hostHolder.getUser();
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/list");
        page.setRows(messageService.findConversationCount(user.getId()));
        // 会话列表
        List<Message> conversationList =
                messageService.findConversations(user.getId(), page.getOffset(), page.getLimit());
        List<Map<String, Object>> conversations = new ArrayList<>();
        if (conversationList != null) {
            for (Message message : conversationList) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("conversation", message);
                map.put("letterCount", messageService.findLetterCount(message.getConversationId()));
                map.put(
                        "unreadCount",
                        messageService.findLetterUnreadCount(
                                user.getId(), message.getConversationId()));
                int targetId =
                        user.getId() == message.getFromId()
                                ? message.getToId()
                                : message.getFromId();
                map.put("target", userService.findUserById(targetId));
                conversations.add(map);
            }
        }
        model.addAttribute("conversations", conversations);
        // 查询未读消息数量
        int letterUnreadCount = messageService.findLetterUnreadCount(user.getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount =
                messageService.findNoticeUnreadCount(hostHolder.getUser().getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);
        return "site/letter";
    }

    @GetMapping("/letter/detail/{conversationId}")
    public String getLetterDetail(
            @PathVariable("conversationId") String conversationId, Page page, Model model) {
        // 分页信息
        page.setLimit(5);
        page.setPath("/letter/detail/" + conversationId);
        page.setRows(messageService.findLetterCount(conversationId));

        // 私信列表
        List<Message> letterList =
                messageService.findLetters(conversationId, page.getOffset(), page.getLimit());
        List<Map<String, Object>> letters = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                Map<String, Object> map = new HashMap<>(16);
                map.put("letter", message);
                map.put("fromUser", userService.findUserById(message.getFromId()));
                letters.add(map);
            }
        }
        model.addAttribute("letters", letters);
        model.addAttribute("letters", letters);
        // 私信目标
        model.addAttribute("target", getLetterTarget(conversationId));

        // 设置已读
        List<Integer> ids = getLetterIds(letterList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }

        return "site/letter-detail";
    }

    /**
     * 获取所有未读消息id
     *
     * @param letterList
     * @return
     */
    private List<Integer> getLetterIds(List<Message> letterList) {
        List<Integer> ids = new ArrayList<>();
        if (letterList != null) {
            for (Message message : letterList) {
                if (hostHolder.getUser().getId() == message.getToId() && message.getStatus() == 0) {
                    ids.add(message.getId());
                }
            }
        }
        return ids;
    }

    private User getLetterTarget(String conversationId) {
        String[] ids = conversationId.split("_");
        int id0 = Integer.parseInt(ids[0]);
        int id1 = Integer.parseInt(ids[1]);
        if (hostHolder.getUser().getId() == id0) {
            return userService.findUserById(id1);
        } else {
            return userService.findUserById(id0);
        }
    }

    @PostMapping("/letter/send")
    @ResponseBody
    public String sendLetter(String toName, String content) {
        User target = userService.findUserByName(toName);
        if (target == null) {
            return CommunityUtil.getJSONString(1, "目标用户不存在");
        }
        Message message = new Message();
        message.setFromId(hostHolder.getUser().getId());
        message.setToId(target.getId());
        message.setConversationId(
                message.getFromId() < message.getToId()
                        ? message.getFromId() + "_" + message.getToId()
                        : message.getToId() + "_" + message.getFromId());
        message.setContent(content);
        message.setCreateTime(new Date());
        messageService.addMessage(message);
        return CommunityUtil.getJSONString(0);
    }

    /**
     * 系统通知列表
     *
     * @param model
     * @return
     */
    @GetMapping("/notice/list")
    public String getNoticeList(Model model) {
        // 查询评论类通知
        Map<String, Object> messageVO = getNoticeVO(TOPIC_COMMENT);
        model.addAttribute("commentNotice", messageVO);

        // 查询点赞类通知
        messageVO = getNoticeVO(TOPIC_LIKE);
        model.addAttribute("likeNotice", messageVO);

        // 查询关注类通知
        messageVO = getNoticeVO(TOPIC_FOLLOW);
        model.addAttribute("followNotice", messageVO);

        // 查询未读消息数量
        int letterUnreadCount =
                messageService.findLetterUnreadCount(hostHolder.getUser().getId(), null);
        model.addAttribute("letterUnreadCount", letterUnreadCount);
        int noticeUnreadCount =
                messageService.findNoticeUnreadCount(hostHolder.getUser().getId(), null);
        model.addAttribute("noticeUnreadCount", noticeUnreadCount);

        int noticeCount = messageService.findNoticeCount(hostHolder.getUser().getId(), null);
        model.addAttribute("noticeCount", noticeCount);

        return "site/notice";
    }

    private Map<String, Object> getNoticeVO(String topicType) {
        User user = hostHolder.getUser();
        Message message = messageService.findLatestNotice(user.getId(), topicType);
        Map<String, Object> messageVO = new HashMap<>(16);
        if (message != null) {
            messageVO.put("message", message);
            String content = HtmlUtils.htmlUnescape(message.getContent());
            Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
            messageVO.put("user", userService.findUserById((Integer) data.get("userId")));
            messageVO.put("entityType", data.get("entityType"));
            messageVO.put("entityId", data.get("entityId"));
            if (!TOPIC_FOLLOW.equals(topicType)) {
                messageVO.put("postId", data.get("postId"));
            }

            int count = messageService.findNoticeCount(user.getId(), topicType);
            messageVO.put("count", count);

            int unread = messageService.findNoticeUnreadCount(user.getId(), topicType);
            messageVO.put("unread", unread);
        }
        return messageVO;
    }

    /**
     * 查看系统通知详情
     *
     * @param topic
     * @param page
     * @param model
     * @return
     */
    @GetMapping("/notice/detail/{topic}")
    public String getNoticeDetail(@PathVariable("topic") String topic, Page page, Model model) {
        User user = hostHolder.getUser();
        page.setLimit(5);
        page.setPath("/notice/detail/" + topic);
        page.setRows(messageService.findNoticeCount(user.getId(), topic));
        List<Message> noticeList =
                messageService.findNotices(user.getId(), topic, page.getOffset(), page.getLimit());
        List<Map<String, Object>> noticeVolist = new ArrayList<>();
        if (noticeList != null) {
            for (Message notice : noticeList) {
                Map<String, Object> map = new HashMap<>(16);
                // 通知
                map.put("notice", notice);
                // 内容
                String content = HtmlUtils.htmlUnescape(notice.getContent());
                Map<String, Object> data = JSONObject.parseObject(content, HashMap.class);
                map.put("user", userService.findUserById((Integer) data.get("userId")));
                map.put("entityType", data.get("entityType"));
                map.put("entityId", data.get("entityId"));
                map.put("postId", data.get("postId"));
                // 通知作者
                map.put("fromUser", userService.findUserById(notice.getFromId()));
                noticeVolist.add(map);
            }
        }
        model.addAttribute("notices", noticeVolist);
        // 设置已读
        List<Integer> ids = getLetterIds(noticeList);
        if (!ids.isEmpty()) {
            messageService.readMessage(ids);
        }
        return "site/notice-detail";
    }
}
