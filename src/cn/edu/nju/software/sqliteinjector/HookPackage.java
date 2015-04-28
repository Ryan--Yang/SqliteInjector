package cn.edu.nju.software.sqliteinjector;

import java.util.Vector;

public class HookPackage {
 String packageName = "";
 Vector<String> hookClassVec = new Vector<String>();
 public HookPackage(String className){
	 this.packageName = className;
 }
 
 public void addHookClass(String hookClass){
	 hookClassVec.add(hookClass);
 }
 
 public boolean isContainHookClass(String hookClass){
	 return hookClassVec.contains(hookClass);
 }
 
 public Vector<String> getHookClassVec(){
	 return hookClassVec;
 }

public String getPackageName() {
	return packageName;
}
 
}
