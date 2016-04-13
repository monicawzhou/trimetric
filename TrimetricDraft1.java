import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PVector;


public class TrimetricDraft1 extends PApplet {

	private static final long serialVersionUID = 1L;
	PFont f;
	PVector axis1 = new PVector(700,200);
	PVector axis1Dir = new PVector(300,0);
	PVector axis1End = PVector.add(axis1,axis1Dir);
	PVector axesConverge = axis1End;
	PVector axis1Rotate;
	
	PVector trimetricAxesConverge;
	PVector dimetricDir;

	
	PVector rotateProjPt;
	
	// stroke color for projection lines
	int projLine = 235;
	
	PVector[] plan = new PVector[4];
	
	int row = 4;
	int col = 2;
	PVector[][] sectionHeights = new PVector[row][col];
	
	PVector[][] dimetricHeights = new PVector[row][col];
	PVector[][] trimetricHeights = new PVector[row][col];

	

	public static void main(String args[]) {
		PApplet.main(new String[] { "--present", "MyProcessingSketch" });
	}

	public void setup() {
		
		size(1400,800);
		background(255);
		smooth(2);
		
		plan[0] = new PVector(800,100);
		plan[1] = new PVector(850,100);
		plan[2] = new PVector(850,50);
		plan[3] = new PVector(800,50);
		
		sectionHeights[0][0] = new PVector(800,220);
		sectionHeights[0][1] = new PVector(800,270);
		sectionHeights[1][0] = new PVector(850,220);
		sectionHeights[1][1] = new PVector(850,270);
		sectionHeights[2][0] = new PVector(850,220);
		sectionHeights[2][1] = new PVector(850,270);
		sectionHeights[3][0] = new PVector(800,220);
		sectionHeights[3][1] = new PVector(800,270);
	}

	public void draw() {
		stroke(0);
		
		
		// draw plan
		int k=1;
		for(int i=0;i<plan.length;i++){
			drawPoint(plan[i]);
			
			k=k%(plan.length);
			drawLine(plan[i],plan[k]);
			k++;
		}
		
		drawLine(axis1,axis1End);
		
		// draw section
		for(int j=0; j<row; j++) {
			for(int n=0; n<col-1; n++) {
				drawLine(sectionHeights[j][n],sectionHeights[j][n+1]);
			}
		}
		
		
		axis1Rotate = rotatePVector(axis1Dir,PI*3/2);
		PVector axis1RotateEnd = PVector.add(axis1End,axis1Rotate);
		drawLine(axis1End,axis1RotateEnd);
		
		drawDimetric(PI/6);
		
		// draw section widths
		drawLine(sectionHeights[0][0],sectionHeights[1][0]);
		drawLine(sectionHeights[0][1],sectionHeights[1][1]);


	}
	
	int counter = 1;
	public void mousePressed() {

		stroke(0); 
		fill(0);
		PVector mouse = new PVector(mouseX,mouseY);

		drawTrimetric(mouse, trimetricAxesConverge);

		
	}
	
	// draws dimetric
	public void drawDimetric(float radians) {
		
		// draw dimetric projection axis 
		PVector axis1DirAntiparallel = antiparallel(axis1Dir); 
		dimetricDir = drawRotatedAxis(axesConverge, axis1DirAntiparallel,radians);
		
		// draw dimetric perpendicular
		PVector perpProjAxis = drawRotatedAxis(axesConverge, dimetricDir,PI/2);
		
		trimetricAxesConverge = PVector.add(axesConverge,dimetricDir);
		
		// find dimetric projected points
		for (int i=0; i<plan.length;i++){
			for (int j=0; j<col;j++) {
				PVector intersection = intersection(sectionHeights[i][j],axis1Dir,axesConverge,axis1Rotate);
				drawLine(sectionHeights[i][j],intersection);
				rotateProjPt = drawProjArc(intersection,axesConverge,radians);
				PVector dimetricIntersection = intersection(plan[i],perpProjAxis,rotateProjPt,dimetricDir);
				dimetricHeights[i][j] = dimetricIntersection;
				drawProjIntersection(plan[i],perpProjAxis,rotateProjPt,dimetricDir,axesConverge);
			}
		}
		
		// draw darkened outlines of dimetric heights
		for (int k=0;k<row;k++){
			for(int m=0;m<col-1;m++) {
				stroke(0);
				drawLine(dimetricHeights[k][m],dimetricHeights[k][m+1]);
			}
		}
		
		// draw darkened outlines of dimetric widths
		drawLine(dimetricHeights[0][0],dimetricHeights[2][0]);
		drawLine(dimetricHeights[0][1],dimetricHeights[2][1]);
		
	}
	
	// draws rotated axis and returns rotated direction vector
	public PVector drawRotatedAxis(PVector start, PVector dir, float radians) {
		PVector rotated = rotatePVector(dir,radians);
		PVector projAxis = PVector.add(start,rotated);
		drawLine(projAxis,start);
		return rotated;
	}
	
	
	public void drawTrimetric(PVector mouse, PVector axesConverge) {
		
		// draw projection axis (mouse click generated axis)
		drawLine(mouse,axesConverge);
		
		// draw perpendicular to projection axis
		PVector mouseToConverge = PVector.sub(mouse,axesConverge);
		PVector rotate = drawRotatedAxis(axesConverge,mouseToConverge,PI*3/2);
		
		PVector perpDimetric = drawRotatedAxis(axesConverge,dimetricDir,PI*3/2);
		
		float angle = fullAngleBetween(perpDimetric,rotate);
		text(degrees(angle),10,10*counter);
//		
		counter++;

		text(degrees(angle)+" degrees",mouse.x,mouse.y+24);
		
		for (int i=0; i<plan.length;i++) {
			for(int j=0;j<col;j++) {
			
				PVector intersection = intersection(plan[i],dimetricDir,axesConverge,perpDimetric);
				PVector rotatePt = drawProjArc(intersection,axesConverge,angle);
				drawProjIntersection(plan[i],dimetricDir,axesConverge,perpDimetric,axesConverge);
				
				drawProjIntersection(dimetricHeights[i][j],rotate,rotatePt,mouseToConverge,axesConverge);
				PVector trimetricIntersection = intersection(dimetricHeights[i][j],rotate,rotatePt,mouseToConverge);
				trimetricHeights[i][j] = trimetricIntersection;
			}
		}
		
		// draw darkened heights
		for (int k=0;k<plan.length;k++) {
			for (int n=0;n<col-1;n++) {
				stroke(0);
				drawLine(trimetricHeights[k][n],trimetricHeights[k][n+1]);
			}
		}
		
		// draw darkened widths
		int c=1;
		for (int m=0;m<col;m++) {
			for (int p=0; p<plan.length;p++) {
				stroke(0);
				c=c%plan.length;
				drawLine(trimetricHeights[p][m],trimetricHeights[c][m]);
				c++;
			}
		}
		
		// find dimetric projected points
//		for (int i=0; i<plan.length;i++){
//			for (int j=0; j<col;j++) {
//				PVector intersection = intersection(sectionHeights[i][j],axis1Dir,axesConverge,axis1Rotate);
//				drawLine(sectionHeights[i][j],intersection);
//				rotateProjPt = drawProjArc(intersection,axesConverge,angle);
//				PVector dimetricIntersection = intersection(plan[i],rotate,rotateProjPt,mouseToConverge);
//				dimetricHeights[i][j] = dimetricIntersection;
//				drawProjIntersection(plan[i],rotate,rotateProjPt,mouseToConverge,axesConverge);
//			}
//		}
//		
//		// draw darkened outlines of dimetric heights
//		for (int k=0;k<row;k++){
//			for(int m=0;m<col-1;m++) {
//				stroke(0);
//				drawLine(dimetricHeights[k][m],dimetricHeights[k][m+1]);
//			}
//		}
//		
//		// draw darkened outlines of dimetric widths
//		drawLine(dimetricHeights[0][0],dimetricHeights[2][0]);
//		drawLine(dimetricHeights[0][1],dimetricHeights[2][1]);
		
	}
	
	
	public void drawProjAxes(PVector mouse, PVector axesConverge) {
		
		// draw projection axis (mouse click generated axis)
		drawLine(mouse,axesConverge);
		// draw perpendicular to projection axis
		PVector axis1ToMouse = PVector.sub(mouse,axesConverge);
		PVector rotate = rotatePVector(axis1ToMouse,PI/2);
		PVector perpEnd = PVector.add(axesConverge,rotate);
		drawLine(axesConverge,perpEnd);

		PVector axis1DirAntiparallel = antiparallel(axis1Dir);
		
		float angle = fullAngleBetween(axis1DirAntiparallel,axis1ToMouse);
//		text(degrees(angle),10,10*counter);
//		
//		counter++;

		text(degrees(angle)+" degrees",mouse.x,mouse.y+24);
		
//		PVector intersection = intersection(pointSection,axis1Dir,axesConverge,axis1Rotate);
//		rotateProjPt = drawProjArc(intersection,axesConverge,angle);
//		drawProjIntersection(pointPlan,rotate,rotateProjPt,axis1ToMouse,axesConverge);
		
		// find dimetric projected points
		for (int i=0; i<plan.length;i++){
			for (int j=0; j<col;j++) {
				PVector intersection = intersection(sectionHeights[i][j],axis1Dir,axesConverge,axis1Rotate);
				drawLine(sectionHeights[i][j],intersection);
				rotateProjPt = drawProjArc(intersection,axesConverge,angle);
				PVector dimetricIntersection = intersection(plan[i],rotate,rotateProjPt,axis1ToMouse);
				dimetricHeights[i][j] = dimetricIntersection;
				drawProjIntersection(plan[i],rotate,rotateProjPt,axis1ToMouse,axesConverge);
			}
		}
		
		// draw darkened outlines of dimetric heights
		for (int k=0;k<row;k++){
			for(int m=0;m<col-1;m++) {
				stroke(0);
				drawLine(dimetricHeights[k][m],dimetricHeights[k][m+1]);
			}
		}
		
		// draw darkened outlines of dimetric widths
		drawLine(dimetricHeights[0][0],dimetricHeights[2][0]);
		drawLine(dimetricHeights[0][1],dimetricHeights[2][1]);
		
	}
	
	// draws the projection line from point to intersection on axis
	public void drawProjIntersection(PVector a, PVector aDir, PVector b, PVector bDir, PVector axesConverge) {
		drawPoint(a);
		
		PVector intersection = intersection(a,aDir,b,bDir);
		drawPoint(intersection);
		
//		PVector pointForRotate = PVector.sub(intersection,axesConverge);
//		pointMagnitude = pointForRotate.mag();

		stroke(projLine);
		drawLine(a,intersection);
		drawLine(intersection,b);
		
	}
	
	public PVector drawProjArc(PVector intersection, PVector axesConverge, float angle) {
		PVector axisToPoint = PVector.sub(intersection,axesConverge);
		text(axisToPoint.x+", "+axisToPoint.y,100,100);

		PVector rotated = rotatePVector(axisToPoint,angle);
		PVector rotateProjPt = PVector.add(axesConverge,rotated);
		drawPoint(rotateProjPt);
		
		fill(255,0);
		stroke(projLine);
		PVector rotationRef = new PVector(100,0);
		float angleStart = fullAngleBetween(rotated,rotationRef);
		float pointMagnitude = axisToPoint.mag();
		
		arc(axesConverge.x,axesConverge.y,pointMagnitude*2,pointMagnitude*2,angleStart,angleStart+angle);
		return rotateProjPt;
	
	}
	
	public PVector antiparallel(PVector p) {
		PVector antiparallel = new PVector(-1*p.x,-1*p.y);
		return antiparallel;
	}
	
	// returns full 360 rotation, not only 180
//	public float angleBetween(PVector a, PVector b) {
//		float numerator = a.dot(b);
//		float magA = a.mag();
//		float magB = b.mag();
//		float denominator = magA*magB;
//		float theta = acos(numerator/denominator);
//		return theta;
//	}
	
	// returns full 360 rotation, not only 180
	public float fullAngleBetween(PVector a, PVector b) {
		float dot = a.dot(b);
		float determinant = determinant(a,b);
		float angle = atan2(determinant,dot);
		
		if(angle>0) {
			angle = 2*PI-angle;
		}
		if(angle<0) {
			angle = -1*angle;
		}

		return angle;
	}
	
	public float determinant(PVector a, PVector b) {
		return a.x*b.y-a.y*b.x;
	}

	
	public void drawPerp(PVector p, PVector start) {
		PVector rotated = rotatePVector(p,PI/4);
		PVector end = PVector.add(start, rotated);
		drawLine(start,end);
	}
	
	// returns direction vector rotated, not end vector
	// rotates counterclockwise
	public PVector rotatePVector(PVector dir, float theta) {
		
		float r = dir.mag();
		float angle;
		if(dir.x>0) {
			angle = asin(dir.y/r);
		} else {
			angle = PI-asin(dir.y/r);
		}
		PVector rotateDir = new PVector(r*cos(theta-angle),-1*r*sin(theta-angle));
		return rotateDir;	
	}
	
//	//rotates counterclockwise
//	public PVector rotatePVector(PVector dir, float theta) {
//		theta = 2*PI-theta;
//		PVector rotateDir = new PVector();
//		rotateDir.x = (dir.x*cos(theta)-dir.y*sin(theta));
//		rotateDir.y = (dir.x*sin(theta)+dir.y*cos(theta));
//		return rotateDir;
//	}
	
	public void drawPoint(PVector p) {
		stroke(0);
		fill(0);
		ellipse(p.x,p.y,2,2);
		
	}
	
	public void drawLine(PVector start, PVector end) {
		line(start.x,start.y,end.x,end.y);
		textLineCoord(start,end);
	}
	
	public void textLineCoord(PVector start, PVector end) {
		fill(0,50);
		f = createFont("Arial",9,true);
		textFont(f);
		text((int)start.x+", "+(int)start.y, start.x, start.y+12);
		text((int)end.x+", "+(int)end.y, end.x, end.y+12);
	}
	
	public PVector intersection(PVector startA, PVector dirA, PVector startB, PVector dirB) {
		
		// find qDir scalar h
		PVector perpToDirA = new PVector(-1*dirA.y,dirA.x);
		PVector aMinusB = PVector.sub(startA,startB);
		float numerator = aMinusB.dot(perpToDirA);
		float denominator = dirB.dot(perpToDirA);
		float h = numerator/denominator;
        
		// intersection
		PVector hqDir = new PVector(dirB.x,dirB.y);
		hqDir.mult(h);
		PVector intersection = PVector.add(startB,hqDir);

		return intersection;
	}
	
	public void drawPlan(int x, int y, int sideCoord) {
		line(sideCoord+x,0+y,0+x,sideCoord+y);
		line(0+x,sideCoord+y,sideCoord+x,2*sideCoord+y);
		line(sideCoord+x,2*sideCoord+y,2*sideCoord+x,sideCoord+y);
		line(2*sideCoord+x,sideCoord+y,sideCoord+x,0+y);
	}
	
	public void drawSection() {
		
	}
}
