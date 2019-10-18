package com.MOLAPDataWarehouse.IIITB.MOLAPDataWarehouse;
import java.io.*;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
//import org.apache.poi.xssf.usermodel.XSSFSheet;
//import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import com.monitorjbl.xlsx.StreamingReader;
import com.monitorjbl.xlsx.impl.StreamingCell;


public class MetaDimGenerator
{
	public void genratedimMeta(Properties prop) throws Exception 
	{
		FileInputStream fs= new FileInputStream(new File(prop.getProperty("datafile")));
		//XSSFWorkbook wb= new XSSFWorkbook(fs);
		Workbook wb = StreamingReader.builder()
				 .sstCacheSize(100)   // buffer size to use when reading InputStream to file (defaults to 1024)
		        .open(fs);            // InputStream or File for XLSX file (required)		
		for (int i = 1; i < wb.getNumberOfSheets(); i++)
		{
			Sheet sheet = wb.getSheetAt(i);
			if(sheet.getSheetName().equals("Schema")) {continue;}
			FileOutputStream fos;
			ObjectOutputStream wo;
			ArrayList<String> aRow= new ArrayList<String>();
			double addr;
			fos= new FileOutputStream(new File(prop.getProperty("dimensionInfoPath")+ wb.getSheetName(i)));
			wo= new ObjectOutputStream(fos);
			String rowstr="";
			for(Row row: sheet)
			{
				addr=row.getRowNum();
				if(addr==0)
				{continue;}
				rowstr="";
				for(Cell cell : row ) {rowstr+=((StreamingCell)cell).getStringCellValue()+" ";}
				aRow.add(rowstr);
			}
			wo.writeObject(aRow);
			wo.close();
			fos.close();	
		}
		wb.close();fs.close();
	 }
}

  
