/**************************************************************
					        T O    D O 
**************************************************************/



4. Get file opening working again

5. Refactor XML

6. Implement colour selection dialogue
 - Include three panes
     Colour spectrum from example code
	 Colour pallete from that nice website, with 20,40,60,80% luminence of each
	 Recent colours and saved favourites (leave blank for now) colors

	
7. Selecting an object when pressed
This requires the edit tool I think, unless we have double tap for selected
Then perform a look-up in the shape database for nearby objects
Then we can figure out a way to change the appearance of the object to show it has
been selected

8. Show axes using drawText?
Use window object - five markers along shorter edge
float markerDist = RoundToNearest10(Math.min(window.width, window.height)/5);
float firstMarker = markerDist * Math.ceil(window.width/markerDist);
for (int i = 0; i < 5; i++) {
	markerX = firstMarker + markerDist*i;
	drawText("| " + String.format("%30f", markerX), markerX, near_the_bottom, textPaintStyle);
}
// do something similar for the vertical 

9. Limit dragging to a boundary around all object
Have shapeDatabase track  For each new shape
added, check depending on the shape type to see if it goes outside
Then when dragging, after performing the drag but before invalidate()
check whether the window borders would exceed any of the min, max based
upon the current windowOrigin and scale. (Also need to account for the 
canvas actually being smaller than the screen due to other stuff)


10. Shapes self-crop to view window using line segments, arcs and nothing when outside
Compute window minX, minY, maxX, maxY using window Origin and scale factor
Except that's stupid because this requires the shape to know the screen resolution
Instead we should pass in the current screen window as a class probably - 
DrawingWindow - with attributes for scale, origin, min and max X and Y
Then we compare the primary components of what we're drawing against those values 
to see if we need to truncate. 
This code determines whether line segments intersect each other or not, could be modified
to take line segment objects and return the point not just the boolean. Even if I don't 
use it here, helpful to have 
https://github.com/pgkelley4/line-segments-intersect/blob/master/js/line-segments-intersect.js

Okay new plan to try, shapes come with getYatX and getXatY function which take an input in 
one axis and return a corresponding location in the other axis. Then we can test for line 
intersection against all the line segments forming the window border. This is still going 
to be a large amount of processing to regularly do however. It is at least less processing
than doing the full line segment intersection because we have the benefit of knowing that 
one of the lines is vertical or horizontal. Anyway, going to leave this for a while now 
because its starting to hurt my brain. 

The window object can have vertical and horizontal spans. Span can also have a method to test 
whether a point lies between the span (this does require vertical and horizontal to be distinct classes)
The window object can test whether a point lies inside it using both spans. If the line is inside, all
good. 

11. Share option to distribute bitmap, vector or QuickDraw files

12. Improvements to corner detection

13. Improvements to circle fitting - also circle fit constraint needs an icon

14. Arc fitting

15. Draw more icons

16. Line thickness could be a slider
17. Change point subdivision in quadtree depending on scale

18. Constraint precedence resolution thoughts
19. Spatial constraint implementation: incidence, perpendiular, midpoint, circle centre
20. Temporal constraints: equal length, perpendicular to, parallel to
21. Fix line incidence during continuous drawing

22. Join line segments to create closed objects
23. Allow fill of objects, and forming of independent objects once joined
24. Wireframe mock-ups of eventual design on android
25. Research C++/CLI/C# in terms of windows application design
26. Look into Windows UI design in preparation to port to a C++ program / CLI?
27. See the android to windows api mapping guide
28. Android dependency inclusion




/**************************************************************
					  Display Debug Axes
**************************************************************/
// To display axes we first need to work out the range currently
// in view. This is very simply performed using the scaleFactor
// and the windowOrigin
windowWidth = screenResWidth / scaleFactor;
windowHeight = screenResHeight / scaleFactor;
// work out to have five markers along the bottom
markerDist = RoundToNearest10(windowWidth/5);
// Find location of first marker horizontally and vertically

// Generate remainder of marker locations

// create markers as text on the canvas with | and --  and axis value


// Then lets go for the different paint settings with the spinner selection
// Some sort of variable will need to be set from the view which allows the 
// drawingCanvas to know what paint style to use

// Show lines over the top of points to see how the fit is occurring
// It would be interesting to investigate the quality of the circle fit 
// and corner detection



/**************************************************************
					    C O M P L E T E D
**************************************************************/

5. Rework activity into a fragment to allow persistance of data even as fragment gets destroyed

4. Have file open/close actually work using file names 
 - Only display the actual file name in list
 - Save files with the .qdr extension and only open those

3. Handle screen rotation
onSaveInstanceState() and onRestoreInstanceState() should be implemented
and they should save the current shape database as temp.qdr (qdr gonna be
the QuickDraw file extension). Apparently as this data gets large it may 
be better to create it in a fragment which can maintain a data instance 
across rotation for example. The default saveInstance and restoreInstance
may be able to do it automatically - it saves a bundle including data, but
it may take a while for larger data as it serialises and de-serialises. 

An important thing to do will be to create another layout xml file for horizontal
layout. See the following for info on how to declare the XML files such that 
the correct one is automatically selected for the current screen orientation.

Without a different layout file, it uses the same one for both orientations, and 
that currently looks really dumb in portrait mode

http://developer.android.com/guide/practices/screens_support.html
http://stackoverflow.com/questions/2124046/how-do-i-specify-different-layouts-for-portrait-and-landscape-orientations

2. Actions for JSON save/load off main thread
See this for async tasks, or since the UI doesn't actually need to be modified
we might be able to get away with the simpler one, which just creates a runnable
thread and gives it some work to do
http://developer.android.com/guide/components/processes-and-threads.html

1. File selection dialouge

1. Research saving and loading options from canvas as bitmap image
toDisk = Bitmap.createBitmap(640,480,Bitmap.Config.ARGB_8888);
Canvas canvas = new Canvas(toDisk);
canvas.drawPath(currentPath, pen);
toDisk.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(new File("Image.png")));

2. Look into documentation regarding open-source vector drawings
http://www.svgbasics.com/aboutus.html
<?xml version="1.0" standalone="no"?>
<!DOCTYPE svg PUBLIC "-//W3C//DTD SVG 1.1//EN" "http://www.w3.org/Graphics/SVG/1.1/DTD/svg11.dtd"> 
<svg viewBox = "0 0 500 600" version = "1.1">
    <rect x = "100" y = "100" width = "400" height = "200" fill = "yellow" stroke = "black" stroke-width = "3"/>
    <rect x = "100" y = "350" rx = "100" ry = "50" width =  "400" height = "200" fill = "salmon" stroke = "black" stroke-width = "3"/>
</svg>

3. Research serialisation of objects into JSON so we can store files with all data
// Okay looks like we have two options - we either use the standard java 'serialisable'
// class implementation, which doesn't actually do anything apart from mark the class
// as one you want to serialise. The normal implementation writes a binary output file. 
// This is not human readable. The other way to save objects is to write a JSON or XML
// structured file. These can be done using other libraries like 'Jackson' or 'JSON-IO'
// Still not too clear on the process of adding library dependencies to android

// For the moment, serialising and saving the shapeDB file would be perfect, then on loading
// we load that up, as it contains all the info we actually need. 

// toString() can be overriden in any class because all java classes implicitly implement 
// the java.lang.Object class which includes the toString() method
//Make serialisable class with toString function
public class MyClass implements Serializable{}
//Output object to file stream
FileOutputStream fout = new FileOutputStream("c:/myclassObj.txt");
ObjectOutputStream oos = new ObjectOutputStream(fout);   
oos.writeObject(myclassobj);
// Read object back in
FileInputStream fin = new FileInputStream("c:\\address.ser");
ObjectInputStream ois = new ObjectInputStream(fin);
address = (Address) ois.readObject();

// Or perhaps a JSON library could be better - simples!
https://github.com/FasterXML/jackson
ObjectMapper mapper = new ObjectMapper();							// Write
mapper.writeValue(new File("c:/myobj.json"), myObj);				// Write 
mapper.writerWithDefaultPrettyPrinter().writeValueAsString(user);	// Write pretty indented + lines
MyClass myObj = mapper.readValue(new File("c:/user.json"), MyClass.class);	// Read


https://code.google.com/p/json-io/	
MyClass myObj;									// Write
String json = JsonWriter.objectToJson(myObj); 	// Write
JsonReader jr = new JsonReader(inputStream);	// Read
Employee emp = (Employee) jr.readObject();		// Read

4. Look into android save / load files & filesystem stuff
<manifest ...>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
	<uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE" />
	
	Also need to look into file saving and selection dialogue

/**************************************************************
				  Ap ply scale to new drawings
**************************************************************/
// Convert a shape fitted at current scale and window origin
// to the absolute coordinate system
shapeDB.add(InputPointSet ptSet) {
	for (Point pt: pts) {
		pt = locToAbsCoords(pt, scaleFactor, WindowOrigin);
	}
	// This is not ideal - ideally each point set should do it to itself
	// perhaps with a 
	if (ptSet.fittedShape.type == ShapeTypes.LINE) {
		ptSet.fittedShape.start = locToAbsCoords(ptSet.fittedShape.start, scaleFactor, WindowOrigin);
		ptSet.fittedShape.finish = locToAbsCoords(ptSet.fittedShape.finish, scaleFactor, WindowOrigin);
		ptSet.fittedShape.length = ptSet.fittedShape.length / scaleFactor;
	}
	if (ptSet.fittedShape.type == ShapeTypes.CIRCLE) {
		ptSet.fittedShape.centre = locToAbsCoords(ptSet.fittedShape.centre, scaleFactor, WindowOrigin);
		ptSet.fittedShape.radius = ptSet.fittedShape.radius / scaleFactor;
	}
}

locToAbsCoords(Point pt, float SF, Point origin) {}
locToAbsCoords(Point pt) {
	perform the function of loc to abs coords but use the values stored in the view 
	so that we don't actually need to pass them in so many times. 
}

/**************************************************************
					  Output as Points
**************************************************************/
// Is there a way that we can store the last scale factor 
// output so that we don't need to recompute the changes 
// every time invalidate is called? And it will be called
// very regularly. Ahh but I suppose for the fitted shapes
// Its not really an issue - its not many points at all
// just for the debug output it is
@Override
private void onDraw() {
	if ()//Access preferences in here somehow
		getPointsAsPathDEBUG(10.0f, scaleFactor, windowOrigin);
	else getPath(scaleFactor, windowOrigin);
}

// Function returns a set of circles for each input point it contains
getPointsAsPathDEBUG(float ptCircRadius, scaleFactor, windowOrigin) {
	if path.reset();
	for (Point pt : pts) {
		Point loc = absToLocCoords()
		path.addCircle(pt.x,pt.y,ptCircRadius,0);
	}
	return pointsAsPath;
}


/**************************************************************
					Modify Paint Styles
**************************************************************/
// Use the spinner view to create a view which has a drop down (or drop up)
// The content is specified with a SpinnerAdapter, which can be an 
// arrayAdapter which we have already seen
// We'll use the user selections here to modify paint width and colour
http://developer.android.com/guide/topics/ui/controls/spinner.html



Spinner spinner = (Spinner) findViewById(R.id.spinner);
// Create an ArrayAdapter using the string array and a default spinner layout
ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        R.array.planets_array, android.R.layout.simple_spinner_item);
// Specify the layout to use when the list of choices appears
adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
// Apply the adapter to the spinner
spinner.setAdapter(adapter);


public class SpinnerActivity extends Activity implements OnItemSelectedListener {
    ...
    
    public void onItemSelected(AdapterView<?> parent, View view, 
            int pos, long id) {
        // An item was selected. You can retrieve the selected item using
        // parent.getItemAtPosition(pos)
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Another interface callback
    }
}

Spinner spinner = (Spinner) findViewById(R.id.spinner);
spinner.setOnItemSelectedListener(this);

