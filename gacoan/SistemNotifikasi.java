package gacoan;

import java.awt.Toolkit;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;

public class SistemNotifikasi {

    public static void panggilAntrean(int nomorMeja) {
        File audioFile = new File("audio/meja_" + nomorMeja + ".wav");
        if (audioFile.isFile() && playAudio(audioFile)) {
            return;
        }

        Toolkit.getDefaultToolkit().beep();
        System.out.println("[Java Notification] Pesanan untuk meja nomor " + nomorMeja + ", silakan ambil.");
    }

    private static boolean playAudio(File audioFile) {
        CountDownLatch done = new CountDownLatch(1);

        try (AudioInputStream stream = AudioSystem.getAudioInputStream(audioFile);
             Clip clip = AudioSystem.getClip()) {

            clip.addLineListener(event -> {
                if (event.getType() == LineEvent.Type.STOP || event.getType() == LineEvent.Type.CLOSE) {
                    done.countDown();
                }
            });

            clip.open(stream);
            setVolumeMax(clip);
            clip.start();

            long timeoutMs = Math.max(2000, clip.getMicrosecondLength() / 1000 + 1000);
            if (!done.await(timeoutMs, TimeUnit.MILLISECONDS)) {
                clip.stop();
            }
            return true;
        } catch (Exception e) {
            System.err.println("[Java Notification] Failed to play audio: " + e.getMessage());
            return false;
        }
    }

    private static void setVolumeMax(Clip clip) {
        if (!clip.isControlSupported(FloatControl.Type.MASTER_GAIN)) {
            return;
        }

        FloatControl gain = (FloatControl) clip.getControl(FloatControl.Type.MASTER_GAIN);
        gain.setValue(Math.min(0.0f, gain.getMaximum()));
    }
}
