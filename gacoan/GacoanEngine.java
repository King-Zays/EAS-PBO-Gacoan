package gacoan;

public class GacoanEngine {
    private static boolean libraryLoaded = false;

    static {
        loadLibrary();
    }

    public static void loadLibrary() {
        if (!libraryLoaded) {
            try {
                System.loadLibrary("GacoanEngine");
                libraryLoaded = true;
                System.out.println("[JNI] GacoanEngine.dll loaded successfully!");
            } catch (UnsatisfiedLinkError e) {
                System.err.println("[JNI] Warning: GacoanEngine.dll not loaded yet. Need compilation. Details: " + e.getMessage());
            }
        }
    }

    public static boolean isLibraryLoaded() {
        return libraryLoaded;
    }

    // Native method: calculates bill, formats ASCII receipt, and returns it as a String
    public native String hitungNota(TransaksiPesanan transaksi);
}
