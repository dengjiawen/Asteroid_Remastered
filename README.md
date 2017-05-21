# Kill_Everyone
A modern take on the decade old game "Asteroid".
This is the result of over 1 week of development,
and tens of minutes of manpower.
It will be constantly improved upon with new features and GUI.

# Multi-Threading Functionality
Many of the program's power hungry logical classes had been moved
to a new multi-threaded structure.
For example, the enhanced collision detection algorithm is now running 
in its own CachedThreadPool to ensure maximum efficiency.
The algorithm was stress tested with over 2,000 entities with zero lag
observed.

# Enhanced Collision Detection
The program now checks for the distance between two entities, as well as
their types, before initiating the brute force collision detection.
If the distance between the two entities are more than 100 px, or if
the two entities do not interact with one another, the brute force
collision detection will not kick in.

# Enhanced GUI
The game now features fully functional, prerendered GUI systems.
Right side contains information such as health and weapon overheat detection.

# Weapon Overheat
The weapon will overheat if fired too long.
Let go every few seconds in order for the weapon to cool down.

# Basic Controls
Arrow Keys: Up, Down, Left, Right
Left Click: Shoot

# Added Controls
Right Click: Teleport to the cursor's locatioN
Shift: Initiate Protective Shielding
* Both features are currently under development.
