import libs.sound.Music;
import libs.sound.Sound;
import libs.sound.TinySound;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by freddeng on 2017-05-28.
 */
class Test{

    public static void main (String[] args){

        TinySound.init();

        LoadingGUI loading = new LoadingGUI();
        loading.setVisible(true);
    }

}

class LoadingGUI extends JFrame{

    private Timer logo_sequence;
    private Timer load_sequence;

    private Music logo;
    private Music load;

    private int visual_frameCount;
    private int ball_frameCount;

    private JPanel visual;

    private BufferedImage visual_image;
    private BufferedImage load_ball;

    private BufferedImage[] logo_seq;
    private BufferedImage[] load_seq;
    private BufferedImage[] ball_seq;

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

        visual = new JPanel(){
            protected void paintComponent (Graphics g){
                g.drawImage(visual_image,0,0,this);
                g.drawImage(load_ball,900,460,this);
            }
        };
        visual.setBounds(0,0,1000,563);
        visual.setOpaque(false);

        logo_seq = new BufferedImage[363];
        load_seq = new BufferedImage[600];
        ball_seq = new BufferedImage[95];

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
                load.play(true,0.5);
                Runtime.getRuntime().gc();
                importLoadSequence();
                load_sequence.start();
            }

        });

        load_sequence = new Timer(Resources.REFRESH_RATE, e -> {

            visual_image = load_seq[visual_frameCount];
            load_ball = ball_seq[ball_frameCount];

            repaint();

            if (visual_frameCount < 450){
                visual_frameCount ++;
            } else {
                visual_frameCount = 300;
            }

            if (ball_frameCount < 94 - 1) {
                ball_frameCount++;
            } else {
                ball_frameCount = 0;
            }

        });

        logo_sequence.start();
        logo = TinySound.loadMusic(getClass().getResource("/resources/sound/logo.wav"));
        load = TinySound.loadMusic(getClass().getResource("/resources/sound/load.wav"));
        logo.play(false,0.5);

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
            for (int i = 0; i < )
        });
    }


}