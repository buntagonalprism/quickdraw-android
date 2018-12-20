%%%%%%% Generate noisy points around a rotated ellipse %%%%%%%

% Parameters for the rotated ellipse
centreX = 25;
centreY = -10;
lengthX = 20;
lengthY = 18;
angle = 30;

% Rotation matrix to rotate ellipse parametric equations
RotationMatrix = [cosd(angle), -sind(angle); sind(angle), cosd(angle)];

% Generate noisy data points
pts.x = zeros(length(0:0.1:2*pi),1);
pts.y = zeros(length(0:0.1:2*pi),1);
i = 1;
for t = 0:0.1:2*pi
   point = [lengthX*cos(t); lengthY*sin(t)];
   rotatedPoint = RotationMatrix*point;
   translatedPoint = rotatedPoint + [centreX; centreY];
   pts.x(i) = translatedPoint(1) + (centreX + centreY)/20 * randn(1);
   pts.y(i) = translatedPoint(2) + (centreX + centreY)/20 * randn(1);
   i = i+1;
end

% Plot noisy data points and underlying curve
close all
plot(pts.x,pts.y,'ro');
hold on
drawRotatedElipse(centreX, centreY, lengthX, lengthY, angle);

% Focal length and foci of reference elipse shape
focalLength = sqrt(lengthX^2 - lengthY^2);
if (lengthX > lengthY)
    circToFociDist = 2*lengthX;
    foci(:,1) = RotationMatrix*[focalLength; 0]+ [centreX;centreY];
    foci(:,2) = RotationMatrix*[-focalLength; 0]+ [centreX;centreY];
else 
    circToFociDist = 2*yLength;
    foci(:,1) = RotationMatrix*[0; focalLength] + [centreX;centreY];
    foci(:,2) = RotationMatrix*[0; - focalLength]+ [centreX;centreY];
end

plot(foci(1,1),foci(2,1),'go');
plot(foci(1,2),foci(2,2),'go');
axis equal
% 