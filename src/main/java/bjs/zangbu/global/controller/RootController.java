package bjs.zangbu.global.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootController {

  @GetMapping(value = "/", produces = "text/plain;charset=UTF-8")
  public String root() {
    return "API 서버에 오신 것을 환영합니다.";
  }
}

