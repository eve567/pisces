package net.ufrog.pisces.console.controllers;

import net.ufrog.common.Result;
import net.ufrog.common.app.App;
import net.ufrog.common.spring.interceptor.PropertiesInterceptor;
import net.ufrog.common.utils.Strings;
import net.ufrog.common.web.RequestParam;
import net.ufrog.pisces.domain.models.Prop;
import net.ufrog.pisces.service.PropService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

/**
 * 属性控制器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@Controller
@RequestMapping("/prop")
public class PropController {

    private static final String PROP_PREFIX = "_p_";

    /** 系统属性业务接口 */
    private final PropService propService;

    /**
     * 构造函数
     *
     * @param propService 系统属性业务接口
     */
    @Autowired
    public PropController(PropService propService) {
        this.propService = propService;
    }

    /**
     * 索引
     *
     * @return view for prop/index.html
     */
    @GetMapping({"", "/", "/index"})
    public String index() {
        return "prop/index";
    }

    /**
     * 配置视图
     *
     * @param view 视图名称
     * @return view for prop/prop_${view}.html
     */
    @GetMapping("/_{view}")
    public String propView(@PathVariable("view") String view) {
        return "prop/prop_" + view;
    }

    /**
     * 保存属性
     *
     * @return 保存结果
     */
    @PostMapping("/save")
    @ResponseBody
    public Result<List<Prop>> save() {
        List<Prop> lProp = new ArrayList<>();
        RequestParam.current().getParams().entrySet().parallelStream().filter(entry -> entry.getKey().startsWith(PROP_PREFIX)).forEach(entry -> {
            String code = entry.getKey().substring(PROP_PREFIX.length()).trim();
            String value = Strings.empty(entry.getValue()[0], null);
            lProp.add(propService.save(code, value == null ? null : value.trim()));
        });
        PropertiesInterceptor.clear();
        return Result.success(lProp, App.message("prop.save.success"));
    }
}
