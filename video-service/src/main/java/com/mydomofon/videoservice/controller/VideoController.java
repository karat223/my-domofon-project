package com.mydomofon.videoservice.controller;

import com.mydomofon.videoservice.service.VideoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final VideoService videoService;

    @Autowired
    public VideoController(VideoService videoService) {
        this.videoService = videoService;
    }

    @GetMapping(value = "/snapshot", produces = MediaType.IMAGE_PNG_VALUE)
    public ResponseEntity<byte[]> getWebcamSnapshot() {
        return ResponseEntity.ok(videoService.getWebcamImageAsBytes());
    }

    // --- НОВЫЕ ЭНДПОИНТЫ ---
    @PostMapping("/record/start")
    public ResponseEntity<String> startRecording() {
        try {
            // Файл будет сохраняться в корневую папку проекта video-service
            String filename = "archive-" + System.currentTimeMillis() + ".mp4";
            videoService.startRecording(filename);
            return ResponseEntity.ok("Recording started. Saving to " + filename);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("Failed to start recording: " + e.getMessage());
        }
    }

    @PostMapping("/record/stop")
    public ResponseEntity<String> stopRecording() {
        try {
            videoService.stopRecording();
            return ResponseEntity.ok("Recording stopped.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Failed to stop recording: " + e.getMessage());
        }
    }
}