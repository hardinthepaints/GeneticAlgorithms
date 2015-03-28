import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.geom.*;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import javax.imageio.*;
import java.util.ArrayList;
import java.lang.InterruptedException;
import java.util.Collections;

// Each MyPolygon has a color and a Polygon object
class MyPolygon {

	Polygon polygon;
	Color color;

	public MyPolygon(Polygon _p, Color _c) {
		polygon = _p;
		color = _c;
	}

	public Color getColor() {
		return color;
	}

    public void setColor(Color _c) {
        color = _c;
    }
    
	public Polygon getPolygon() {
		return polygon;
	}

}


// Each GASolution has a list of MyPolygon objects
class GASolution implements Comparable {

	ArrayList<MyPolygon> shapes;
    int fitness;
	// width and height are for the full resulting image
	int width, height;

	public GASolution(int _width, int _height) {
		shapes = new ArrayList<MyPolygon>();
		width = _width;
		height = _height;
        
	}
    
    public void setFitness(int _fitness) {
        fitness = _fitness;
    }
    
    public int getFitness() {
        return fitness;
    }

	public void addPolygon(MyPolygon p) {
		shapes.add(p);
	}	

	public ArrayList<MyPolygon> getShapes() {
		return shapes;
	}

	public int size() {
		return shapes.size();
	}
	
    public int compareTo(Object otherGASol) {
        GASolution other = (GASolution) otherGASol;
        //System.out.println("Compared " + this.getFitness() + " to " + other.getFitness() );
        return (new Double(this.getFitness())).compareTo(new Double(other.getFitness()));
    }  
	// Create a BufferedImage of this solution
	// Use this to compare an evolved solution with 
	// a BufferedImage of the target image
	//
	// This is almost surely NOT the fastest way to do this...
	public BufferedImage getImage() {
		BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		for (MyPolygon p : shapes) {
			Graphics g2 = image.getGraphics();			
			g2.setColor(p.getColor());
			Polygon poly = p.getPolygon();
			if (poly.npoints > 0) {
				g2.fillPolygon(poly);
			}
		}
		return image;
	}

	public String toString() {
		return "" + shapes;
	}
}


// A Canvas to draw the highest ranked solution each epoch
class GACanvas extends JComponent{

    int width, height;
    GASolution solution;

    public GACanvas(int WINDOW_WIDTH, int WINDOW_HEIGHT) {
    	width = WINDOW_WIDTH;
    	height = WINDOW_HEIGHT;
    }
 
    public int getWidth() { return width; }
    public int getHeight() { return height; }

    public void setImage(GASolution sol) {
  		solution = sol;
    }

    public void paintComponent(Graphics g) {
    
		BufferedImage image = solution.getImage();
		g.drawImage(image, 0, 0, null);
    	
    }
}


public class GA extends JComponent{
	
    GACanvas canvas;
    int width, height;
    BufferedImage realPicture;
    ArrayList<GASolution> population;

    // Adjust these parameters as necessary for your simulation
    double MUTATION_RATE = 0.01;
    double CROSSOVER_RATE = 0.6;
    int MAX_POLYGON_POINTS = 4;
    int MAX_POLYGONS = 4;
    int POP_SIZE = 100;
    int MUTATE_STRENGTH = 5;
    int P_SAMPLE_SIZE = 3000;
    

    public GA(GACanvas _canvas, BufferedImage _realPicture) {
    	canvas = _canvas;
		realPicture = _realPicture;
		width = realPicture.getWidth();
		height = realPicture.getHeight();
		
		population = new ArrayList<GASolution>();

		// You'll need to define the following functions
		createPopulation(POP_SIZE);	// Make 50 new, random chromosomes
		
    }

    //XXX XANDER CODE START
    
    public Color randColor(){
    	//return a random color
    	return new Color((int)(Math.random() * 255), (int)(Math.random() * 255), (int)(Math.random() * 255));
    	
    }
    
    public void createPopulation(int size){
    	//initialize a population, an arrayList of GA Solutions
    	
    	for (int j = 0; j < POP_SIZE; j++){
    		GASolution newChromosome = new GASolution(width, height);
    		
			for (int i = 0; i < MAX_POLYGONS; i++){
				
				//create new polygon with some random stats
				Polygon newPoly = new Polygon();
				
				//add random points to polygon object
				for (int k = 0; k < MAX_POLYGON_POINTS; k++){
					newPoly.addPoint((int)(Math.random() * width), (int)(Math.random()*height));
				}
				
				//put polygon in MyPolygon object
				MyPolygon nuPol = new MyPolygon(newPoly, randColor());
				
				//put MyPoly in solution object
				newChromosome.addPolygon(nuPol);
			}
            calcFitness(newChromosome);
			population.add(newChromosome);	
		}
		//population.set(1, perfectSolution());
        
        canvas.setImage(population.get(0));
    }
    
    public void evolve(int epochs){
    	//evolve the population epochs times
    		//mate parents together
    		//mutate child
    		//set image
    	//canvas.setImage(newChromosome);
    	for (int i = 0; i < epochs; i++){
            //System.out.println(i);
            
    		newGeneration();
    		
    		//population.add(5, perfectSolution());
//     		System.out.println("Perfect soln's fitness: " + perfectSolution().getFitness());
     		System.out.println("getFittest's fitness: " + getFittest().getFitness());
    		//ArrayList.sort(population);
    		Collections.sort(population);
    		
            //get fittest and display it
     		//if (i % 10 ==0 ){
                GASolution fittest = getFittest();
                canvas.setImage(fittest);
                //canvas.setImage(population.get(50));
                
                
                canvas.repaint();
            //}
            // if (true){
//             	for (GASolution sol : population){
//             		System.out.println(sol.getFitness());
//             	}
//             }

    	}	

    }
    
    public GASolution getFittest(){
    	//get the fittest soln
    	int fittestIndex = 0;
    	int greatestFitness = 0;
    	for (int i =0; i < POP_SIZE; i++){
    		if((population.get(i).getFitness()) >= greatestFitness) {
    			fittestIndex = i;
    			greatestFitness = population.get(i).getFitness(); //XXX need to reassign greatest fitness
    		}
    		
    	}
    	return population.get(fittestIndex);
    }
    
    public void newGeneration(){
    
    	ArrayList<GASolution> new_population = new ArrayList<>();
    	
    	for (int i = 0 ; i < (int)Math.round(POP_SIZE*CROSSOVER_RATE); i++){
    		GASolution p1 = pickParent();
    		GASolution p2 = pickParent();
//     		GASolution p1 = getFittest();
//     		GASolution p2 = getFittest();
    		GASolution child = child(p1, p2);
    		child = mutate(child);
    		calcFitness(child);
    		System.out.println("p1.getFitness():  p2.getFitness():  child.getFitness():" + 
    			p1.getFitness() + " " + p2.getFitness() + " " + child.getFitness()); 

    		new_population.add(child);
    	}
        for (int i = 0; i < (int)Math.round(POP_SIZE * (1 - CROSSOVER_RATE)); i++) {
            new_population.add(pickParent());
            // TODO: possibly mutate
        }
    	
    	population = new_population;
    }
    
    public ArrayList<MyPolygon> deepCopy(ArrayList<MyPolygon> p1Shapes){
    	
    	ArrayList<MyPolygon> copy = new ArrayList<MyPolygon>();
   		 //perform deep copy
    	for (MyPolygon myPoly : p1Shapes){
    		Color newColor = new Color(myPoly.getColor().getRGB());
    		//newColor = myPoly.getColor();
    		
    		Polygon nuPol = new Polygon(myPoly.getPolygon().xpoints.clone(), myPoly.getPolygon().ypoints.clone(), myPoly.getPolygon().npoints);
    		
    		
    		MyPolygon newPoly = new MyPolygon(nuPol, newColor); 
    		copy.add(newPoly);
    		
    		
    	}
    	return copy;
    }
    
    public GASolution child(GASolution p1, GASolution p2){
    	
    	
    	
    	
    	//shapes from parents
    	
    	ArrayList<MyPolygon> p1Shapes = deepCopy(p1.getShapes());
    	ArrayList<MyPolygon> p2Shapes = deepCopy(p2.getShapes());
    	
    	

		//create child soln
		GASolution child = new GASolution(width, height);
		
		
		
		//choose random number of polygons from p1 to pass on
		int pivot = (int)Math.round((float)(Math.random() * MAX_POLYGONS));
		for (int i = 0; i < MAX_POLYGONS; i++){
			if (i < pivot) {
				child.addPolygon(p1Shapes.get(i));
			}
			else {
				child.addPolygon(p2Shapes.get(i));
			}
		}
    	return child;
    }
    
    public GASolution mutate(GASolution input){
    	//mutate a GASolution
    	ArrayList<MyPolygon> polygons = input.getShapes();
    	
    	for (MyPolygon poly : polygons){
			if (Math.random() < MUTATION_RATE){
				mutatePolygon(poly);
				//System.out.println("mutated a polygon!");
			}
    	}
    	return input;
    }
    
    public int intCoinFlip() {
        if (Math.round(Math.random()) == 1)
            return 1;
        else return -1;
            
    }
    
    public int colorBounce(int r) {
        if (r > 255) {
                r = 255;
            }
            if (r < 0) {
                r = 0;
            }
            return r;
    }
    
    public int pointBounce(int r, int bound) {
    if (r > bound) {
                r = bound;
            }
            if (r < 0) {
                r = 0;
            }
            return r;
    }
    public void mutatePolygon(MyPolygon input){
    	//mutate a single polygon
        Polygon toMutate = input.getPolygon();
    	
        for (int i = 0; i < toMutate.npoints; i++) {
        		
            	//mutate points
                toMutate.xpoints[i] += (int)Math.round(Math.random()*MUTATE_STRENGTH)*intCoinFlip();
                toMutate.ypoints[i] += (int)Math.round(Math.random()*MUTATE_STRENGTH)*intCoinFlip();
                //ensure the points are within bounding box
                toMutate.xpoints[i] = pointBounce(toMutate.xpoints[i], width);
                toMutate.ypoints[i] = pointBounce(toMutate.ypoints[i], height);
        }
        
        int r = input.getColor().getRed();
        int g = input.getColor().getGreen();
        int b = input.getColor().getBlue();
        
        r += Math.round(Math.random()*MUTATE_STRENGTH)*intCoinFlip();
        g += Math.round(Math.random()*MUTATE_STRENGTH)*intCoinFlip();
        b += Math.round(Math.random()*MUTATE_STRENGTH)*intCoinFlip();
        Color mutatedColor= new Color(colorBounce(r),colorBounce(g),colorBounce(b));
        input.setColor(mutatedColor);
    }
    
    public int sumFitnessSqrd(){
        int sum = 0;
        for (int i = 0; i < POP_SIZE; i++) {
            //System.out.println("sum : " + sum);
            sum += population.get(i).getFitness()^2;
        }
        
        return sum;
    }
    
    public GASolution pickParent(){
    	//select a parent from the population based on fitness and spinner thingy
        // Code from Matthew Whitehead Lecture

        int fitness_sum = sumFitnessSqrd();
    
        int r = (int)(Math.random()*fitness_sum);

        int running_sum = 0;
        int current_parent = 0;
        while (running_sum <= r) {
        //    System.out.println("sumfitness " + fitness_sum);
         //   System.out.println("runningsum: " + running_sum);
           // System.out.println("r : "  + r);
          /*  if (current_parent == 50) {
            System.out.println("first value" + population.get(0).getFitness());
            System.out.println("final value" +population.get(current_parent-1).getFitness());
            }*/
            running_sum += population.get(current_parent).getFitness()^2;
            current_parent += 1;
        }
    	return population.get(current_parent - 1);
    }
    
    public GASolution perfectSolution(){
    	//create a perfect solution from the getgo
    	GASolution perfect = new GASolution(width, height);
    	
    		int[] currentpt = {0, 0};
    		
			for (int i = 0; i < 4; i++){
				
				//create new polygon with some random stats
				Polygon newPoly = new Polygon();
				
				if (i == 2){
					
					currentpt[0] += width/2;
					currentpt[1] = 0;
				}
				
				//get color of squares in real picture
				int[] squareColor = int_to_RGB(realPicture.getRGB(currentpt[0] + 10, currentpt[1] + 10));
				Color sqColor = new Color(squareColor[0], squareColor[1], squareColor[2]);
				
				//put 
				for (int j = 0; j < 4; j++){
				  	
					if (j == 0){
						//top right point
					} else if (j == 1){
						//top left point
						currentpt[0] += width/2;
					
					} else if (j == 2 ) {
						//bottom left pt
						currentpt[1] += height/2;
					
					} else {
						//bottom right
						currentpt[0] -= width/2;
					}
					
					newPoly.addPoint(currentpt[0], currentpt[1]);
					
				}
				
				
				//put polygon in MyPolygon object
				MyPolygon nuPol = new MyPolygon(newPoly, sqColor);
				
				//put MyPoly in solution object
				perfect.addPolygon(nuPol);
			}
            calcFitness(perfect);
			//population.add(perfect);	
    	return perfect;
    }
    
    public int sumFitness() {
        int sum = 0;
        for (int i = 0; i < POP_SIZE; i++) {
            //System.out.println("sum : " + sum);
            sum += population.get(i).getFitness();
        }
        
        return sum;
    }
    
    public int[] int_to_RGB(int rgb){
            int red =   (rgb >> 16) & 0xFF;
            int green = (rgb >>  8) & 0xFF;
            int blue =  (rgb      ) & 0xFF;
            int[] output = new int[] {red, green, blue};
            return output;
    }
    
    public int calcFitness(GASolution input){
		//return fitness of a given solution 
		//base it off random sampled pixels of target image and the actual polygons
        int diff = 0;
        for (int i = 0; i < P_SAMPLE_SIZE; i++) {
            int x = (int)(Math.random()*width);
            int y = (int)(Math.random()*height);

            int[] rgb2 = int_to_RGB(Color.WHITE.getRGB());
            int[] rgb1 = int_to_RGB(realPicture.getRGB(x,y));
            
            //declare arraylist to store shapes
            ArrayList<MyPolygon> polygons = input.getShapes();
            
            //iterate backwards through list of shapes because they visible in that order
            for (int k = polygons.size() - 1; k >= 0; k-- ) {
            	if (polygons.get(k).getPolygon().contains(x,y)){ 
            		//if point is in polygon
            		Color c2 = polygons.get(k).getColor();
            		rgb2 = int_to_RGB(c2.getRGB());

            		break;
            	}
            }
            

			
			for (int j = 0; j < 3; j++) {
                diff += Math.abs(rgb1[j] - rgb2[j]);
            }
            
          // System.out.println("DIFF: " + (int)((255 - diff/(P_SAMPLE_SIZE*3))));
        }
        int fitness = (int)((255 - diff/(P_SAMPLE_SIZE*3)));
        input.setFitness(fitness);
        //System.out.println(fitness);
		return fitness;
    }
    
    public int calcAltFitness(GASolution input){
		//return fitness of a given solution 
		//base it off random sampled pixels of target image and realpicture
        int diff = 0;
        for (int i = 0; i < P_SAMPLE_SIZE; i++) {
            int x = (int)(Math.random()*width);
            int y = (int)(Math.random()*height);
            
            int[] rgb1 = int_to_RGB(realPicture.getRGB(x,y));

            int[] rgb2 = int_to_RGB(input.getImage().getRGB(x,y));
      
            
            for (int j = 0; j < 3; j++) {
                diff += Math.abs(rgb1[j] - rgb2[j]);
            }
          // System.out.println("DIFF: " + (int)((255 - diff/(P_SAMPLE_SIZE*3))));
        }
        int fitness = (int)((255 - diff/(P_SAMPLE_SIZE*3)));
        input.setFitness(fitness);
		return fitness;
    }
    
    //XXX XANDER CODE END
	

    public void runSimulation() {
    	
		evolve(100000); 	// Run for a million epochs 

    }

    public static void main(String[] args) throws IOException {
    


		String realPictureFilename = "test.jpg";

		BufferedImage realPicture = ImageIO.read(new File(realPictureFilename));

		JFrame frame = new JFrame();
		frame.setSize(realPicture.getWidth(), realPicture.getHeight());
		frame.setTitle("GA Simulation of Art");
		
		GACanvas theCanvas = new GACanvas(realPicture.getWidth(), realPicture.getHeight());
		
		frame.add(theCanvas);
		frame.setVisible(true);

		GA pt = new GA(theCanvas, realPicture);
			pt.runSimulation();
    }
}