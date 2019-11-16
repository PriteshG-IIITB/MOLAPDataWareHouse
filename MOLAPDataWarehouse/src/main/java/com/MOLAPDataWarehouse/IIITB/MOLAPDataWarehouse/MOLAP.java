package com.MOLAPDataWarehouse.IIITB.MOLAPDataWarehouse;

import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class MOLAP{
	
	public void getLattice(Properties prop,Scanner sc) throws Exception
	{
		FileInputStream fs;ObjectInputStream os;
		fs=new FileInputStream(prop.getProperty("schemaPath")+"dimSchema");
		os= new ObjectInputStream(fs);
		HashMap<Integer,String> schemahsh=(HashMap<Integer,String>)os.readObject();
		os.close();fs.close();
		
		String baseCuboid="",ip="";
		System.out.println("Do you want to see base Cuboid? (y/n): ");
		ip=sc.nextLine();

		if(ip.equals("y"))
		{
			System.out.println("Displaying Base Cuboid...");
			for(int i=0;i<schemahsh.size();i++){baseCuboid+=i;}
			printCuboid(prop, baseCuboid, schemahsh);
		}
		
		do 
		{
			System.out.println("Select dimensions to get required cuboid:");
			for(int key:schemahsh.keySet())
			{
				System.out.print((key-1)+"."+schemahsh.get(key)+" ");
			}
			System.out.println();
			ip=sc.nextLine().replaceAll("\\s","");
			printCuboid(prop, ip, schemahsh);
			System.out.println("Do you wish to get other cuboid? (y/n): ");
			ip=sc.nextLine();
		}while(ip.equals("y"));
	}
	private void printCuboid(Properties prop,String cuboidno,HashMap<Integer,String> schemahsh)throws Exception
	{
		FileInputStream fs;ObjectInputStream os;
		fs=new FileInputStream(prop.getProperty("latticeDirPath")+"cuboid"+cuboidno);
		os= new ObjectInputStream(fs);
		HashMap<String,double[]> baseCube=(HashMap<String,double[]>)os.readObject();
		os.close();fs.close();
		
		long startTime = System.currentTimeMillis();
		System.out.println("==================================================");
		for(int key:schemahsh.keySet())
		{
			if(cuboidno.contains(""+(key-1))) {System.out.print(schemahsh.get(key)+"\t");}
		}
		System.out.println("Fact Value  \t Avg.");
		for(String key:baseCube.keySet())
		{
			double sum=baseCube.get(key)[0],cnt=baseCube.get(key)[1];
			System.out.println(key+"\t"+sum+"\t"+(sum/cnt));
		}
		System.out.println("==================================================");
		System.out.println("Time Required: "+(System.currentTimeMillis()-startTime)/1000d+"secs.");
	}
	
}
