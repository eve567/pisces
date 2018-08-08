package net.ufrog.pisces.console.controllers;

import net.ufrog.common.KeyValuePair;
import net.ufrog.common.cache.Caches;
import net.ufrog.common.dict.Dicts;
import net.ufrog.pisces.domain.models.Job;
import net.ufrog.pisces.domain.models.JobCtrl;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 索引控制器
 *
 * @author ultrafrog, ufrog.net@gmail.com
 * @version 0.1, 2017-09-06
 * @since 0.1
 */
@Controller
public class IndexController {

    private static Map<String, List<KeyValuePair<String, String>>> mKeyValuePair;
    private static Map<String, Class<?>> mClass;

    static {
        mKeyValuePair = new HashMap<>();
        mClass = new HashMap<>();

        mClass.put("job_type", Job.Type.class);
        mClass.put("job_ctrl_type", JobCtrl.Type.class);
    }

    /**
     * 索引
     *
     * @return view for index.html
     */
    @GetMapping({"", "/", "/index"})
    public String index() {
        return "index";
    }

    /**
     * 展示视图
     *
     * @param view 视图
     * @return view for ${view.replace('@')}
     */
    @GetMapping("_view/{view}")
    public String toView(@PathVariable("view") String view) {
        return view.replaceAll("@", "/");
    }

    /**
     * 字典键值对
     *
     * @param type 类型
     * @return 键值对列表
     */
    @GetMapping("/_dict/{type}")
    @ResponseBody
    public List<KeyValuePair<String, String>> getDict(@PathVariable("type") String type) {
        List<KeyValuePair<String, String>> lKeyValuePair = mKeyValuePair.get(type);
        if (lKeyValuePair == null) {
            Map<Object, Dicts.Elem> mElem = Dicts.elements(mClass.get(type));
            List<KeyValuePair<String, String>> lKVP = new ArrayList<>(mElem.size());

            mElem.forEach((key, value) -> lKVP.add(new KeyValuePair<>(key.toString(), value.getName())));
            mKeyValuePair.put(type, lKVP);
            lKeyValuePair = lKVP;
        }
        return lKeyValuePair;
    }
}
