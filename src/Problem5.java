import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

class Instance
{
	int instance;
	String manufacturer;
	String	power;
	String AI;
	String decision;

	public Instance(){}
	
	public Instance(int instance, String manufacturer, String power, String aI,
			String decision) 
	{
		super();
		this.instance = instance;
		this.manufacturer = manufacturer;
		this.power = power;
		AI = aI;
		this.decision = decision;
	}


	public int getInstance() {
		return instance;
	}
	public void setInstance(int instance) {
		this.instance = instance;
	}
	public String getManufacturer() {
		return manufacturer;
	}
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	public String getPower() {
		return power;
	}
	public void setPower(String power) {
		this.power = power;
	}
	public String getAI() {
		return AI;
	}
	public void setAI(String aI) {
		AI = aI;
	}
	public String getDecision() {
		return decision;
	}
	public void setDecision(String decision) {
		this.decision = decision;
	}
}

class Tree
{
	private String attributeLabel;
	private Map<String,Tree> association;
	
	public Tree()
	{ 
		attributeLabel = new String();
		association = new HashMap<String,Tree>();
	}

	public String getAttributeLabel() {
		return attributeLabel;
	}

	public void setAttributeLabel(String attributeLabel) {
		this.attributeLabel = attributeLabel;
	}

	public Map<String, Tree> getAssociation() {
		return association;
	}

	public void setAssociation(Map<String, Tree> association) {
		this.association = association;
	}
}

public class Problem5 
{
	
	static ArrayList<Instance> instances=new ArrayList<Instance>();
	static BufferedWriter bw;
	static FileWriter fw = null;
	static File dotFile = null;
	static int attrCounter=0,leafCounter=0;
	
	public static void main(String[] args)
	{
		/*Read instances from input data file */
		BufferedReader br = null;
		
		try
		{
			String currentRow;
 
			br = new BufferedReader(new FileReader("/home/rendezvous/workspace/ML_HW3/src/input_data.txt"));
					
			while ((currentRow = br.readLine()) != null) 
			{
				String[] data_elements = currentRow.split("	");
				instances.add(new Instance(Integer.valueOf(data_elements[0]),data_elements[1],data_elements[2],data_elements[3],data_elements[4]));
			}

			/* Build Decision Tree on the instances */
			buildDecisionTree();
			br.close();
		}
		catch (IOException e) 
		{
			e.printStackTrace();
		}
		
	}

	private static void buildDecisionTree() 
	{
		    /* Create a map to keep track which attributes are already used for classification in 
		     * the tree.
		     */
		    Map<String,Boolean> attributesUsed= new HashMap<String, Boolean>();
	        attributesUsed.put("Manufacturer", false);
	        attributesUsed.put("Power", false);
	        attributesUsed.put("AI", false);
	        
	        /*Create a tree to keep track of attribute nodes and attribute value branches*/
		    Tree object = new Tree();
		    
		    /*Invoke the Decision Tree Algorithm*/
			object=decisionTreeAlgorithm(instances,attributesUsed,object);
		
			/*Print the decision tree and generate the dt.dot file*/
			dotFile=new File("/home/rendezvous/workspace/ML_HW3/src/dt.dot");
			try 
			{
				fw = new FileWriter(dotFile.getAbsoluteFile());
				bw = new BufferedWriter(fw);
				bw.write("digraph G {");
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("Printing the decision tree : ");
			printTree(object);
			try
			{
				bw.append("\n }");
				bw.close();
				fw.close();
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	}

	private static String printTree(Tree object) 
	{
		/*Print the value of the attribute in the tree node*/
		String temp = object.getAttributeLabel();
		System.out.println(temp);
		
		/*Check base condition of recursion
		 * If label is "Y" or "N", it is a leaf node
		 * in the decision tree
		 */
		if(temp.equals("Y") || temp.equals("N"))
		{
			try 
			{
				leafCounter++;
				String currentLeaf="leaf"+leafCounter;
				bw.append("\n" + currentLeaf + "[shape=\"plaintext\",label=\"" + temp.toString() +"\"]");
				return currentLeaf;
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		/*Write attribute to dt.dot file*/
		String currentAttribute="";
		try 
		{
			attrCounter++;
			currentAttribute = "attr"+attrCounter;
			bw.append("\n" + currentAttribute + "[shape=\"rectangle\",label=\"" + temp.toString() +"\"]");
		}
		catch (IOException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*Retrieve the association map for a tree node and print the branches and subtrees*/
		Map<String,Tree> treeMap = new HashMap<String,Tree>();
		treeMap=object.getAssociation();
		
		Iterator<Entry<String, Tree>> iter = treeMap.entrySet().iterator();
		while(iter.hasNext())
		{
			Map.Entry<String, Tree> entry = (Entry<String, Tree>) iter.next();
			
			/*Print the branch value*/
			System.out.println(entry.getKey());
			
			/*Print remaining subtree*/
			String childAttribute = printTree(entry.getValue());
			
			/*Write connection to dt.dot file*/
			try 
			{
				bw.append("\n" + currentAttribute + "->" + childAttribute + "[label=\"" + entry.getKey() + "\"]");
			}
			catch (IOException e) 
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		return currentAttribute;
		
	}

	private static Tree decisionTreeAlgorithm(ArrayList<Instance> instances, Map<String, Boolean> attributesUsed2, Tree treeObject) 
	{
		Iterator<Entry<String, Boolean>> iter = attributesUsed2.entrySet().iterator();
		double bestGain=0.0;
		String bestAttribute = "";
		Map<String,ArrayList<Instance>> subArrayLists = new HashMap<String,ArrayList<Instance>>();

		/* Iterate over all attributes that are not yet tested  and find the best attribute*/
		while(iter.hasNext())
		{
			Map.Entry<String, Boolean> entry = (Entry<String, Boolean>) iter.next();
			if( entry.getValue() == false)
			{
				System.out.println(entry.getKey());
				if(entry.getKey().equals("Manufacturer"))
				{
					ArrayList<Instance> iRobot=new ArrayList<Instance>();
					ArrayList<Instance> Honda=new ArrayList<Instance>();
					ArrayList<Instance> BotsonDynamics=new ArrayList<Instance>();
					
					for(Instance i:instances)
					{
						if(i.getManufacturer().equals("iRobot"))
						{
							iRobot.add(i);
						}
						else if(i.getManufacturer().equals("Honda"))
						{
							Honda.add(i);
						}
						else if(i.getManufacturer().equals("Botson-Dynamics"))
						{
							BotsonDynamics.add(i);
						}
					}
				
					/* Calculate Gain */
					double gain = calculateEntropy(instances) - (((double)iRobot.size()/(double)instances.size()))*(calculateEntropy(iRobot))
							- ((double)Honda.size()/(double)instances.size())*calculateEntropy(Honda) - ((double)BotsonDynamics.size()/(double)instances.size())*calculateEntropy(BotsonDynamics);
					
					/* Compare with best gain */
					if( gain > bestGain)
					{
						bestGain=gain;
						bestAttribute="Manufacturer";
						System.out.println("Best gain "+bestGain+" Best attribute "+ bestAttribute);
						subArrayLists = new HashMap<String,ArrayList<Instance>>();
						subArrayLists.put("iRobot",iRobot);
						subArrayLists.put("Honda",Honda);
						subArrayLists.put("Botson-Dynamics",BotsonDynamics);
					}
					
				}
				else if(entry.getKey().equals("Power"))
				{
					ArrayList<Instance> Battery=new ArrayList<Instance>();
					ArrayList<Instance> Gas=new ArrayList<Instance>();
					
					for(Instance i:instances)
					{
						if(i.getPower().equals("Battery"))
						{
							Battery.add(i);
						}
						else if(i.getPower().equals("Gas"))
						{
							Gas.add(i);
						}
					}
					
					/* Calculate gain */
					double gain = calculateEntropy(instances) - ((double)Battery.size()/(double)instances.size())*calculateEntropy(Battery)
							- ((double)Gas.size()/(double)instances.size())*calculateEntropy(Gas); 
					
					/* Compare with best gain */
					if( gain > bestGain)
					{
						bestGain=gain;
						bestAttribute="Power";
						System.out.println("Best gain "+bestGain+" Best attribute "+ bestAttribute);
						subArrayLists = new HashMap<String,ArrayList<Instance>>();
						subArrayLists.put("Battery",Battery);
						subArrayLists.put("Gas",Gas);
					}
					
				}
				else if(entry.getKey().equals("AI"))
				{
					ArrayList<Instance> MLBased=new ArrayList<Instance>();
					ArrayList<Instance> AlienTech=new ArrayList<Instance>();
					ArrayList<Instance> RandomPlanner=new ArrayList<Instance>();
					ArrayList<Instance> AIAlgorithm=new ArrayList<Instance>();
					
					for(Instance i:instances)
					{
						if(i.getAI().equals("ML-Based"))
						{
							MLBased.add(i);
						}
						else if(i.getAI().equals("Alien-tech"))
						{
							AlienTech.add(i);
						}
						else if(i.getAI().equals("Random-planner"))
						{
							RandomPlanner.add(i);
						}
						else if(i.getAI().equals("AI-algorithm"))
						{
							AIAlgorithm.add(i);
						}
					}
					
					/* Calculate gain */
					double gain = calculateEntropy(instances) - ((double)MLBased.size()/(double)instances.size())*calculateEntropy(MLBased)
							- ((double)AlienTech.size()/(double)instances.size())*calculateEntropy(AlienTech) 
							- ((double)RandomPlanner.size()/(double)instances.size())*calculateEntropy(RandomPlanner)
							- ((double)AIAlgorithm.size()/(double)instances.size())*calculateEntropy(AIAlgorithm);
					        
					
					/* Compare with best gain */
					if( gain > bestGain)
					{
						bestGain=gain;
						bestAttribute="AI";
						System.out.println("Best gain "+bestGain+" Best attribute "+ bestAttribute);
						subArrayLists = new HashMap<String,ArrayList<Instance>>();
						subArrayLists.put("ML-Based",MLBased);
						subArrayLists.put("Random-planner",RandomPlanner);
						subArrayLists.put("Alien-tech",AlienTech);
						subArrayLists.put("AI-algorithm",AIAlgorithm);
					}

				}
			}
		}
		
		/* Setup datastructures for recursive call */

		//Mark attribute as used
		attributesUsed2.put(bestAttribute, true);
		System.out.println(attributesUsed2.keySet());
		System.out.println(attributesUsed2.values());

		//Populate the tree datastructure
		treeObject.setAttributeLabel(bestAttribute);
		
		//Handle the subArrayLists
		Iterator<Entry<String, ArrayList<Instance>>> iter1 =  subArrayLists.entrySet().iterator();
		while(iter1.hasNext())
		{
			Map.Entry<String, ArrayList<Instance>> entry1 = (Entry<String, ArrayList<Instance>>) iter1.next();
			System.out.println(entry1.getKey());
			ArrayList<Instance> childArray = entry1.getValue();
			System.out.println(childArray.size());
			
			if(childArray.size()!=0)
			{
				/*If entropy of the sublist is zero, 
				 *this is a leaf node.
				 * */
				if(calculateEntropy(childArray) == 0)
				{
								System.out.println("Zero Entropy Leaf Node : "+childArray.get(0).getDecision());
								Tree leaf = new Tree();
								leaf.setAttributeLabel(childArray.get(0).getDecision());
								treeObject.getAssociation().put(entry1.getKey(), leaf);
				}
				else
				{
					System.out.println("Recursion for attribute value : " + entry1.getKey());
					Tree nonLeaf = new Tree();
					Map<String,Boolean> copyOfMap = new HashMap<String, Boolean>(attributesUsed2);
					System.out.println(attributesUsed2.keySet());
					System.out.println(attributesUsed2.values());
					
					/*Invoke recursion on the sublist*/
					nonLeaf = decisionTreeAlgorithm(childArray, copyOfMap,nonLeaf);
					treeObject.getAssociation().put(entry1.getKey(), nonLeaf);
				}
			}
		}
		return treeObject;
	}

	private static double calculateEntropy(ArrayList<Instance> instances2) 
	{
		if(instances2.size()==0) 
			return 0.0;

		int countY=0;
		/*Calculate count for decision value "Y"*/
		for(Instance i:instances2)
		{
			if(i.getDecision().equals("Y"))
				countY++;
		}

		/*If count is zero, entropy is 0*/
		if(countY==0)
		{
			return 0;
		}
		
		/*Calculate probability*/
		double probabilityY = (double)countY/(double)instances2.size();
		
		if(probabilityY == 1)
		{
			return 0;
		}

		return ((-1)*probabilityY*Math.log10(probabilityY) + (-1)*(1-probabilityY)*Math.log10(1-probabilityY))/Math.log10(2);
	}
}