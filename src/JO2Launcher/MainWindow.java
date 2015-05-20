/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package JO2Launcher;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import static java.nio.file.StandardCopyOption.REPLACE_EXISTING;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JLabel;

/**
 *
 * @author Abhishek
 */
public class MainWindow extends javax.swing.JFrame {

    /**
     * Creates new form MainWindow
     */
    //private static String ptl = "http://127.0.0.1/patchlist.txt";
    private static String ptl = "http://www.jutsuonline.co/patchlist.txt";
    static OsCheck.OSType ostype=OsCheck.getOperatingSystemType();
    private final static Charset ENCODING = StandardCharsets.UTF_8;
      private static void log(Object aObject){
    System.out.println(String.valueOf(aObject));
  }
    public static void fetch_patch(String addr,String patch_name,int n) throws FileNotFoundException, MalformedURLException, IOException
          {
                BufferedReader reader = null;
                java.io.BufferedWriter bout = null;
                java.io.BufferedInputStream in = null;
                java.io.FileOutputStream fos = null;
                FileWriter fileWriter = null;

              	String site=addr;
                File fl = null;
                if(n==1)
                {
                fl = new File(System.getProperty("user.dir").replace("\\", "/") +"/" + patch_name);
                //log(1);
                }
                else        //originally i had else if(n==2) here
                {
                    String s = System.getProperty("user.dir").replace("\\", "/") + "/temp/" + patch_name;
                 fl = new File(s);
                 log(s);

                }
                try {
                        URL url=new URL(site);
                        HttpURLConnection connection =(HttpURLConnection) url.openConnection();
                        int filesize = connection.getContentLength();
                        float totalDataRead;
                          totalDataRead = 0;
                                
                    in = new java.io.BufferedInputStream(connection.getInputStream());
                    fos = new java.io.FileOutputStream(fl);
                                reader = new BufferedReader(new InputStreamReader(in, ENCODING));
                                                        
                    fileWriter = new FileWriter(fl,true);
                                
                    bout = new BufferedWriter(fileWriter);
                                String contentType = connection.getHeaderField("Content-Type");
                        String line;
                        while((line = reader.readLine())!= null) {
                            bout.append(line);
                            bout.append("\r\n");
                            System.out.println(line);
                        }
            } finally {
                    reader.close();
                    bout.close();
                    in.close();
                    fileWriter.close();
                    fos.close();
                }
      }
    static Thread update_first = new Thread(new Runnable() {
     public void run() {
            List<Path> p_src = new ArrayList<Path>();
            List<Path> p_dest = new ArrayList<Path>();
            Path temp_src,temp_dest;
        try {
            Path ls_path = Paths.get("patchlist.txt");
            FileInputStream ls_file;
            ls_file = new FileInputStream(new File("patchlist.txt"));
            BufferedReader ls = new BufferedReader(new InputStreamReader(ls_file));
            String patch_url;
            String patch_md5;
            String patch_name;
            String check;
            while((check=ls.readLine()) != null)
            {
                patch_name=ls.readLine();
                patch_url=ls.readLine();
                patch_md5=ls.readLine();
                //log(patch_name);
                Downloader D1 = new Downloader(patch_url,patch_name);
                D1.start();
                D1.setVisible(true);
                Downloader.t.join();
                D1.setVisible(false);
                temp_dest=Paths.get(System.getProperty("user.dir").replace("\\", "/") + "/Data/" + patch_name);
                temp_src=Paths.get(System.getProperty("user.dir").replace("\\", "/") + "/temp/" + patch_name);
                p_src.add(temp_src);
                p_dest.add(temp_dest);
            }
            ls.close();
            ls_file.close();
            JFrame frm=new JFrame();
            JLabel lb1 = new JLabel();
            lb1.setText("Please Wait While the files are being moved to their Respective folders,Do not close the launcher unless told to do so");
            frm.add(lb1);
            frm.setVisible(true);
            frm.setSize(400, 200);
            frm.setDefaultCloseOperation(EXIT_ON_CLOSE);
            for(int i = 0;i < p_src.size();++i)
            {
                log("Moving File:"+String.valueOf(i));
                
                Files.move(p_src.get(i), p_dest.get(i), REPLACE_EXISTING);
            }
            lb1.setText("You may close the Launcher now");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
             Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
         } catch (InterruptedException ex) {
             Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
         }        
       }});
    static Thread update = new Thread(new Runnable() {

        public void run() {
            FileInputStream ls_new_file = null,ls_old_file;
            List<Path> p_src = new ArrayList<Path>();
            List<Path> p_dest = new ArrayList<Path>();
            Path temp_src,temp_dest,ls_old_path,ls_new_path;
            BufferedReader ls_new,ls_old;
            int asd=0;
            String patch_old_url,patch_old_md5,patch_old_name,patch_new_url,patch_new_md5,patch_new_name,check;
            try {
                ls_new_path = Paths.get("temp/patchlist.txt");
                ls_old_path = Paths.get("patchlist.txt");
                ls_new_file = new FileInputStream(new File("temp/patchlist.txt"));
                ls_old_file = new FileInputStream(new File("patchlist.txt"));
                ls_new = new BufferedReader(new InputStreamReader(ls_new_file));
                ls_old = new BufferedReader(new InputStreamReader(ls_old_file));
                try {
                    
                    while((check=ls_new.readLine()) != null)
                    {
                        if((check=ls_old.readLine()) != null)
                        {
                            patch_old_name=ls_old.readLine();
                            patch_old_url=ls_old.readLine();
                            patch_old_md5=ls_old.readLine();
                            patch_new_name=ls_new.readLine();
                            patch_new_url=ls_new.readLine();
                            patch_new_md5=ls_new.readLine();
                            log(patch_new_md5);
                            File f1 = new File(System.getProperty("user.dir").replace("\\", "/") + "/Data/" + patch_new_name);
                            if((patch_new_md5.equals(patch_old_md5))&&(f1.exists()))
                            {
                                log("patch already exists");
                            }
                            else
                            {   log("gonna download new patch");
                            asd=1;
                            Downloader D1 = new Downloader(patch_new_url,patch_new_name);
                            D1.start();
                            D1.setVisible(true);
                            Downloader.t.join();
                            D1.setVisible(false);
                            log("downloaded new patch");
                            temp_dest=Paths.get(System.getProperty("user.dir").replace("\\", "/") + "/Data/" + patch_new_name);
                            temp_src=Paths.get(System.getProperty("user.dir").replace("\\", "/") + "/temp/" + patch_new_name);
                            p_src.add(temp_src);
                            p_dest.add(temp_dest);
                            }
                        }
                        else
                        {
                            log("there is a new patch in the new patchlist");
                            asd=1;
                            patch_new_name=ls_new.readLine();
                            patch_new_url=ls_new.readLine();
                            patch_new_md5=ls_new.readLine();
                            log(patch_new_name);
                            Downloader D1 = new Downloader(patch_new_url,patch_new_name);
                            D1.start();
                            D1.setVisible(true);
                            Downloader.t.join();
                            D1.setVisible(false);
                            temp_dest=Paths.get(System.getProperty("user.dir").replace("\\", "/") + "/Data/" + patch_new_name);
                            temp_src=Paths.get(System.getProperty("user.dir").replace("\\", "/") + "/temp/" + patch_new_name);
                            p_src.add(temp_src);
                            p_dest.add(temp_dest);
                        }
                    }
                } catch (InterruptedException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                } finally {
                    ls_new.close();
                    ls_old.close();
                    ls_new_file.close();
                    ls_old_file.close();
                    if(asd==1)
                    {
                        JFrame frm=new JFrame();
                        JLabel lb1 = new JLabel();
                        lb1.setText("Please Wait While the files are being moved to their Respective folders,Do not close the launcher unless told to do so");
                        frm.add(lb1);
                        frm.setVisible(true);
                        frm.setSize(400, 200);
                        frm.setDefaultCloseOperation(EXIT_ON_CLOSE);
                        Files.move(ls_new_path,ls_old_path,REPLACE_EXISTING);
                        for(int i = 0;i < p_src.size();++i)
                        {
                            log("Moving File:"+String.valueOf(i));
                            Files.move(p_src.get(i), p_dest.get(i), REPLACE_EXISTING);
                        }
                        lb1.setText("You may close the Launcher now");
                    }
                }
                //delete(ls_old_path);
            } catch (FileNotFoundException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    });
    public MainWindow() {
        initComponents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        home = new java.awt.Button();
        forums = new java.awt.Button();
        play = new java.awt.Button();
        market = new java.awt.Button();
        media = new java.awt.Button();
        jLabel2 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setMinimumSize(new java.awt.Dimension(1152, 550));

        home.setBackground(new java.awt.Color(11, 56, 79));
        home.setForeground(new java.awt.Color(246, 132, 28));
        home.setLabel("JO2@Home");
        home.setPreferredSize(new java.awt.Dimension(150, 40));
        home.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                homeActionPerformed(evt);
            }
        });

        forums.setBackground(new java.awt.Color(11, 56, 79));
        forums.setForeground(new java.awt.Color(246, 132, 28));
        forums.setLabel("Forums");
        forums.setPreferredSize(new java.awt.Dimension(150, 40));
        forums.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                forumsActionPerformed(evt);
            }
        });

        play.setBackground(new java.awt.Color(11, 56, 79));
        play.setForeground(new java.awt.Color(246, 132, 28));
        play.setLabel("Start");
        play.setPreferredSize(new java.awt.Dimension(150, 40));
        play.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                playActionPerformed(evt);
            }
        });

        market.setBackground(new java.awt.Color(11, 56, 79));
        market.setForeground(new java.awt.Color(246, 132, 28));
        market.setLabel("MarketPlace");
        market.setPreferredSize(new java.awt.Dimension(150, 40));
        market.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                marketActionPerformed(evt);
            }
        });

        media.setBackground(new java.awt.Color(11, 56, 79));
        media.setForeground(new java.awt.Color(246, 132, 28));
        media.setLabel("Media");
        media.setPreferredSize(new java.awt.Dimension(150, 40));
        media.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                mediaActionPerformed(evt);
            }
        });

        jLabel2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/JO2-LOGO-1.png"))); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(home, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(forums, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(play, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(market, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGap(18, 18, 18)
                        .addComponent(media, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                .addGap(76, 76, 76))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(media, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(market, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(play, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(home, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(forums, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void homeActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_homeActionPerformed
        try {
            // TODO add your handling code here:
            URI uri = new URI("http://jutsuonline.co/");
            Desktop dt=Desktop.getDesktop();
            dt.browse(uri);
        } catch (URISyntaxException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_homeActionPerformed

    private void forumsActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_forumsActionPerformed
        try {
            // TODO add your handling code here:
            URI uri = new URI("http://jutsuonline.co/forum/");
            Desktop dt=Desktop.getDesktop();
            dt.browse(uri);
        } catch (URISyntaxException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_forumsActionPerformed

    private void marketActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_marketActionPerformed
        try {
            // TODO add your handling code here:
            URI uri = new URI("http://jutsuonline.co/marketplace");
            Desktop dt=Desktop.getDesktop();
            dt.browse(uri);
        } catch (URISyntaxException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_marketActionPerformed

    private void mediaActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_mediaActionPerformed
        try {
            // TODO add your handling code here:
            URI uri = new URI("http://jutsuonline.co/media");
            Desktop dt=Desktop.getDesktop();
            dt.browse(uri);
        } catch (URISyntaxException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }//GEN-LAST:event_mediaActionPerformed

    private void playActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_playActionPerformed
        // TODO add your handling code here:
        if(!Downloader.t_check.isAlive())
        {
            switch(ostype)
            {
                case Windows:log("Windows operating system");
            {
                try {
                   //Process p = Runtime.getRuntime().exec(System.getProperty("user.dir").replace("\\", "/") + "/JO2.exe");
                   // Runtime.getRuntime().exec(System.getProperty("user.dir").replace("\\", "/") + "/JO2.exe", null, new File(System.getProperty("user.dir").replace("\\", "/")));
                    // Runtime.getRuntime().exec(System.getProperty("user.dir") + "\\a.bat", null, new File(System.getProperty("user.dir")));
                    String s = "cmd /c start "+System.getProperty("user.dir")+"\\JO2.exe";
                    log(s);
                    Process p =Runtime.getRuntime().exec(s);
                    String a = System.getProperty("user.dir")+"/JO2.app";
                    log(a);
                    if(p.isAlive())
                        log("working");

                }
                catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    log(ex);
                }
            }
                break;
                case MacOS:log("MacOS , Awesome man i couldn't test it on one, so be careful ;D");
                String a = System.getProperty("user.dir")+"/JO2.app";
                String s = "open"+a;
            {
                try {
                    Runtime.getRuntime().exec(s);
                } catch (IOException ex) {
                    Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

                    break;
                default:System.out.println("Sorry unsupported OS");
            }
        }
    }//GEN-LAST:event_playActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(MainWindow.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        /* Create and display the form */
        switch(ostype)
        {
            case Windows:log("Windows operating system");
            break;
            case MacOS:break;
            default:System.out.println("Sorry unsupported OS");
        }
        Path path_old = Paths.get("patchlist.txt");
        Path path_new = Paths.get("temp/patchlist.txt");
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainWindow().setVisible(true);
                if(Files.exists(path_old))
                    {
                    try{
                        Files.deleteIfExists(path_new);
                        fetch_patch(ptl,"patchlist.txt",2);
                        System.out.println(System.getProperty("os.name"));
                            log("update now!!!!");
                            update.start();
                            log("joining update");
                        } catch (IOException ex) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);/*Might need to delete this*/
                    }

                        }
                        else
                        {
                             log("shimata");
                    try {
                        fetch_patch(ptl,"patchlist.txt",1);
                    } catch (MalformedURLException ex) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (IOException ex) {
                        Logger.getLogger(MainWindow.class.getName()).log(Level.SEVERE, null, ex);
                    }
                             update_first.start();
                        }
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private java.awt.Button forums;
    private java.awt.Button home;
    private javax.swing.JLabel jLabel2;
    private java.awt.Button market;
    private java.awt.Button media;
    private java.awt.Button play;
    // End of variables declaration//GEN-END:variables
}
