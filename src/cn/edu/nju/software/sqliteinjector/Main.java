package cn.edu.nju.software.sqliteinjector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Vector;

import android.content.ContentValues;
import android.os.Environment;
import android.util.Log;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class Main implements IXposedHookLoadPackage {

	XSharedPreferences pref;
	final String SEPARATOR = " | ";// may be configurable later
	final String PNAME = "com.hamzahrmalik.smstimeappend";
	ArrayList<HookPackage> packageList = new ArrayList<HookPackage>();
	Vector<String> methodHookedVec = new Vector<String>();
	Vector<String> ignoreSQLVec = new Vector<String>();
	Vector<String> ignoreMethodVec = new Vector<String>();

	HookPackage hp = null;
	
	public Main(){
		HookPackage tnew = null;
/*		{//hook mobile qq
			tnew = new HookPackage("com.tencent.mobileqq");
			tnew.addHookClass("com.tencent.mobileqq.app.SQLiteDatabase");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");		
			packageList.add(tnew);
		}
		
		{//hook weixin
			tnew = new HookPackage("com.tencent.mm");
			tnew.addHookClass("com.tencent.kingkong.database.SQLiteDatabase");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");		
			packageList.add(tnew);
		}*/
		
		{//hook telephony
			tnew = new HookPackage("com.android.providers.telephony");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");		
			packageList.add(tnew);
		}
			
		/*{//hook orweb
			tnew = new HookPackage("info.guardianproject.browser");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");		
			packageList.add(tnew);
		}
		
		{//hook whatsapp
			tnew = new HookPackage("com.whatsapp");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");		
			packageList.add(tnew);
		}*/
		{//hook taobao
			tnew = new HookPackage("com.taobao.taobao");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");		
			packageList.add(tnew);
		}
		
		{//hook sina weibo
			tnew = new HookPackage("com.sina.weibo");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");		
			packageList.add(tnew);
		}
		
		{
			methodHookedVec.add("execSQL");
		}
		
		{//initialize ignore SQL statement
			ignoreSQLVec.add("BEGIN");
			ignoreSQLVec.add("BEGIN EXCLUSIVE");
			ignoreSQLVec.add("COMMIT");
			ignoreSQLVec.add("BEGIN IMMEDIATE");
			ignoreSQLVec.add("PRAGMA page_size = 4096");
			ignoreSQLVec.add("PRAGMA wal_checkpoint");
			ignoreSQLVec.add("Reporting");
			ignoreSQLVec.add("param");
			ignoreSQLVec.add("CREATE INDEX");
			ignoreSQLVec.add("DROP INDEX");

		}
		
		{//initialize ignore method
			ignoreMethodVec.add("openOrCreateDatabase");
			ignoreMethodVec.add("is");
			ignoreMethodVec.add("isDbLockedByCurrentThread");
			ignoreMethodVec.add("close");
			ignoreMethodVec.add("get");
			ignoreMethodVec.add("set");
			ignoreMethodVec.add("isReadOnly");
			ignoreMethodVec.add("query");
			ignoreMethodVec.add("compileStatement");
			ignoreMethodVec.add("findEditTable");
			ignoreMethodVec.add("rawQuery");
			ignoreMethodVec.add("findEditTable");
			ignoreMethodVec.add("inTransaction");
			ignoreMethodVec.add("beginTransactionNonExclusive");
			ignoreMethodVec.add("endTransaction");
			ignoreMethodVec.add("insertWithOnConflict");
			ignoreMethodVec.add("updateWithOnConflict");
			ignoreMethodVec.add("releaseMemory");	
			ignoreMethodVec.add("needUpgrade");	
			ignoreMethodVec.add("yieldIf");
		}		
	}
	
    private void hookClass(String className,  ClassLoader classLoader,XC_MethodHook xmh)
    {
        try {
        	Class<?> clazz = findClass(className, classLoader);
            int i = 0;
            for (Method method : clazz.getDeclaredMethods()){
            	
                if (!Modifier.isAbstract(method.getModifiers())
                        && Modifier.isPublic(method.getModifiers())) {
                	if(!isContain(ignoreMethodVec,method.getName())){
                	 XposedBridge.hookMethod(method, xmh);
                	 XposedBridge.log("\nmethod hooked succeed:"+method);
                	}
                }
            }
        } catch (Exception e) {
            XposedBridge.log("hook class failed:"+className);
        } 
    }
	@Override
	public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
		//XposedBridge.log("packageName: "+lpparam.packageName);
        for(int i = 0; i < packageList.size(); i++){
        	if(lpparam.packageName.matches(".*"+packageList.get(i).getPackageName()+".*")){
        		this.hp = packageList.get(i);
        		handleHook(lpparam);
        	}
        }
	}
	
	private void handleHook(LoadPackageParam lpparam){
    	Log.v("package hooked: ", lpparam.packageName);
    	XposedBridge.log("className: "+lpparam.appInfo.className);
		XposedBridge.log("package hooked: "+lpparam.packageName);

		for(int n = 0; n < hp.getHookClassVec().size(); n++)
			hookClass(hp.getHookClassVec().get(n), lpparam.classLoader,new XC_LOCALE_MethodHook(lpparam));

	}
    private void write(String content)
    {
        try
        {
            File dir = Environment.getExternalStorageDirectory();//得到data目录  
            String log = (new SimpleDateFormat("MM-dd-HH",Locale.CHINA)).format(new Date());
            log = hp.packageName+"_"+log;
            log = log.replace(".", "_");
           // XposedBridge.log("dir path:"+dir+"/log/"+log+".txt");
            File newFile=new File(Common.LogFile.HOOK_LOG_DIR+"/log/"+log+".txt");  
            File parent = newFile.getParentFile();
            if(!parent.exists()){
            	parent.mkdirs();
            }
            if(!newFile.exists()){
            	newFile.createNewFile();
            }
            FileWriter fw=new FileWriter(newFile,true);//以追加的模式将字符写入  
            BufferedWriter bw=new BufferedWriter(fw);//又包裹一层缓冲流 增强IO功能  
            bw.write(content);  
            bw.flush();//将内容一次性写入文件  
            bw.close(); 
        }
        catch (Exception e)
        {
        	XposedBridge.log(e);
        }
    }
    private boolean isContain(Vector v,String s){
    	
    	int i = 0;
    	for(i = 0; i < v.size(); i++)
    		if(s.matches(".*"+v.get(i)+".*")){
    			break ;
    		}
    	if(i == v.size()){
    		return false;
    	}
    	return true;
    }
    
    private String getContent(Object o){
    	if(o instanceof Integer){
    		return o.toString();
    	}
    	if(o instanceof String){
    		return o.toString();
    	}
    	if(o instanceof String[]){
    		String[] s =(String[])o;
    		String t = "";
    		for(int i = 0 ;i < s.length ;i++){
    			t += s[i].toString()+"  ";
    		}
    		return t;
    	}
    	if(o instanceof Boolean){
    		return o.toString();
    	}
    	if(o instanceof ContentValues){
    		ContentValues values = (ContentValues)o;
    		return values.toString();
    	}
    	if(o == null ){
    		return "null";
    	}
    	if(o instanceof Object){
    		return o.toString();
    	}

    	return "";
    }
    
    public class XC_LOCALE_MethodHook extends XC_MethodHook{
    	LoadPackageParam lpparam;
    	public XC_LOCALE_MethodHook(LoadPackageParam lpparam){
    		this.lpparam = lpparam;
    	}
    	
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        	//if(param.args.length <= 1)return ;
        	String time = (new SimpleDateFormat("mm:ss:SS",Locale.CHINA)).format(new Date());
        	String tmp = "\nprocess:"+lpparam.processName;
        	tmp += "\ntime:"+time;
        	tmp += "\nmethod:"+param.method.toString();
        	String tmp2 = "";
        	int l = param.args.length;
			tmp = tmp + "\nparam length:"+l+"";    
        	int i = 0;
        	try{
        		if(l > 0){   		
        			for(i = 0; i < l; i++)
        				tmp2 = tmp2 + getContent(param.args[i])+" && ";	                   		
        			if(isContain(ignoreSQLVec,tmp2))
        				return ;
        		}
        	} catch(Exception e) {
            	Log.v("………………<<hook error>>………………", tmp);
        		XposedBridge.log("………………<<hook error>>………………"+tmp);
        		XposedBridge.log(e);
        	}

            tmp +=  "\nparam:"+tmp2;
        	Log.v("\n#####################\nhook log", tmp);
    		XposedBridge.log("\n#####################\nhook log"+tmp);
    		String content = "\n<HOOK TAG>"+tmp+"\n";
    		write(content);
        }
    }   
    
    
}
