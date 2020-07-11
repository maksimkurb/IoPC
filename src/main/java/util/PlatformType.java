package util;

public enum PlatformType {
    Windows("windows"),
    MacOS("macos"),
    Linux("linux");

    PlatformType(String platform){
        this.platform=platform;
    }
    protected String platform;

    public String getPlatform() {
        return platform;
    }
}
