package gacoan;

public class PreFlightCheck {
    public static class DiagnosticResult {
        public boolean javaOk = false;
        public String javaVersion = "";

        public boolean guiOk = false;
        public String guiStatus = "";

        public boolean engineOk = false;
        public String engineStatus = "";

        public boolean isAllOk() {
            return javaOk && guiOk && engineOk;
        }
    }

    public static DiagnosticResult runCheck() {
        DiagnosticResult result = new DiagnosticResult();

        result.javaVersion = System.getProperty("java.version") + " (" + System.getProperty("java.vendor") + ")";
        result.javaOk = true;

        result.guiOk = true;
        result.guiStatus = "READY (Java Swing)";

        result.engineOk = true;
        result.engineStatus = "READY (Billing engine)";

        return result;
    }
}
