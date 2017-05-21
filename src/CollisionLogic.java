import java.awt.*;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

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
                    if (bullet.viableBullet()){
                        if (bullet.enemyBullet() && distanceCheck(bullet,player)){
                            if (collisionCheck(bullet,player)){
                                bullet.hitPlayer();
                            }
                        } else if (!bullet.enemyBullet()){
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

    private int entityDistance(Point e1, Point e2){
        return (int)(Math.sqrt(Math.pow((e1.x-e2.x), 2) + Math.pow((e1.y-e2.y), 2)));
    }

}