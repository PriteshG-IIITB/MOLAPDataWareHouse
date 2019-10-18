package com.MOLAPDataWarehouse.IIITB.MOLAPDataWarehouse;
import java.io.*;
import java.util.*;
public class LatticeGenerator
{
	public void createLatticeOfCuboids(int set_size,Properties prop)throws Exception
	{
		/*set_size of power set of a set with set_size n is (2^n )*/
        long pow_set_size =  (long)Math.pow(2, set_size); 
        FileOutputStream fos;ObjectOutputStream wo;String cuboidName;
        LinkedList<Double> addrList= new LinkedList<Double>();
        LinkedList<Double> taddrList= new LinkedList<Double>();
        HashMap<String, LinkedList<Double>> hshMp = new HashMap<String, LinkedList<Double>>();
        HashMap<String, LinkedList<Double>> thshMp = new HashMap<String, LinkedList<Double>>();
        HashMap<String, LinkedList<Double>> reshshMp = new HashMap<String, LinkedList<Double>>();
        /*Run from counter 000..1 to 111..1*/
        for(int counter = 1; counter <  pow_set_size; counter++) 
        { 
        																																																																																																																																																																															cuboidName="";
            hshMp = new HashMap<String, LinkedList<Double>>();
            thshMp = new HashMap<String, LinkedList<Double>>();
            reshshMp = new HashMap<String, LinkedList<Double>>();
        	for(int j = 0; j < set_size; j++) 
            { 
            	/* Check if jth bit in the  counter is set If set then  
                Use dimension file of jth column to create cuboid nodes in lattice */
        		if((counter & (1 << j)) > 0) 
                {
                	cuboidName+=j;
                	if(hshMp.isEmpty()){hshMp=getHshDim(j,prop);}
                	else
                	{
                		thshMp=getHshDim(j, prop);
                		reshshMp=new HashMap<String, LinkedList<Double>>();
                		for(String k:hshMp.keySet())
                		{
                			addrList=hshMp.get(k);
                			for(String tk:thshMp.keySet())
                			{
                				taddrList=(LinkedList<Double>) thshMp.get(tk).clone();
                				taddrList.retainAll(addrList);
                				if(!taddrList.isEmpty())
                				{reshshMp.put(k+"\t ||"+tk,(LinkedList<Double>) taddrList.clone());}
                			}
                		}hshMp.clear();hshMp.putAll(reshshMp);
                	}
                }    
            }
            fos= new FileOutputStream(new File(prop.getProperty("latticePath")+cuboidName));
			wo= new ObjectOutputStream(fos);wo.writeObject(hshMp);
			wo.close();fos.close();
        } 
	}

	private HashMap<String, LinkedList<Double>> getHshDim(int j, Properties prop) throws Exception
	{
		File file = new File(prop.getProperty("dimensionsPath")+j);
	    FileInputStream f = new FileInputStream(file);
	    ObjectInputStream s = new ObjectInputStream(f);
	    HashMap<String, LinkedList<Double>> dim = (HashMap<String, LinkedList<Double>>)s.readObject();
	    s.close();f.close();
	    return dim;
	}

}
