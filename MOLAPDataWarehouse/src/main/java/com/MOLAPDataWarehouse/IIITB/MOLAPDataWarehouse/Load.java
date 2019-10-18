package com.MOLAPDataWarehouse.IIITB.MOLAPDataWarehouse;

import java.io.*;
import java.util.*;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;

//import org.apache.poi.xssf.usermodel.*;
//import org.apache.poi.ss.usermodel.*;
import com.monitorjbl.xlsx.StreamingReader;
import com.monitorjbl.xlsx.impl.StreamingCell;
public class Load
{
	public void createBase(Properties prop)throws Exception
	{
		FileInputStream fs= new FileInputStream(new File(prop.getProperty("datafile")));
		//XSSFWorkbook wb= new XSSFWorkbook(fs);
		//XSSFSheet sheet = wb.getSheetAt(0);
		Workbook wb = StreamingReader.builder()
		        .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
		        .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
		        .open(fs);            // InputStream or File for XLSX file (required)		
		Sheet sheet=wb.getSheetAt(0);
		System.out.println("Buidling Hashmap...");
		FileOutputStream fos;ObjectOutputStream wo;int filenos=1; 
		fos= new FileOutputStream(new File(prop.getProperty("basePath")+filenos));filenos++;
		wo= new ObjectOutputStream(fos);
		try 
		{	
			HashMap<Double,ArrayList<String>> baser= new HashMap<Double,ArrayList<String>>();
			ArrayList<String> aRow;
			double addr,keys=0,hshmpLimit=Double.parseDouble(prop.getProperty("hashMapLimit"));
			for(Row row :sheet)
			{
				addr=row.getRowNum();
				if(addr==0){continue;}
				aRow= new ArrayList<String>();
				for (Cell cell :row)
				{aRow.add(((StreamingCell)cell).getStringCellValue());}
				if(keys<=hshmpLimit)
				{baser.put(addr, aRow);keys++;}
				else 
				{
					wo.writeObject(baser);wo.close();fos.close();
					fos= new FileOutputStream(new File(prop.getProperty("basePath")+"base"+filenos));filenos++;
					wo= new ObjectOutputStream(fos);
					baser= new HashMap<Double,ArrayList<String>>();
					baser.put(addr, aRow);keys=1;
				}
			}
			wo.writeObject(baser);wo.close();fos.close();wb.close();fs.close();			
			createDimension(prop);
		}catch (Exception e) 
		{e.printStackTrace();}	
	}
	
	private void createDimension(Properties prop) throws Exception
	{
		FileInputStream fs;ObjectInputStream os;
    	fs=new FileInputStream(prop.getProperty("schemaPath")+"dimSchema");
		os= new ObjectInputStream(fs);
		HashMap<Integer,String> dimhsh=(HashMap<Integer,String>)os.readObject();
		os.close();fs.close();
		int dimCount=dimhsh.size();dimhsh.clear();
		//fs= new FileInputStream(new File(prop.getProperty("datafile")));
		//XSSFWorkbook wb= new XSSFWorkbook(fs);
		//XSSFSheet sheet = wb.getSheetAt(0);
		Workbook wb;// = StreamingReader.builder()
				 //.sstCacheSize(100)    // buffer size to use when reading InputStream to file (defaults to 1024)
		        //.open(fs);            // InputStream or File for XLSX file (required)		
		Sheet sheet;//=wb.getSheetAt(0);
		System.out.println("Indexing Dimensions...");
//		for(Row hrow :sheet)
//		{dimCount=hrow.getLastCellNum();break;}wb.close();
		for(int i=0;i<dimCount;i++)
		{
			FileOutputStream fos= new FileOutputStream(new File(prop.getProperty("dimensionsPath")+i));
			ObjectOutputStream wo= new ObjectOutputStream(fos);
			try
			{
				HashMap<String,LinkedList<Double>> dimr= new HashMap<String,LinkedList<Double>>();
				LinkedList<Double>listAddr=new LinkedList<Double>();
				double addr;String key;
				fs= new FileInputStream(new File(prop.getProperty("datafile")));
				wb = StreamingReader.builder().sstCacheSize(100).open(fs);
				sheet=wb.getSheetAt(0);
				for(Row row :sheet)
				{
					addr=row.getRowNum();
					if(addr==0) {continue;}
					key=((StreamingCell)(row.getCell(i))).getStringCellValue();
					if(dimr.containsKey(key))
					{listAddr=dimr.get(key);listAddr.add(addr);dimr.put(key, listAddr);}
					else {listAddr=new LinkedList<Double>();listAddr.add(addr);dimr.put(key,listAddr);}
				}
				wo.writeObject(dimr);wo.close();fos.close();wb.close();fs.close();			
			}catch (Exception e)
			{e.printStackTrace();}
		}
	}
		
}
