/**
 * Created by Jiawen (Fred) Deng on 2017-04-24.
 * DO NOT REDISTRIBUTE WITHOUT PERMISSION
 * COPYRIGHT 2017
 *
 * Revision 1.2.4
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;

class ConfirmStart extends JPanel{

    private JButton proceed;
    private JButton abort;

    public ConfirmStart(GameGUI gameGUI){

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

        setBounds(0,0,1280, 750);
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

            int health_percentage = Math.round((Player.player_data.health / 800f) * 100);

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

    }

    public void init() {

        left_hud_init.start();
        logo_hud_init.start();
        health_meter_init.start();

    }

}

class Space extends JPanel {

    private int y1;
    private int y2;

    private Timer background_frameUpdate;

    public Space() {

        super();

        setBounds(0, 0, 1280, 720);
        setLayout(null);
        setOpaque(false);

        y1 = 0;
        y2 = -750;

        background_frameUpdate = new Timer(Resources.REFRESH_RATE, e -> {
            if (y1 == 750) {
                y1 = -750;
            } else if (y2 == 750) {
                y2 = -750;
            }

            y1 += 2;
            y2 += 2;

            repaint();
        });

        background_frameUpdate.start();

    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(Resources.space_background[0], 0, y1, this);
        g.drawImage(Resources.space_background[1], 0, y2, this);
    }

}

class CharacterProperties {

    public int x;
    public int y;
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

    public CharacterProperties(int x, int y, int health, int character_type) {

        this.x = x;
        this.y = y;
        this.health = health;
        this.character_type = character_type;
        this.dead = false;

    }

}

interface Entitative{
    Point location();
    Rectangle hitBox();
}
