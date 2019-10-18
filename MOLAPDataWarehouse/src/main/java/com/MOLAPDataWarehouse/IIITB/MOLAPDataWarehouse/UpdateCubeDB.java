package com.MOLAPDataWarehouse.IIITB.MOLAPDataWarehouse;

import java.io.*;
import java.util.*;

import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Workbook;
import com.monitorjbl.xlsx.StreamingReader;
import com.monitorjbl.xlsx.impl.StreamingCell;

public class UpdateCubeDB
{
	public void UpdateBase(Properties prop,String updFile)throws Exception
	{
		System.out.println("Updating Fact Table...");
		FileInputStream fs= new FileInputStream(new File(updFile));
		Workbook wb = StreamingReader.builder()
		        .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
		        .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
		        .open(fs);            // InputStream or File for XLSX file (required)		
		Sheet sheet=wb.getSheetAt(0);
		System.out.println("Buidling Hashmap...");
		try 
		{	
			FileInputStream fes=new FileInputStream(prop.getProperty("basePath")+"1");
			ObjectInputStream os= new ObjectInputStream(fes);
			HashMap<Double,ArrayList<String>> baser= (HashMap<Double,ArrayList<String>>)os.readObject();
			os.close();fes.close();
			FileOutputStream fos;ObjectOutputStream wo;int filenos=1; 
			fos= new FileOutputStream(new File(prop.getProperty("basePath")+filenos));filenos++;
			wo= new ObjectOutputStream(fos);
			double lstidx=baser.size();
			ArrayList<String> aRow;
			double addr,keys=lstidx,hshmpLimit=Double.parseDouble(prop.getProperty("hashMapLimit"));
			for(Row row :sheet)
			{
				addr=row.getRowNum()+lstidx;
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
			updateDimension(prop,lstidx,updFile);
		}catch (Exception e) 
		{e.printStackTrace();}	
	}
	
	private void updateDimension(Properties prop,double lstidx,String updFile) throws Exception
	{
		FileInputStream fs;ObjectInputStream os;
    	fs=new FileInputStream(prop.getProperty("schemaPath")+"dimSchema");
		os= new ObjectInputStream(fs);
		HashMap<Integer,String> dimhsh=(HashMap<Integer,String>)os.readObject();
		os.close();fs.close();
		int dimCount=dimhsh.size();dimhsh.clear();
		Workbook wb;Sheet sheet;
		System.out.println("Indexing Dimensions...");
		for(int i=0;i<dimCount;i++)
		{
			fs=new FileInputStream(new File(prop.getProperty("dimensionsPath")+i));
			os= new ObjectInputStream(fs);
			HashMap<String,LinkedList<Double>> dimr= (HashMap<String,LinkedList<Double>>)os.readObject();
			os.close();fs.close();
			FileOutputStream fos= new FileOutputStream(new File(prop.getProperty("dimensionsPath")+i));
			ObjectOutputStream wo= new ObjectOutputStream(fos);
			try
			{
				LinkedList<Double>listAddr=new LinkedList<Double>();
				double addr;String key;
				fs= new FileInputStream(new File(updFile));
				wb = StreamingReader.builder().sstCacheSize(100).open(fs);
				sheet=wb.getSheetAt(0);
				for(Row row :sheet)
				{
					addr=row.getRowNum()+lstidx;
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
