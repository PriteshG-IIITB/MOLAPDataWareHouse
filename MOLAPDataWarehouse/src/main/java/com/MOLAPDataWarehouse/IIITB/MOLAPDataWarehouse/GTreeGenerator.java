package com.MOLAPDataWarehouse.IIITB.MOLAPDataWarehouse;
import com.MOLAPDataWarehouse.IIITB.MOLAPDataWarehouse.GTreeNode;
import java.io.*;
import java.util.*;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import com.monitorjbl.xlsx.StreamingReader;
import com.monitorjbl.xlsx.impl.StreamingCell;
public class GTreeGenerator
{
	static GTreeNode root;
	static HashMap<Integer,String> dimhsh;
	static ArrayList<GTreeNode>leafList;
	static ArrayList<String>ancestorList;
	//function to read data and create G-tree
	public static void createGTree(Properties prop,String constraints)throws Exception
	{
		FileInputStream fs;ObjectInputStream os;
    	fs=new FileInputStream(prop.getProperty("schemaPath")+"dimSchema");
		os= new ObjectInputStream(fs);
		dimhsh=(HashMap<Integer,String>)os.readObject();
		os.close();fs.close();
		int dimCount=dimhsh.size();dimhsh.clear();
		fs= new FileInputStream(new File(prop.getProperty("datafile")));
		Workbook wb = StreamingReader.builder()
		        .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
		        .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
		        .open(fs);            // InputStream or File for XLSX file (required)		
		Sheet sheet=wb.getSheetAt(0);
		System.out.println("Constructing G-Tree....");
		root= new GTreeNode("root", "root", 0, false);
		leafList= new ArrayList<GTreeNode>();
		for(Row row:sheet)
		{
			if(row.getRowNum()==0)continue;
			int factidx=row.getLastCellNum();
			double factVal=Double.parseDouble(((StreamingCell)row.getCell(factidx-1)).getStringCellValue());
			ancestorList= new ArrayList<String>();
			root.addfact(factVal);
			root.addfactCnt();
			makePath(root,row,0,dimCount,factVal);
		}
		FileOutputStream fos= new FileOutputStream(new File(prop.getProperty("basePath")+"GTree"));
		ObjectOutputStream wo= new ObjectOutputStream(fos);
		wo.writeObject(root);wo.close();fos.close();
		fos= new FileOutputStream(new File(prop.getProperty("basePath")+"LeafList"));
		wo= new ObjectOutputStream(fos);
		wo.writeObject(leafList);wo.close();fos.close();
		wb.close();fs.close();
		//Generate lattice of cuboids
		LatticeGenerator.latticeGenerator(prop,leafList,dimCount,constraints);
	}
	//recursion to build G-tree
	public static void makePath(GTreeNode root,Row row,int i,int dimCount,double factVal)
	{
		if (i==dimCount)
		{
			root.ancestorList=ancestorList;
			leafList.add(root);
			return;
		}
		if(i>dimCount)return;
		StreamingCell cell=(StreamingCell) row.getCell(i);
		String dataVal=cell.getStringCellValue();
		ancestorList.add(dataVal);
		int cellnum=cell.getColumnIndex();
		boolean isLeaf=cellnum==dimCount-1?true:false;
		GTreeNode node;
		if(!root.childMap.containsKey(dataVal))
		{
			node=new GTreeNode(dimhsh.get(cellnum), dataVal,factVal, isLeaf);
			root.childMap.put(dataVal, node);
		}
		else
		{
			node=root.childMap.get(dataVal);
			node.addfact(factVal);
			node.addfactCnt();
		}
		makePath(node,row,i+1,dimCount,factVal);
	}
	public static void loadGTree(Properties prop)throws Exception
	{
		FileInputStream fis=new FileInputStream(new File(prop.getProperty("basePath")+"GTree"));
		ObjectInputStream ro=new ObjectInputStream(fis);
		GTreeNode tree=(GTreeNode)(ro.readObject());
		fis=new FileInputStream(new File(prop.getProperty("basePath")+"LeafList"));
		ro=new ObjectInputStream(fis);
		ArrayList<GTreeNode>ll=(ArrayList<GTreeNode>)(ro.readObject());
		System.out.println(tree.childMap.size());
		System.out.println(ll.get(0).ancestorList.get(0));
	}
}
