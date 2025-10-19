package com.rutear.demo.controller;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;


@RestController
public class Ping{

    @GetMapping("/ping")
    
    public String ping(){
        return "pong";
    }


}
