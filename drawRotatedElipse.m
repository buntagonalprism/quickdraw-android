function figHandle = drawRotatedElipse(xCentre, yCentre, xLength, yLength, angle) 

RotationMatrix = [cosd(angle), -sind(angle); sind(angle), cosd(angle)];

% Note the 'length' parameters are actually the semi-major axis

pts.x = zeros(length(0:0.1:2*pi),1);
pts.y = zeros(length(0:0.1:2*pi),1);
i = 1;

for t = 0:0.05:2*pi
    ParametricFormCoeffs = [xLength*cos(t); yLength*sin(t)];
    rotatedPoint = RotationMatrix*ParametricFormCoeffs;
    translatedPoint = rotatedPoint + [xCentre; yCentre];
    pts.x(i) = translatedPoint(1);
    pts.y(i) = translatedPoint(2);
    i = i + 1;
end
plot(pts.x,pts.y,'b-');
