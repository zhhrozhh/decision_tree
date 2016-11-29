//package proj2;

import java.io.*;
import java.util.*;
public class DecisionTree {
	public class Attr {
		public List<List<String>> attlist;
		public String goal;
		public Attr(){
			attlist = new ArrayList<List<String>>();
		}
		public void addAttr(String attr) throws Exception{
			List<String> newattr = new ArrayList<String>();
			String[]l = attr.split("[ {},]");
			if(!l[0].equals(new String("@attribute")))throw new Exception("not an attribute");
			for(int i=1;i<l.length;i++)
				if(! l[i].isEmpty()) newattr.add(l[i]);
			attlist.add(newattr);
			goal = newattr.get(0);
		}
		public String ask(String attr){
			for(List<String>x:attlist){
				if(x.contains(attr))
					return x.get(0);
			}
			return new String("@attribute->UKN");
		}
		public List<String>getAttr(String cata,List<String>q){
			List<String>res = new ArrayList<String>();
			for(String s:q)
				if(ask(s).equals(cata))res.add(s);
			return res;
		}
		public List<String>getOpt(String cata){
			List<String>res = new ArrayList<String>();
			for(int i=0;i<attlist.size();i++){
				if(attlist.get(i).contains(cata))
					return attlist.get(i).subList(1,attlist.get(i).size());
			}
			return res;
		}
	}
	public class SampleSpace {
		public List<List<String>> ss;
		public SampleSpace(){
			ss = new ArrayList<List<String>>();
		}
		public void addSample(String s){
			ss.add(Arrays.asList(s.split("[, ]")));
		}
	}
	public class STT{
		public List<List<String>>data;
		public Map<String,Integer> P;
		public int T;
		public STT(List<List<String>>sset){
			data = new ArrayList<List<String>>(sset);
			T = 0;
			P = new HashMap<String,Integer>();
			List<String>goalOpt = attr.getOpt(attr.goal);
			for(String x:goalOpt)P.put(x,0);
			for(List<String>x:sset){
				P.put(x.get(x.size()-1),P.get(x.get(x.size()-1))+1);
				T++;
			}
		}
		public double H(){
			double res = 0;
			for(List<String>x:data){
				double p = (double)(P.get(x.get(x.size()-1)))/(double)(T);
				res-=Math.log(p)*p;
			}
			return res;
			
		}
	}
	
	public Attr attr;
	public SampleSpace ss;
	public DecisionTree(){
		attr = new Attr();
		ss = new SampleSpace();
	}
	public void process(String fn)throws Exception{
		try (BufferedReader br = new BufferedReader(new FileReader(fn))) {
		    String line;
		    while ((line = br.readLine()) != null) {
		    	//System.out.println(line);
		    	if (line.startsWith("%"))continue;
		    	String key = line.split(" ")[0];
		    	if(key.equals(new String("@attribute")))attr.addAttr(line);
		    	else if(line.startsWith("@"))continue;
		    	else if(line.isEmpty())continue;
		    	else ss.addSample(line);
		    }
		}
	}
	public double H(List<List<String>>sset){
		double res = 0;
		int T=0;
		Map<String,Integer> P = new HashMap<String,Integer>();
		List<String>goalOpt = attr.getOpt(attr.goal);
		for(String x:goalOpt)P.put(x,0);
		for(List<String>x:sset){
			P.put(x.get(x.size()-1),P.get(x.get(x.size()-1))+1);
			T++;
		}
		for(List<String>x:sset){
			double p = (double)(P.get(x.get(x.size()-1)))/(double)(T);
			res-=Math.log(p)*p;
		}
		return res;
	}
	public Map<String,List<List<String>>>seperate(List<List<String>>sset,String cata){
		Map<String,List<List<String>>>res = new HashMap<String,List<List<String>>>();
		for(String x:attr.getOpt(cata)){
			List<List<String>> l = new ArrayList<List<String>>();
			for(List<String>y:sset)
				if(y.contains(x))l.add(y);
			res.put(x,l);
		}
		return res;
	}
	
	
	public void buildTree(List<List<String>>sset,int level,List<String>fcata){
		double sH = H(sset);
		double s = (double)sset.size();
		double maxG = (double)(-0xff);
		String cata = "";
		
		Map<String,List<List<String>>>maxSep=new HashMap<String,List<List<String>>>();
		for(List<String> x:attr.attlist){
			String catap = x.get(0);
			Map<String,List<List<String>>>sep = seperate(sset,catap);
			double pR = 0;
			if(!fcata.contains(catap)){
				for(Map.Entry<String,List<List<String>>>y:sep.entrySet()){
					double ss = (double)y.getValue().size();
					double f = ss/s;
					pR+=f*H(y.getValue());
				}
				if(sH-pR>maxG){
					maxSep=sep;
					maxG = sH - pR;
					cata = catap;
				}
			}
		}
		List<String>ncata = new ArrayList<String>(fcata);
		ncata.add(cata);
		boolean ret = false;
		for(Map.Entry<String,List<List<String>>>x:maxSep.entrySet()){
			STT stt = new STT(x.getValue());
			System.out.print("|");
			for(int i = 0;i<level;i++)System.out.print("-");
			System.out.println(cata+" = "+x.getKey() + "(gain:"+Double.toString(maxG)+","+stt.P.toString()+")");
			for(Map.Entry<String,Integer>y:stt.P.entrySet()){
				if(y.getValue()==x.getValue().size())
					ret=true;
			}
			if(!ret)buildTree(x.getValue(),level+1,ncata);
		}
	}
	public void build(){
		System.out.println("the number of training instance: "+Integer.toString(ss.ss.size()));
		buildTree(ss.ss,0,Arrays.asList(attr.goal));
	}
	public void debug(){
		for(List<String>x:ss.ss){
			System.out.println(x.toString());
		}
		for(List<String>x:attr.attlist){
			System.out.println(x.toString());
		}
		System.out.println("xx"+attr.goal);
	}
}
