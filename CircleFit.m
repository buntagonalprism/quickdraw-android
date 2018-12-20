clear

pts.x = zeros(length(0:0.1:2*pi),1);
pts.y = zeros(length(0:0.1:2*pi),1);
radius = 30;
i = 1;
centreX = 25;
centreY = -10;
for theta = 0:0.1:2*pi
   angleDelta = randn(1)/15; 
   radiusDelta = randn(1);
   pts.x(i) = centreX + (radiusDelta + radius)*cos(theta + angleDelta);
   pts.y(i) = centreY + (radiusDelta + radius)*sin(theta + angleDelta);
   i = i+1;
end

close all
scatter(pts.x,pts.y,'ro');
axis equal;
hold on
xlabel('x axis');
ylabel('y axis');
title('Noisy circle data');

rangeX = max(pts.x)-min(pts.x);
rangeY = max(pts.y)-min(pts.y);
radiusGuess = ((rangeX + rangeY)/2)/2;
centreGuessX = (max(pts.x)+min(pts.x))/2;
centreGuessY = (max(pts.y)+min(pts.y))/2;

guessFig = drawCircle(centreGuessX+10,centreGuessY+10,radiusGuess+2);

alpha = 1;

x = centreGuessX+10;
y = centreGuessY+10;
r = radiusGuess+10;
tic
for iters = 1:150 
    
    deltaX = 0;
    deltaY = 0;
    deltaR = 0;
    cost = 0;
    for i = 1:length(pts.x)
        centreDist = sqrt((pts.x(i) - x)^2 +(pts.y(i)-y)^2);
        deltaX = deltaX + (r-centreDist)*2*(pts.x(i)-x)/centreDist;
        deltaY = deltaY + (r-centreDist)*2*(pts.y(i)-y)/centreDist;
        deltaR = (r-centreDist);
        cost = cost + (r - centreDist)^2;
    end
    
    x = x - alpha * deltaX / length(pts.x);
    y = y - alpha * deltaY / length(pts.x);
    r = r - alpha * deltaR / length(pts.x);
    costPlot(iters) = cost / (2*length(pts.x));
    
    %delete(guessFig);
    %guessFig = drawCircle(x,y,r);
    %pause(0.01);
      
end
toc
disp(sprintf('x: %f, y: %f, r: %f', x,y,r));
figure(2)
plot(costPlot);
