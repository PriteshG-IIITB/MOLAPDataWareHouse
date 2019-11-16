package com.MOLAPDataWarehouse.IIITB.MOLAPDataWarehouse;
import java.io.*;
import java.util.*;

public class ReadData 
{
	public void slice(Properties prop,String dimFile,Scanner sc) throws Exception
	{
		FileInputStream fs;ObjectInputStream os;
		fs=new FileInputStream(prop.getProperty("schemaPath")+dimFile+"_Schema");
		os= new ObjectInputStream(fs);
		ArrayList<String> dimschema=(ArrayList<String>)os.readObject();
		os.close();fs.close();
		System.out.println("Do you want additional Meta-Data information?(y/n): ");
		String ip=sc.nextLine();
		if(ip.equals("y"))
		{
			System.out.println("==================================================");
			System.out.println(dimschema);
			fs=new FileInputStream(prop.getProperty("dimensionInfoPath")+dimFile);
			os= new ObjectInputStream(fs);
			ArrayList<String> dimmeta=(ArrayList<String>)os.readObject();
			os.close();fs.close();
			dimmeta.forEach(t -> System.out.println(t));
			System.out.println("==================================================");
		}
		
		System.out.println("Enter parameter for Slice:");
		ip=sc.nextLine();
		fs=new FileInputStream(prop.getProperty("schemaPath")+"dimSchema");
		os= new ObjectInputStream(fs);
		HashMap<Integer,String> schemahsh=(HashMap<Integer,String>)os.readObject();
		os.close();fs.close();
		
		String baseCuboid="cuboid";
		for(int i=0;i<schemahsh.size();i++){baseCuboid+=i;}
		fs=new FileInputStream(prop.getProperty("latticeDirPath")+baseCuboid);
		os= new ObjectInputStream(fs);
		HashMap<String,double[]> baseCube=(HashMap<String,double[]>)os.readObject();
		os.close();fs.close();
		
		long startTime = System.currentTimeMillis();
		System.out.println("==================================================");
		for(int key:schemahsh.keySet())
		{
			System.out.print(schemahsh.get(key)+"\t");
		}
		System.out.println("Fact Value");
		for(String key:baseCube.keySet())
		{
			if(key.contains(ip))
			{System.out.println(key+"\t"+baseCube.get(key)[0]);}
		}
		System.out.println("==================================================");
		System.out.println("Time Required: "+(System.currentTimeMillis()-startTime)/1000d+"secs.");
	}
	public void dice(Properties prop, String[] dimfiles, Scanner sc)throws Exception
	{
		FileInputStream fs;ObjectInputStream os;
		String ip="";
		do
		{
			System.out.println("Do you want additional Meta-Data information?(y/n): ");
			ip=sc.nextLine();
			if(ip.equals("y"))
			{
				System.out.println("Select dimension for information:");
				System.out.println(Arrays.toString(dimfiles));
				String dimFile=sc.nextLine();
				fs=new FileInputStream(prop.getProperty("schemaPath")+dimFile+"_Schema");
				os= new ObjectInputStream(fs);
				ArrayList<String> dimschema=(ArrayList<String>)os.readObject();
				os.close();fs.close();
				System.out.println("==================================================");
				System.out.println(dimschema);
				fs=new FileInputStream(prop.getProperty("dimensionInfoPath")+dimFile);
				os= new ObjectInputStream(fs);
				ArrayList<String> dimmeta=(ArrayList<String>)os.readObject();
				os.close();fs.close();
				dimmeta.forEach(t -> System.out.println(t));
				System.out.println("==================================================");
			}
		}while(ip.equals("y"));
		
		System.out.println("Enter parameters for dice:");
		ip=sc.nextLine();
		String query[]=ip.split(" ");
		ArrayList<String> parameters=new ArrayList<String>();
		for( int i = 1; i < query.length; i += 2){parameters.add(query[i]);}
		
		fs=new FileInputStream(prop.getProperty("schemaPath")+"dimSchema");
		os= new ObjectInputStream(fs);
		HashMap<Integer,String> schemahsh=(HashMap<Integer,String>)os.readObject();
		os.close();fs.close();
		
		String baseCuboid="cuboid";
		for(int i=0;i<schemahsh.size();i++){baseCuboid+=i;}
		fs=new FileInputStream(prop.getProperty("latticeDirPath")+baseCuboid);
		os= new ObjectInputStream(fs);
		HashMap<String,double[]> baseCube=(HashMap<String,double[]>)os.readObject();
		os.close();fs.close();
		
		boolean chk=true;
		long startTime = System.currentTimeMillis();
		System.out.println("==================================================");
		for(int key:schemahsh.keySet())
		{
			System.out.print(schemahsh.get(key)+"\t");
		}
		System.out.println("Fact Value");
		for(String key:baseCube.keySet())
		{
			chk=true;
			for(String param:parameters)
			{
				if(!key.contains(param)) {chk=false;break;}
					
			}
			if(chk){System.out.println(key+"\t"+baseCube.get(key)[0]);}
		}
		System.out.println("==================================================");
		System.out.println("Time Required: "+(System.currentTimeMillis()-startTime)/1000d+"secs.");
	}

}
