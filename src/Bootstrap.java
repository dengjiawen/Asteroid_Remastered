import libs.sound.Music;
import libs.sound.Sound;
import libs.sound.TinySound;

import javax.annotation.Resource;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by freddeng on 2017-05-28.
 */
class Bootstrap{

    public static LoadingGUI loading;
    public static GameGUI gameGUI;

    public static void main (String[] args){

        TinySound.init();

        loading = new LoadingGUI();
        loading.setVisible(true);
    }

}

class LoadingGUI extends JFrame{

    private Timer logo_sequence;
    private Timer load_sequence;
    private Timer tips_sequence;

    private int visual_frameCount;
    private int ball_frameCount;
    private int load_count;
    private int tip_count;

    private boolean purged;

    private JPanel visual;
    private JLabel status;

    private BufferedImage visual_image;
    private BufferedImage load_ball;
    private BufferedImage tip;

    private BufferedImage[] logo_seq;
    private BufferedImage[] load_seq;
    private BufferedImage[] ball_seq;
    private BufferedImage[] tips;

    private ExecutorService import_pool_1 = Executors.newCachedThreadPool();
    private ExecutorService import_pool_2 = Executors.newCachedThreadPool();
    private ExecutorService import_pool_3 = Executors.newCachedThreadPool();

    public LoadingGUI() {

        super();

        setSize(1000, 563);
        setUndecorated(true);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.black);

        visual_frameCount = 0;
        ball_frameCount = 0;
        load_count = 0;
        tip_count = 0;
        purged = false;

        visual = new JPanel(){
            protected void paintComponent (Graphics g){
                g.drawImage(visual_image,0,0,this);
                g.drawImage(load_ball,900,460,this);
                g.drawImage(tip,600,250,this);
            }
        };
        visual.setBounds(0,0,1000,563);
        visual.setBackground(Color.black);
        visual.setOpaque(false);

        status = new JLabel();
        status.setForeground(Color.white);
        status.setFont(Resources.standard);
        status.setBounds(750,460,1000,50);

        logo_seq = new BufferedImage[363];
        load_seq = new BufferedImage[600];
        ball_seq = new BufferedImage[95];
        tips = new BufferedImage[5];

        importLogoSequence();

        logo_sequence = new Timer(Resources.REFRESH_RATE, e -> {

            visual_image = logo_seq[visual_frameCount];

            repaint();

            if (visual_frameCount < 362 - 1){
                visual_frameCount ++;
            } else {
                logo_sequence.stop();
                visual_frameCount = 0;
                visual_image = null;
                logo_seq = null;
                Resources.loadMusic();
                Runtime.getRuntime().gc();
                importLoadSequence();
                load_sequence.start();
                tips_sequence.start();
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

    public void importLoadSequence(){

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

    public void importLogoSequence(){

        import_pool_1.submit(() -> {
            try {
                for (int i = 0; i < 101; i++) {
                    logo_seq[i] = ImageIO.read(getClass().getResource("resources/intro_seq/logo/" + i + ".jpeg"));
                }
            } catch (IOException e){
                System.out.println(e.getMessage());
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

    public void purgeAnimation(){
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
                });
                status.setText("Playing God...");
                break;

            case 7:
                import_pool_1.submit(() -> {
                    Bootstrap.gameGUI = new GameGUI();
                });
                break;

            case 8:
                status.setText("Almost There...");
                tips_sequence.stop();
                tip = tips[4];

                getRootPane().getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER,0),"enter");

                getRootPane().getActionMap().put("enter", new AbstractAction() {
                    @Override
                    public void actionPerformed(ActionEvent e) {
                        notifyCompletion();
                    }
                });
                break;

            case 9:

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