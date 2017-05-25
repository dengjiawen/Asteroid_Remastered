import kuusisto.tinysound.Music;
import kuusisto.tinysound.Sound;
import kuusisto.tinysound.TinySound;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.TimerTask;
import java.util.concurrent.*;

class Launcher {

    public static GameGUI gameGUI;

    public static boolean player_loaded = false;
    public static boolean enemy_loaded = false;
    public static boolean hud_loaded = false;
    public static boolean space_loaded = false;

    private static ExecutorService init = Executors.newCachedThreadPool();
    private static Timer notify = new Timer(Resources.REFRESH_RATE,null);

    public static void main(String[] args) {
        JOptionPane.showMessageDialog(null,"Close this window to start.","Close",JOptionPane.INFORMATION_MESSAGE);

        notify.addActionListener(e -> {
            System.out.println("checking");
            if (player_loaded && enemy_loaded && hud_loaded && space_loaded){

                SwingUtilities.invokeLater(() -> {
                    gameGUI = new GameGUI();
                    gameGUI.setVisible(true);
                });

                System.out.println("Woke");

                notify.stop();
            }
        });

        notify.start();

        init.submit(() -> {
            init();
        });

    }

    private static void init() {

        TinySound.init();
        Resources.importBulletResources();
        Resources.importPlayerResources();
        Resources.importEnemyResources();
        Resources.importHUDResources();
        Resources.importSpaceResources();

    }
}

class Resources {

    public static final byte FRAME_RATE = 30;
    public static final byte REFRESH_RATE = (byte) (1000 / FRAME_RATE);

    public static final int FRAME_WIDTH = 1450;
    public static final int FRAME_HEIGHT = 800;

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

    public static final BufferedImage[] asteroid_fire = new BufferedImage[180];
    public static final BufferedImage[] asteroid_ice = new BufferedImage[317];

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
    public static BufferedImage blockade_sprite;
    public static BufferedImage boss_sprite;
    public static BufferedImage shockwave;

    public static BufferedImage cheat;
    public static BufferedImage boot_confirmation;
    public static BufferedImage point_slot;

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

    public static ExecutorService enemy_resources = Executors.newCachedThreadPool();
    public static ExecutorService player_resources = Executors.newCachedThreadPool();
    public static ExecutorService HUD_resources = Executors.newCachedThreadPool();
    public static ExecutorService space_resources = Executors.newCachedThreadPool();
    private static ScheduledExecutorService cursor_periodic_update = Executors.newScheduledThreadPool(0);
    private static ExecutorService cursor_frameUpdate = Executors.newCachedThreadPool();

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

        cursor_periodic_update.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {

                cursor_frameUpdate.submit(() -> {

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
                });
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

        enemy_resources.submit(() -> {
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

            Launcher.enemy_loaded = true;
            System.out.println(Launcher.enemy_loaded);

            enemy_resources.shutdownNow();
        });
    }

    public static void importPlayerResources() {

        player_resources.submit(() -> {
            try {
                for (int i = 0; i < 44; i++) {
                    player_fire[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/self_on_fire/" + i + ".png"));
                }
                for (int i = 0; i < 11; i++) {
                    player_teleport[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/self_teleporting/" + i + ".png"));
                }
                for (int i = 0; i < 30; i++) {
                    bubble_init[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/bubble_init/" + i + ".png"));
                }
                for (int i = 0; i < 150; i++) {
                    bubble[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/protective_bubble/" + i + ".png"));
                }
                player_sprite = ImageIO.read(Resources.class.getResource("/resources/sprite/spaceship_sprite.png"));
                shockwave = ImageIO.read(Resources.class.getResource("/resources/shockwave.png"));
            } catch (java.io.IOException e) {
                fileNotFound(e);
            }

            Launcher.player_loaded = true;
            System.out.println(Launcher.player_loaded);

            player_resources.shutdownNow();
        });
    }

    public static void importHUDResources(){

        HUD_resources.submit(() -> {
            try {
                boot_confirmation = ImageIO.read(Resources.class.getResource("/resources/gui/start_confirmation.png"));
                cheat = ImageIO.read(Resources.class.getResource("/resources/gui/cheat.png"));
                point_slot = ImageIO.read(Resources.class.getResource("/resources/hud/point_slot.png"));
                for (int i = 0; i < 731; i++) {
                    left_hud[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/dev_hud_1/" + i + ".png"));
                }
                for (int i = 0; i < 150; i++) {
                    logo_hud[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/sub_hud_logo/init/" + i + ".png"));
                    logo_hud[150 + i] = ImageIO.read(Resources.class.getResource("/resources/sequence/sub_hud_logo/loop/" + i + ".png"));
                }
                for (int i = 0; i < 30; i++) {
                    low_health_meter[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/sub_hud_health/low_health_pane/" + i + ".png"));
                    weapon_state_overHeat[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/sub_hud_weapon/over_heat/" + i + ".png"));
                }
                for (int i = 0; i < 80; i++) {
                    health_meter[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/sub_hud_health/normal_health/" + i + ".png"));
                }
                for (int i = 0; i < 60; i++) {
                    health_init[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/sub_hud_health/health_init/" + i + ".png"));
                    weapon_state_init[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/sub_hud_weapon/init/" + i + ".png"));
                }
                for (int i = 0; i < 180; i++) {
                    weapon_state_normal[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/sub_hud_weapon/normal/" + i + ".png"));
                    weapon_state_engage[i] = ImageIO.read(Resources.class.getResource("/resources/sequence/sub_hud_weapon/engage/" + i + ".png"));
                }
            } catch (java.io.IOException e) {
                fileNotFound(e);
            }

            Launcher.hud_loaded = true;
            System.out.println(Launcher.hud_loaded);

            HUD_resources.shutdownNow();
        });

    }

    public static void importSpaceResources(){
        space_resources.submit(() -> {
            try {
                for (int i = 0; i < 2; i++) {
                    space_background[i] = ImageIO.read(Resources.class.getResource("/resources/background_loop.jpg"));
                }
                for (int i = 0; i < 180; i++){
                    asteroid_fire[i] = ImageIO.read(Resources.class.getResource("/resources/asteroids/fire_rock/sequence/" + i + ".png"));
                }
                for (int i = 0; i < 317; i++){
                    asteroid_ice[i] = ImageIO.read(Resources.class.getResource("/resources/asteroids/ice_rock/sequence/" + i + ".png"));
                }
            } catch (java.io.IOException e) {
                fileNotFound(e);
            }

            Launcher.space_loaded = true;
            System.out.println(Launcher.space_loaded);

            space_resources.shutdownNow();
        });
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

    public static EnemyPane enemy_pane;
    public static BulletPane bullet_pane;
    public static HUD hud;

    private static ConfirmStart start_confirmation;
    private static CollisionLogic collision_logic;
    public static Player player;
    public static Space space;

    public GameGUI() {

        super();

        setSize(Resources.FRAME_WIDTH, Resources.FRAME_HEIGHT);
        setUndecorated(true);
        setResizable(false);
        setLocationRelativeTo(null);
        setLayout(null);
        getContentPane().setBackground(Color.black);

        Resources.cursor_frameUpdate();

        System.out.println(SwingUtilities.isEventDispatchThread());

        bullet_pane = new BulletPane();
        hud = new HUD();

        start_confirmation = new ConfirmStart(this);
        space = new Space();
        player = new Player(new Point((Resources.FRAME_WIDTH - Resources.player_sprite.getWidth())/2, 650));
        enemy_pane = new EnemyPane();
        collision_logic = new CollisionLogic(EnemyPane.enemies, BulletPane.bullets,player);

        Resources.music();

        add(start_confirmation);
        add(hud);
        add(enemy_pane);
        add(player);
        add(bullet_pane);
        add(space);

    }

    public void init() {
        enemy_pane.init();
        hud.init();
    }
}

class ConfirmStart extends JPanel{

    private JButton proceed;
    private JButton abort;

    public ConfirmStart(GameGUI gameGUI){

        super();

        setBounds((Resources.FRAME_WIDTH - 300)/2,(Resources.FRAME_HEIGHT - 309)/2,300, 309);
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
            gameGUI.init();
        });

        abort = new JButton();
        abort.setBounds(0,260,150,49);
        abort.setCursor(new Cursor(Cursor.HAND_CURSOR));
        abort.setBorderPainted(false);
        abort.setOpaque(false);
        abort.setContentAreaFilled(false);
        abort.addActionListener(e -> {
            System.exit(0);
        });

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(MouseEvent e) {
                super.mouseEntered(e);
                ConfirmStart.this.grabFocus();
            }
        });

        add(proceed);
        add(abort);

    }

    @Override
    protected void paintComponent(Graphics g){
        super.paintComponent(g);
        g.drawImage(Resources.boot_confirmation, 0,0,this);
    }

}

class HUD extends JPanel{

    private Timer left_hud_init;
    private Timer health_meter_init;
    private Timer logo_hud_init;
    private Timer logo_hud_frameUpdate;
    private Timer left_hud_frameUpdate;
    private Timer health_meter_frameUpdate;
    private Timer weapon_state_frameUpdate;

    private BufferedImage left_hud;
    private BufferedImage logo_hud;
    private BufferedImage health_meter;
    private BufferedImage weapon_state;

    private int left_hud_frameCount;
    private int logo_hud_frameCount;
    private int health_meter_frameCount;
    private int weapon_state_frameCount;

    private int health_percentage;

    private JLabel health_number;

    public HUD(){

        setBounds((Resources.FRAME_WIDTH - 1280)/2,(Resources.FRAME_HEIGHT - 750)/2,1280, 750);
        setLayout(null);
        setOpaque(false);

        health_number = new JLabel();
        health_number.setBounds(995,186,50,29);
        health_number.setFont(Resources.standard);

        left_hud_frameCount = 0;
        logo_hud_frameCount = 0;
        health_percentage = 0;
        health_meter_frameCount = 0;
        weapon_state_frameCount = 0;

        left_hud_init = new Timer(Resources.REFRESH_RATE, e -> {
            left_hud = Resources.left_hud[left_hud_frameCount];

            if (left_hud_frameCount < 730){
                left_hud_frameCount ++;
            } else {
                left_hud_init.stop();
                left_hud_init = null;
                left_hud_frameUpdate.start();
            }
        });
        left_hud_frameUpdate = new Timer(Resources.REFRESH_RATE, e -> {
            left_hud = Resources.left_hud[left_hud_frameCount];

            if (left_hud_frameCount < 730){
                left_hud_frameCount ++;
            } else {
                left_hud_frameCount = 345;
            }
        });

        logo_hud_init = new Timer(Resources.REFRESH_RATE, e -> {
            logo_hud = Resources.logo_hud[logo_hud_frameCount];

            if (logo_hud_frameCount < 299) {
                logo_hud_frameCount++;
            } else {
                logo_hud_init.stop();
                logo_hud_init = null;
                logo_hud_frameUpdate.start();
            }
        });
        logo_hud_frameUpdate = new Timer(Resources.REFRESH_RATE, e -> {
            logo_hud = Resources.logo_hud[logo_hud_frameCount];

            if (logo_hud_frameCount < 299) {
                logo_hud_frameCount++;
            } else {
                logo_hud_frameCount = 150;
            }
        });

        health_meter_init = new Timer(Resources.REFRESH_RATE, e -> {
            health_number.setForeground(Resources.techno_BLUE);
            health_number.setText("    " + health_percentage);
            if (health_percentage < 100){
                health_percentage ++;
            } else {
                health_meter_init.stop();
                health_meter_init = null;
                health_meter_frameCount = 0;
                health_meter_frameUpdate.start();
                weapon_state_frameUpdate.start();
            }

            health_meter = Resources.health_init[health_meter_frameCount];
            weapon_state = Resources.weapon_state_init[health_meter_frameCount];
            if (health_meter_frameCount < 59) {
                health_meter_frameCount++;
            }
        });
        health_meter_frameUpdate = new Timer(Resources.REFRESH_RATE, e -> {

            health_percentage = GameGUI.player.healthPercentage();

            if (health_percentage > 20) {
                if (health_percentage > this.health_percentage) {
                    this.health_percentage++;
                } else if (health_percentage < this.health_percentage) {
                    this.health_percentage--;
                }
                health_number.setForeground(Resources.techno_BLUE);
                health_number.setText("    " + health_percentage);

                health_meter = Resources.health_meter[this.health_percentage - 21];

            } else if (health_percentage <= 20) {
                health_meter = Resources.low_health_meter[health_meter_frameCount];
                if (health_meter_frameCount < 29) {
                    health_meter_frameCount++;
                } else {
                    health_meter_frameCount = 0;
                }

                if (health_percentage > this.health_percentage) {
                    this.health_percentage++;
                } else if (health_percentage < this.health_percentage) {
                    this.health_percentage--;
                }

                health_number.setForeground(Resources.techno_RED);
                health_number.setText("    " + health_percentage);
            }
        });

        weapon_state_frameUpdate = new Timer(Resources.REFRESH_RATE, e -> {
            if (Player.overHeating) {
                weapon_state = Resources.weapon_state_overHeat[weapon_state_frameCount];
                if (weapon_state_frameCount < 29) {
                    weapon_state_frameCount++;
                } else {
                    weapon_state_frameCount = 0;
                }
            } else if (Player.isFiring) {
                if (Player.bullet_heat_factor <= 179) {
                    weapon_state = Resources.weapon_state_engage[Player.bullet_heat_factor];
                }
            } else {
                weapon_state = Resources.weapon_state_normal[Player.bullet_heat_factor];
            }
        });

        add(health_number);

    }

    @Override
    protected void paintComponent(Graphics g){

        super.paintComponent(g);

        g.drawImage(left_hud, 0, 0, this);
        g.drawImage(logo_hud, 1000, 30, this);
        g.drawImage(health_meter, 1048, 186, this);
        g.drawImage(weapon_state, 1000, 225, this);
        //g.drawImage(Resources.point_slot,1000,500,this);

    }

    public void init() {

        left_hud_init.start();
        logo_hud_init.start();
        health_meter_init.start();

    }

}

class CharacterProperties {

    public Point location;
    public int health;

    public int character_type;
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
    public boolean dead;

    public CharacterProperties(Point location, int health, int character_type) {

        this.location = location;
        this.health = health;
        this.character_type = character_type;
        this.dead = false;

    }

    public void updateLocation (int x, int y){
        location.x += x;
        location.y += y;
    }

    public void setLocation (boolean xChanged, int x, boolean yChanged, int y){
        if (xChanged){
            location.x = x;
        }
        if (yChanged){
            location.y = y;
        }
    }

    public void setLocation (int x, int y){
        location.x = x;
        location.y = y;
    }

    public boolean onTop (){
        return location.y < 80;
    }

    public boolean onBottom (){
        return location.y > Resources.FRAME_HEIGHT;
    }

    public boolean onLeft (){
        return location.x < 0;
    }

    public boolean onRight (){
        return location.x > Resources.FRAME_WIDTH;
    }

    public int getX () {
        return location.x;
    }

    public int getY () {
        return location.y;
    }

}

class Asteroid implements Entitative{

    public BufferedImage asteroid_sprite;
    public CharacterProperties asteroid_properties;

    public Rectangle hitBox;

    private int asteroid_frameCount;

    private Timer asteroid_frameUpdate;

    private Timer explode_frameUpdate;

    public Asteroid(Point origin, int asteroidType){

        explode_frameUpdate = new Timer(Resources.REFRESH_RATE,null);

        hitBox = new Rectangle();

        asteroid_properties = new CharacterProperties(origin, 200, -1);
        asteroid_frameCount = 0;

        asteroid_frameUpdate = new Timer((int)(Resources.REFRESH_RATE * 1.5),null);
        asteroid_frameUpdate.addActionListener(e -> {

            if (asteroid_properties.health <= 0){
                explode();
            }

            asteroid_properties.updateLocation(0,2);

            if (asteroidType == 1) {

                asteroid_sprite = Resources.asteroid_ice[asteroid_frameCount];

                hitBox.setBounds(asteroid_properties.getX() + 80, asteroid_properties.getY() + 80, 60,60);

                if (asteroid_frameCount < Resources.asteroid_ice.length - 1) {
                    asteroid_frameCount++;
                } else {
                    asteroid_frameCount = 0;
                }
            } else if (asteroidType == 2){
                asteroid_sprite = Resources.asteroid_fire[asteroid_frameCount];

                hitBox.setBounds(asteroid_properties.getX() + 14,asteroid_properties.getY() + 14,58,58);

                if (asteroid_frameCount < Resources.asteroid_fire.length - 1) {
                    asteroid_frameCount++;
                } else {
                    asteroid_frameCount = 0;
                }
            }

            GameGUI.space.repaint();

        });

        asteroid_frameUpdate.start();

    }

    public Point location(){
        return asteroid_properties.location;
    }

    public Rectangle hitBox(){
        return hitBox;
    }

    public void explode() {

        asteroid_frameUpdate.stop();
        asteroid_frameCount = 0;

        explode_frameUpdate.addActionListener(e -> {
            asteroid_sprite = Resources.explosion[asteroid_frameCount];
            if (!asteroid_properties.dead) {
                Resources.explosionSound();
                asteroid_properties.dead = true;
            }
            if (asteroid_frameCount < 21) {
                asteroid_frameCount++;
            } else if (asteroid_frameCount >= 21) {
                explode_frameUpdate.stop();
            }

        });

        explode_frameUpdate.start();

    }

}

class Space extends JPanel {

    private int y1;
    private int y2;

    private Timer background_frameUpdate;
    private Timer spawn_asteroid;

    public static ArrayList<Asteroid> asteroids = new ArrayList<>();

    public Space() {

        super();

        setBounds(0, 0, Resources.FRAME_WIDTH, Resources.FRAME_HEIGHT);
        setLayout(null);
        setOpaque(false);

        y1 = 0;
        y2 = -1 * Resources.FRAME_HEIGHT;

        spawn_asteroid = new Timer(Resources.REFRESH_RATE * 5, e -> {
            spawnAsteroid();
        });
        spawn_asteroid.start();

        background_frameUpdate = new Timer(Resources.REFRESH_RATE, e -> {
            if (y1 == Resources.FRAME_HEIGHT) {
                y1 = -1 * Resources.FRAME_HEIGHT;
            } else if (y2 == Resources.FRAME_HEIGHT) {
                y2 = -1 * Resources.FRAME_HEIGHT;
            }

            y1 += 2;
            y2 += 2;

            repaint();
        });

        background_frameUpdate.start();

    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(Resources.space_background[0], 0, y1, Resources.FRAME_WIDTH, Resources.FRAME_HEIGHT, this);
        g.drawImage(Resources.space_background[1], 0, y2, Resources.FRAME_WIDTH, Resources.FRAME_HEIGHT,this);

        Graphics2D g2d = (Graphics2D)g;
        g2d.setColor(new Color(255,0,0,150));

        asteroids.forEach(asteroid -> {
            g.drawImage(asteroid.asteroid_sprite,asteroid.asteroid_properties.getX(),asteroid.asteroid_properties.getY(),this);
        });
    }

    private boolean isSpawnAsteroid() {
        if (Math.random() < 0.7){
            return false;
        } else {
            return true;
        }
    }

    private void spawnAsteroid() {

        int x = ThreadLocalRandom.current().nextInt(0, Resources.FRAME_WIDTH);

        if (isSpawnAsteroid()) {
            asteroids.add(new Asteroid(
                    new Point(ThreadLocalRandom.current().nextInt(0, Resources.FRAME_WIDTH), -200),
                    ThreadLocalRandom.current().nextInt(1, 2 + 1)));
            this.repaint();
        }
    }

}

class BulletProperties {

    public Point location;

    public final boolean enemy_fire;
    public final boolean large_bullet;

    public boolean hit;

    public BulletProperties(Point location, boolean enemy_fire) {
        this.location = location;
        this.enemy_fire = enemy_fire;
        this.large_bullet = Resources.large_bullet;
        this.hit = false;
    }

    public int getX () {
        return location.x;
    }

    public int getY () {
        return location.y;
    }

    public void setLocation (int x, int y){
        location.x = x;
        location.y = y;
    }

    public boolean outOfFrame (){
        return getX() < -10 || getX() > Resources.FRAME_WIDTH + 50 ||
                getY() < -10 || getY() > Resources.FRAME_HEIGHT + 50;
    }


}

class Bullet implements Entitative {

    public BufferedImage bullet_sprite;

    private int targetTime;
    private int currentTime;
    private int explosion_frameCount;

    private Rectangle hitBox;
    private BulletProperties bullet_properties;

    private Timer bullet_impact_frameUpdate;

    private final int x1;
    private final int x2;
    private final int y1;
    private final int y2;

    public Bullet(Point origin, Point target, boolean enemy_fire) {

        Resources.bulletSound(enemy_fire);

        x1 = origin.x;
        x2 = target.x;
        y1 = origin.y;
        y2 = target.y;

        targetTime = 800;
        currentTime = 0;
        explosion_frameCount = 0;
        hitBox = new Rectangle();
        bullet_properties = new BulletProperties(origin, enemy_fire);

        if (bullet_properties.large_bullet){
            bullet_sprite = Resources.large_bullet_sprite;
        } else {
            bullet_sprite = Resources.bullet_sprite;
        }

        bullet_impact_frameUpdate = new Timer(Resources.REFRESH_RATE, null);
        bullet_impact_frameUpdate.addActionListener(e -> {
            if (explosion_frameCount < 8) {
                bullet_sprite = Resources.bullet_impact[explosion_frameCount];
                explosion_frameCount++;
            } else {
                bullet_impact_frameUpdate.stop();
                cleanUp();
            }
        });
    }

    private void cleanUp() {
        BulletPane.removeBullet(this);
    }

    public void hitPlayer() {

        if (bullet_properties.large_bullet) {
            GameGUI.player.changeHealth(-100);
            bullet_impact_frameUpdate.start();
        } else {
            GameGUI.player.changeHealth(-5);
            bullet_impact_frameUpdate.start();
        }

        bullet_properties.hit = true;
    }

    public void hitEnemy(Hostile e){
        e.enemy_properties().health -= 10;

        bullet_impact_frameUpdate.start();
        bullet_properties.hit = true;
    }

    public void hitNothing() {
        bullet_impact_frameUpdate.start();
        bullet_properties.hit = true;
    }

    public void hitAsteroid(Asteroid a){

        System.out.println("Hit Asteroid");

        a.asteroid_properties.health -= 20;

        bullet_impact_frameUpdate.start();
        bullet_properties.hit = true;
    }

    public boolean isViableBullet(){
        return !this.bullet_properties.hit;
    }

    public boolean isEnemyBullet(){
        return this.bullet_properties.enemy_fire;
    }

    public Point location() {
        return bullet_properties.location;
    }

    public Rectangle hitBox() {
        updateHitBox();
        return hitBox;
    }

    public void updateHitBox() {
        if (bullet_properties.large_bullet) {
            hitBox.setBounds(bullet_properties.getX(), bullet_properties.getY(), 30, 30);
        } else {
            hitBox.setBounds(bullet_properties.getX(), bullet_properties.getY(), 10, 10);
        }
    }

    public void tickUpdate() {

        bullet_properties.setLocation(
                (x1 + currentTime * (x2 - x1) / targetTime),
                (y1 + currentTime * (y2 - y1) / targetTime));

        currentTime += 33;

        if (bullet_properties.outOfFrame()) {
            cleanUp();
        }
    }
}

class BulletPane extends JPanel {

    public static ArrayList<Bullet> bullets = new ArrayList<>();
    private int bulletCount = 0;

    private ScheduledExecutorService bullet_periodic_update;
    private ExecutorService bullet_location_update;

    public BulletPane() {

        setBounds(0, 0, Resources.FRAME_WIDTH, Resources.FRAME_HEIGHT);
        setOpaque(false);
        setLayout(null);

        bullet_periodic_update = Executors.newScheduledThreadPool(1);
        bullet_location_update = Executors.newCachedThreadPool();
        bullet_periodic_update.scheduleAtFixedRate(() -> {
            bullet_location_update.submit(() -> {
                bullets.forEach(bullet -> {
                    if (bullet.isViableBullet()) {
                        bullet.tickUpdate();
                    }
                });
            });
        }, 0, Resources.REFRESH_RATE, TimeUnit.MILLISECONDS);

    }

    @Override
    protected void paintComponent(Graphics g){

        super.paintComponent(g);

        bullets.forEach(bullet -> {
            g.drawImage(bullet.bullet_sprite, bullet.location().x,
                    bullet.location().y, this);
        });
    }

    public void fireBullet(Point origin, Point target, boolean enemy_fire){

        final Point p1 = new Point(origin.x, origin.y);
        final Point p2 = new Point(target.x, target.y);

        bullets.add(new Bullet(p1, p2, enemy_fire));
        System.out.println("Bullet " + bulletCount + "fired");
        bulletCount++;

        repaint();

    }

    public static void removeBullet(Bullet bullet){
        bullets.remove(bullet);
    }

}

class Ocelot implements Hostile {

    public BufferedImage ocelot_sprite;

    private final Timer fire_bullet;
    private final Timer explode_frameUpdate;
    private final Timer movement_frameUpdate;

    public Rectangle hitBox;
    public final CharacterProperties enemy_properties;

    private int y_increment = 5;
    private int x_increment = 5;

    private int onFire_frameCount;
    private int explosion_frameCount;
    private final int enemyNumber;
    private boolean collisionDeath;

    Ocelot(Point origin) {

        try {
            ocelot_sprite = Resources.ocelot_sprite;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        enemy_properties = new CharacterProperties(origin, 70, 3);
        hitBox = new Rectangle();

        onFire_frameCount = 0;
        explosion_frameCount = 0;
        enemyNumber = EnemyPane.enemyCount;
        x_increment = 5;
        y_increment = 5;
        collisionDeath = false;

        movement_frameUpdate = new Timer(Resources.REFRESH_RATE, null);
        explode_frameUpdate = new Timer((int)(Resources.REFRESH_RATE * 1.5), null);
        fire_bullet = new Timer(500, null);

        /*movement_frameUpdate.addActionListener(e -> {

            EnemyPane.enemy_location_update.submit(() -> {

            });

        });*/

        fire_bullet.addActionListener(e -> {

            GameGUI.bullet_pane.fireBullet(
                    enemy_properties.location,
                    GameGUI.player.location(), true);

        });
        fire_bullet.setInitialDelay(ThreadLocalRandom.current().nextInt(3000, 4000));

        movement_frameUpdate.start();
        fire_bullet.start();

    }

    public void collisionDeath() {
        GameGUI.player.changeHealth(-20);
        explode(true);
    }

    public boolean isDead(){
        return enemy_properties.dead;
    }

    public void explode(boolean collisionDeath) {

        if (!collisionDeath){
            GameGUI.player.changeHealth(200);
        }

        explode_frameUpdate.addActionListener(e -> {
            ocelot_sprite = Resources.explosion[explosion_frameCount];
            if (!enemy_properties.dead) {
                movement_frameUpdate.stop();
                Resources.explosionSound();
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

    private void cleanUp() {
        EnemyPane.enemies.remove(this);
    }

    public CharacterProperties enemy_properties(){
        return enemy_properties;
    }

    public BufferedImage enemy_sprite(){
        return ocelot_sprite;
    }

    public Point targetPoint(){
        return GameGUI.player.location();
    }

    public Rectangle hitBox(){
        return hitBox;
    }

    public Point location() {
        return enemy_properties.location;
    }

    public void tickUpdate() {
        enemy_properties.updateLocation(x_increment,y_increment);

        if (enemy_properties.onBottom()) {
            y_increment = -5;
        } else if (enemy_properties.onTop()) {
            y_increment = 5;
        }

        if (enemy_properties.onRight()) {
            x_increment = -5;
        } else if (enemy_properties.onLeft()) {
            x_increment = 5;
        }

        hitBox.setBounds(enemy_properties.getX() - 50, enemy_properties.getY() - 25, 40, 40);

        //targetPoint = GameGUI.player.location();

        if (enemy_properties.health <= 0) {
            explode(false);
        } else if (enemy_properties.health <= 50) {
            ocelot_sprite = Resources.ocelot_fire[onFire_frameCount];
            if (onFire_frameCount < 43) {
                onFire_frameCount++;
            } else {
                onFire_frameCount = 0;
            }
        }
    }

}

class Karmakazi implements Hostile {

    private static final double targetTime = 300000;
    private double currentTime;

    public BufferedImage karmakazi_sprite;

    private final Timer fire_bullet;
    private final Timer explode_frameUpdate;
    private final Timer movement_frameUpdate;

    public Rectangle hitBox;
    public final CharacterProperties enemy_properties;

    private int onFire_frameCount;
    private int explosion_frameCount;
    private final int enemyNumber;

    Karmakazi(Point origin) {

        try {
            karmakazi_sprite = Resources.karmakazi_sprite;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        currentTime += 33;

        enemy_properties = new CharacterProperties(origin, 250, 2);
        hitBox = new Rectangle();

        onFire_frameCount = 0;
        explosion_frameCount = 0;
        enemyNumber = EnemyPane.enemyCount;

        movement_frameUpdate = new Timer(Resources.REFRESH_RATE, null);
        explode_frameUpdate = new Timer((int)(Resources.REFRESH_RATE * 1.5), null);
        fire_bullet = new Timer(5000, null);

        fire_bullet.addActionListener(e -> {

            GameGUI.bullet_pane.fireBullet(
                    enemy_properties.location,
                    GameGUI.player.location(), true);

        });
        fire_bullet.setInitialDelay(ThreadLocalRandom.current().nextInt(5000, 6500));

        movement_frameUpdate.start();
        fire_bullet.start();

    }

    public void collisionDeath() {
        GameGUI.player.changeHealth(-100);
        explode(true);
    }

    public boolean isDead(){
        return enemy_properties.dead;
    }

    public void explode(boolean collisionDeath) {

        if (!collisionDeath){
            GameGUI.player.changeHealth(150);
        }

        explode_frameUpdate.addActionListener(e -> {
            karmakazi_sprite = Resources.explosion[explosion_frameCount];
            if (!enemy_properties.dead) {
                movement_frameUpdate.stop();
                Resources.explosionSound();
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

    private void cleanUp() {
        EnemyPane.enemies.remove(this);
    }

    public CharacterProperties enemy_properties(){
        return enemy_properties;
    }

    public BufferedImage enemy_sprite(){
        return karmakazi_sprite;
    }

    public Rectangle hitBox(){
        return hitBox;
    }

    public Point location() {
        return enemy_properties.location;
    }

    public void tickUpdate() {
        enemy_properties.setLocation(
                (int) (enemy_properties.getX() + currentTime * (GameGUI.player.location().x - enemy_properties.getX()) / targetTime),
                (int) (enemy_properties.getY() + currentTime * (GameGUI.player.location().y - enemy_properties.getY()) / targetTime));

        currentTime += 33;

        hitBox = new Rectangle(enemy_properties.getX() - 50, enemy_properties.getY() - 25, 60, 60);

        if (enemy_properties.health <= 0) {
            explode(false);
        } else if (enemy_properties.health <= 50) {
            karmakazi_sprite = Resources.karmakazi_fire[onFire_frameCount];
            if (onFire_frameCount < 43) {
                onFire_frameCount++;
            } else {
                onFire_frameCount = 0;
            }
        }
    }

}

class RegularEnemy implements Hostile {

    public BufferedImage enemy_sprite;

    private final Timer fire_bullet;
    private final Timer explode_frameUpdate;
    private final Timer movement_frameUpdate;

    public Rectangle hitBox;
    public Point targetPoint;
    public final CharacterProperties enemy_properties;

    private int y_increment = 1;
    private int x_increment = 1;

    private int onFire_frameCount;
    private int explosion_frameCount;
    private final int enemyNumber;

    RegularEnemy(Point origin) {

        try {
            enemy_sprite = Resources.regular_enemy_sprite;
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        enemy_properties = new CharacterProperties(origin, 100, 1);
        hitBox = new Rectangle();

        onFire_frameCount = 0;
        explosion_frameCount = 0;
        enemyNumber = EnemyPane.enemyCount;
        x_increment = 1;
        y_increment = 1;

        movement_frameUpdate = new Timer(Resources.REFRESH_RATE, null);
        explode_frameUpdate = new Timer((int)(Resources.REFRESH_RATE * 1.5), null);
        fire_bullet = new Timer(1500, null);

        fire_bullet.addActionListener(e -> {

            GameGUI.bullet_pane.fireBullet(
                    enemy_properties.location,
                    GameGUI.player.location(), true);

        });
        fire_bullet.setInitialDelay(ThreadLocalRandom.current().nextInt(5000, 6500));

        movement_frameUpdate.start();
        fire_bullet.start();

    }

    public void collisionDeath() {
        GameGUI.player.changeHealth(-50);
        explode(true);
    }

    public boolean isDead(){
        return enemy_properties.dead;
    }

    public void explode(boolean collisionDeath) {

        if (!collisionDeath){
            GameGUI.player.changeHealth(80);
        }

        explode_frameUpdate.addActionListener(e -> {
            enemy_sprite = Resources.explosion[explosion_frameCount];
            if (!enemy_properties.dead) {
                Resources.explosionSound();
                movement_frameUpdate.stop();
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

    private void cleanUp() {
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

    public Point location() {
        return enemy_properties.location;
    }

    public void tickUpdate() {
        enemy_properties.updateLocation(x_increment,y_increment);

        System.out.println(enemy_properties);

        if (enemy_properties.onBottom()) {
            y_increment = -1;
        } else if (enemy_properties.onTop()) {
            y_increment = 1;
        }

        if (enemy_properties.onRight()) {
            x_increment = -1;
        } else if (enemy_properties.onLeft()) {
            x_increment = 1;
        }

        hitBox = new Rectangle(enemy_properties.getX() - 50, enemy_properties.getY() - 25, 60, 60);

        if (enemy_properties.health <= 0) {
            explode(false);
        } else if (enemy_properties.health <= 50) {
            enemy_sprite = Resources.regular_enemy_fire[onFire_frameCount];
            if (onFire_frameCount < 43) {
                onFire_frameCount++;
            } else {
                onFire_frameCount = 0;
            }
        }
    }

}

class EnemyPane extends JPanel {

    public static ScheduledExecutorService enemy_periodic_update;
    public static ExecutorService enemy_location_update;
    public static int enemyCount = 0;
    public static ArrayList<Hostile> enemies = new ArrayList<>();

    public Timer scheduledSpawn;

    private boolean allDead = false;

    Point targetPoint = GameGUI.player.location();

    EnemyPane() {

        setBounds(0, 0, Resources.FRAME_WIDTH, Resources.FRAME_HEIGHT);
        setOpaque(false);
        setLayout(null);

        enemy_periodic_update = Executors.newScheduledThreadPool(1);
        enemy_location_update = Executors.newCachedThreadPool();
        enemy_periodic_update.scheduleAtFixedRate(() -> {
            enemy_location_update.submit(() -> {
                enemies.forEach(enemy -> {
                    if (!enemy.isDead()) {
                        enemy.tickUpdate();
                    }
                });
            });
        }, 0, Resources.REFRESH_RATE, TimeUnit.MILLISECONDS);

        scheduledSpawn = new Timer(5000, null);
        scheduledSpawn.addActionListener(e -> {

            allDead = true;

            for (int i = 0; i < enemies.size(); i++) {
                if (!(EnemyPane.enemies.get(i) == null)) {
                    if (!EnemyPane.enemies.get(i).enemy_properties().dead) {
                        allDead = false;
                    }
                }
            }

            if (Player.shockwave_frameUpdate.isRunning()){
                allDead = false;
            }

            if (allDead) {
                spawnMoreEnemy(2);
            }
        });

    }

    @Override
    protected void paintComponent (Graphics g){

        super.paintComponent(g);

        enemies.forEach(enemy -> {

            if (enemy.enemy_properties().character_type != 6) {

                Graphics2D g2d = (Graphics2D) g.create();
                int initialX = enemy.enemy_properties().getX();
                int initialY = enemy.enemy_properties().getY();

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

                g2d.drawImage(enemy.enemy_sprite(), x, y, this);

            } else {
                Graphics2D g2d = (Graphics2D) g;
                g2d.drawImage(enemy.enemy_sprite(), enemy.enemy_properties().getX(), enemy.enemy_properties().getY(), this);
                g2d.setColor(Color.red);
                g2d.fillRect(enemy.hitBox().x, enemy.hitBox().y, (int) enemy.hitBox().getHeight(), (int) enemy.hitBox().getWidth());
            }

        });

    }

    private void spawnMoreEnemy(int spawnNumber) {

        for (int i = 0; i < spawnNumber; i++) {
            addRegularEnemy();
            addKarmakazi();
            addOcelot();
        }

    }

    public void addRegularEnemy() {

        int randomX = ThreadLocalRandom.current().nextInt(20, 1100);
        enemies.add(new RegularEnemy(new Point(randomX, -50)));

        System.out.println("Enemy " + enemyCount + " created");
        enemyCount++;

    }

    public void addKarmakazi(){

        int randomX = ThreadLocalRandom.current().nextInt(20, 1100);
        enemies.add(new Karmakazi(new Point(randomX, -50)));

        System.out.println("Karmakazi " + enemyCount + " created");
        enemyCount++;

    }

    public void addOcelot(){

        int randomX = ThreadLocalRandom.current().nextInt(20, 1200);
        enemies.add(new Ocelot(new Point(randomX, -50)));

        System.out.println("Ocelot " + enemyCount + " created");
        enemyCount++;

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

    public static void enemyExplosion(BufferedImage enemy_sprite,Hostile enemy){

    }

}

interface Hostile extends Entitative{
    CharacterProperties enemy_properties();
    BufferedImage enemy_sprite();

    boolean isDead();
    void collisionDeath();
    void explode(boolean e);
    void tickUpdate();
}

class CollisionLogic {

    private ScheduledExecutorService bullet_collision;
    private ScheduledExecutorService enemy_collision;

    private ExecutorService cached_pool_bullet;
    private ExecutorService cached_pool_enemy;

    public CollisionLogic(ArrayList<Hostile> enemies, ArrayList<Bullet> bullets, Player player){

        cached_pool_bullet = Executors.newCachedThreadPool();
        cached_pool_enemy = Executors.newCachedThreadPool();

        bullet_collision = Executors.newScheduledThreadPool(1);
        bullet_collision.scheduleAtFixedRate(() -> {
            cached_pool_bullet.submit(() -> {
                bullets.forEach(bullet -> {
                    if (bullet.isViableBullet()){

                        Space.asteroids.forEach(asteroid -> {
                            if (distanceCheck(bullet,asteroid)){
                                if (collisionCheck(bullet,asteroid)){
                                    bullet.hitAsteroid(asteroid);
                                }
                            }
                        });

                        if (bullet.isEnemyBullet() && distanceCheck(bullet,player)){
                            if (collisionCheck(bullet,player)){
                                bullet.hitPlayer();
                            }
                        } else if (!bullet.isEnemyBullet()){
                            enemies.forEach(enemy -> {
                                if (collisionCheck(enemy,bullet)){
                                    bullet.hitEnemy(enemy);
                                }
                            });
                        }
                    }
                });
            });
        }, 0, Resources.REFRESH_RATE, TimeUnit.MILLISECONDS);

        enemy_collision = Executors.newScheduledThreadPool(1);
        enemy_collision.scheduleAtFixedRate(() -> {
            cached_pool_enemy.submit(() -> {
                enemies.forEach(enemy -> {
                    if (!enemy.isDead() && distanceCheck(enemy, player)) {
                        if (collisionCheck(enemy, player)) {
                            enemy.collisionDeath();
                        }
                    }
                });
            });
        },0,Resources.REFRESH_RATE,TimeUnit.MILLISECONDS);

    }

    private boolean distanceCheck(Entitative e1, Entitative e2){
        return entityDistance(e1.location(), e2.location()) < 100;
    }

    private boolean collisionCheck (Entitative e1, Entitative e2){
        return (e1.hitBox().intersects(e2.hitBox()));
    }

    public static int entityDistance(Point e1, Point e2){
        return (int)(Math.sqrt(Math.pow((e1.x-e2.x), 2) + Math.pow((e1.y-e2.y), 2)));
    }

}

interface Entitative{
    Point location();
    Rectangle hitBox();
}

class Player extends JPanel implements ActionListener, Entitative {

    public static CharacterProperties player_data;
    public static Rectangle hitBox;
    private static Rectangle shockwave_hitBox;
    private static BufferedImage player_sprite;
    public static BufferedImage protect_bubble;
    private static BufferedImage shockwave;

    private static int onFire_frameCount;

    public static int bullet_heat_factor;
    private static int secondary_heat_factor;

    private Timer movement_frameUpdate;
    private Timer fire_bullet;
    private Timer heat_factor;

    private Timer changeVelocityX;
    private Timer changeVelocityY;

    private int velocityX_changeFactor;
    private int velocityY_changeFactor;

    private boolean warningAlreadyStarted;
    public static boolean isFiring;
    public static boolean overHeating;

    private int shockwaveX = 0;
    private int shockwaveY = 0;
    private int shockwaveSize = 0;

    MouseAdapter over_heat_warning;

    private int velocityY = 0;
    private int velocityX = 0;

    int teleport_frameCount = 0;
    int protect_frameCount = 0;
    Timer teleport_disappearance = new Timer (Resources.REFRESH_RATE, null);
    Timer teleport_appearance = new Timer(Resources.REFRESH_RATE, null);

    public static Timer shockwave_frameUpdate = new Timer(Resources.REFRESH_RATE, null);

    MouseAdapter mouseControl = new MouseAdapter() {

        public void mouseMoved(MouseEvent e) {
            Resources.mouse_location = e.getPoint();
            repaint();
        }

        public void mousePressed(MouseEvent e) {

            switch(e.getButton()){
                case MouseEvent.BUTTON1: {
                    fire_bullet.start();
                    break;
                }
                case MouseEvent.BUTTON3: {
                    teleport();
                    break;
                }
            }
        }

        public void mouseReleased(MouseEvent e) {
            fire_bullet.stop();
        }

        public void mouseEntered(MouseEvent e) {
            Player.this.grabFocus();
        }
        public void mouseDragged(MouseEvent e) {
            Resources.mouse_location = e.getPoint();
            repaint();
        }

    };

    public Point location() {
        return player_data.location;
    }
    public Rectangle hitBox() {
        updateHitBox();
        return hitBox;
    }

    Player(Point origin) {

        super();

        setBounds(0, 0, Resources.FRAME_WIDTH, Resources.FRAME_HEIGHT);
        setLayout(null);
        setOpaque(false);
        setFocusable(true);
        requestFocus();

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_UP){
                    velocityY_changeFactor = 1;
                    changeVelocityY.start();
                } else if (e.getKeyCode() == KeyEvent.VK_DOWN){
                    velocityY_changeFactor = -1;
                    changeVelocityY.start();
                }
            }
            @Override
            public void keyReleased(KeyEvent e){
                changeVelocityY.stop();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_LEFT){
                    velocityX_changeFactor = -1;
                    changeVelocityX.start();
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT){
                    velocityX_changeFactor = 1;
                    changeVelocityX.start();
                }
            }
            @Override
            public void keyReleased(KeyEvent e){
                changeVelocityX.stop();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                super.keyPressed(e);
                if (e.getKeyCode() == KeyEvent.VK_SHIFT){
                    protect_init();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    shockWave(location());
                }
            }
        });

        onFire_frameCount = 0;
        bullet_heat_factor = 0;
        secondary_heat_factor = 0;
        warningAlreadyStarted = false;
        isFiring = false;
        overHeating = false;

        player_data = new CharacterProperties(origin, 800, 0);
        player_sprite = Resources.player_sprite;
        hitBox = new Rectangle();
        shockwave_hitBox = new Rectangle();

        changeVelocityY = new Timer((int)(Resources.REFRESH_RATE * 1.5), e -> {
            velocityY += velocityY_changeFactor;
        });
        changeVelocityX = new Timer((int)(Resources.REFRESH_RATE * 1.5), e -> {
            velocityX += velocityX_changeFactor;
        });

        movement_frameUpdate = new Timer(Resources.REFRESH_RATE, e -> {

            if (player_data.onTop()) {
                player_data.setLocation(false, 0, true, 80);
                velocityY = 0;
            } else if (player_data.onBottom()) {
                player_data.setLocation(false, 0, true, Resources.FRAME_HEIGHT);
                velocityY = 0;
            } else {
                player_data.updateLocation(0, -velocityY);
                this.repaint();
            }

            if (player_data.onLeft()) {
                player_data.setLocation(true, 0, false, 0);
                velocityX = 0;
            } else if (player_data.onRight()) {
                player_data.setLocation(true, Resources.FRAME_WIDTH, false, 0);
                velocityX = 0;
            } else {
                player_data.updateLocation(velocityX,0);
                this.repaint();
            }
        });


        fire_bullet = new Timer(180, this);
        heat_factor = new Timer((int)(Resources.REFRESH_RATE * 1.5), null);
        Timer health_frameUpdate = new Timer(Resources.REFRESH_RATE, null);

        health_frameUpdate.addActionListener(e -> {

            if (Player.player_data.health > 800){
                Player.player_data.health = 800;
            } else if (Player.player_data.health < 0){
                Player.player_data.health = 0;
            }

            if (player_data.health <= 100) {
                if (!warningAlreadyStarted){
                    Resources.low_health.play(true);
                    warningAlreadyStarted = true;
                }
                player_sprite = Resources.player_fire[onFire_frameCount];

                if (onFire_frameCount < 43) {
                    onFire_frameCount++;
                } else {
                    onFire_frameCount = 0;
                }

            } else if (player_data.health > 100){

                if (warningAlreadyStarted){
                    Resources.low_health.stop();
                    warningAlreadyStarted = false;
                }

                player_sprite = Resources.player_sprite;

            }
        });

        over_heat_warning = new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                Resources.over_heat.play();
            }
        };

        heat_factor.addActionListener(e -> {
            if (fire_bullet.isRunning()){
                secondary_heat_factor ++;
                isFiring = true;
            } else {
                secondary_heat_factor -= 5;
                isFiring = false;
            }
            if (secondary_heat_factor < 0){
                bullet_heat_factor = 0;
                secondary_heat_factor = 0;
            } else if (secondary_heat_factor > 179 && !overHeating){
                bullet_heat_factor = 179;
                secondary_heat_factor = 280;
                if (!overHeating){
                    Resources.over_heat.play();
                    overHeating = true;
                    fire_bullet.stop();
                    removeMouseListener(mouseControl);
                    addMouseListener(over_heat_warning);
                }
            } else if (secondary_heat_factor <= 179) {
                bullet_heat_factor = secondary_heat_factor;
                if (overHeating){
                    overHeating = false;
                    removeMouseListener(over_heat_warning);
                    addMouseListener(mouseControl);
                }
            }
        });

        teleport_appearance.addActionListener(e -> {

            player_sprite = Resources.player_teleport[teleport_frameCount];
            this.repaint();

            teleport_frameCount--;

            if (teleport_frameCount == 0) {
                player_sprite = Resources.player_sprite;
                addMouseListener(mouseControl);
                teleport_appearance.stop();
                teleport_frameCount = 0;
            }

        });
        teleport_disappearance.addActionListener(e -> {

            player_sprite = Resources.player_teleport[teleport_frameCount];
            this.repaint();

            teleport_frameCount++;

            if (teleport_frameCount > 10) {
                teleport_disappearance.stop();
                player_data.setLocation(Resources.mouse_location.x,Resources.mouse_location.y);
                teleport_frameCount = 10;
                teleport_appearance.start();
            }

        });

        addMouseListener(mouseControl);
        addMouseMotionListener(mouseControl);

        movement_frameUpdate.start();
        health_frameUpdate.start();
        heat_factor.start();

    }

    @Override
    protected void paintComponent(Graphics g) {

        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        double rotation = 0f;

        int width = player_data.getX() * 2;
        int height = player_data.getY() * 2;

        if (Resources.mouse_location != null) {
            int x = width / 2;
            int y = height / 2;

            int deltaX = Resources.mouse_location.x - x;
            int deltaY = Resources.mouse_location.y - y;

            rotation = -Math.atan2(deltaX, deltaY);

            rotation = Math.toDegrees(rotation) + 180;
        }
        int x = (width - 50) / 2;
        int y = (height - 50) / 2;

        g2d.rotate(Math.toRadians(rotation), width / 2, height / 2);

        g2d.drawImage(player_sprite, x, y, this);
        g2d.drawImage(protect_bubble, player_data.getX() - 35, player_data.getY() - 32, this);

        g2d.rotate(-1 * Math.toRadians(rotation), width / 2, height / 2);
        g2d.drawImage(shockwave,shockwaveX,shockwaveY,shockwaveSize,shockwaveSize,this);

    }

    public void actionPerformed(ActionEvent e) {
        GameGUI.bullet_pane.fireBullet(
                player_data.location, Resources.mouse_location, false);


        repaint();
    }

    public void teleport (){

        removeMouseListener(mouseControl);
        teleport_disappearance.start();

    }

    public void shockWave (Point Epicenter){

        shockwave = Resources.shockwave;
        shockwave_frameUpdate.addActionListener(e -> {

            shockwaveSize += 40;
            shockwaveX = Epicenter.x - (shockwaveSize - player_sprite.getWidth())/2;
            shockwaveY = Epicenter.y - (shockwaveSize - player_sprite.getHeight())/2;

            shockwave_hitBox.setBounds(shockwaveX + 100,shockwaveY + 90,shockwaveSize - 300, shockwaveSize - 300);

            this.repaint();

            BulletPane.bullets.forEach(bullet -> {
                if (shockwave_hitBox.intersects(bullet.hitBox())){
                    bullet.hitNothing();
                }
            });

            EnemyPane.enemies.forEach(enemy -> {
                if (shockwave_hitBox.intersects(enemy.hitBox()) && !(enemy.location().y < 0)){
                    enemy.explode(true);
                }
            });

            if (shockwaveSize > Resources.FRAME_WIDTH + 3000){
                shockwaveSize = 0;
                shockwaveX = 0;
                shockwaveY = 0;
                shockwave = null;
                shockwave_hitBox = new Rectangle();
                shockwave_frameUpdate.stop();
            }
        });

        shockwave_frameUpdate.start();
    }

    public void protect_init(){

        Timer protect_init = new Timer (Resources.REFRESH_RATE, null);

        System.out.println(" triggered");

        protect_init.addActionListener(e -> {

            System.out.println("timer triggered");
            protect_bubble = Resources.bubble_init[protect_frameCount];
            this.repaint();

            protect_frameCount ++;

            if (protect_frameCount > 29){
                protect_init.stop();
                protect(player_data.health);
                protect_frameCount = 0;
            }

        });

        protect_init.start();

    }

    public void protect(int currentHealth){

        Timer bubble_animation = new Timer (Resources.REFRESH_RATE, null);
        Timer bubble_disappear = new Timer (Resources.REFRESH_RATE, null);
        Timer stop_protection = new Timer (15000, null);

        bubble_animation.addActionListener(e -> {

            protect_bubble = Resources.bubble[protect_frameCount];
            this.repaint();

            player_data.health = currentHealth;

            if (protect_frameCount < 149){
                protect_frameCount ++;
            } else {
                protect_frameCount = 0;
            }

        });
        bubble_disappear.addActionListener(e -> {

            protect_bubble = Resources.bubble_init[protect_frameCount];
            this.repaint();

            protect_frameCount --;

            if (protect_frameCount == 0){
                bubble_disappear.stop();
                protect_frameCount = 0;
            }

        });
        stop_protection.addActionListener(e -> {

            protect_frameCount = 29;

            bubble_animation.stop();
            stop_protection.stop();
            bubble_disappear.start();
        });

        bubble_animation.start();
        stop_protection.start();

    }

    public void updateHitBox(){
        hitBox.setBounds(player_data.getX() - 23, player_data.getY() - 23, 80, 80);
    }

    public void changeHealth(int healthChange){
        if (Player.player_data.health + healthChange < 0){
            Player.player_data.health = 0;
        } else if (Player.player_data.health + healthChange > 800){
            Player.player_data.health = 800;
        } else {
            player_data.health += healthChange;
        }
    }

    public int healthPercentage(){
        int health_percentage = Math.round((Player.player_data.health / 800f) * 100);
        if (health_percentage < 0){
            health_percentage = 0;
        } else if (health_percentage > 100){
            health_percentage = 100;
        }
        return health_percentage;
    }

}

class FriendlyPane extends JPanel {

    FriendlyPane() {

        super();

        setBounds(0, 0, 1280, 750);
        setLayout(null);
        setOpaque(false);


    }

}