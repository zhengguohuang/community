package tech.turl.community.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import tech.turl.community.service.DataService;

import java.util.Date;

/**
 * @author zhengguohuang
 * @date 2021/03/29
 */
@RequestMapping("/data")
@Controller
public class DataContoller {
    @Autowired private DataService dataService;

    /**
     * 统计页面
     *
     * @return
     */
    @RequestMapping(method = {RequestMethod.GET, RequestMethod.POST})
    public String getDataPage() {
        return "site/admin/data";
    }

    /**
     * 统计UV
     *
     * @param start
     * @param end
     * @param model
     * @return
     */
    @PostMapping("/uv")
    public String getUV(
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
            Model model) {
        long uv = dataService.calculateUV(start, end);
        model.addAttribute("uvResult", uv);
        model.addAttribute("uvStartDate", start);
        model.addAttribute("uvEndDate", end);
        return "forward:/data";
    }

    /**
     * 统计网站活跃用户
     *
     * @param start
     * @param end
     * @param model
     * @return
     */
    @PostMapping("/dau")
    public String getDAU(
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date start,
            @DateTimeFormat(pattern = "yyyy-MM-dd") Date end,
            Model model) {
        long dau = dataService.calculateDAU(start, end);
        model.addAttribute("dauResult", dau);
        model.addAttribute("dauStartDate", start);
        model.addAttribute("dauEndDate", end);
        return "forward:/data";
    }
}
