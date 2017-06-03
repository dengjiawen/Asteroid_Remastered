/**
 * Copyright 2017 (C) Jiawen Deng
 * ALL RIGHTS RESERVED
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *     Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 *
 * Created on : 02-06-2017
 * Author     : Jiawen Deng
 *
 *-----------------------------------------------------------------------------
 * Revision History (Release 2.0)
 *-----------------------------------------------------------------------------
 * VERSION     AUTHOR/      DESCRIPTION OF CHANGE
 * OLD/NEW     DATE
 *-----------------------------------------------------------------------------
 * --/0.1  | J.D.          | Initial creation of program
 *         | 01-05-17      |
 *---------|---------------|---------------------------------------------------
 * 0.1/0.2 | J.D.          | Making the launcher functional
 *         | 15-05-17      | Implementing it into the actual game
 *---------|---------------|---------------------------------------------------
 * 0.2/1.0 | J.D.          | Added animation for the game studio,
 *         | 20-05-17      | as well as animation for during loading
 *---------|---------------|---------------------------------------------------
 * 1.0/1.1 | J.D.          | Added "tips".
 *         | 22-05-17      |
 *---------|---------------|---------------------------------------------------
 * 1.1/2.0 | J.D.          | Added online functionalities.
 *         | 02-06-17      | Hooked into SaveProcessor for cloud save.
 *---------|---------------|---------------------------------------------------
 *
 * This is a custom loader which plays a looped animation with music while the
 * files are being loaded. It also initializes the game by loading resources
 * into BufferedImage arrays in the Resources class.
 *
 */

import libs.sound.TinySound;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Bootstrap class serves as entry point for
 * program execution, and contains the
 * launcher method for loading the loading
 * screen.
 */
class Bootstrap{

    public static LoadingGUI loading;   //Instance of GUI for loading
    public static GameGUI gameGUI;      //Instance of the actual game

    /**
     * Main method, entry point for program
     * execution.
     * @param args ~~~
     */
    public static void main (String[] args){

        TinySound.init();               //Initiate TinySound for music & sound
        // ^^ THIS has to be the first line to be executed

        System.out.println("Initializing audio components...");

        consoleInit();

        System.out.println("Loading the loading screen...");

        loading = new LoadingGUI();     //Instantiate loading GUI
        loading.setVisible(true);       //Make loading GUI visible
    }

    /**
     * Prints a welcome message to the console.
     */
    public static void consoleInit(){

        Resources.outputSeperator();
        System.out.println("Welcome to Asteroid: Remastered.");
        System.out.println("Initializing Console.");
        System.out.println("This is where devs will see the magic happen.");
        Resources.outputSeperator();

    }

    /**
     * Method used for checking connection to the
     * cloud save & activation server.
     * @return boolean to indicate whether a
     *         connection can be successfully
     *         established.
     */
    public static boolean checkConnection(){

        Resources.outputSeperator();
        System.out.println("Attempting to establish connection to server...");

        /* Using socket to ping the server at public port 80
         * if pinging is successful within 10 seconds, connection
         * should be OK.
         */
        try (Socket server = new Socket()){
            System.out.println("Connecting to 185.176.43.78, port 80");
            server.connect(new InetSocketAddress("185.176.43.78",80),10000);
            System.out.println("Successfully connected.");
            return true;
        } catch (IOException e){

            //If connection failed, output error message and return false.

            System.out.println("Error: Cannot connect to server. Status code 85.");
            return false;
        }

    }

    /**
     * If there had been an error while connecting
     * to the server, this method displays a
     * JOptionPane informing that the game
     * cannot function without Internet access.
     */
    public static void connectionError(){

        JOptionPane.showMessageDialog(loading,
                "The Internet connection to remote server had been lost." +
                        "\nIt is necessary for DRM and cloud save purposes." +
                        "\nThe game cannot function without Internet access.", "Error",
                JOptionPane.ERROR_MESSAGE);

        System.exit(85); //Status 85: Exit due to connection error
    }
}

/**
 * LoadingGUI class contains GUI for the
 * loading screen.
 */
class LoadingGUI extends JFrame{

    //Timer for animation regulation
    private Timer logo_sequence;        //Executing logo sequence
    private Timer load_sequence;        //Executing loading animation
    private Timer tips_sequence;        //Loading tips

    //Timer for various frame counts
    private int visual_frameCount;      //For counting logo & loading animation frames
    private int ball_frameCount;        //For counting the ball loading animation
    private int load_count;             //For counting the current step in the loading process
    private int tip_count;              //For counting the current tips displayed.

    /* The purged boolean indicates whether
     * the loading sequence of the loading
     * sequence had been completed (this
     * refers to the short sequence before
     * the loading animation loop).
     *
     * Once the sequence had been completed,
     * all of its resources will be purged
     * to conserve RAM, and the actual
     * loading of game files will begin.
     */
    private boolean purged;

    private JPanel visual;      //Displaying animations
    private JLabel status;      //Displaying loading status

    private BufferedImage visual_image;     //For displaying animations
    private BufferedImage load_ball;        //For displaying loading ball animation
    private BufferedImage tip;              //For displaying tips

    private BufferedImage[] logo_seq;       //Logo sequence images
    private BufferedImage[] load_seq;       //Load sequence images
    private BufferedImage[] ball_seq;       //Ball sequence images
    private BufferedImage[] tips;           //Tips

    //Single threaded ThreadPools for importing resources in parallel
    private ExecutorService import_pool_1 = Executors.newFixedThreadPool(1);
    private ExecutorService import_pool_2 = Executors.newFixedThreadPool(1);
    private ExecutorService import_pool_3 = Executors.newFixedThreadPool(1);

    /**
     * Constructor
     */
    public LoadingGUI() {

        super();

        setSize(1000, 563);
        setUndecorated(true);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.black);

        //initialize instance variables
        visual_frameCount = 0;
        ball_frameCount = 0;
        load_count = 0;
        tip_count = 0;
        purged = false;

        System.out.println("Initializing visual components...");

        //Initialize JPanel for animation display
        visual = new JPanel(){

            /**
             * Override paintComponent class
             * @param g default paintComponent parameter
             */
            protected void paintComponent (Graphics g){

                //draw animation frame,ball animation, and tip
                g.drawImage(visual_image,0,0,this);
                g.drawImage(load_ball,900,460,this);
                g.drawImage(tip,600,250,this);
            }
        };
        visual.setBounds(0,0,1000,563);
        visual.setBackground(Color.black);
        visual.setOpaque(false);

        //Initialize JLabel for loading status
        status = new JLabel();
        status.setForeground(Color.white);
        status.setFont(Resources.standard);
        status.setBounds(765,460,1000,50);

        //Initialize BufferedImage arrays
        logo_seq = new BufferedImage[363];
        load_seq = new BufferedImage[600];
        ball_seq = new BufferedImage[95];
        tips = new BufferedImage[5];

        //Import logo animation
        importLogoSequence();

        System.out.println("Initializing animation timers...");

        /* Set visual image as the #visual_frameCount item on
         * the logo sequence animation array
         *
         * If visual_frameCount is less than the number of images
         * in the array, keep adding 1 every 33 seconds (for 30 fps)
         *
         * Cleanup all resources if the animation is complete to
         * conserve RAM usage.
         */
        logo_sequence = new Timer(Resources.REFRESH_RATE, e -> {

            visual_image = logo_seq[visual_frameCount];
            repaint();

            if (visual_frameCount < 362 - 1){
                visual_frameCount ++;
            } else {

                System.out.println("Logo animation complete;\nStarting cleanup sequence.");

                //Cleanup
                logo_sequence.stop();       //Stop this timer
                visual_frameCount = 0;      //Reset frameCounter for loading animations
                visual_image = null;        //Reset visual_image for loading animations
                logo_seq = null;            //Dump the BufferedImage array
                Runtime.getRuntime().gc();  //Call garbage collector

                System.out.println("Cleanup complete;\nStarting loading sequence.");

                //Load next animation
                Resources.loadMusic();      //Start the loading music
                importLoadSequence();       //Import loading animation
                load_sequence.start();      //Start loading_sequence
                tips_sequence.start();      //Start the tip_update timer
            }

        });

        load_sequence = new Timer(Resources.REFRESH_RATE, e -> {

            visual_image = load_seq[visual_frameCount];

            if (purged) {
                load_ball = ball_seq[ball_frameCount];
            }

            repaint();

            if (visual_frameCount < 450){
                visual_frameCount ++;
            } else {
                if (!purged){
                    purgeAnimation();
                    notifyCompletion();
                    purged = true;
                }
                visual_frameCount = 300;
            }

            if (purged) {
                if (ball_frameCount < 94 - 1) {
                    ball_frameCount++;
                } else {
                    ball_frameCount = 0;
                }
            }

        });

        tips_sequence = new Timer(5000, e -> {

            if (purged) {
                tip = tips[tip_count];

                if (tip_count < 3) {
                    tip_count++;
                } else {
                    tip_count = 0;
                }
            }
        });
        tips_sequence.setInitialDelay(0);

        logo_sequence.start();
        Resources.logoTheme();

        add(status);
        add(visual);

    }

    private void importLoadSequence(){

        import_pool_1.submit(() -> {
            try {
                for (int i = 0; i < 251; i++) {
                    load_seq[i] = ImageIO.read(getClass().getResource("resources/intro_seq/load/" + i + ".jpeg"));
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });

        import_pool_2.submit(() -> {
            try {
                for (int i = 251; i < 501; i++) {
                    load_seq[i] = ImageIO.read(getClass().getResource("resources/intro_seq/load/" + i + ".jpeg"));
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });

        import_pool_3.submit(() -> {
            try {
                for (int i = 501; i < load_seq.length; i++) {
                    load_seq[i] = ImageIO.read(getClass().getResource("resources/intro_seq/load/" + i + ".jpeg"));
                }
                for (int i = 0; i < ball_seq.length; i++){
                    ball_seq[i] = ImageIO.read(getClass().getResource("/resources/intro_seq/golf_balls/" + i + ".png"));
                }
                for (int i = 0; i < tips.length; i++){
                    tips[i] = ImageIO.read(getClass().getResource("/resources/tips/" + i + ".png"));
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        });

    }

    /**
     * Using all three ThreadPools to import logo
     * animation resources in parallel.
     */
    private void importLogoSequence(){

        System.out.println("Importing logo animation.");

        //Reading images using ImageIO
        //Pool 1: Import image 0 - 100
        import_pool_1.submit(() -> {
            try {
                for (int i = 0; i < 101; i++) {
                    logo_seq[i] = ImageIO.read(getClass().getResource("resources/intro_seq/logo/" + i + ".jpeg"));
                }
            } catch (Exception e){
                fileNotFound();
            }
        });

        import_pool_2.submit(() -> {
            try {
                for (int i = 101; i < 201; i++) {
                    logo_seq[i] = ImageIO.read(getClass().getResource("resources/intro_seq/logo/" + i + ".jpeg"));
                }
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        });

        import_pool_3.submit(() -> {
            try {
                for (int i = 201; i < logo_seq.length; i++) {
                    logo_seq[i] = ImageIO.read(getClass().getResource("resources/intro_seq/logo/" + i + ".jpeg"));
                }
            } catch (IOException e){
                System.out.println(e.getMessage());
            }
        });

    }

    private void purgeAnimation(){
        import_pool_1.submit(() -> {
            for (int i = 0; i < 300; i++){
                load_seq[i] = null;
            }

            import_pool_2.shutdownNow();
            import_pool_3.shutdownNow();

            import_pool_2 = null;
            import_pool_3 = null;

            Runtime.getRuntime().gc();
        });
    }

    public void fileNotFound() {

        JOptionPane.showMessageDialog(this,"One or more files required to run this program is missing.\n" +
                "Please ensure that the \"resource\" folder is in the same folder as the java files.","Error",JOptionPane.ERROR_MESSAGE);
        System.exit(80);

    }

    public void notifyCompletion(){

        load_count ++;

        switch (load_count){

            case 1:
                import_pool_1.submit(() -> {
                    Resources.importBulletResources();
                });
                status.setText("Doing Stuff...");
                break;

            case 2:
                import_pool_1.submit(() -> {
                    Resources.importSpaceResources();
                });
                status.setText("Killing Aliens...");
                break;

            case 3:
                import_pool_1.submit(() -> {
                    Resources.importEnemyResources();
                });
                status.setText("Crunching ##s...");
                break;

            case 4:
                import_pool_1.submit(() -> {
                    Resources.importHUDResources();
                });
                status.setText("Building Ships...");
                break;

            case 5:
                import_pool_1.submit(() -> {
                    Resources.importPlayerResources();
                });
                status.setText("Playing God...");
                break;

            case 6:
                import_pool_1.submit(() -> {
                    Resources.importIntroResources();
                    status.setText("Building World...");
                });
                break;

            case 7:
                import_pool_1.submit(() -> {
                    if (Bootstrap.checkConnection()) {
                        Resources.importData();
                    } else {
                        Bootstrap.connectionError();
                    }
                    status.setText("Herding Cows...");
                });
                break;

            case 8:
                import_pool_1.submit(() -> {
                    Bootstrap.gameGUI = new GameGUI();
                });
                break;

            case 9:
                status.setText("Almost There...");
                tips_sequence.stop();
                tip = tips[4];

                getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(
                        KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"enter");
                getRootPane().getActionMap().put("enter", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        notifyCompletion();
                    }
                });
                break;

            case 10:

                status.setText("Here We Go...");

                tip = null;

                load_sequence.removeActionListener(load_sequence.getActionListeners()[0]);
                load_sequence.addActionListener(e -> {

                    visual_image = load_seq[visual_frameCount];

                    load_ball = ball_seq[ball_frameCount];

                    repaint();

                    if (visual_frameCount < 599 - 1) {
                        visual_frameCount++;
                    } else {
                        load_sequence.stop();
                        load_seq = null;
                        import_pool_1.shutdownNow();
                        this.dispose();
                        Bootstrap.loading = null;
                        Runtime.getRuntime().gc();

                        Bootstrap.gameGUI.setVisible(true);
                    }

                    if (ball_frameCount < 94 - 1) {
                        ball_frameCount++;
                    } else {
                        ball_frameCount = 0;
                    }

                });

                break;

        }
    }
}