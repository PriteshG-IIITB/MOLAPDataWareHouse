package com.MOLAPDataWarehouse.IIITB.MOLAPDataWarehouse;
import java.io.Serializable;
import java.util.*;
public class GTreeNode implements Serializable {

	String dimension,dataVal;
	boolean isadded;
	double factSum;int factCnt;
	HashMap<String,GTreeNode> childMap;
	ArrayList<String> ancestorList;
	public GTreeNode(String dimension,String dataVal,double factSum,boolean isLeaf)
	 {
		 this.dimension=dimension;this.dataVal=dataVal;this.factSum=factSum;factCnt=1;
		 childMap=new HashMap<String,GTreeNode>();
		 if(isLeaf){ancestorList= new ArrayList<String>();}
		 else ancestorList=null;
		 isadded=false;
	 }
	public void addfact(double fact){factSum+=fact;}
	public void addfactCnt(){factCnt++;}
}
