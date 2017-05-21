import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

class Launcher {

    public static GameGUI gameGUI;

    public static void main(String[] args) {
        init();
    }

    private static void init() {
        TinySound.init();
        Resources.importBulletResources();
        Resources.importPlayerResources();
        Resources.importEnemyResources();
        Resources.importHUDResources();
        gameGUI = new GameGUI();
        Resources.cursor_frameUpdate();
    }
}

class Resources {

    public static final byte FRAME_RATE = 30;
    public static final byte REFRESH_RATE = (byte) (1000 / FRAME_RATE);

    public static final BufferedImage[] space_background = new BufferedImage[2];
    public static final BufferedImage[] bullet_impact = new BufferedImage[9];
    public static final BufferedImage[] regular_enemy_fire = new BufferedImage[44];
    public static final BufferedImage[] karmakazi_fire = new BufferedImage[44];
    public static final BufferedImage[] ocelot_fire = new BufferedImage[44];
    public static final BufferedImage[] explosion = new BufferedImage[22];
    public static final BufferedImage[] player_fire = new BufferedImage[44];

    public static final BufferedImage[] left_hud = new BufferedImage[731];
    public static final BufferedImage[] logo_hud = new BufferedImage[2 * 150];
    public static final BufferedImage[] health_meter = new BufferedImage[80];
    public static final BufferedImage[] low_health_meter = new BufferedImage[30];
    public static final BufferedImage[] weapon_state_engage = new BufferedImage[180];
    public static final BufferedImage[] weapon_state_normal = new BufferedImage[180];
    public static final BufferedImage[] weapon_state_overHeat = new BufferedImage[30];

    public static final BufferedImage[] player_teleport = new BufferedImage[11];
    public static final BufferedImage[] bubble_init = new BufferedImage[30];
    public static final BufferedImage[] bubble = new BufferedImage[150];

    private static final BufferedImage[] regular_cursor = new BufferedImage[60];
    private static final BufferedImage[] aimed_cursor = new BufferedImage[60];

    public static BufferedImage[] health_init = new BufferedImage[60];
    public static BufferedImage[] weapon_state_init = new BufferedImage[60];

    public static BufferedImage bullet_sprite;
    public static BufferedImage large_bullet_sprite;
    public static BufferedImage player_sprite;
    public static BufferedImage regular_enemy_sprite;
    public static BufferedImage karmakazi_sprite;
    public static BufferedImage ocelot_sprite;
    public static BufferedImage boot_confirmation;
    public static BufferedImage blockade_sprite;
    public static BufferedImage boss_sprite;
    public static BufferedImage cheat;

    public static Point mouse_location = new Point(0,0);

    public static final Font standard = new Font("Calibri",Font.PLAIN,13);
    public static final Color techno_RED = Color.decode("#ed3737");
    public static final Color techno_BLUE = Color.decode("#2bede6");

    public static int total_points = 0;
    public static int top_score;

    private static byte cursor_frameCount = 0;
    private static final Rectangle cursor_hitBox = new Rectangle();
    private static boolean cursor_onTarget;
    private static Cursor cursor;

    public static Sound enemy_fire = TinySound.loadSound(Resources.class.getResource("/resources/sound/enemy_fire.wav"));
    public static Sound friendly_fire = TinySound.loadSound(Resources.class.getResource("/resources/sound/friendly_fire.wav"));
    private static Sound explosion_sound = TinySound.loadSound(Resources.class.getResource("/resources/sound/explosion.wav"));
    public static Sound over_heat = TinySound.loadSound(Resources.class.getResource("/resources/sound/over_heat.wav"));
    private static Music music = TinySound.loadMusic(Resources.class.getResource("/resources/sound/music.wav"));
    public static Music low_health = TinySound.loadMusic(Resources.class.getResource("/resources/sound/low_health_warning.wav"));

    public static boolean infinite_health = false;
    public static boolean large_bullet = false;

    private static void importCursorResources() {

        try {
            for (int i = 0; i < 60; i++) {
                Resources.regular_cursor[i] =
                        ImageIO.read(Resources.class.getResource("/resources/sequence/cursor/regular/" + i + ".png"));
                Resources.aimed_cursor[i] =
                        ImageIO.read(Resources.class.getResource("/resources/sequence/cursor/aimed/" + i + ".png"));
            }
        } catch (java.io.IOException e) {
            fileNotFound(e);
        }

    }

    public static void cursor_frameUpdate() {

        importCursorResources();

        ScheduledExecutorService cursor_frameUpdate = Executors.newScheduledThreadPool(1);
        cursor_frameUpdate.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                cursor_onTarget = false;

                cursor_hitBox.setBounds(mouse_location.x, mouse_location.y, 50, 50);

                for (int i = 0; i < EnemyPane.enemies.size(); i++) {
                    if (EnemyPane.enemies.get(i) != null) {
                        if (cursor_hitBox.intersects(EnemyPane.enemies.get(i).hitBox())) {
                            cursor_onTarget = true;
                        }
                    }
                }

                if (cursor_onTarget) {
                    cursor = Toolkit.getDefaultToolkit().createCustomCursor(aimed_cursor[cursor_frameCount], new Point(0, 0), null);
                } else {
                    cursor = Toolkit.getDefaultToolkit().createCustomCursor(regular_cursor[cursor_frameCount], new Point(0, 0), null);
                }

                Launcher.gameGUI.getContentPane().setCursor(cursor);

                if (cursor_frameCount < 59) {
                    cursor_frameCount++;
                } else {
                    cursor_frameCount = 0;
                }
            }
        }, 0, REFRESH_RATE, TimeUnit.MILLISECONDS);

    }

    public static void importBulletResources() {

        try {
            bullet_sprite = ImageIO.read(Resources.class.getResource("/resources/sprite/bullet_sprite.png"));
            large_bullet_sprite = ImageIO.read(Resources.class.getResource("/resources/sprite/large_bullet.png"));
            for (int i = 0; i < 9; i++) {
                bullet_impact[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/bullet_impact/" + i + ".png"));
            }
        } catch (java.io.IOException e) {
            fileNotFound(e);
        }
    }

    public static void importEnemyResources() {

        try {
            regular_enemy_sprite = ImageIO.read(Resources.class.getResource("/resources/sprite/regular_enemy.png"));
            ocelot_sprite = ImageIO.read(Resources.class.getResource("/resources/sprite/ocelot.png"));
            karmakazi_sprite = ImageIO.read(Resources.class.getResource("/resources/sprite/karmakazi.png"));
            blockade_sprite = ImageIO.read(Resources.class.getResource("/resources/sprite/blockade.png"));
            boss_sprite = ImageIO.read(Resources.class.getResource("/resources/sprite/boss_sprite.png"));
            for (int i = 0; i < 44; i++) {
                regular_enemy_fire[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/enemy1_on_fire/" + i + ".png"));
                karmakazi_fire[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/karmakazi_on_fire/" + i + ".png"));
                ocelot_fire[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/ocelot_on_fire/" + i + ".png"));
            }
            for (int i = 0; i < 21; i++) {
                explosion[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/explosion/" + i + ".png"));
            }
        } catch (java.io.IOException e) {
            fileNotFound(e);
        }
    }

    public static void importPlayerResources() {

        try {
            for (int i = 0; i < 44; i++) {
                player_fire[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/self_on_fire/" + i + ".png"));
            }
            for (int i = 0; i < 11; i++){
                player_teleport[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/self_teleporting/" + i + ".png"));
            }
            for (int i = 0; i < 30; i++){
                bubble_init[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/bubble_init/" + i + ".png"));
            }
            for (int i = 0; i < 150; i++){
                bubble[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/protective_bubble/" + i + ".png"));
            }
            player_sprite = ImageIO.read(Resources.class.getResource("/resources/sprite/spaceship_sprite.png"));
        } catch (java.io.IOException e) {
            fileNotFound(e);
        }

    }

    public static void importHUDResources(){

        try {
            boot_confirmation = ImageIO.read(Resources.class.getResource("/resources/gui/start_confirmation.png"));
            cheat = ImageIO.read(Resources.class.getResource("/resources/gui/cheat.png"));
            for (int i = 0; i < 731; i++) {
                left_hud[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/dev_hud_1/" + i + ".png"));
            }
            for (int i = 0; i < 150; i++){
                logo_hud[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/sub_hud_logo/init/" + i + ".png"));
                logo_hud[150 + i] = ImageIO.read(Resources.class.getResource("/resources/sequence/sub_hud_logo/loop/" + i + ".png"));
            }
            for (int i = 0; i < 30; i++){
                low_health_meter[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/sub_hud_health/low_health_pane/" + i + ".png"));
                weapon_state_overHeat[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/sub_hud_weapon/over_heat/" + i + ".png"));
            }
            for (int i = 0; i < 80; i++){
                health_meter[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/sub_hud_health/normal_health/" + i + ".png"));
            }
            for (int i = 0; i < 60; i++){
                health_init[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/sub_hud_health/health_init/" + i + ".png"));
                weapon_state_init[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/sub_hud_weapon/init/" + i + ".png"));
            }
            for (int i = 0; i < 180; i++){
                weapon_state_normal[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/sub_hud_weapon/normal/" + i + ".png"));
                weapon_state_engage[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/sub_hud_weapon/engage/" + i + ".png"));
            }
            for (int i = 0; i < 2; i++){
                space_background[i] = ImageIO.read(Resources.class.getResource("/resources/background_loop.jpg"));
            }
        } catch (java.io.IOException e) {
            fileNotFound(e);
        }

    }

    public static void cleanUpAnimation(){

        health_init = null;
        weapon_state_init = null;

        for (int i = 0; i < 300; i ++){
            Resources.left_hud[i] = null;
        }
        for (int i = 0; i < 100; i++){
            Resources.logo_hud[i] = null;
        }

    }

    private static void fileNotFound(Exception e) {

        System.out.println(e);
        JOptionPane.showMessageDialog(null,"One or more files required to run this program is missing.\n" +
                "Please ensure that the \"resource\" folder is in the same folder as the java files.","Error",JOptionPane.ERROR_MESSAGE);
        System.exit(20);

    }

    public static void explosionSound(){
        explosion_sound.play();
    }

    public static void music() {
        music.play(true);
    }

    public static void bulletSound(boolean e){
        if (e) {
            enemy_fire.play();
        } else {
            friendly_fire.play();
        }
    }
}

class GameGUI extends JFrame{

    public static FriendlyPane friendly_pane;
    public static EnemyPane enemy_pane;
    public static BulletPane bullet_pane;
    public static HUD hud;

    private static ConfirmStart start_confirmation;
    private static CollisionLogic collision_logic;
    private static Player player;
    private static Space space;

    public GameGUI() {

        super();

        setSize(1280, 750);
        setUndecorated(true);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.black);

        friendly_pane = new FriendlyPane();
        enemy_pane = new EnemyPane();
        bullet_pane = new BulletPane();
        hud = new HUD();

        start_confirmation = new ConfirmStart(this);
        space = new Space();
        player = new Player();
        collision_logic = new CollisionLogic(EnemyPane.enemies, BulletPane.bullets,player);

        Resources.music();

        add(start_confirmation);
        add(hud);
        add(enemy_pane);
        add(player);
        add(bullet_pane);
        add(space);

        setVisible(true);

    }

    public void init() {
        enemy_pane.init();
        hud.init();
    }
}
