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
    public interface Kernel32 extends StdCallLibrary {
        Kernel32 INSTANCE = Native.load("kernel32", Kernel32.class);

        /**
         * Retrieves the number of milliseconds that have elapsed since the system was started.
         *
         * @return number of milliseconds that have elapsed since the system was started.
         * @see <a href="https://docs.microsoft.com/en-us/windows/win32/api/sysinfoapi/nf-sysinfoapi-gettickcount>nf-sysinfoapi-gettickcount</a>
         */
        int GetTickCount();
    }

    public interface User32 extends StdCallLibrary {
        User32 INSTANCE = Native.load("user32", User32.class);

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
        User32.LASTINPUTINFO lastInputInfo = new User32.LASTINPUTINFO();
        User32.INSTANCE.GetLastInputInfo(lastInputInfo);
        return Kernel32.INSTANCE.GetTickCount() - lastInputInfo.dwTime;
    }
}

