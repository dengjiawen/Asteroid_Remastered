/**
 * Created by Jiawen (Fred) Deng on 2017-04-24.
 * DO NOT REDISTRIBUTE WITHOUT PERMISSION
 * COPYRIGHT 2017
 *
 * Revision 1.2.4
 */

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import kuusisto.tinysound.TinySound;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.Music;

class launcher {

    public static MainWindow game;

    public static void main(String[] args) {

        TinySound.init();
        resources.importBulletResources();
        resources.importPlayerResources();
        resources.importHUDResources();
        game = new MainWindow();
        resources.cursor_frameUpdate();
        resources.importEnemyResources();

    }
}

class resources {

    public static final byte FRAME_RATE = 30;
    public static final byte REFRESH_RATE = (byte) (1000 / FRAME_RATE);
    public static final BufferedImage[] mini_explosion = new BufferedImage[9];
    public static final BufferedImage[] enemy_onFire = new BufferedImage[44];
    public static final BufferedImage[] karmakazi_onFire = new BufferedImage[44];
    public static final BufferedImage[] ocelot_onFire = new BufferedImage[44];
    public static final BufferedImage[] explosion = new BufferedImage[22];
    private static final BufferedImage[] regular_cursor = new BufferedImage[60];
    private static final BufferedImage[] aimed_cursor = new BufferedImage[60];
    public static final BufferedImage[] player_on_fire = new BufferedImage[44];
    public static final BufferedImage[] HUD_1 = new BufferedImage[731];
    public static final BufferedImage[] HUD_logo = new BufferedImage[2 * 150];
    public static final BufferedImage[] health_number = new BufferedImage[21 + 80];
    public static final BufferedImage[] normal_health = new BufferedImage[80];
    public static final BufferedImage[] low_health_warning = new BufferedImage[30];
    public static final BufferedImage[] health_init = new BufferedImage[60];
    public static final BufferedImage[] weapon_state_engage = new BufferedImage[180];
    public static final BufferedImage[] weapon_state_normal = new BufferedImage[180];
    public static final BufferedImage[] weapon_state_init = new BufferedImage[60];
    public static final BufferedImage[] weapon_state_overHeat = new BufferedImage[30];
    public static BufferedImage bullet_sprite;
    public static BufferedImage large_bullet;
    public static BufferedImage player_sprite;
    public static BufferedImage regular_enemy_sprite;
    public static BufferedImage karmakazi_sprite;
    public static BufferedImage ocelot_sprite;
    public static BufferedImage boot_confirmation;
    public static BufferedImage blockade_sprite;
    private static byte cursor_frame = 0;
    private static final Rectangle cursor_hitBox = new Rectangle();
    public static Point mouse_location = new Point(0,0);

    private static boolean cursor_onTarget;
    private static Cursor cursor;

    public static Sound enemy_fire = TinySound.loadSound(resources.class.getResource("/resources/sound/enemy_fire.wav"));
    public static Sound friendly_fire = TinySound.loadSound(resources.class.getResource("/resources/sound/friendly_fire.wav"));
    public static Sound explosion_sound = TinySound.loadSound(resources.class.getResource("/resources/sound/explosion.wav"));
    public static Sound lock_on = TinySound.loadSound(resources.class.getResource("/resources/sound/lock_on.wav"));
    public static Music music = TinySound.loadMusic(resources.class.getResource("/resources/sound/music.wav"));
    public static Music low_health = TinySound.loadMusic(resources.class.getResource("/resources/sound/low_health_warning.wav"));

    private static void importCursorResources() {

        try {
            for (int i = 0; i < 60; i++) {
                resources.regular_cursor[i] =
                        ImageIO.read(resources.class.getResource("/resources/sequence/cursor/regular/" + i + ".png"));
                resources.aimed_cursor[i] =
                        ImageIO.read(resources.class.getResource("/resources/sequence/cursor/aimed/" + i + ".png"));
            }
        } catch (java.io.IOException f) {
            System.out.println(f.getMessage());
        }

    }

    public static void cursor_frameUpdate() {

        importCursorResources();

        Timer cursor_frameUpdate = new Timer(REFRESH_RATE, e -> {

            cursor_onTarget = false;

            cursor_hitBox.setBounds(mouse_location.x, mouse_location.y, 50, 50);
            for (int i = 0; i < EnemyPane.enemies.size(); i++) {
                if (EnemyPane.enemies.get(i) != null) {
                    if (cursor_hitBox.intersects(EnemyPane.enemies.get(i).hitBox())) {
                        System.out.println("intersected");
                        cursor_onTarget = true;
                    }
                }
            }

            if (cursor_onTarget) {
                cursor = Toolkit.getDefaultToolkit().createCustomCursor(aimed_cursor[cursor_frame], new Point(0, 0), null);
            } else {
                cursor = Toolkit.getDefaultToolkit().createCustomCursor(regular_cursor[cursor_frame], new Point(0, 0), null);
            }

            launcher.game.getContentPane().setCursor(cursor);
            if (cursor_frame < 59) {
                cursor_frame++;
            } else {
                cursor_frame = 0;
            }

        });

        cursor_frameUpdate.start();

    }

    public static void importBulletResources() {

        try {
            bullet_sprite = ImageIO.read(resources.class.getResource("/resources/bullet_sprite.png"));
            large_bullet = ImageIO.read(resources.class.getResource("/resources/large_bullet.png"));
            for (int i = 0; i < 9; i++) {
                mini_explosion[i] = ImageIO.read(resources.class.getResource("/resources/sequence/hit_notify/" + i + ".png"));
            }
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void importEnemyResources() {

        try {
            regular_enemy_sprite = ImageIO.read(resources.class.getResource("/resources/regular_enemy.png"));
            ocelot_sprite = ImageIO.read(resources.class.getResource("/resources/ocelot.png"));
            karmakazi_sprite = ImageIO.read(resources.class.getResource("/resources/karmakazi.png"));
            blockade_sprite = ImageIO.read(resources.class.getResource("/resources/blockade.png"));
            for (int i = 0; i < 44; i++) {
                enemy_onFire[i] = ImageIO.read(resources.class.getResource("/resources/sequence/enemy1_on_fire/" + i + ".png"));
                karmakazi_onFire[i] = ImageIO.read(resources.class.getResource("/resources/sequence/karmakazi_on_fire/" + i + ".png"));
                ocelot_onFire[i] = ImageIO.read(resources.class.getResource("/resources/sequence/ocelot_on_fire/" + i + ".png"));
            }
            for (int i = 0; i < 21; i++) {
                explosion[i] = ImageIO.read(resources.class.getResource("/resources/sequence/explosion/" + i + ".png"));
            }
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }
    }

    public static void importPlayerResources() {

        try {
            for (int i = 0; i < 44; i++) {
                player_on_fire[i] = ImageIO.read(resources.class.getResource("/resources/sequence/self_on_fire/" + i + ".png"));
            }
            player_sprite = ImageIO.read(resources.class.getResource("/resources/spaceship_sprite.png"));
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }

    }

    public static void importHUDResources(){

        try {
            boot_confirmation = ImageIO.read(resources.class.getResource("/resources/gui/start_confirmation.png"));
            for (int i = 0; i < 731; i++) {
                HUD_1[i] = ImageIO.read(resources.class.getResource("/resources/sequence/dev_hud_1/" + i + ".png"));
            }
            for (int i = 0; i < 150; i++){
                HUD_logo[i] = ImageIO.read(resources.class.getResource("/resources/sequence/sub_hud_logo/init/" + i + ".png"));
                HUD_logo[150 + i] = ImageIO.read(resources.class.getResource("/resources/sequence/sub_hud_logo/loop/" + i + ".png"));
            }
            for (int i = 0; i < 21; i++){
                health_number[i] = ImageIO.read(resources.class.getResource("/resources/sequence/sub_hud_health/low_health_number/" + i + ".png"));
            }
            for (int i = 21; i < 21+80; i++){
                health_number[i] = ImageIO.read(resources.class.getResource("/resources/sequence/sub_hud_health/normal_health_number/normal" +
                        new DecimalFormat("00000").format(i-21) + ".png"));
            }
            for (int i = 0; i < 30; i++){
                low_health_warning[i] = ImageIO.read(resources.class.getResource("/resources/sequence/sub_hud_health/low_health_pane/" + i + ".png"));
                weapon_state_overHeat[i] = ImageIO.read(resources.class.getResource("/resources/sequence/sub_hud_weapon/over_heat/" + i + ".png"));
            }
            for (int i = 0; i < 80; i++){
                normal_health[i] = ImageIO.read(resources.class.getResource("/resources/sequence/sub_hud_health/normal_health/" + i + ".png"));
            }
            for (int i = 0; i < 60; i++){
                health_init[i] = ImageIO.read(resources.class.getResource("/resources/sequence/sub_hud_health/health_init/" + i + ".png"));
                weapon_state_init[i] = ImageIO.read(resources.class.getResource("/resources/sequence/sub_hud_weapon/init/" + i + ".png"));
            }
            for (int i = 0; i < 180; i++){
                weapon_state_normal[i] = ImageIO.read(resources.class.getResource("/resources/sequence/sub_hud_weapon/normal/" + i + ".png"));
                weapon_state_engage[i] = ImageIO.read(resources.class.getResource("/resources/sequence/sub_hud_weapon/engage/" + i + ".png"));
            }
        } catch (java.io.IOException e) {
            System.out.println(e.getMessage());
        }

    }
}

class data {

    public static final Font name = new Font("Calibri",Font.BOLD,20);
    public static final Font points = new Font("Calibri",Font.BOLD,50);
    public static final Font emphasis = new Font("Calibri",Font.BOLD,18);
    public static final Font standard = new Font("Calibri",Font.PLAIN,13);
    public static final Font bold = new Font("Calibri",Font.BOLD,10);

    public static int total_points = 0;

    public static final Color techno_RED = Color.decode("#ed3737");
    public static final Color techno_BLUE = Color.decode("#2bede6");

}

class MainWindow extends JFrame{

    private static PlayerPane player_pane;
    public static EnemyPane enemy_pane;
    public static BulletPane bullet_pane;
    BootConfirmationGUI boot_confirmation;

    MainWindow() {

        super();

        setSize(1280, 750);
        setUndecorated(true);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.black);

        player_pane = new PlayerPane();
        enemy_pane = new EnemyPane();
        bullet_pane = new BulletPane();
        SpaceBackground space = new SpaceBackground();
        //Status devBar = new Status();
        HUD hud = new HUD();
        boot_confirmation = new BootConfirmationGUI();

        resources.music.play(true);

        //add(devBar);
        add(boot_confirmation);
        add(hud);
        add(enemy_pane);
        add(player_pane);
        add(bullet_pane);
        add(space);
        setVisible(true);

    }
}

class BootConfirmationGUI extends JPanel{

    JButton proceed;
    JButton abort;

    BootConfirmationGUI(){

        super();

        setBounds(490,225,300, 309);
        setOpaque(false);
        setLayout(null);

        setCursor(new Cursor(Cursor.DEFAULT_CURSOR));

        proceed = new JButton();
        proceed.setBounds(150,260,150,49);
        proceed.setCursor(new Cursor(Cursor.HAND_CURSOR));
        proceed.setBorderPainted(false);
        proceed.setOpaque(false);
        proceed.setContentAreaFilled(false);
        proceed.addActionListener(e -> {
            setVisible(false);
            launcher.game.enemy_pane.init();
            HUD.hud_1_frameUpdate.start();
        });

        abort = new JButton();
        abort.setBounds(0,260,150,49);
        abort.setCursor(new Cursor(Cursor.HAND_CURSOR));
        abort.setBorderPainted(false);
        abort.setOpaque(false);
        abort.setContentAreaFilled(false);
        abort.addActionListener(e -> {
            System.exit(100);
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                BootConfirmationGUI.this.grabFocus();
            }
        });

        add(proceed);
        add(abort);

    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(resources.boot_confirmation, 0,0,this);
    }

}

class HUD extends JPanel{

    public static Timer hud_1_frameUpdate;
    public static Timer normal_health;
    public static Timer weapon_overheat;

    BufferedImage frame_Update;
    BufferedImage logo;
    BufferedImage health_bar;
    public static BufferedImage weapon_box;
    int frameCount;
    int logo_frameCount;
    int number_frameCount;
    int bar_init_frameCount;
    int low_health_frameCount;
    int health_number_frameCount;
    int weapon_state_frameCount;

    String[] weapon_state = {"normal", "engage"};

    JLabel health_number;

    HUD(){

        setBounds(0,0,1280, 750);
        setLayout(null);
        setOpaque(false);

        health_number = new JLabel();
        health_number.setBounds(995,186,50,29);
        health_number.setFont(data.standard);

        frameCount = 0;
        logo_frameCount = 0;
        number_frameCount = 0;
        bar_init_frameCount = 0;
        weapon_state_frameCount = 0;
        hud_1_frameUpdate = new Timer(resources.REFRESH_RATE, e -> {
            frame_Update = resources.HUD_1[frameCount];
            if (frameCount < 730){
                frameCount ++;
            } else {
                frameCount = 345;
            }
            logo = resources.HUD_logo[logo_frameCount];
            if (logo_frameCount < 299){
                logo_frameCount ++;
            } else {
                logo_frameCount = 150;
            }
            health_number.setForeground(data.techno_BLUE);
            health_number.setText("    " + number_frameCount);
            if (number_frameCount < 100){
                number_frameCount ++;
            } else {}
            health_bar = resources.health_init[bar_init_frameCount];
            weapon_box = resources.weapon_state_init[bar_init_frameCount];
            if (bar_init_frameCount < 59){
                bar_init_frameCount ++;
            } else {
                health_bar = resources.normal_health[79];
                normal_health.start();
            }

        });
        health_number_frameCount = 100;
        normal_health = new Timer(resources.REFRESH_RATE, e -> {
            if (Player.player_data.health > 800){
                Player.player_data.health = 800;
            } else if (Player.player_data.health < 0){
                Player.player_data.health = 0;
            }
            int healthPercent = Math.round((Player.player_data.health / 800f) * 100);
            if (healthPercent > 20){
                if (healthPercent > health_number_frameCount){
                    health_number_frameCount ++;
                } else if (healthPercent < health_number_frameCount){
                    health_number_frameCount --;
                }
                health_bar = resources.normal_health[health_number_frameCount - 21];
                health_number.setForeground(data.techno_BLUE);
                health_number.setText("    " + health_number_frameCount);
            } else if (healthPercent <= 20) {
                health_bar = resources.low_health_warning[low_health_frameCount];
                if (low_health_frameCount < 29){
                    low_health_frameCount ++;
                } else {
                    low_health_frameCount = 0;
                }
                health_number.setForeground(data.techno_RED);
                health_number.setText("    " + healthPercent);
            }

            if (Player.over_heating) {
                weapon_box = resources.weapon_state_overHeat[weapon_state_frameCount];
                if (weapon_state_frameCount < 29) {
                    weapon_state_frameCount++;
                } else {
                    weapon_state_frameCount = 0;
                }
            } else {
                if (Player.isFiring){
                    if (Player.bullet_heat_factor <= 179) {
                        weapon_box = resources.weapon_state_engage[Player.bullet_heat_factor];
                    } else {}
                } else {
                    weapon_box = resources.weapon_state_normal[Player.bullet_heat_factor];
                }
            }
        });

        add(health_number);

    }

    @Override
    protected void paintComponent(Graphics g){

        super.paintComponent(g);

        g.drawImage(frame_Update, 0, 0, this);
        g.drawImage(logo, 1000, 30, this);
        g.drawImage(health_bar, 1048, 186, this);
        g.drawImage(weapon_box, 1000, 225, this);

    }

}

class SpaceBackground extends JPanel {

    private static final ImageIcon background_loop1 = new ImageIcon(Image.class.getResource("/resources/background_loop.jpg"));
    private static final ImageIcon background_loop2 = new ImageIcon(Image.class.getResource("/resources/background_loop.jpg"));

    private static final Timer frameUpdate = new Timer(resources.REFRESH_RATE, null);

    private static int y1 = 0;
    private static int y2 = -750;

    SpaceBackground() {

        super();

        setBounds(0, 0, 1280, 720);
        setLayout(null);
        setOpaque(false);

        frameUpdate.addActionListener(e -> {

            if (y1 == 750) {
                y1 = -750;
            } else if (y2 == 750) {
                y2 = -750;
            }

            y1 += 2;
            y2 += 2;
            repaint();

        });
        frameUpdate.start();

    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        background_loop1.paintIcon(this, g, 0, y1);
        background_loop2.paintIcon(this, g, 0, y2);
    }

}

class Status extends JPanel {

    private BufferedImage devBar;

    private final JLabel FPS = new JLabel();
    private final JLabel xcord = new JLabel();
    private final JLabel ycord = new JLabel();
    private final JLabel health = new JLabel();

    private int Frames = 0;

    Status() {

        setBounds(0, 0, 600, 30);
        setLayout(null);

        try {
            devBar = ImageIO.read(getClass().getResource("/resources/devBar.png"));
        } catch (IOException ex) {
            ex.printStackTrace();
        }

        Timer fpsCheck = new Timer(500, e -> {
            Frames = resources.FRAME_RATE + ThreadLocalRandom.current().nextInt(-5, 5 + 1);

            FPS.setText("" + Frames);

            xcord.setText("" + Player.player_data.x / 2);
            ycord.setText("" + Player.player_data.y / 2);

            health.setText("" + Player.player_data.health);
        });

        fpsCheck.start();

        xcord.setBounds(400, 10, 50, 10);
        ycord.setBounds(460, 10, 50, 10);
        health.setBounds(500, 10, 50, 10);

        FPS.setText("" + Frames);
        FPS.setBounds(275, 10, 20, 10);

        add(xcord);
        add(ycord);
        add(health);
        add(FPS);

    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        g.drawImage(devBar, 0, 0, this);

    }


}

class CharacterProperties {

    int x;
    int y;
    int health;

    private final int character_type;
    /* character_type key:
     * 0: player
     * 1: regular_enemy
     * 2: karmakazi
     * 3: ocelot
     * 4: teleporter
     * 5: blockade
     * 6: boss
     * 7: friendly
     */
    boolean dead;

    CharacterProperties(int x, int y, int health, int character_type) {

        this.x = x;
        this.y = y;
        this.health = health;
        this.character_type = character_type;
        this.dead = false;

    }

}

class BulletProperties {

    int x;
    int y;
    private final int bullet_number;
    final boolean enemy_fire;
    public final boolean large_bullet;

    BulletProperties(int x, int y, boolean enemy_fire, boolean large_bullet) {

        this.x = x;
        this.y = y;
        this.bullet_number = BulletPane.bulletCount;
        this.enemy_fire = enemy_fire;
        this.large_bullet = large_bullet;

    }

}

class Bullet {

    private static final double targetTime = 1000;

    private double currentTime;
    private final double x1;
    private final double x2;
    private final double y1;
    private final double y2;

    private final int bulletNumber;
    private int explosion_frameCount;

    public BufferedImage bullet_sprite;
    private final Rectangle hitBox;
    public final BulletProperties bullet_properties;

    public final Timer bullet_frameUpdate;
    public final Timer collision_detection;
    public final Timer mini_explosion_frameUpdate;
    public final Timer large_explosion_frameUpdate;


    Bullet(Point origin, Point target, boolean enemy_fire, boolean large_bullet) {

        importAudioResource(enemy_fire);

        hitBox = new Rectangle();
        bullet_properties = new BulletProperties(origin.x, origin.y, enemy_fire, large_bullet);

        if (bullet_properties.large_bullet){
            bullet_sprite = resources.large_bullet;
        } else {
            bullet_sprite = resources.bullet_sprite;
        }

        x1 = origin.x;
        y1 = origin.y;
        x2 = target.x;
        y2 = target.y;
        explosion_frameCount = 0;
        bulletNumber = BulletPane.bulletCount;
        currentTime = 0;

        bullet_frameUpdate = new Timer(resources.REFRESH_RATE, null);
        collision_detection = new Timer(resources.REFRESH_RATE, null);
        mini_explosion_frameUpdate = new Timer(resources.REFRESH_RATE, null);
        large_explosion_frameUpdate = new Timer(resources.REFRESH_RATE, null);

        bullet_frameUpdate.addActionListener(e -> {

            bullet_properties.x = (int) (x1 + currentTime * (x2 - x1) / targetTime);
            bullet_properties.y = (int) (y1 + currentTime * (y2 - y1) / targetTime);

            currentTime += 33;

            if (bullet_properties.large_bullet) {
                hitBox.setBounds(bullet_properties.x, bullet_properties.y, 30, 30);
            } else {
                hitBox.setBounds(bullet_properties.x, bullet_properties.y, 10, 10);
            }

            if (bullet_properties.x < -10 || bullet_properties.x > 1350 ||
                bullet_properties.x < -10 || bullet_properties.y > 780) {
                cleanUp();
            }

        });
        mini_explosion_frameUpdate.addActionListener(e -> {

            if (explosion_frameCount < 8) {
                bullet_sprite = resources.mini_explosion[explosion_frameCount];
                explosion_frameCount++;
            } else {
                cleanUp();
            }

        });
        large_explosion_frameUpdate.addActionListener(e -> {

            if (explosion_frameCount < 21) {
                bullet_sprite = resources.explosion[explosion_frameCount];
                explosion_frameCount++;
            } else {
                cleanUp();
            }

        });
        collision_detection.addActionListener(e -> {

            if (hitBox.intersects(Player.hitBox) && bullet_properties.enemy_fire) {
                if (bullet_properties.large_bullet) {
                    Player.player_data.health -= 70;
                    large_explosion_frameUpdate.start();
                } else {
                    Player.player_data.health -= 5;
                    mini_explosion_frameUpdate.start();
                }
            }

            if (!bullet_properties.enemy_fire) {
                for (int i = 0; i < EnemyPane.enemies.size(); i++) {
                    if (EnemyPane.enemies.get(i) != null){
                        if (hitBox.intersects(EnemyPane.enemies.get(i).hitBox()) && !EnemyPane.enemies.get(i).enemy_properties().dead) {

                            EnemyPane.enemies.get(i).enemy_properties().health -= 10;
                            System.out.println("EnemyPane " + i + " Health " + EnemyPane.enemies.get(i).enemy_properties().health);

                            bullet_frameUpdate.stop();
                            collision_detection.stop();
                            mini_explosion_frameUpdate.start();
                        }
                    }
                }

            }

        });


        bullet_frameUpdate.start();
        collision_detection.start();

    }

    private void importAudioResource(boolean enemy_fire){

        if (enemy_fire) {
            resources.enemy_fire.play();
        } else {
            resources.friendly_fire.play();
        }
    }

    private void cleanUp() {

        System.out.println("Cleanup " + bulletNumber + " completed.");

        large_explosion_frameUpdate.stop();
        mini_explosion_frameUpdate.stop();
        bullet_frameUpdate.stop();
        collision_detection.stop();

        BulletPane.bullets.remove(this);

    }

}

class BulletPane extends JPanel {

    public static final ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    public static int bulletCount = 0;

    BulletPane() {

        setBounds(0, 0, 1280, 750);
        setOpaque(false);
        setLayout(null);


    }

    @Override
    protected void paintComponent(Graphics g){

        super.paintComponent(g);
        for (int i = 0; i < bullets.size(); i++){
            if (bullets.get(i) != null) {
                g.drawImage(bullets.get(i).bullet_sprite, bullets.get(i).bullet_properties.x,
                            bullets.get(i).bullet_properties.y, this);
            }
        }
    }

    public void fireBullet(Point origin, Point target, boolean enemy_fire){

        bullets.add(new Bullet(origin, target, enemy_fire, false));
        System.out.println("Bullet " + bulletCount + "fired");
        bulletCount++;

        repaint();

    }

    public void fireLargeBullet(Point origin, Point target, boolean enemy_fire){

        bullets.add(new Bullet(origin, target, enemy_fire, true));
        System.out.println("Large Bullet " + bulletCount + "fired");
        bulletCount++;

        repaint();

    }

}

class Blockade implements Enemy {

    public BufferedImage blockade_sprite;

    private final Timer fire_bullet;
    private final Timer explode_frameUpdate;
    private final Timer movement_frameUpdate;

    public Rectangle hitBox;
    public Point targetPoint;
    public final CharacterProperties enemy_properties;

    private double y_increment = 1;
    private double x_increment = 1;

    private int onFire_frameCount;
    private int explosion_frameCount;
    private final int enemyNumber;
    private boolean collisionDeath;

    Blockade(int xcord, int ycord) {

        try {
            blockade_sprite = ImageIO.read(getClass().getResource("/resources/blockade.png"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        enemy_properties = new CharacterProperties(xcord, ycord, 600, 5);
        hitBox = new Rectangle();
        targetPoint = new Point(xcord, 900);

        onFire_frameCount = 0;
        explosion_frameCount = 0;
        enemyNumber = EnemyPane.enemyCount;
        x_increment = 1;
        y_increment = 1;
        collisionDeath = false;

        movement_frameUpdate = new Timer(resources.REFRESH_RATE, null);
        explode_frameUpdate = new Timer((int)(resources.REFRESH_RATE * 1.5), null);
        fire_bullet = new Timer(5000, null);

        movement_frameUpdate.addActionListener(e -> {

            enemy_properties.y += y_increment;

            hitBox = new Rectangle(enemy_properties.x - 50, enemy_properties.y - 25, 60, 50);
            if (hitBox.intersects(Player.hitBox)) {
                movement_frameUpdate.stop();
                Player.player_data.health -= 150;
                enemy_properties.health = 0;
                collisionDeath = true;
            }
            targetPoint.x = Player.player_data.x;
            targetPoint.y = Player.player_data.y;

            if (enemy_properties.health <= 0) {
                explode();
            } else if (enemy_properties.health <= 50) {
                //TODO
                blockade_sprite = resources.ocelot_onFire[onFire_frameCount];
                if (onFire_frameCount < 43) {
                    onFire_frameCount++;
                } else {
                    onFire_frameCount = 0;
                }
            }

            if (enemy_properties.y > 850){
                cleanUp();
            }


        });
        fire_bullet.addActionListener(e -> {

            MainWindow.bullet_pane.fireLargeBullet(
                    new Point(enemy_properties.x, enemy_properties.y),
                    targetPoint, true);

        });
        fire_bullet.setInitialDelay(4000);

        movement_frameUpdate.start();
        fire_bullet.start();

    }

    private void explode() {

        if (!collisionDeath){
            Player.player_data.health += 150;
        }

        explode_frameUpdate.addActionListener(e -> {
            blockade_sprite = resources.explosion[explosion_frameCount];
            if (!enemy_properties.dead) {
                ExplosionSound();
                enemy_properties.dead = true;
                fire_bullet.stop();
            }
            if (explosion_frameCount < 21) {
                explosion_frameCount++;
            } else if (explosion_frameCount >= 21) {
                explode_frameUpdate.stop();
                cleanUp();
            }

        });

        explode_frameUpdate.start();

    }

    private void ExplosionSound() {
        resources.explosion_sound.play();
    }

    private void cleanUp() {
        explode_frameUpdate.stop();
        fire_bullet.stop();
        movement_frameUpdate.stop();
        EnemyPane.enemies.remove(this);
    }

    public CharacterProperties enemy_properties(){
        return enemy_properties;
    }

    public BufferedImage enemy_sprite(){
        return blockade_sprite;
    }

    public Point targetPoint(){
        return targetPoint;
    }

    public Rectangle hitBox(){
        return hitBox;
    }

}

class Ocelot implements Enemy {

    public BufferedImage ocelot_sprite;

    private final Timer fire_bullet;
    private final Timer explode_frameUpdate;
    private final Timer movement_frameUpdate;

    public Rectangle hitBox;
    public Point targetPoint;
    public final CharacterProperties enemy_properties;

    private double y_increment = 5;
    private double x_increment = 5;

    private int onFire_frameCount;
    private int explosion_frameCount;
    private final int enemyNumber;
    private boolean collisionDeath;

    Ocelot(int xcord, int ycord) {

        try {
            ocelot_sprite = ImageIO.read(getClass().getResource("/resources/ocelot.png"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        enemy_properties = new CharacterProperties(xcord, ycord, 70, 3);
        hitBox = new Rectangle();
        targetPoint = new Point(Player.player_data.x, Player.player_data.x);

        onFire_frameCount = 0;
        explosion_frameCount = 0;
        enemyNumber = EnemyPane.enemyCount;
        x_increment = 5;
        y_increment = 5;
        collisionDeath = false;

        movement_frameUpdate = new Timer(resources.REFRESH_RATE, null);
        explode_frameUpdate = new Timer((int)(resources.REFRESH_RATE * 1.5), null);
        fire_bullet = new Timer(500, null);

        movement_frameUpdate.addActionListener(e -> {

            enemy_properties.x += x_increment;
            enemy_properties.y += y_increment;

            if (enemy_properties.y > 600) {
                y_increment = -5;
            } else if (enemy_properties.y < 50) {
                y_increment = 5;
            }

            if (enemy_properties.x > 1200) {
                x_increment = -5;
            } else if (enemy_properties.x < 50) {
                x_increment = 5;
            }

            hitBox = new Rectangle(enemy_properties.x - 50, enemy_properties.y - 25, 40, 40);
            if (hitBox.intersects(Player.hitBox)) {
                movement_frameUpdate.stop();
                Player.player_data.health -= 20;
                enemy_properties.health = 0;
                collisionDeath = true;
            }
            targetPoint.x = Player.player_data.x;
            targetPoint.y = Player.player_data.y;

            if (enemy_properties.health <= 0) {
                explode();
            } else if (enemy_properties.health <= 50) {
                ocelot_sprite = resources.ocelot_onFire[onFire_frameCount];
                if (onFire_frameCount < 43) {
                    onFire_frameCount++;
                } else {
                    onFire_frameCount = 0;
                }
            }


        });
        fire_bullet.addActionListener(e -> {

            MainWindow.bullet_pane.fireBullet(
                    new Point(enemy_properties.x, enemy_properties.y),
                    targetPoint, true);

        });
        fire_bullet.setInitialDelay(ThreadLocalRandom.current().nextInt(3000, 4000));

        movement_frameUpdate.start();
        fire_bullet.start();

    }

    private void explode() {

        movement_frameUpdate.stop();
        if (!collisionDeath){
            Player.player_data.health += 200;
        }

        explode_frameUpdate.addActionListener(e -> {
            ocelot_sprite = resources.explosion[explosion_frameCount];
            if (!enemy_properties.dead) {
                ExplosionSound();
                enemy_properties.dead = true;
                fire_bullet.stop();
            }
            if (explosion_frameCount < 21) {
                explosion_frameCount++;
            } else if (explosion_frameCount >= 21) {
                explode_frameUpdate.stop();
                cleanUp();
            }

        });

        explode_frameUpdate.start();

    }

    private void ExplosionSound() {
        resources.explosion_sound.play();
    }

    private void cleanUp() {
        explode_frameUpdate.stop();
        fire_bullet.stop();
        movement_frameUpdate.stop();
        EnemyPane.enemies.remove(this);
    }

    public CharacterProperties enemy_properties(){
        return enemy_properties;
    }

    public BufferedImage enemy_sprite(){
        return ocelot_sprite;
    }

    public Point targetPoint(){
        return targetPoint;
    }

    public Rectangle hitBox(){
        return hitBox;
    }

}

class Karmakazi implements Enemy {

    private static final double targetTime = 300000;
    private double currentTime;

    public BufferedImage karmakazi_sprite;

    private final Timer fire_bullet;
    private final Timer explode_frameUpdate;
    private final Timer movement_frameUpdate;

    public Rectangle hitBox;
    public Point targetPoint;
    public final CharacterProperties enemy_properties;

    private double y_increment = 1;
    private double x_increment = 1;

    private int onFire_frameCount;
    private int explosion_frameCount;
    private final int enemyNumber;

    private boolean collisionDeath;

    Karmakazi(int xcord, int ycord) {

        try {
            karmakazi_sprite = ImageIO.read(getClass().getResource("/resources/karmakazi.png"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        currentTime += 33;

        enemy_properties = new CharacterProperties(xcord, ycord, 250, 2);
        hitBox = new Rectangle();
        targetPoint = new Point(Player.player_data.x, Player.player_data.x);

        onFire_frameCount = 0;
        explosion_frameCount = 0;
        enemyNumber = EnemyPane.enemyCount;
        x_increment = 2;
        y_increment = 2;
        collisionDeath = false;

        movement_frameUpdate = new Timer(resources.REFRESH_RATE, null);
        explode_frameUpdate = new Timer((int)(resources.REFRESH_RATE * 1.5), null);
        fire_bullet = new Timer(5000, null);

        movement_frameUpdate.addActionListener(e -> {

            enemy_properties.x = (int) (enemy_properties.x + currentTime * (targetPoint.x - enemy_properties.x) / targetTime);
            enemy_properties.y = (int) (enemy_properties.y + currentTime * (targetPoint.y - enemy_properties.y) / targetTime);

            currentTime += 33;

            hitBox = new Rectangle(enemy_properties.x - 50, enemy_properties.y - 25, 60, 60);
            targetPoint.x = Player.player_data.x;
            targetPoint.y = Player.player_data.y;

            if (hitBox.intersects(Player.hitBox)) {
                movement_frameUpdate.stop();
                Player.player_data.health -= 100;
                enemy_properties.health = 0;
                collisionDeath = true;
            }

            if (enemy_properties.health <= 0) {
                explode();
            } else if (enemy_properties.health <= 50) {
                karmakazi_sprite = resources.karmakazi_onFire[onFire_frameCount];
                if (onFire_frameCount < 43) {
                    onFire_frameCount++;
                } else {
                    onFire_frameCount = 0;
                }
            }


        });
        fire_bullet.addActionListener(e -> {

            MainWindow.bullet_pane.fireBullet(
                    new Point(enemy_properties.x, enemy_properties.y),
                    targetPoint, true);

        });
        fire_bullet.setInitialDelay(ThreadLocalRandom.current().nextInt(5000, 6500));

        movement_frameUpdate.start();
        fire_bullet.start();

    }

    private void explode() {

        movement_frameUpdate.stop();
        if (!collisionDeath){
            Player.player_data.health += 150;
        }

        explode_frameUpdate.addActionListener(e -> {
            karmakazi_sprite = resources.explosion[explosion_frameCount];
            if (!enemy_properties.dead) {
                ExplosionSound();
                enemy_properties.dead = true;
                fire_bullet.stop();
            }
            if (explosion_frameCount < 21) {
                explosion_frameCount++;
            } else if (explosion_frameCount >= 21) {
                cleanUp();
            }

        });

        explode_frameUpdate.start();

    }

    private void ExplosionSound() {
        resources.explosion_sound.play();
    }

    private void cleanUp() {
        fire_bullet.stop();
        movement_frameUpdate.stop();
        EnemyPane.enemies.remove(this);
    }

    public CharacterProperties enemy_properties(){
        return enemy_properties;
    }

    public BufferedImage enemy_sprite(){
        return karmakazi_sprite;
    }

    public Point targetPoint(){
        return targetPoint;
    }

    public Rectangle hitBox(){
        return hitBox;
    }

}

class RegularEnemy implements Enemy {

    public BufferedImage enemy_sprite;

    private final Timer fire_bullet;
    private final Timer explode_frameUpdate;
    private final Timer movement_frameUpdate;

    public Rectangle hitBox;
    public Point targetPoint;
    public final CharacterProperties enemy_properties;

    private double y_increment = 1;
    private double x_increment = 1;

    private int onFire_frameCount;
    private int explosion_frameCount;
    private final int enemyNumber;

    private boolean collisionDeath;

    RegularEnemy(int xcord, int ycord) {

        try {
            enemy_sprite = ImageIO.read(getClass().getResource("/resources/regular_enemy.png"));
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        enemy_properties = new CharacterProperties(xcord, ycord, 100, 1);
        hitBox = new Rectangle();
        targetPoint = new Point(Player.player_data.x, Player.player_data.x);

        onFire_frameCount = 0;
        explosion_frameCount = 0;
        enemyNumber = EnemyPane.enemyCount;
        x_increment = 1;
        y_increment = 1;
        collisionDeath = false;

        movement_frameUpdate = new Timer(resources.REFRESH_RATE, null);
        explode_frameUpdate = new Timer((int)(resources.REFRESH_RATE * 1.5), null);
        fire_bullet = new Timer(1500, null);

        movement_frameUpdate.addActionListener(e -> {

            enemy_properties.x += x_increment;
            enemy_properties.y += y_increment;

            if (enemy_properties.y > 500) {
                y_increment = -1;
            } else if (enemy_properties.y < 50) {
                y_increment = 1;
            }

            if (enemy_properties.x > 1200) {
                x_increment = -1;
            } else if (enemy_properties.x < 50) {
                x_increment = 1;
            }

            hitBox = new Rectangle(enemy_properties.x - 50, enemy_properties.y - 25, 60, 60);
            if (hitBox.intersects(Player.hitBox)) {
                movement_frameUpdate.stop();
                Player.player_data.health -= 50;
                enemy_properties.health = 0;
                collisionDeath = true;
            }
            targetPoint.x = Player.player_data.x;
            targetPoint.y = Player.player_data.y;

            if (enemy_properties.health <= 0) {
                explode();
            } else if (enemy_properties.health <= 50) {
                enemy_sprite = resources.enemy_onFire[onFire_frameCount];
                if (onFire_frameCount < 43) {
                    onFire_frameCount++;
                } else {
                    onFire_frameCount = 0;
                }
            }


        });
        fire_bullet.addActionListener(e -> {

            MainWindow.bullet_pane.fireBullet(
                    new Point(enemy_properties.x, enemy_properties.y),
                    targetPoint, true);

        });
        fire_bullet.setInitialDelay(ThreadLocalRandom.current().nextInt(5000, 6500));

        movement_frameUpdate.start();
        fire_bullet.start();

    }

    private void explode() {

        movement_frameUpdate.stop();
        if (!collisionDeath){
            Player.player_data.health += 80;
        }

        explode_frameUpdate.addActionListener(e -> {
            enemy_sprite = resources.explosion[explosion_frameCount];
            if (!enemy_properties.dead) {
                ExplosionSound();
                enemy_properties.dead = true;
                fire_bullet.stop();
            }
            if (explosion_frameCount < 21) {
                explosion_frameCount++;
            } else if (explosion_frameCount >= 21) {
                explode_frameUpdate.stop();
                cleanUp();
            }

        });

        explode_frameUpdate.start();

    }

    private void ExplosionSound() {
        resources.explosion_sound.play();
    }

    private void cleanUp() {
        explode_frameUpdate.stop();
        fire_bullet.stop();
        movement_frameUpdate.stop();
        EnemyPane.enemies.remove(this);
    }

    public CharacterProperties enemy_properties(){
        return enemy_properties;
    }

    public BufferedImage enemy_sprite(){
        return enemy_sprite;
    }

    public Point targetPoint(){
        return targetPoint;
    }

    public Rectangle hitBox(){
        return hitBox;
    }

}

class EnemyPane extends JPanel {

    public static int enemyCount = 0;
    public static final ArrayList<Enemy> enemies = new ArrayList<>();
    public Timer scheduledSpawn;

    private boolean allDead = false;

    EnemyPane() {

        setBounds(0, 0, 1280, 750);
        setOpaque(false);
        setLayout(null);

        scheduledSpawn = new Timer(1000, null);
        scheduledSpawn.addActionListener(e -> {

            allDead = true;

            for (int i = 0; i < EnemyPane.enemies.size(); i++) {
                if (!(EnemyPane.enemies.get(i) == null)) {
                    if (!EnemyPane.enemies.get(i).enemy_properties().dead) {
                        allDead = false;
                    }
                }
            }

            if (allDead) {
                spawnMoreEnemy(2);
            }
        });

    }

    @Override
    protected void paintComponent (Graphics g){

        super.paintComponent(g);

        for (int i = 0; i < EnemyPane.enemies.size(); i++){

            Graphics2D g2d = (Graphics2D) g.create();

            int initialX = EnemyPane.enemies.get(i).enemy_properties().x;
            int initialY = EnemyPane.enemies.get(i).enemy_properties().y;
            Point targetPoint = EnemyPane.enemies.get(i).targetPoint();

            double rotation = 0f;

            int width = initialX * 2;
            int height = initialY * 2;

            if (targetPoint != null) {

                int x = width / 2;
                int y = height / 2;

                int deltaX = targetPoint.x - x;
                int deltaY = targetPoint.y - y;

                if (!(deltaX == 0 || deltaY == 0)) {

                    rotation = -Math.atan2(deltaX, deltaY);
                    rotation = Math.toDegrees(rotation) + 180;
                }
            }

            int x = (width - 40) / 2;
            int y = (height - 40) / 2;

            g2d.rotate(Math.toRadians(rotation), width / 2, height / 2);

            g2d.drawImage(EnemyPane.enemies.get(i).enemy_sprite(), x, y, this);

        }

    }

    private void spawnMoreEnemy(int spawnNumber) {

        for (int i = 0; i < spawnNumber; i++) {
            addRegularEnemy();
            addKarmakazi();
            addOcelot();
            //addBlockade();
        }

    }

    private void addRegularEnemy() {

        int randomX = ThreadLocalRandom.current().nextInt(20, 1100);
        enemies.add(new RegularEnemy(randomX, -50));

        System.out.println("Enemy " + enemyCount + " created");
        enemyCount++;

    }

    public void addKarmakazi(){

        int randomX = ThreadLocalRandom.current().nextInt(20, 1100);
        enemies.add(new Karmakazi(randomX, -50));

        System.out.println("Karmakazi " + enemyCount + " created");
        enemyCount++;

    }

    public void addOcelot(){

        int randomX = ThreadLocalRandom.current().nextInt(20, 1200);
        enemies.add(new Ocelot(randomX, -50));

        System.out.println("Ocelot " + enemyCount + " created");
        enemyCount++;

    }

    public void addBlockade(){

        for (int i = 40; i < 1280; i += 120){
            enemies.add(new Blockade(i, -50));
            enemyCount ++;
        }

        System.out.println("Blockade group spawned");

    }

    public void init(){

        Timer delay = new Timer(10, null);
        delay.addActionListener(e -> {
            spawnMoreEnemy(2);
            scheduledSpawn.start();
        });

        delay.setInitialDelay(5000);

        //*** Delete this one line of code and execute the program.
        //*** There will be a beautiful bug.
        delay.setRepeats(false);

        delay.start();
    }

}

class Player extends JPanel implements MouseListener, MouseMotionListener, ActionListener {

    public static CharacterProperties player_data;
    public static final Rectangle hitBox = new Rectangle();
    private static BufferedImage player_sprite;
    private static int onFire_frameCount;
    public static int bullet_heat_factor;
    private static int secondary_heat_factor;

    private static Timer perpendicular_frameUpdate;
    private static Timer lateral_frameUpdate;
    private static Timer fire_bullet;
    private static Timer heat_factor;

    private boolean warningAlreadyStarted;
    public static boolean isFiring;
    public static boolean over_heating;
    private static boolean over_heating_notified;

    MouseAdapter over_heat_warning;

    Player() {

        super();

        setBounds(0, 0, 1280, 750);
        setLayout(null);
        setOpaque(false);
        setFocusable(true);
        requestFocus();

        onFire_frameCount = 0;
        bullet_heat_factor = 0;
        secondary_heat_factor = 0;
        warningAlreadyStarted = false;
        isFiring = false;
        over_heating = false;
        over_heating_notified = false;

        player_data = new CharacterProperties(500, 650, 800, 0);
        player_sprite = resources.player_sprite;
        hitBox.setBounds(player_data.x, player_data.y, 50, 50);

        perpendicular_frameUpdate = new Timer(resources.REFRESH_RATE, null);
        lateral_frameUpdate = new Timer(resources.REFRESH_RATE, null);
        fire_bullet = new Timer(180, null);
        heat_factor = new Timer(resources.REFRESH_RATE, null);
        Timer health_frameUpdate = new Timer(resources.REFRESH_RATE, null);

        health_frameUpdate.addActionListener(e -> {
            hitBox.setBounds(player_data.x - 23, player_data.y - 23, 80, 80);
            if (player_data.health <= 100) {
                if (!warningAlreadyStarted){
                    resources.low_health.play(true);
                    warningAlreadyStarted = true;
                }
                player_sprite = resources.player_on_fire[onFire_frameCount];

                if (onFire_frameCount < 43) {
                    onFire_frameCount++;
                } else {
                    onFire_frameCount = 0;
                }

            } else if (player_data.health > 100){

                if (warningAlreadyStarted){
                    resources.low_health.stop();
                    warningAlreadyStarted = false;
                }

                player_sprite = resources.player_sprite;

            }
        });

        fire_bullet.addActionListener(this);

        over_heat_warning = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                resources.lock_on.play();
            }
        };

        heat_factor.addActionListener(e -> {
            if (fire_bullet.isRunning()){
                secondary_heat_factor ++;
                isFiring = true;
            } else {
                secondary_heat_factor -= 3;
                isFiring = false;
            }
            if (secondary_heat_factor < 0){
                bullet_heat_factor = 0;
                secondary_heat_factor = 0;
            } else if (secondary_heat_factor > 179 && !over_heating){
                bullet_heat_factor = 179;
                secondary_heat_factor = 280;
                if (!over_heating){
                    resources.lock_on.play();
                    over_heating = true;
                    fire_bullet.stop();
                    removeMouseListener(this);
                    addMouseListener(over_heat_warning);
                }
            } else if (secondary_heat_factor <= 179) {
                bullet_heat_factor = secondary_heat_factor;
                if (over_heating){
                    over_heating = false;
                    removeMouseListener(over_heat_warning);
                    addMouseListener(this);
                }
            }
        });

        //lateral control
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_LEFT) {

                    lateral_frameUpdate.addActionListener(e14 -> {

                        if (player_data.x >= 0) {
                            player_data.x -= 1;
                            repaint();
                        } else {
                            player_data.x = 0;
                            lateral_frameUpdate.addActionListener(null);
                        }

                    });
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {

                    lateral_frameUpdate.addActionListener(e13 -> {
                        if (player_data.x <= 1280) {
                            player_data.x += 1;
                            repaint();
                        } else {
                            player_data.x = 1280;
                            lateral_frameUpdate.addActionListener(null);
                        }
                    });
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        //perpendicular control
        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {}

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_UP) {

                    perpendicular_frameUpdate.addActionListener(e12 -> {
                        if (player_data.y >= 60) {
                            player_data.y -= 1;
                            repaint();
                        } else {
                            player_data.y = 60;
                            perpendicular_frameUpdate.addActionListener(null);
                        }
                    });

                    perpendicular_frameUpdate.start();

                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {

                    perpendicular_frameUpdate.addActionListener(e1 -> {
                        if (player_data.y <= 750) {
                            player_data.y += 1;
                            repaint();
                        } else {
                            player_data.y = 750;
                            perpendicular_frameUpdate.addActionListener(null);
                        }
                    });

                    perpendicular_frameUpdate.start();

                }
            }

            @Override
            public void keyReleased(KeyEvent e) {}
        });

        addKeyListener(new KeyListener() {
            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_SPACE){
                    launcher.game.enemy_pane.init();
                }
            }

            @Override
            public void keyReleased(KeyEvent e) {

            }
        });

        addMouseListener(this);
        addMouseMotionListener(this);

        perpendicular_frameUpdate.start();
        lateral_frameUpdate.start();
        health_frameUpdate.start();
        heat_factor.start();

    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        double rotation = 0f;

        int width = player_data.x * 2;
        int height = player_data.y * 2;

        if (resources.mouse_location != null) {
            int x = width / 2;
            int y = height / 2;

            int deltaX = resources.mouse_location.x - x;
            int deltaY = resources.mouse_location.y - y;

            rotation = -Math.atan2(deltaX, deltaY);

            rotation = Math.toDegrees(rotation) + 180;
        }
        int x = (width - 50) / 2;
        int y = (height - 50) / 2;

        g2d.rotate(Math.toRadians(rotation), width / 2, height / 2);
        g2d.drawImage(player_sprite, x, y, this);

    }

    public void mouseMoved(MouseEvent e) {
        resources.mouse_location = e.getPoint();
        repaint();
    }

    public void mousePressed(MouseEvent e) {
        Player.fire_bullet.start();
    }

    public void mouseReleased(MouseEvent e) {
        Player.fire_bullet.stop();
    }

    public void mouseEntered(MouseEvent e) {
        Player.this.grabFocus();
    }
    public void mouseExited(MouseEvent e) {}
    public void mouseClicked(MouseEvent e) {}
    public void mouseDragged(MouseEvent e) {
        resources.mouse_location = e.getPoint();
        repaint();
    }
    public void actionPerformed(ActionEvent e){
        MainWindow.bullet_pane.fireBullet(
                new Point(player_data.x, player_data.y), resources.mouse_location, false);


        repaint();
    }


}

class PlayerPane extends JPanel {

    private final Player player = new Player();

    PlayerPane() {

        super();

        setBounds(0, 0, 1280, 750);
        setLayout(null);
        setOpaque(false);

        add(player);

    }

}

interface Enemy{

    public CharacterProperties enemy_properties();
    public BufferedImage enemy_sprite();
    public Point targetPoint();
    public Rectangle hitBox();

}
