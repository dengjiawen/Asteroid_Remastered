import com.sun.org.apache.regexp.internal.RE;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

class Ocelot implements Hostile {

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
            ocelot_sprite = Resources.ocelot_sprite;
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

        movement_frameUpdate = new Timer(Resources.REFRESH_RATE, null);
        explode_frameUpdate = new Timer((int)(Resources.REFRESH_RATE * 1.5), null);
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

            targetPoint.x = Player.player_data.x;
            targetPoint.y = Player.player_data.y;

            if (enemy_properties.health <= 0) {
                bulletDeath();
            } else if (enemy_properties.health <= 50) {
                ocelot_sprite = Resources.ocelot_fire[onFire_frameCount];
                if (onFire_frameCount < 43) {
                    onFire_frameCount++;
                } else {
                    onFire_frameCount = 0;
                }
            }


        });
        fire_bullet.addActionListener(e -> {

            GameGUI.bullet_pane.fireBullet(
                    new Point(enemy_properties.x, enemy_properties.y),
                    targetPoint, true);

        });
        fire_bullet.setInitialDelay(ThreadLocalRandom.current().nextInt(3000, 4000));

        movement_frameUpdate.start();
        fire_bullet.start();

    }

    public void bulletDeath() {
        explode(false);
    }

    public void collisionDeath() {
        Player.player_data.health -= 20;
        enemy_properties.health = 0;
        explode(true);
    }

    public boolean isDead(){
        return enemy_properties.dead;
    }

    public void explode(boolean collisionDeath) {

        if (!collisionDeath){
            Player.player_data.health += 200;
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
        return targetPoint;
    }

    public Rectangle hitBox(){
        return hitBox;
    }

    public Point location() {
        return new Point(enemy_properties.x, enemy_properties.y);
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
    public Point targetPoint;
    public final CharacterProperties enemy_properties;

    private int onFire_frameCount;
    private int explosion_frameCount;
    private final int enemyNumber;

    Karmakazi(int xcord, int ycord) {

        try {
            karmakazi_sprite = Resources.karmakazi_sprite;
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

        movement_frameUpdate = new Timer(Resources.REFRESH_RATE, null);
        explode_frameUpdate = new Timer((int)(Resources.REFRESH_RATE * 1.5), null);
        fire_bullet = new Timer(5000, null);

        movement_frameUpdate.addActionListener(e -> {

            enemy_properties.x = (int) (enemy_properties.x + currentTime * (targetPoint.x - enemy_properties.x) / targetTime);
            enemy_properties.y = (int) (enemy_properties.y + currentTime * (targetPoint.y - enemy_properties.y) / targetTime);

            currentTime += 33;

            hitBox = new Rectangle(enemy_properties.x - 50, enemy_properties.y - 25, 60, 60);
            targetPoint.x = Player.player_data.x;
            targetPoint.y = Player.player_data.y;

            if (enemy_properties.health <= 0) {
                bulletDeath();
            } else if (enemy_properties.health <= 50) {
                karmakazi_sprite = Resources.karmakazi_fire[onFire_frameCount];
                if (onFire_frameCount < 43) {
                    onFire_frameCount++;
                } else {
                    onFire_frameCount = 0;
                }
            }


        });
        fire_bullet.addActionListener(e -> {

            GameGUI.bullet_pane.fireBullet(
                    new Point(enemy_properties.x, enemy_properties.y),
                    targetPoint, true);

        });
        fire_bullet.setInitialDelay(ThreadLocalRandom.current().nextInt(5000, 6500));

        movement_frameUpdate.start();
        fire_bullet.start();

    }

    public void bulletDeath() {
        explode(false);
    }

    public void collisionDeath() {
        Player.player_data.health -= 100;
        enemy_properties.health = 0;
        explode(true);
    }

    public boolean isDead(){
        return enemy_properties.dead;
    }

    public void explode(boolean collisionDeath) {

        if (!collisionDeath){
            Player.player_data.health += 150;
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

    public Point targetPoint(){
        return targetPoint;
    }

    public Rectangle hitBox(){
        return hitBox;
    }

    public Point location() {
        return new Point(enemy_properties.x, enemy_properties.y);
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

    private double y_increment = 1;
    private double x_increment = 1;

    private int onFire_frameCount;
    private int explosion_frameCount;
    private final int enemyNumber;

    RegularEnemy(int xcord, int ycord) {

        try {
            enemy_sprite = Resources.regular_enemy_sprite;
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

        movement_frameUpdate = new Timer(Resources.REFRESH_RATE, null);
        explode_frameUpdate = new Timer((int)(Resources.REFRESH_RATE * 1.5), null);
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

            targetPoint.x = Player.player_data.x;
            targetPoint.y = Player.player_data.y;

            hitBox = new Rectangle(enemy_properties.x - 50, enemy_properties.y - 25, 60, 60);

            if (enemy_properties.health <= 0) {
                bulletDeath();
            } else if (enemy_properties.health <= 50) {
                enemy_sprite = Resources.regular_enemy_fire[onFire_frameCount];
                if (onFire_frameCount < 43) {
                    onFire_frameCount++;
                } else {
                    onFire_frameCount = 0;
                }
            }


        });
        fire_bullet.addActionListener(e -> {

            GameGUI.bullet_pane.fireBullet(
                    new Point(enemy_properties.x, enemy_properties.y),
                    targetPoint, true);

        });
        fire_bullet.setInitialDelay(ThreadLocalRandom.current().nextInt(5000, 6500));

        movement_frameUpdate.start();
        fire_bullet.start();

    }

    public void bulletDeath() {
        explode(false);
    }

    public void collisionDeath() {
        Player.player_data.health -= 50;
        enemy_properties.health = 0;
        explode(true);
    }

    public boolean isDead(){
        return enemy_properties.dead;
    }

    public void explode(boolean collisionDeath) {

        if (!collisionDeath){
            Player.player_data.health += 80;
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
        return new Point(enemy_properties.x, enemy_properties.y);
    }

}

class EnemyPane extends JPanel {

    public static int enemyCount = 0;
    public static final ArrayList<Hostile> enemies = new ArrayList<>();
    public Timer scheduledSpawn;

    private boolean allDead = false;

    EnemyPane() {

        setBounds(0, 0, 1280, 750);
        setOpaque(false);
        setLayout(null);

        scheduledSpawn = new Timer(1000, null);
        scheduledSpawn.addActionListener(e -> {

            allDead = true;

            for (int i = 0; i < enemies.size(); i++) {
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

        for (int i = 0; i < enemies.size(); i++){

            if (enemies.get(i).enemy_properties().character_type != 6) {

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

                g2d.drawImage(enemies.get(i).enemy_sprite(), x, y, this);
            } else {
                Graphics2D g2d = (Graphics2D) g;
                g2d.drawImage(enemies.get(i).enemy_sprite(), enemies.get(i).enemy_properties().x, enemies.get(i).enemy_properties().y, this);
                g2d.setColor(Color.red);
                g2d.fillRect(enemies.get(i).hitBox().x,enemies.get(i).hitBox().y, (int)enemies.get(i).hitBox().getHeight(),(int)enemies.get(i).hitBox().getWidth());
            }

        }

    }

    private void spawnMoreEnemy(int spawnNumber) {

        for (int i = 0; i < spawnNumber; i++) {
            addRegularEnemy();
            addKarmakazi();
            addOcelot();
            //addBlockade();
            //addBoss();
        }

    }

    public void addRegularEnemy() {

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

interface Hostile extends Entitative{
    CharacterProperties enemy_properties();
    BufferedImage enemy_sprite();
    Point targetPoint();

    boolean isDead();
    void collisionDeath();
    void bulletDeath();
    void explode(boolean e);
}