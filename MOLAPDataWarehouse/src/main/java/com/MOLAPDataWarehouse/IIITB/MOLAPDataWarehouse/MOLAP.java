package com.MOLAPDataWarehouse.IIITB.MOLAPDataWarehouse;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class MOLAP{
	
	public void getLattice(Properties prop,Scanner sc) throws Exception
	{
	
		String[] ip=dispSchema(prop,sc).split(",");
		String code=ip[0];int factidx=Integer.parseInt(ip[1]);
		System.out.println("Select Agreegation Operation: 1.Sum 2.Average 3.Count");
		String agr=sc.nextLine();
		long startTime = System.currentTimeMillis();
		File cuboidfile = new File(prop.getProperty("latticePath")+code);
		FileInputStream cfs = new FileInputStream(cuboidfile);
	    ObjectInputStream cos = new ObjectInputStream(cfs);
	    LinkedList<Double> addrList= new LinkedList<Double>();
	    HashMap<String, LinkedList<Double>> cbhsh = (HashMap<String, LinkedList<Double>>)cos.readObject();
	    cos.close();cfs.close();
	    FileInputStream bsfs;ObjectInputStream bsos;
	    bsfs=new FileInputStream(prop.getProperty("basePath")+"1");
		bsos= new ObjectInputStream(bsfs);
		HashMap<Double,ArrayList<String>> baser= (HashMap<Double,ArrayList<String>>)bsos.readObject();
		ArrayList<String> factRow;double res;
		if(agr.equals("1"))
		{
			for(String key:cbhsh.keySet())
		    {
		    	addrList= cbhsh.get(key);
		    	res=0;
		    	for(Double d:addrList)
		    	{
		    		factRow=baser.get(d);
		    		res+=Double.parseDouble(factRow.get(factidx));
		    	}
		    	System.out.println(key + " \t ||"+res);
		    }
		}
		else if(agr.equals("2"))
		{
			for(String key:cbhsh.keySet())
		    {
		    	addrList= cbhsh.get(key);
		    	res=0;
		    	for(Double d:addrList)
		    	{
		    		factRow=baser.get(d);
		    		res+=Double.parseDouble(factRow.get(factidx));
		    	}
		    	System.out.println(key + " \t ||"+res/addrList.size());
		    }
		}
		else if(agr.equals("3"))
		{
			for(String key:cbhsh.keySet())
		    {
		    	addrList= cbhsh.get(key);
		    	System.out.println(key + " \t ||"+addrList.size());
		    }
		}
		System.out.println("===================================================");
		System.out.println("Time Required: "+(System.currentTimeMillis()-startTime)/1000d+"secs.");
	    bsos.close();bsfs.close();
	}
	private String dispSchema(Properties prop,Scanner sc) throws Exception
	{
		FileInputStream fs;ObjectInputStream os;
		fs=new FileInputStream(prop.getProperty("schemaPath")+"dimSchema");
		os= new ObjectInputStream(fs);
		HashMap<Integer,String> dimhsh=(HashMap<Integer,String>)os.readObject();
		os.close();fs.close();
		fs=new FileInputStream(prop.getProperty("schemaPath")+"factSchema");
		os= new ObjectInputStream(fs);
		HashMap<Integer,String> facthsh=(HashMap<Integer,String>)os.readObject();
		os.close();fs.close();
		System.out.println("Fact variables are :");
		for(Entry<Integer, String> entry : facthsh.entrySet()){
		    System.out.print(entry.getKey()-1 +"."+ entry.getValue() +"\t");
		}
		System.out.println("\nSelect a fact variable to analyse: ");
		int fact= Integer.parseInt(sc.nextLine())+dimhsh.size();
		System.out.println("Dimensions are :");
		for(Entry<Integer, String> entry : dimhsh.entrySet()){
		    System.out.print(entry.getKey()-1 +"."+ entry.getValue() +"\t");
		}
		System.out.println("\nSelect Dimensions for Rollup/DrillDown: ");
		String code= sc.nextLine();
		code+=","+fact;
		return code;
	}
	public static void main(String[] args)throws Exception {
		Properties prop=new Properties();
		prop.load(new FileInputStream(new File("sales_dwh"+"_config.properties")));
		File cuboidfile = new File(prop.getProperty("latticePath")+"0");
		FileInputStream cfs = new FileInputStream(cuboidfile);
	    ObjectInputStream cos = new ObjectInputStream(cfs);
	    LinkedList<Double> addrList= new LinkedList<Double>();
	    HashMap<String, LinkedList<Double>> cbhsh = (HashMap<String, LinkedList<Double>>)cos.readObject();
	    cos.close();cfs.close();
	}
}
