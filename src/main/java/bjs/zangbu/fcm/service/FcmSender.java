package bjs.zangbu.fcm.service;

import java.util.List;

public interface FcmSender {
    void send(String token, String title, String body, String url);
    void sendToMany(List<String> tokens, String title, String body, String url);
}
