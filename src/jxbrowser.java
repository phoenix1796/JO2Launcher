import javax.swing.*;
import java.awt.*;
import com.teamdev.jxbrowser.chromium.Browser;
import com.teamdev.jxbrowser.chromium.swing.BrowserView;
/**
 * This sample demonstrates how to create Browser instance,
 * embed it into Swing BrowserView container, display it in JFrame and
 * navigate to the "www.google.com" web site.
 */
public class jxbrowser implements Runnable{
    public static Thread b;
   public static Thread b_check;
    public void run() {
        Browser browser = new Browser();
        BrowserView browserView;
        browserView = new BrowserView(browser);

        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.add(browserView, BorderLayout.CENTER);
        frame.setSize(700, 500);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        browser.loadURL("http://www.google.com");
    }
    public void start ()
                {
                   System.out.println("Starting " +  "browser" );


                      b = new Thread (this, "browser");
                      b_check = b;
                      b.start();

                }
}