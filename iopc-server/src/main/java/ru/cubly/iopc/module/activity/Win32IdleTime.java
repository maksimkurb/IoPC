package ru.cubly.iopc.module.activity;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import com.sun.jna.win32.StdCallLibrary;

/**
 * Utility method to retrieve the idle time on Windows and sample code to test it.
 * JNA shall be present in your classpath for this to work (and compile).
 *
 * @author ochafik
 * @see <a href="https://ochafik.com/p_98">Original post</a>
 */
public class Win32IdleTime {
    private static Kernel32 kernel32 = null;
    private static User32 user32 = null;

    public static void init() {
        if (kernel32 == null)
            kernel32 = Native.load("kernel32", Kernel32.class);
        if (user32 == null)
            user32 = Native.load("user32", User32.class);
    }

    public interface Kernel32 extends StdCallLibrary {
        /**
         * Retrieves the number of milliseconds that have elapsed since the system was started.
         *
         * @return number of milliseconds that have elapsed since the system was started.
         * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/sysinfoapi/nf-sysinfoapi-gettickcount>nf-sysinfoapi-gettickcount</a>
         */
        int GetTickCount();
    }

    public interface User32 extends StdCallLibrary {
        /**
         * Contains the time of the last input.
         *
         * @see <a href="http://msdn.microsoft.com/library/default.asp?url=/library/en-us/winui/winui/windowsuserinterface/userinput/keyboardinput/keyboardinputreference/keyboardinputstructures/lastinputinfo.asp>lastinputinfo</a>
         */
        @Structure.FieldOrder({"cbSize", "dwTime"})
        class LASTINPUTINFO extends Structure {
            public int cbSize = 8;
            /// Tick count of when the last input event was received.
            public int dwTime;
        }

        /**
         * Retrieves the time of the last input event.
         *
         * @return time of the last input event, in milliseconds
         * @see <a href="http://msdn.microsoft.com/library/default.asp?url=/library/en-us/winui/winui/windowsuserinterface/userinput/keyboardinput/keyboardinputreference/keyboardinputfunctions/getlastinputinfo.asp>getlastinputinfo</a>
         */
        boolean GetLastInputInfo(LASTINPUTINFO result);
    }

    /**
     * Get the amount of milliseconds that have elapsed since the last input event
     * (mouse or keyboard)
     *
     * @return idle time in milliseconds
     */
    public static int getIdleTimeMillisWin32() {
        if (user32 == null || kernel32 == null) {
            throw new NullPointerException("You must call Win32IdleTime.init() before calling other static methods in this class");
        }

        User32.LASTINPUTINFO lastInputInfo = new User32.LASTINPUTINFO();
        user32.GetLastInputInfo(lastInputInfo);
        return kernel32.GetTickCount() - lastInputInfo.dwTime;
    }
}

