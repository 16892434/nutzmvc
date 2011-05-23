package nutz.study.demo.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import nutz.study.demo.domain.Hello;

import org.nutz.json.Json;
import org.nutz.lang.Lang;
import org.nutz.lang.Strings;
import org.nutz.lang.meta.Email;
import org.nutz.mvc.Mvcs;
import org.nutz.mvc.adaptor.JsonAdaptor;
import org.nutz.mvc.annotation.AdaptBy;
import org.nutz.mvc.annotation.At;
import org.nutz.mvc.annotation.Attr;
import org.nutz.mvc.annotation.Ok;
import org.nutz.mvc.annotation.Param;

@Ok("json")
public class HelloController {

	@At
	public String say() {
		return "Hello World !";
	}
	
	@At("/tiles")
	@Ok("tiles:test")
	public String tiles(@Param("key") String key) {
		return "from tiles: Hello " + key;
	}
	
	@At("/yousay")
	public String sayMore(@Param("word") String word) {
		if(Strings.isBlank(word)) {
			return say();
		}
		return "You said: " + word;
	}
	
	@At("/time")
	public Calendar tellTime() {
		return Calendar.getInstance();
	}
	
	@At("/map")
	@Ok("json:{compact:false, quoteName:false}")
	public Map<String, Object> tellMap() {
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("anotherMap", Json.fromJson(HashMap.class, "{a:2, b:'ttt'}"));
		map.put("customized object", new Email("zhangsan@aaa.com"));
		map.put("array", Lang.array(new Email("A@B"), new Email("C@D")));
		map.put("list", Json.fromJson(ArrayList.class, "[2, 3, 'tttt']"));
		return map;
	}
	
	@At("/params")
	public String tellMore(HttpServletRequest request, ServletContext context, @Param("word") String word, HttpSession session) {
		return "You said: " 
				+ word
				+ " => "
				+ request.getLocale().toString()
				+ " :: session: "
				+ session.getId()
				+ " :: "
				+ context.getContextPath();
	}
	
	@At({"/tls", "/lots"})
	public String telllots(HttpServletRequest request) {
		return String.format("URL is '%s'", Mvcs.getRequestPath(request));
	}
	
	@At("/path/*")
	public String pathId(int id) {
		return String.format("My is id [%d]", id);
	}
	
	@At("/path2/*")
	public String pathMulti(int id, String txt, @Param("word") String word) {
		return String.format("id: %d | txt: %s | word: %s", id, txt, word);
	}
	
	@At("/auto/jump")
	@Ok("jsp")
	public String auto() {
		return "Auto jumping @ " + System.currentTimeMillis();
	}
	
	@At("/msg")
	public String getMessage(@Attr("msg") Map<String, String> msg, @Param("key") String key) {
		String s = msg.get(key);
		if(Strings.isBlank(s)) {
			return "<Unknown Key>";
		}
		return s;
	}
	
	@AdaptBy(type=JsonAdaptor.class)
	@At("/demojson")
	public String demoJson(Map<?, ?> map) {
		return String.format("Map has %d elements:\n%s", map.size(), Json.toJson(map));
	}
	
	@At("/demomap")
	public String demoMap(@Param("..") Map<?, ?> map) {
		return String.format("HTTP Map has %d elements:\n%s", map.size(), Json.toJson(map));
	}
	
	@At("/demoredirect/byid")
	@Ok("redirect:/yousay?word=${id}")
	public String demoRedirect(@Param("t") String t) {
		return "R: " + t;
	}
	
	@At("/demoredirect/byobj")
	@Ok("redirect:/yousay?word=${obj.name}")
	public Hello demoRedirectByObj(@Param("t") String t) {
		Hello re = new Hello();
		re.setName(t);
		return re;
	}
}
