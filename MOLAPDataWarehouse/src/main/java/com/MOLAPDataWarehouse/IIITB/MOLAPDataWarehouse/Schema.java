package com.MOLAPDataWarehouse.IIITB.MOLAPDataWarehouse;
import java.io.*;
import java.util.*;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

import com.monitorjbl.xlsx.StreamingReader;
import com.monitorjbl.xlsx.impl.StreamingCell;

public class Schema{
	
	public void generateSchema(Properties prop) throws Exception
	{
		FileInputStream fs= new FileInputStream(new File(prop.getProperty("datafile")));
		//XSSFWorkbook wb= new XSSFWorkbook(fs);
		//XSSFSheet sheet = wb.getSheetAt(0);
		Workbook wb = StreamingReader.builder()
		        .rowCacheSize(100)    // number of rows to keep in memory (defaults to 10)
		        .bufferSize(4096)     // buffer size to use when reading InputStream to file (defaults to 1024)
		        .open(fs);            // InputStream or File for XLSX file (required)		
		Sheet sheet=wb.getSheet("Schema");
		try
		{
			System.out.println("Generating Schema file....");
			int colindx=-1;
			HashMap<Integer,String> dimSchmHsh=new HashMap<Integer,String>();
			HashMap<Integer,String> factSchmHsh=new HashMap<Integer,String>();
			for(Row row : sheet)
			{
				if(row.getRowNum()==1)
				{   
					for(Cell cell: row)
					{
						colindx=cell.getColumnIndex();
						if(colindx==0){continue;}
						dimSchmHsh.put(colindx, ((StreamingCell)cell).getStringCellValue());
					}
				}
				else if(row.getRowNum()==2)
				{
					for(Cell cell: row)
					{
						colindx=cell.getColumnIndex();
						if(colindx==0){continue;}
						factSchmHsh.put(colindx, ((StreamingCell)cell).getStringCellValue());
					}
				}
				else continue;
			}
			wb.close();fs.close();
			FileOutputStream fos= new FileOutputStream(new File(prop.getProperty("schemaPath")+"dimSchema"));
			ObjectOutputStream wo= new ObjectOutputStream(fos);
			wo.writeObject(dimSchmHsh);wo.close();fos.close();
			fos= new FileOutputStream(new File(prop.getProperty("schemaPath")+"factSchema"));
			wo= new ObjectOutputStream(fos);
			wo.writeObject(factSchmHsh);wo.close();fos.close();
			generatedimMetaSchema(prop);
		}catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	private static void generatedimMetaSchema(Properties prop) throws Exception 
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
			fos= new FileOutputStream(new File(prop.getProperty("schemaPath")+ wb.getSheetName(i)+"_Schema"));
			wo= new ObjectOutputStream(fos);
			String rowstr="";
			for(Row row: sheet)
			{
				rowstr="";
				for(Cell cell : row ) {rowstr+=((StreamingCell)cell).getStringCellValue()+" ";}
				aRow.add(rowstr);
				break;
			}
			wo.writeObject(aRow);
			wo.close();
			fos.close();	
		}
		wb.close();fs.close();
	}

}
