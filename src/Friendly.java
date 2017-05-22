import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

class Player extends JPanel implements ActionListener, Entitative {

    ExecutorService intensive_task;

    public static CharacterProperties player_data;
    public static Rectangle hitBox;
    private static Rectangle shockwave_hitBox;
    private static BufferedImage player_sprite;
    public static BufferedImage protect_bubble;
    private static BufferedImage shockwave;
    private static int onFire_frameCount;
    public static int bullet_heat_factor;
    private static int secondary_heat_factor;

    private static Timer perpendicular_frameUpdate;
    private static Timer lateral_frameUpdate;
    private static Timer fire_bullet;
    private static Timer heat_factor;

    private boolean warningAlreadyStarted;
    public static boolean isFiring;
    public static boolean overHeating;

    private int shockwaveX = 0;
    private int shockwaveY = 0;
    private int shockwaveSize = 0;

    public static Robot robot;

    MouseAdapter over_heat_warning;

    int teleport_frameCount = 0;
    int protect_frameCount = 0;
    Timer teleport_disappearance = new Timer (Resources.REFRESH_RATE, null);
    Timer teleport_appearance = new Timer(Resources.REFRESH_RATE, null);

    MouseAdapter mouseControl = new MouseAdapter() {

        public void mouseMoved(MouseEvent e) {
            Resources.mouse_location = e.getPoint();
            repaint();
        }

        public void mousePressed(MouseEvent e) {

            switch(e.getButton()){
                case MouseEvent.BUTTON1: {
                    Player.fire_bullet.start();
                    break;
                }
                case MouseEvent.BUTTON3: {
                    teleport();
                    break;
                }
            }
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
            Resources.mouse_location = e.getPoint();
            repaint();
        }

    };

    public Point location() {
        return new Point(player_data.x, player_data.y);
    }
    public Rectangle hitBox() {
        updateHitBox();
        return hitBox;
    }

    Player() {

        super();

        setBounds(0, 0, Resources.FRAME_WIDTH, Resources.FRAME_HEIGHT);
        setLayout(null);
        setOpaque(false);
        setFocusable(true);
        requestFocus();

        intensive_task = Executors.newCachedThreadPool();

        onFire_frameCount = 0;
        bullet_heat_factor = 0;
        secondary_heat_factor = 0;
        warningAlreadyStarted = false;
        isFiring = false;
        overHeating = false;

        try {
            robot = new Robot();
        } catch (Exception e){
            System.out.println(e.getMessage());
        }

        player_data = new CharacterProperties((Resources.FRAME_WIDTH - Resources.player_sprite.getWidth())/2, 650, 800, 0);
        player_sprite = Resources.player_sprite;
        hitBox = new Rectangle();
        shockwave_hitBox = new Rectangle();

        perpendicular_frameUpdate = new Timer(Resources.REFRESH_RATE, null);
        lateral_frameUpdate = new Timer(Resources.REFRESH_RATE, null);
        fire_bullet = new Timer(180, null);
        heat_factor = new Timer(Resources.REFRESH_RATE, null);
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

        fire_bullet.addActionListener(this);

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

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_SHIFT) {
                    protect_init();
                } else if (e.getKeyCode() == KeyEvent.VK_ENTER){
                    shockWave(location());
                }
            }
        });

        //lateral control
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_LEFT) {

                    lateral_frameUpdate.addActionListener(e14 -> {

                        if (player_data.x > 10) {
                            player_data.x -= 2;
                            repaint();
                        } else {
                            player_data.x = 10;
                            robot.keyPress(KeyEvent.VK_RIGHT);

                        }});
                } else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {

                    lateral_frameUpdate.addActionListener(e13 -> {
                        if (player_data.x <= Resources.FRAME_WIDTH) {
                            player_data.x += 2;
                            repaint();
                        } else {
                            player_data.x = Resources.FRAME_WIDTH;
                            robot.keyPress(KeyEvent.VK_LEFT);
                        }
                    });
                }
            }

        });

        //perpendicular control
        addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {

                if (e.getKeyCode() == KeyEvent.VK_UP) {

                    perpendicular_frameUpdate.addActionListener(e12 -> {
                        if (player_data.y >= 60) {
                            player_data.y -= 2;
                            repaint();
                        } else {
                            player_data.y = 60;
                            robot.keyPress(KeyEvent.VK_DOWN);
                        }
                    });

                    perpendicular_frameUpdate.start();

                } else if (e.getKeyCode() == KeyEvent.VK_DOWN) {

                    perpendicular_frameUpdate.addActionListener(e1 -> {
                        if (player_data.y <= Resources.FRAME_HEIGHT) {
                            player_data.y += 2;
                            repaint();
                        } else {
                            player_data.y = Resources.FRAME_HEIGHT;
                            robot.keyPress(KeyEvent.VK_UP);
                        }
                    });

                    perpendicular_frameUpdate.start();

                }
            }

        });

        teleport_appearance.addActionListener(e -> {

            intensive_task.submit(() -> {
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

        });
        teleport_disappearance.addActionListener(e -> {

            intensive_task.submit(() -> {
                player_sprite = Resources.player_teleport[teleport_frameCount];
                this.repaint();

                teleport_frameCount++;

                if (teleport_frameCount > 10) {
                    teleport_disappearance.stop();
                    player_data.x = Resources.mouse_location.x;
                    player_data.y = Resources.mouse_location.y;
                    teleport_frameCount = 10;
                    teleport_appearance.start();
                }
            });

        });

        addMouseListener(mouseControl);
        addMouseMotionListener(mouseControl);

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
        g2d.drawImage(protect_bubble, player_data.x - 35, player_data.y - 32, this);

        g2d.rotate(-1 * Math.toRadians(rotation), width / 2, height / 2);
        g2d.drawImage(shockwave,shockwaveX,shockwaveY,shockwaveSize,shockwaveSize,this);

    }

    public void actionPerformed(ActionEvent e) {
        GameGUI.bullet_pane.fireBullet(
                new Point(player_data.x, player_data.y), Resources.mouse_location, false);


        repaint();
    }

    public void teleport (){

        removeMouseListener(mouseControl);
        teleport_disappearance.start();

    }

    public void shockWave (Point Epicenter){

        shockwave = Resources.shockwave;

        Timer shockwave_frameUpdate = new Timer(Resources.REFRESH_RATE, null);
        shockwave_frameUpdate.addActionListener(e -> {

            shockwaveSize += 30;
            shockwaveX = Epicenter.x - (shockwaveSize - player_sprite.getWidth())/2;
            shockwaveY = Epicenter.y - (shockwaveSize - player_sprite.getHeight())/2;

            shockwave_hitBox.setBounds(shockwaveX + 100,shockwaveY + 90,shockwaveSize - 300, shockwaveSize - 300);

            this.repaint();

            intensive_task.submit(() -> {
                BulletPane.bullets.forEach(bullet -> {
                    if (shockwave_hitBox.intersects(bullet.hitBox())){
                        bullet.hitNothing();
                    }
                });
            });

            intensive_task.submit(() -> {
                EnemyPane.enemies.forEach(enemy -> {
                    if (shockwave_hitBox.intersects(enemy.hitBox()) && !(enemy.location().y < 0)){
                        enemy.explode(true);
                    }
                });
            });

            if (shockwaveSize > Resources.FRAME_WIDTH + 1500){
                shockwaveSize = 0;
                shockwaveX = 0;
                shockwaveY = 0;
                shockwave = null;
                shockwave_hitBox = new Rectangle();
                shockwave_frameUpdate.stop();
                System.out.println("stopped");
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

    boolean expired = false;

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
        hitBox.setBounds(player_data.x - 23, player_data.y - 23, 80, 80);
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