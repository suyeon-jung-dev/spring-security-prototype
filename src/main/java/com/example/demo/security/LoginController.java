package com.example.demo.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class LoginController {

    /**
     * Principal 객체는 해당 시스템을 사용하고자 하는 사용자나 디바이스를 말한다.
     * (= Request 에 대한 주체, 계정 주)
     *
     * 여기서는 Principal 그대로 파라미터로 받지만, 나중에 토크나이즈 해주면서 공통작업으로 빼줘야 한다.
     * 참고. https://devidea.tistory.com/16
     */

    @GetMapping(value = {"/", "/index"})
    public String index() {
        return "index";
    }

    @GetMapping("/home")
    public String home(Principal principal, Model model) {
        model.addAttribute("user", principal.getName());
        model.addAttribute("roles", ((UsernamePasswordAuthenticationToken) principal).getAuthorities());

        return "/home";
    }

    @GetMapping("/admin")
    public String admin(Principal principal, Model model) {
        model.addAttribute("user", principal.getName());
        model.addAttribute("roles", ((UsernamePasswordAuthenticationToken) principal).getAuthorities());

        return "/admin";
    }

    @GetMapping("/login")
    public String login(Principal principal) {
        return "/login";
    }

    @GetMapping("/403")
    public String forbidden() {
        return "/403";
    }
}
