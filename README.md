# TargetTrackingSystem
In this project, the polar coordinates of a target with known cartesian coordinates are calculated and displayed on a GUI.

## Used Formulas
In the initial position, the target, radar and camera positions are defined. Let's denote these positions by $T(x_t,y_t), R(x_r,y_r), C(x_c,y_c)$. According to these expressions, the formulas used in the project are as follows.
- ### Calculate Polar Coordinate of Target: $(x_t,y_t) -> (d,\theta)$
  $d = \sqrt{{(x_t - x_r)^2 + (y_t - y_r)^2}}$
  
  $\theta = \arctan\left(\frac{{y_t - y_r}}{{x_t - x_r}}\right)$
  
- ### Calculate Camera Angle: ( $\phi$ )
  $x_t = x_r + d \cdot \cos(\theta)$
  
  $y_t = y_r + d \cdot \sin(\theta)$
  
  $\phi = \arctan\left(\frac{{y_t - y_c}}{{x_t - x_c}}\right)$
