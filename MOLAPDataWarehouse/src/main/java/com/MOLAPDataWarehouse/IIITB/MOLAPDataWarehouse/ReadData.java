package com.MOLAPDataWarehouse.IIITB.MOLAPDataWarehouse;
import java.io.*;
import java.util.*;
import java.util.Map.Entry;

public class ReadData 
{
	public void slice(Properties prop,String dimFile,Scanner sc) throws Exception
	{
		FileInputStream fs;ObjectInputStream os;
		fs=new FileInputStream(prop.getProperty("schemaPath")+dimFile+"_Schema");
		os= new ObjectInputStream(fs);
		ArrayList<String> dimMeta=(ArrayList<String>)os.readObject();
		os.close();fs.close();
		fs=new FileInputStream(prop.getProperty("schemaPath")+"dimSchema");
		os= new ObjectInputStream(fs);
		HashMap<Integer,String> dimhsh=(HashMap<Integer,String>)os.readObject();
		os.close();fs.close();int dimidx=0;
		for(int key :dimhsh.keySet())
		{
			if(dimhsh.get(key).equals(dimFile+"Key"))
			{dimidx=key-1;break;}
		}
		fs=new FileInputStream(prop.getProperty("schemaPath")+"factSchema");
		os= new ObjectInputStream(fs);
		os.close();fs.close();
		System.out.println("Info about "+dimFile);
		System.out.println(dimMeta.get(0));
		System.out.println("do you need more info yes/no");
		ArrayList<String> dimMetaData=getDimMeta(prop,dimFile);
		if(sc.nextLine().equals("yes"))
		{for(String str :dimMetaData){System.out.println(str);}}
		String col,val;
		System.out.println("Enter Slice Operation:");
		System.out.println("Column:");
		col=sc.nextLine();
		System.out.println("Filter Value:");
		val=sc.nextLine();
		int colnum=0;
		String[]sch=dimMeta.get(0).split(" ");
		for(String str:sch)
		{
			if(!str.equals(col)) {++colnum;continue;}
			else break;
		}
		ArrayList<String> idxlist= new ArrayList<String>();
		for(String str :dimMetaData)
		{
			sch=str.split(" ");
			if(sch[colnum].equals(val)){idxlist.add(sch[0]);}
		}
		long startTime = System.currentTimeMillis();
		getSlice(idxlist,prop,dimidx);
		System.out.println("Time Required: "+(System.currentTimeMillis()-startTime)/1000d+"secs.");
		
	}
	public void dice(Properties prop, String[] dimfiles, Scanner sc)throws Exception
	{
		FileInputStream fs;ObjectInputStream os;
		fs=new FileInputStream(prop.getProperty("schemaPath")+"dimSchema");
		os= new ObjectInputStream(fs);
		HashMap<Integer,String> dimhsh=(HashMap<Integer,String>)os.readObject();
		os.close();fs.close();
		HashMap<String,Integer> dimidx=new HashMap<String,Integer>();
		for(String dimfile :dimfiles)
		{
			for(int key :dimhsh.keySet())
			{
				if(dimhsh.get(key).equals(dimfile+"Key"))
				{dimidx.put(dimfile,key-1);break;}
			}
		}
		System.out.println("do you need more info yes/no");
		ArrayList<String> dimMetaData;
		if(sc.nextLine().equals("yes"))
		{for(String dimfile :dimfiles)
			{
				System.out.println("Dimension: "+dimfile);
				dimMetaData=getDimMeta(prop,dimfile);
				{for(String str :dimMetaData){System.out.println(str);}}
				System.out.println("======================================");
			}
			System.out.println("======================================");
		}
		System.out.println("Enter Dice Operation [dimension column,value]:");
		String[]query= sc.nextLine().split(",");
		int i=0,k;String dimcol="",dimval;
		for(String dimfile: dimfiles)
		{
			dimcol+= query[++i]+" ";dimval=query[i++];
			dimMetaData=getDimMeta(prop,dimfile);
			for(String str :dimMetaData)
			{
				if(str.contains(dimval))
				{
					k=dimidx.get(dimfile);
					dimidx.remove(dimfile);
					dimidx.put(str.split(" ")[0],k);
					break;
				}
			}
		}
		fs=new FileInputStream(prop.getProperty("schemaPath")+"factSchema");
		os= new ObjectInputStream(fs);
		os.close();fs.close();
		HashMap<Integer, String> idx = new HashMap<Integer,String>();
		for(Map.Entry<String , Integer> entry : dimidx.entrySet()){
		    idx.put(entry.getValue(), entry.getKey());
		}
		long startTime = System.currentTimeMillis();
		getDice(prop,idx,dimcol);
		System.out.println("Time Required: "+(System.currentTimeMillis()-startTime)/1000d+"secs.");
		
	}
	private void getSlice(ArrayList<String> idxlist, Properties prop,int dimidx) throws Exception
	{
		List<Double> addrList= new LinkedList<Double>();
		for(String str:idxlist)
		{
			addrList.addAll(getAddr(str,dimidx,prop));
		}
		HashMap<Double,ArrayList<String>> baser=(HashMap<Double,ArrayList<String>>)getFact(addrList,prop);
		printSchema(prop);
		for(double d:addrList)
		{
			System.out.println(baser.get(d).toString());
		}
		System.out.println("=========================================================");
	}
	private ArrayList<String> getDimMeta(Properties prop,String dimFile) throws Exception
	{
		FileInputStream fs;ObjectInputStream os;
		fs=new FileInputStream(prop.getProperty("dimensionInfoPath")+dimFile);
		os= new ObjectInputStream(fs);
		ArrayList<String> dimMetaData=(ArrayList<String>)os.readObject();
		os.close();fs.close();
		return dimMetaData;
	}
	private void getDice(Properties prop,HashMap<Integer,String> dimHsh,String dimcol) throws Exception
	{
		//dimHsh.put(0, "R1");dimHsh.put(1, "C1");dimHsh.put(4, "P1");
		List<Double> addrList= new LinkedList<Double>();
		for(int key:dimHsh.keySet())
		{
			if(addrList.isEmpty()){addrList=getAddr(dimHsh.get(key),key,prop);}
			else{addrList.retainAll(getAddr(dimHsh.get(key),key,prop));}
		}  
	    HashMap<Double,ArrayList<String>> baser=(HashMap<Double,ArrayList<String>>)getFact(addrList,prop);
		printSchema(prop);
	    for(double d:addrList)
		{
			System.out.println(baser.get(d).toString());
		}
	}
	private HashMap<Double,ArrayList<String>> getFact(List<Double> addrList, Properties prop)throws Exception 
	{
		FileInputStream fs;ObjectInputStream os;
		fs=new FileInputStream(prop.getProperty("basePath")+"1");
		os= new ObjectInputStream(fs);
		HashMap<Double,ArrayList<String>> baser= (HashMap<Double,ArrayList<String>>)os.readObject();
		os.close();fs.close();
		return baser;
	}
	private List<Double> getAddr(String key, int i, Properties prop)throws Exception
	{
		HashMap<String, LinkedList<Double>> dim=readDim(prop.getProperty("dimensionsPath")+i);
		List<Double> addrLst=dim.get(key);
		return addrLst;
	}
	private HashMap<String, LinkedList<Double>> readDim(String fname) throws Exception
	{	
		File file = new File(fname);
	    FileInputStream f = new FileInputStream(file);
	    ObjectInputStream s = new ObjectInputStream(f);
	    HashMap<String, LinkedList<Double>> dim = (HashMap<String, LinkedList<Double>>)s.readObject();
	    s.close();f.close();
	    return dim;
	}
	private void printSchema(Properties prop) throws Exception
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
		for(Entry<Integer, String> entry : dimhsh.entrySet()){
		    System.out.print( entry.getValue() +"||");
		}
		for(Entry<Integer, String> entry : facthsh.entrySet()){
		    System.out.print(entry.getValue() +"||");
		}
		System.out.println();
	}
}
