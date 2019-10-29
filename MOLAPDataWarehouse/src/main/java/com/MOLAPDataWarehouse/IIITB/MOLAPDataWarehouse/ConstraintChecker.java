package com.MOLAPDataWarehouse.IIITB.MOLAPDataWarehouse;
public class ConstraintChecker {

	public static int sumBoundChk(String oprnd,double limit,double[]aggArr)
	{
		double upperbound=aggArr[0];
		double lowerbound=aggArr[3];
		switch (oprnd)
		{
			case ">":
				if(upperbound>limit)return 1;break;
			case ">=":
				if(upperbound>=limit)return 1;break;
			case "<":
				if(upperbound>=limit && lowerbound>=limit)return 0;
				if(upperbound<limit)return 1;
				if(upperbound>=limit && lowerbound<limit)return 2;
				break;
			case "<=":
				if(upperbound>limit && lowerbound>limit)return 0;
				if(upperbound<=limit)return 1;
				if(upperbound>limit && lowerbound<=limit)return 2;
				break;
			default:break;
		}
		return 0;
	}
	public static int cntBoundChk(String oprnd,double limit,double[]aggArr)
	{
		double upperbound=aggArr[1];
		double lowerbound=aggArr[2];
		switch (oprnd)
		{
			case ">":
				if(upperbound>limit)return 1;break;
			case ">=":
				if(upperbound>=limit)return 1;break;
			case "<":
				if(upperbound>=limit && lowerbound>=limit)return 0;
				if(upperbound<limit)return 1;
				if(upperbound>=limit && lowerbound<limit)return 2;
				break;
			case "<=":
				if(upperbound>limit && lowerbound>limit)return 0;
				if(upperbound<=limit)return 1;
				if(upperbound>limit && lowerbound<=limit)return 2;
				break;
			default:break;
		}
		return 0;
	}
}
