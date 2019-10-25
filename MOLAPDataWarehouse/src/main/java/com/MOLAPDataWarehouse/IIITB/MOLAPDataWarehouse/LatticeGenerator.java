package com.MOLAPDataWarehouse.IIITB.MOLAPDataWarehouse;
import java.util.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.Method;
public class LatticeGenerator
{
	private static ArrayList<GTreeNode> leafList;
	private static HashSet<String> rejectList;
	private static String aggfunc,oprnd;
	private static double limit;
	private static FileOutputStream fos;
	private static ObjectOutputStream wo;
	private static Properties prop;
	private static void makeCombination(int n, int r, int index, int dimidx[], int i) throws Exception 
	{ 
		if (index == r)
		{ 
			HashMap<String,double[]>cube=new HashMap<String,double[]>(); 
			String cuboidName="";boolean namechk=false;
			for(GTreeNode leaf : leafList)
			{
				String key="";
				for (int j = 0; j < r; j++)
				{
					if(!namechk){cuboidName+=dimidx[j];}
					key+=leaf.ancestorList.get(dimidx[j]);
					if(rejectList.contains(key)){key="";break;}
				} 
				namechk=true;
				if(key.equals("")){continue;}
				if(cube.containsKey(key))
				{
					cube.get(key)[0]+=leaf.factSum;
					cube.get(key)[1]+=leaf.factCnt;
					cube.get(key)[2]=cube.get(key)[2]>leaf.factSum?leaf.factSum:cube.get(key)[2];
					cube.get(key)[3]=cube.get(key)[3]<leaf.factSum?leaf.factSum:cube.get(key)[3];
					cube.get(key)[4]=cube.get(key)[4]<leaf.factCnt?leaf.factCnt:cube.get(key)[4];
					cube.get(key)[5]=cube.get(key)[5]<leaf.factCnt?leaf.factCnt:cube.get(key)[5];
				}
				else
				{
					double[]aggregates=new double[] {leaf.factSum,leaf.factCnt,leaf.factSum,leaf.factSum,leaf.factCnt,leaf.factCnt};
					cube.put(key,aggregates);
				}
			}
			icebergcube(cube);
			fos= new FileOutputStream(new File(prop.getProperty("latticePath")+cuboidName));
			wo= new ObjectOutputStream(fos);
			wo.writeObject(cube);wo.close();fos.close();
			return; 
		}  
		if (i >= n) return; 
		dimidx[index] = i; 
		makeCombination(n, r, index + 1,  dimidx, i + 1); 
		makeCombination(n, r, index, dimidx, i + 1); 
	} 
	private static void icebergcube(HashMap<String, double[]> cube) throws Exception
	{
		Class<?>[] paramTypes = {String.class,double.class,double[].class};
		Method method=ConstraintChecker.class.getMethod(aggfunc,paramTypes);
		int isValid;
		ArrayList<String>removeKeyList= new ArrayList<String>();
		for(String key:cube.keySet())
		{
			isValid=(int)method.invoke(null, oprnd,limit,cube.get(key));
			if(isValid==0){rejectList.add(key);}
			if(isValid!=1){removeKeyList.add(key);}
		}
		for(String key:removeKeyList){cube.remove(key);}
	} 
	private static void orderedPowerSetRec(int n) throws Exception 
    { 
		for(int r=1;r<=n;r++)
        {	int data[] = new int[r];
        	makeCombination(n, r, 0, data, 0);
        }
    } 
	
	public static void latticeGenerator(Properties prop, ArrayList<GTreeNode> leafList, int dimCount,String constraints) throws Exception
	{
		System.out.println("Generating Lattice Of Cuboids...");
		LatticeGenerator.leafList=leafList;
		LatticeGenerator.prop=prop;
		rejectList= new HashSet<String>();
		if(constraints.contains("sum"))aggfunc="sumBoundChk";
		else if(constraints.contains("min"))aggfunc="minBoundChk";
		else if(constraints.contains("max"))aggfunc="maxBoundChk";
		else if(constraints.contains("cnt"))aggfunc="cntBoundChk";
		else if(constraints.contains("avg"))aggfunc="avgBoundChk";
		if(constraints.charAt(4)!='=')
		{
			limit=Double.parseDouble(constraints.substring(4));
			oprnd=constraints.substring(3,4);
		}
		else
		{
			limit=Double.parseDouble(constraints.substring(5));
			oprnd=constraints.substring(3,5);
		}
		orderedPowerSetRec(dimCount);
	}
}
