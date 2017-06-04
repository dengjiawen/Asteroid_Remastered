## Kill_Everyone
A modern take on the decade old game "Asteroid".

This game requires at least 3.5GB of Java heap space in order to run.
Enter java -Xms3500M Bootstrap to run the compiled classes.

This is the result of over **1 week of development**,
and **tens of minutes of manpower**.
It will be constantly improved upon with new features and GUI.

### Activation
The latest version of the game added activation. You have to enter one of the following keys in order to unlock the game. A save file will be automatically created for you on my remote server.

**VQP4F-V47P8-BBDXK-R7K9Q-B42BB**

**JBH94-K6WKQ-YHTD6-XJFV9-WJP7Y**

**HRCXT-BY6WB-VBM83-CMBXF-BVWYY**

**C626F-H4CCJ-PWR8R-2RB9K-3G3HD**

**TBHJK-W4DPH-9D267-H93VR-WMXQJ**


### Upcoming Improvements
The coding for the program is currently being optimized and simplified.
**In future commits, we plan to:**
- [x] Shrink code line count from 5,000 to 3,500
- [x] Shrink code line count from 3,500 to 3,000
- [ ] Shrink code line count from 3,000 to < 2500
- [ ] Add cloud save feature
- [x] Add teleportation and protective shield
- [x] Add shockwave
- [x] Add asteroids
- [x] Add functional asteroid fragments
- [x] Add point system
- [ ] Make point system functional
- [ ] Add game over screen
- [x] Add cool down timers for abilities
- [x] Add boot up screen
- [ ] Add setting panel
- [ ] Add call backup feature
- [ ] Add new enemy classes

### Multi-Threading Functionality
Many of the program's power hungry logical classes had been moved
to a new multi-threaded structure.

For example, the enhanced collision detection algorithm is now running 
in its own CachedThreadPool to ensure maximum efficiency.

The algorithm was stress tested with over **2,000 entities** with **zero lag**
observed.

### Enhanced Collision Detection
The program now **checks for the distance between two entities**, as well as
**their types**, before initiating the brute force collision detection.

If the distance between the two entities are more than 100 px, or if
the two entities do not interact with one another, the brute force
collision detection will not kick in.

### Enhanced HUD
The game now features a **fully functional, prerendered HUD system**.

Right side contains information such as health and weapon overheat detection.

### Weapon Overheat
**The weapon will overheat if fired too long.**

Let go every few seconds in order for the weapon to cool down.

### Basic Controls
* Arrow Keys: Up, Down, Left, Right
* Left Click: Shoot

### Newly Added Controls
* Right Click: Teleport to the cursor's locatioN
* Shift: Initiate Protective Shielding
* Enter: Shockwave (NEW!)

All of these newly added features are still under development -- they may be buggy.
