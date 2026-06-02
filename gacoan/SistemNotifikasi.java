package gacoan;

public class SistemNotifikasi {
    static {
        GacoanEngine.loadLibrary();
    }

    // Native method: calls out the table number using Windows SAPI TTS
    public static native void panggilAntrean(int nomorMeja);
}
