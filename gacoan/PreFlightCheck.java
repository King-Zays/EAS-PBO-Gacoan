package gacoan;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

public class PreFlightCheck {
    public static class DiagnosticResult {
        public boolean javaOk = false;
        public String javaVersion = "";
        
        public boolean gccOk = false;
        public String gccVersion = "";
        
        public boolean dllOk = false;
        public String dllPath = "";
        
        public boolean isAllOk() {
            return javaOk && gccOk && dllOk;
        }
    }

    public static DiagnosticResult runCheck() {
        DiagnosticResult result = new DiagnosticResult();
        
        // 1. Java check
        result.javaVersion = System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")";
        result.javaOk = true; // Since the app is running, Java is running.

        // 2. GCC check (MinGW 64-bit compiler check)
        try {
            Process process = new ProcessBuilder("g++", "--version").start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
                String firstLine = reader.readLine();
                if (firstLine != null) {
                    result.gccOk = true;
                    result.gccVersion = firstLine;
                } else {
                    result.gccVersion = "g++ found but output is empty.";
                }
            }
            process.waitFor();
        } catch (Exception e) {
            result.gccOk = false;
            result.gccVersion = "g++ compiler not found in system PATH. MinGW-w64 is required.";
        }

        // 3. DLL Check
        // First try to check if it has already been successfully loaded by static blocks
        result.dllOk = GacoanEngine.isLibraryLoaded();
        if (result.dllOk) {
            result.dllPath = "Successfully loaded in memory!";
        } else {
            // Attempt to look for the DLL and load it manually
            File dllInBin = new File("bin/GacoanEngine.dll");
            File dllInRoot = new File("GacoanEngine.dll");
            if (dllInBin.exists()) {
                try {
                    System.load(dllInBin.getAbsolutePath());
                    result.dllOk = true;
                    result.dllPath = dllInBin.getAbsolutePath();
                } catch (UnsatisfiedLinkError e) {
                    result.dllPath = "Found at " + dllInBin.getPath() + " but load failed: " + e.getMessage();
                }
            } else if (dllInRoot.exists()) {
                try {
                    System.load(dllInRoot.getAbsolutePath());
                    result.dllOk = true;
                    result.dllPath = dllInRoot.getAbsolutePath();
                } catch (UnsatisfiedLinkError e) {
                    result.dllPath = "Found at " + dllInRoot.getPath() + " but load failed: " + e.getMessage();
                }
            } else {
                result.dllPath = "GacoanEngine.dll not found in root or bin/ directory. Please run compile.bat first.";
            }
        }

        return result;
    }
}
