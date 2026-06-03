package gacoan;

public class SistemNotifikasi {
    static {
        GacoanEngine.loadLibrary();
    }

    public static native void panggilAntrean(int nomorMeja);
}
