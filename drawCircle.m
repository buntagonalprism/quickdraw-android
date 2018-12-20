function handle = drawCircle(x,y,r)

pts.x = zeros(length(0:0.05:2*pi),1);
pts.y = zeros(length(0:0.05:2*pi),1);
i = 1;
for theta = 0:0.05:2*pi
   pts.x(i) = x + r*cos(theta);
   pts.y(i) = y + r*sin(theta);
   i = i+1;
end

handle = plot(pts.x,pts.y,'b-');


end