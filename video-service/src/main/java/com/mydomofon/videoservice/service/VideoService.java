package com.mydomofon.videoservice.service;

import com.github.sarxos.webcam.Webcam;
import lombok.extern.slf4j.Slf4j;
import org.bytedeco.javacv.FFmpegFrameRecorder;
import org.bytedeco.javacv.Java2DFrameConverter;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

@Service
@Slf4j
public class VideoService {

    private volatile FFmpegFrameRecorder recorder;
    private volatile boolean isRecording = false;
    private final Object lock = new Object(); // Объект для синхронизации

    @Cacheable("webcamImage")
    public byte[] getWebcamImageAsBytes() {
        log.info("--- Capturing image from webcam (SLOW) ---");
        Webcam webcam = Webcam.getDefault();
        if (webcam == null) {
            throw new RuntimeException("No webcam found");
        }
        try {
            webcam.open();
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(webcam.getImage(), "PNG", baos);
            return baos.toByteArray();
        } catch (IOException e) {
            throw new RuntimeException("Failed to capture image", e);
        } finally {
            if (webcam.isOpen()) {
                webcam.close();
            }
        }
    }

    // Метод для начала записи
    public void startRecording(String filename) throws FFmpegFrameRecorder.Exception {
        synchronized (lock) {
            if (isRecording) {
                throw new IllegalStateException("Recording is already in progress.");
            }

            Webcam webcam = Webcam.getDefault();
            if (webcam == null) throw new RuntimeException("No webcam found");
            webcam.open();

            recorder = new FFmpegFrameRecorder(filename, webcam.getViewSize().width, webcam.getViewSize().height, 0);
            recorder.setVideoCodecName("libx264");
            recorder.setFormat("mp4");
            recorder.setFrameRate(24);
            recorder.setPixelFormat(0); // PIX_FMT_YUV420P
            recorder.start();

            isRecording = true;
            log.info("--- Recording started, saving to {} ---", filename);

            // Запускаем процесс захвата кадров в отдельном потоке
            new Thread(() -> {
                Java2DFrameConverter converter = new Java2DFrameConverter();
                while (isRecording) {
                    try {
                        recorder.record(converter.convert(webcam.getImage()));
                        Thread.sleep((long) (1000 / recorder.getFrameRate()));
                    } catch (Exception e) {
                        e.printStackTrace();
                        break;
                    }
                }
                // Останавливаем и освобождаем ресурсы
                try {
                    recorder.stop();
                    recorder.release();
                } catch (FFmpegFrameRecorder.Exception e) {
                    e.printStackTrace();
                }
                webcam.close();
                log.info("--- Recording stopped and file saved ---");
            }).start();
        }
    }

    // Метод для остановки записи
    public void stopRecording() {
        synchronized (lock) {
            if (!isRecording) {
                throw new IllegalStateException("Recording is not in progress.");
            }
            isRecording = false;
        }
    }
}