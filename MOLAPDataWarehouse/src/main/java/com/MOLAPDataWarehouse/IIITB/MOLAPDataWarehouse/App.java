package com.MOLAPDataWarehouse.IIITB.MOLAPDataWarehouse;
import java.io.*;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;
public class App 
{
	static Properties prop=new Properties();
    public static void main( String[] args ) throws Exception
    {	 
    	int ip=-1;String filename,check="";
    	System.out.println("=========Native Cube DBMS======");
    	Scanner sc= new Scanner(System.in);
    	while(true)
    	{
	    	System.out.println("1.Create Datawarehouse");
	    	System.out.println("2.Use Existing Datewarehouse");
	    	System.out.println("3.Load New Data In Existing Datewarehouse");
	    	System.out.println("4.Exit");
	    	ip=Integer.parseInt(sc.nextLine());
	    	if(ip>=4 || ip<1)break;
	    	switch (ip)
	    	{
				case 1: 
						System.out.println("Select Datawarehouse file(.xlsx): ");
						filename=sc.nextLine();
						createDataWareHouse(filename);
						break;
				case 2:
						System.out.println("Enter Datawarehouse Name: ");
						filename=sc.nextLine();prop=new Properties();
						try
						{
							prop.load(new FileInputStream(new File(filename+"_config.properties")));
							readData(prop,filename,sc);
						}catch (Exception e){System.out.println("database doesnot exist..");}
						break;
				case 3: 
						System.out.println("Enter Datawarehouse Name: ");
						String dbname=sc.nextLine();prop=new Properties();
						try
						{prop.load(new FileInputStream(new File(dbname+"_config.properties")));}
						catch (Exception e){System.out.println("database doesnot exist..");e.printStackTrace();}
						System.out.println("Select Datawarehouse file to load(.xlsx): ");
						filename=sc.nextLine();
						UpdateDataWareHouse(prop,filename);
						break;
				default:break;
			}
	    	System.out.println("Do you wish to continue? \n 1->Yes 2->No");
	    	check=sc.nextLine();
	    	if(check.equals("2"))break;
    	}
    	sc.close();
    }

	public static void createDataWareHouse(String filename) throws Exception
	{
		long startTime = System.currentTimeMillis();
		filename=filename.substring(0,filename.length()-5);
		createConfigFile(filename);
		System.out.println("Config Property Successfully Created..");
		prop=new Properties();
		prop.load(new FileInputStream(new File(filename+"_config.properties")));
		loadData(prop);
		genrateLatticeOfCuboids(prop);
		genrateDimensionMetaData(prop);
		System.out.println("Done...");
		System.out.println("Time Required: "+(System.currentTimeMillis()-startTime)/1000d+"secs.");
	}
	private static void UpdateDataWareHouse(Properties prop,String updFile) throws Exception
	{	
		long startTime = System.currentTimeMillis();
		UpdateCubeDB updDb=new UpdateCubeDB();
		updDb.UpdateBase(prop, updFile);
		genrateLatticeOfCuboids(prop);
		System.out.println("Data Loaded Successfully...");
		System.out.println("Time Required: "+(System.currentTimeMillis()-startTime)/1000d+"secs.");	
	}
    private  static void createConfigFile(String filename) throws Exception
    {
    	File cnf= new File(filename+"_config.properties");
    	cnf.createNewFile();prop=new Properties();
    	prop.load(new FileInputStream(new File(filename+"_config.properties")));
    	prop.setProperty("hashMapLimit", "70000000");
    	prop.setProperty("datafile", filename+".xlsx");
    	prop.setProperty("type", "xlsx");
    	prop.setProperty("schemaPath", "db_"+filename+"/schema/");
    	prop.setProperty("baseDirPath", "db_"+filename+"/base/");
    	prop.setProperty("dimensionsDirPath", "db_"+filename+"/dimensions/");
    	prop.setProperty("dimensionInfoDirPath", "db_"+filename+"/dimensionMetadata/");
    	prop.setProperty("latticeDirPath", "db_"+filename+"/lattice/");
    	prop.setProperty("basePath", "db_"+filename+"/base/base");
    	prop.setProperty("dimensionsPath", "db_"+filename+"/dimensions/dim");
    	prop.setProperty("dimensionInfoPath","db_"+filename+"/dimensionMetadata/");
    	prop.setProperty("latticePath", "db_"+filename+"/lattice/cuboid");
    	FileOutputStream out = new FileOutputStream(filename+"_config.properties");
    	prop.store(out, null);
    	out.close();
	}
    //To generate Lattice of Cuboids.
    private static void genrateLatticeOfCuboids(Properties prop)throws Exception
    {
    	FileInputStream fs;ObjectInputStream os;
    	fs=new FileInputStream(prop.getProperty("schemaPath")+"dimSchema");
		os= new ObjectInputStream(fs);
		HashMap<Integer,String> dimhsh=(HashMap<Integer,String>)os.readObject();
		os.close();fs.close();
		int dimCount=dimhsh.size();dimhsh.clear();
		LatticeGenerator lattice= new LatticeGenerator();
		System.out.println("Generating Lattice Of Cuboids...");
		lattice.createLatticeOfCuboids(dimCount, prop);
	}

	//To read Data as per OLAP operations :slice/dice/roll up/drill down.We need UI interaction here.
	private static void readData(Properties prop,String filename,Scanner sc)throws Exception
	{
		int ip=-1;
		while(true)
    	{
			sc= new Scanner(System.in);
			System.out.println("Select MOLAP Operation:");
			System.out.println("1.Slice");
	    	System.out.println("2.Dice");
	    	System.out.println("3.Roll-Up/DrillDown");
	    	System.out.println("4.Back");
	    	ip=Integer.parseInt(sc.nextLine());
	    	if(ip==4) {return;}
	    	if(ip<1 || ip>4)continue;
	    	File folder;File[] listOfFiles;ReadData read;
	    	switch (ip)
	    	{
				case 1:
						System.out.println("Enter one of available dimension:");
						System.out.println("Dimensions:");
						folder= new File(prop.getProperty("dimensionInfoDirPath"));
						listOfFiles = folder.listFiles();
						for (int i = 0; i < listOfFiles.length; i++)
						{
						  if (listOfFiles[i].isFile())
						  {System.out.println(listOfFiles[i].getName());} 
						}
						String dimfile=sc.nextLine();
						read= new ReadData();
						read.slice(prop,dimfile,sc);
						break;
				case 2:
					System.out.println("Dimensions:");
					System.out.println("Enter from available dimension:");
					folder = new File(prop.getProperty("dimensionInfoDirPath"));
					listOfFiles = folder.listFiles();
					for (int i = 0; i < listOfFiles.length; i++)
					{
					  if (listOfFiles[i].isFile())
					  {System.out.println(listOfFiles[i].getName());} 
					}
					String[] dimfiles=sc.nextLine().split(",");
					read= new ReadData();
					read.dice(prop,dimfiles,sc);
					break;
				case 3:
					MOLAP ml=new MOLAP();
					ml.getLattice(prop,sc);
					break;
				default:
						break;
			}
    	}
	}
	
    //To create base & dimension index files
	private static void loadData(Properties prop)throws Exception
	{
        makeDirectory(prop);
        Schema schm= new Schema();
        Load load =new Load();
        schm.generateSchema(prop);
        System.out.println("Creating Fact Table...");
        load.createBase(prop);        
	}

	//make directory as per excel data file name.It will have two folder base and dimensions.
	private static void makeDirectory(Properties prop) throws Exception
	{
		new File(prop.getProperty("schemaPath")).mkdirs();
		new File(prop.getProperty("baseDirPath")).mkdirs();
		new File(prop.getProperty("dimensionsDirPath")).mkdirs();
		new File(prop.getProperty("latticeDirPath")).mkdirs();
		new File(prop.getProperty("dimensionInfoDirPath")).mkdirs();
	}
	private static void genrateDimensionMetaData(Properties prop) throws Exception
	{
		MetaDimGenerator dimMeta= new MetaDimGenerator();
		System.out.println("Collecting Dimension MetaData...");
		dimMeta.genratedimMeta(prop);
	}
}
