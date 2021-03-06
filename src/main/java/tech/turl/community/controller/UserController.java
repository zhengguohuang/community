package tech.turl.community.controller;

import com.qiniu.util.Auth;
import com.qiniu.util.StringMap;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import tech.turl.community.annotation.LoginRequired;
import tech.turl.community.entity.User;
import tech.turl.community.service.FollowService;
import tech.turl.community.service.LikeService;
import tech.turl.community.service.UserService;
import tech.turl.community.util.CommunityConstant;
import tech.turl.community.util.CommunityUtil;
import tech.turl.community.util.HostHolder;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;

/**
 * @author zhengguohuang
 * @date 2021/03/16
 */
@Controller
@RequestMapping("/user")
public class UserController implements CommunityConstant {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserController.class);

    @Value("${community.path.upload}")
    private String uploadPath;

    @Value("${community.path.domain}")
    private String domain;

    @Value("${server.servlet.context-path}")
    private String contextPath;

    @Autowired private UserService userService;

    @Autowired private HostHolder hostHolder;

    @Autowired private LikeService likeService;

    @Autowired private FollowService followService;

    @Value("${qiniu.key.access}")
    private String accessKey;

    @Value("${qiniu.key.secret}")
    private String secretKey;

    @Value("${qiniu.bucket.header.name}")
    private String headerBucketName;

    @Value("${qiniu.bucket.header.url}")
    private String headerBucketUrl;

    /**
     * ??????????????????
     *
     * @return
     */
    @LoginRequired
    @GetMapping("/setting")
    public String getSettingPage(Model model) {
        // ??????????????????
        String fileName = CommunityUtil.generateUUID();
        // ??????????????????
        StringMap policy = new StringMap();
        policy.put("returnBody", CommunityUtil.getJSONString(0));
        // ??????????????????
        Auth auth = Auth.create(accessKey, secretKey);
        String uploadToken = auth.uploadToken(headerBucketName, fileName, 3600, policy);
        model.addAttribute("uploadToken", uploadToken);
        model.addAttribute("fileName", fileName);

        return "site/setting";
    }

    /**
     * ?????? ????????????????????????
     *
     * @param headerImage
     * @param model
     * @return
     */
    @LoginRequired
    @PostMapping("/upload")
    public String uploadHeader(MultipartFile headerImage, Model model) {
        if (headerImage == null) {
            model.addAttribute("error", "???????????????????????????");
            return "site/setting";
        }
        String fileName = headerImage.getOriginalFilename();
        String suffix = fileName.substring(fileName.lastIndexOf("."));

        if (StringUtils.isBlank(suffix)) {
            model.addAttribute("error", "????????????????????????");
            return "site/setting";
        }

        // ?????????????????????
        fileName = CommunityUtil.generateUUID() + suffix;
        // ???????????????????????????
        File dest = new File(uploadPath + "/" + fileName);
        try {
            // ????????????
            headerImage.transferTo(dest);
        } catch (IOException e) {
            LOGGER.error("?????????????????????", e.getMessage());
            throw new RuntimeException("?????????????????????????????????????????????", e);
        }

        // ?????????????????????????????????(web????????????)
        // http://localhost:8080/user/header/xxx.png
        User user = hostHolder.getUser();
        String headerUrl = domain + contextPath + "/user/header/" + fileName;
        userService.updateHeader(user.getId(), headerUrl);
        return "redirect:/";
    }

    /**
     * ?????? ??????????????????
     *
     * @param fileName
     * @param response
     */
    @GetMapping("/header/{fileName}")
    public void getHeader(@PathVariable("fileName") String fileName, HttpServletResponse response) {
        // ?????????????????????
        fileName = uploadPath + "/" + fileName;
        // ????????????
        String suffix = fileName.substring(fileName.lastIndexOf("."));
        // ????????????
        response.setContentType("image/" + suffix);

        try (FileInputStream fis = new FileInputStream(fileName);
                OutputStream os = response.getOutputStream(); ) {
            byte[] buffer = new byte[1024];
            int b = 0;
            while ((b = fis.read(buffer)) != -1) {
                os.write(buffer, 0, b);
            }
        } catch (IOException e) {
            LOGGER.error("?????????????????????", e.getMessage());
        }
    }

    @GetMapping("/profile/{userId}")
    public String getProfilePage(@PathVariable("userId") int userId, Model model) {
        User user = userService.findUserById(userId);
        if (user == null) {
            throw new RuntimeException("??????????????????");
        }
        // ??????
        model.addAttribute("user", user);
        // ????????????
        int likeCount = likeService.findUserLikeCount(userId);
        model.addAttribute("likeCount", likeCount);

        // ????????????
        long followeeCount = followService.findFolloweeCount(userId, ENTITY_TYPE_USER);
        model.addAttribute("followeeCount", followeeCount);

        // ????????????
        long followerCount = followService.findFollowerCount(ENTITY_TYPE_USER, userId);
        model.addAttribute("followerCount", followerCount);

        // ???????????????
        boolean hasFollowed = false;
        if (hostHolder.getUser() != null) {
            hasFollowed =
                    followService.hasFollowed(
                            hostHolder.getUser().getId(), ENTITY_TYPE_USER, userId);
        }
        model.addAttribute("hasFollowed", hasFollowed);
        return "site/profile";
    }

    /**
     * ??????????????????
     *
     * @param fileName
     * @return
     */
    @PostMapping("/header/url")
    @ResponseBody
    public String updateHeaderUrl(String fileName) {
        if (StringUtils.isBlank(fileName)) {
            return CommunityUtil.getJSONString(1, "?????????????????????");
        }
        String url = headerBucketUrl + "/" + fileName;
        userService.updateHeader(hostHolder.getUser().getId(), url);
        return CommunityUtil.getJSONString(0);
    }

    /**
     * ????????????
     *
     * @param
     * @return
     */
    @PostMapping("/forgetPassword")
    @ResponseBody
    public String updatePassword(String oldPassword, String newPassword) {
        // ??????????????????????????????
        if (StringUtils.isBlank(oldPassword)) {
            return CommunityUtil.getJSONString(1, "?????????????????????");
        }
        User user = hostHolder.getUser();
        System.out.println(user);
        user = userService.findUserById(user.getId());
        System.out.println(user);
        oldPassword = CommunityUtil.md5(oldPassword + user.getSalt());
        System.out.println(oldPassword);
        System.out.println(user.getPassword());
        if (!user.getPassword().equals(oldPassword)) {
            return CommunityUtil.getJSONString(1, "??????????????????");
        }

        // ????????????????????????
        if (StringUtils.isBlank(newPassword)) {
            return CommunityUtil.getJSONString(1, "?????????????????????");
        }
        // ??????????????????????????????
        // ????????????????????????
        newPassword = CommunityUtil.md5(newPassword + user.getSalt());
        if (user.getPassword().equals(newPassword)) {
            return CommunityUtil.getJSONString(1, "????????????????????????????????????");
        }

        // ????????????
        int ret = userService.updatePassword(user.getId(), newPassword);
        if (ret == 0) {
            return CommunityUtil.getJSONString(1, "???????????????");
        }
        SecurityContextHolder.clearContext();
        // ??????????????????JSON???????????????0

        return CommunityUtil.getJSONString(0);
    }
}
