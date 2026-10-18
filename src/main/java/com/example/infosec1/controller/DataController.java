
package com.example.infosec1.controller;

import com.example.infosec1.repository.UserRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.HtmlUtils;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class DataController {

    private final UserRepository userRepository;

    public DataController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/data")
    public Map<String, List<String>> getData() {

        List<String> usernames = userRepository.findAll()
                .stream()
                .map(user -> HtmlUtils.htmlEscape(user.getUsername()))
                .toList();

        return Map.of("users", usernames);
    }
}
