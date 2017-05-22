import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

class BulletProperties {

    public int x;
    public int y;

    public final boolean enemy_fire;
    public final boolean large_bullet;

    public boolean hit;

    public BulletProperties(int x, int y, boolean enemy_fire) {

        this.x = x;
        this.y = y;
        this.enemy_fire = enemy_fire;
        this.large_bullet = Resources.large_bullet;
        this.hit = false;

    }

}

class Bullet implements Entitative {

    public BufferedImage bullet_sprite;

    private int targetTime;
    private int currentTime;
    private int explosion_frameCount;

    private int x1,x2,y1,y2;

    private Rectangle hitBox;
    private BulletProperties bullet_properties;

    private Timer bullet_frameUpdate;
    private Timer bullet_impact_frameUpdate;

    public Bullet(Point origin, Point target, boolean enemy_fire) {

        Resources.bulletSound(enemy_fire);

        targetTime = 800;
        currentTime = 0;
        explosion_frameCount = 0;
        hitBox = new Rectangle();
        bullet_properties = new BulletProperties(origin.x, origin.y, enemy_fire);

        x1 = origin.x;
        x2 = target.x;
        y1 = origin.y;
        y2 = target.y;

        if (bullet_properties.large_bullet){
            bullet_sprite = Resources.large_bullet_sprite;
        } else {
            bullet_sprite = Resources.bullet_sprite;
        }

        bullet_frameUpdate = new Timer(Resources.REFRESH_RATE, null);
        bullet_impact_frameUpdate = new Timer(Resources.REFRESH_RATE, null);

        bullet_frameUpdate.addActionListener(e -> {

            bullet_properties.x = x1 + currentTime * (x2 - x1) / targetTime;
            bullet_properties.y = y1 + currentTime * (y2 - y1) / targetTime;

            currentTime += 33;

            if (bullet_properties.x < -10 || bullet_properties.x > Resources.FRAME_WIDTH + 50 ||
                    bullet_properties.y < -10 || bullet_properties.y > Resources.FRAME_HEIGHT + 50) {
                cleanUp();
            }

        });
        bullet_impact_frameUpdate.addActionListener(e -> {

            if (explosion_frameCount < 8) {
                bullet_sprite = Resources.bullet_impact[explosion_frameCount];
                explosion_frameCount++;
            } else {
                cleanUp();
            }

        });

        bullet_frameUpdate.start();
    }

    private void cleanUp() {

        bullet_frameUpdate.stop();
        BulletPane.bullets.remove(this);

    }

    public void hitPlayer() {

        bullet_frameUpdate.stop();

        if (bullet_properties.large_bullet) {
            Player.player_data.health -= 70;
            bullet_impact_frameUpdate.start();
        } else {
            Player.player_data.health -= 5;
            bullet_impact_frameUpdate.start();
        }

        bullet_properties.hit = true;

    }

    public void hitEnemy(Hostile e){

        e.enemy_properties().health -= 10;

        bullet_frameUpdate.stop();
        bullet_impact_frameUpdate.start();

        bullet_properties.hit = true;

    }

    public void hitNothing() {
        bullet_frameUpdate.stop();
        bullet_impact_frameUpdate.start();

        bullet_properties.hit = true;
    }

    public boolean viableBullet(){
        return !this.bullet_properties.hit;
    }

    public boolean enemyBullet(){
        return this.bullet_properties.enemy_fire;
    }

    public Point location() {
        return new Point(bullet_properties.x, bullet_properties.y);
    }

    public Rectangle hitBox() {
        updateHitBox();
        return hitBox;
    }

    public void updateHitBox() {
        if (bullet_properties.large_bullet) {
            hitBox.setBounds(bullet_properties.x, bullet_properties.y, 30, 30);
        } else {
            hitBox.setBounds(bullet_properties.x, bullet_properties.y, 10, 10);
        }
    }

}

class BulletPane extends JPanel {

    public static final ArrayList<Bullet> bullets = new ArrayList<Bullet>();
    public static int bulletCount = 0;

    BulletPane() {

        setBounds(0, 0, Resources.FRAME_WIDTH, Resources.FRAME_HEIGHT);
        setOpaque(false);
        setLayout(null);

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

        bullets.add(new Bullet(origin, target, enemy_fire));
        System.out.println("Bullet " + bulletCount + "fired");
        bulletCount++;

        repaint();

    }

}