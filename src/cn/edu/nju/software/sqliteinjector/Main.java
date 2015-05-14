package cn.edu.nju.software.sqliteinjector;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Vector;

import com.spazedog.lib.reflecttools.ReflectClass;
import com.spazedog.lib.reflecttools.ReflectMethod;
import com.spazedog.lib.reflecttools.utils.ReflectConstants.Match;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteCursor;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodHook.MethodHookParam;
import de.robv.android.xposed.XSharedPreferences;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;
import static de.robv.android.xposed.XposedHelpers.findClass;

public class Main implements IXposedHookLoadPackage {

	XSharedPreferences pref;
	final String SEPARATOR = " | ";// may be configurable later
	final String PNAME = "cn.edu.nju.software.sqliteinjector";
	ArrayList<HookPackage> packageList = new ArrayList<HookPackage>();
	Vector<String> methodHookedVec = new Vector<String>();
	Vector<String> ignoreSQLVec = new Vector<String>();
	Vector<String> ignoreMethodVec = new Vector<String>();
	long startTime;

	HookPackage hp = null;
	
	public Main(){  //initial hook package list
		HookPackage tnew = null;
		{//hook mobile qq
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
		}
		
		{//hook telephony
			tnew = new HookPackage("com.android.providers.telephony");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");		
			packageList.add(tnew);
		}
		
		{//hook contacts
			tnew = new HookPackage("com.android.providers.contacts");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");		
			packageList.add(tnew);
		}
		
		{//hook mms
			tnew = new HookPackage("com.android.mms");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");		
			packageList.add(tnew);
		}
		
		{//hook contacts
			tnew = new HookPackage("com.android.contacts");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");		
			packageList.add(tnew);
		}

		{//hook whatsapp
			tnew = new HookPackage("com.whatsapp");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");		
			packageList.add(tnew);
		}
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
		
		{//hook firefox
			tnew = new HookPackage("cn.mozilla.firefox");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");		
			packageList.add(tnew);
		}
		
		{//hook tmail
			tnew = new HookPackage("com.tmall.wireless");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");		
			packageList.add(tnew);
		}
		
		{//hook facebook
			tnew = new HookPackage("com.facebook.katana");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");		
			packageList.add(tnew);
		}
		
		{//hook alipay
			tnew = new HookPackage("com.eg.android.AlipayGphone");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");		
			packageList.add(tnew);
		}
		
		{//hook filghtmanager
			tnew = new HookPackage("com.flightmanager.view");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");		
			packageList.add(tnew);
		}
		
		{//hook kugou music
			tnew = new HookPackage("com.kugou.android");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");
			tnew.addHookClass("com.tencent.mm.sdk.storage.ISQLiteDatabase");
			packageList.add(tnew);
		}
		
		{//hook autonavi
			tnew = new HookPackage("com.autonavi.minimap");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");
			packageList.add(tnew);
		}
		
		{//hook templerun2
			tnew = new HookPackage("com.imangi.templerun2");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");
			packageList.add(tnew);
		}
		
		{//hook momo
			tnew = new HookPackage("com.immomo.momo");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");
			packageList.add(tnew);
		}
		
		{//hook renren
			tnew = new HookPackage("com.renren.mobile.android");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");
			packageList.add(tnew);
		}
		
		{//hook talkingtom
			tnew = new HookPackage("com.outfit7.talkingtom");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");
			packageList.add(tnew);
		}
		
		{//hook baidu netdisk
			tnew = new HookPackage("com.baidu.netdisk");
			tnew.addHookClass("android.database.sqlite.SQLiteDatabase");
			packageList.add(tnew);
		}
		
		{//hook moji
			tnew = new HookPackage("com.moji.mjweather");
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
			//ignoreMethodVec.add("query");
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
			ignoreMethodVec.add("addCustom");
			ignoreMethodVec.add("mark");
		}		

/*		File cacheDir = Common.LogFile.MAIN.getParentFile();
		ReflectMethod setPermissions = ReflectClass.forName("android.os.FileUtils").findMethod("setPermissions", Match.BEST, String.class, Integer.TYPE, Integer.TYPE, Integer.TYPE);
		
		if (cacheDir.exists() || cacheDir.mkdir()) {
			setPermissions.invoke(cacheDir.getPath(), 0777, -1, -1);
		}*/
	}
	//hook specific package method
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
                	 //XposedBridge.log("\nmethod hooked succeed:"+method);
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
    	if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
    		XposedBridge.log("write file error!!!");
		return;
    	}
        try
        {
            File dir = Environment.getExternalStorageDirectory();//得到data目录  
            String log = (new SimpleDateFormat("MM_dd_HH",Locale.CHINA)).format(new Date());
            log = hp.packageName+"-"+log;
            log = log.replace(".", "_");
 //           XposedBridge.log("dir path:"+Common.LogFile.HOOK_LOG_DIR+"/"+log+".hog");           
            File newFile=new File(dir+"/hog/"+log+".hog");  
            File parent = newFile.getParentFile();
            if(!parent.exists()){
            	parent.mkdirs();
            }
            if(!newFile.exists()){
            	newFile.createNewFile();
            }
            FileWriter  fw=new FileWriter(newFile,true);//以追加的模式将字符写入  
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
    private String columnToString(Cursor cs,int index){
    	switch(cs.getType(index)){
    		case Cursor.FIELD_TYPE_BLOB:return "BLOB block";
    		case Cursor.FIELD_TYPE_FLOAT:return String.valueOf(cs.getFloat(index));
    		case Cursor.FIELD_TYPE_INTEGER:return String.valueOf(cs.getInt(index));
    		case Cursor.FIELD_TYPE_NULL:return "null";
    		case Cursor.FIELD_TYPE_STRING:return cs.getString(index);
    		default:return "";
    	}
    }
    private String columnToString(SQLiteCursor cs,int index){
    	switch(cs.getType(index)){
    		case SQLiteCursor.FIELD_TYPE_BLOB:return "BLOB block";
    		case SQLiteCursor.FIELD_TYPE_FLOAT:return String.valueOf(cs.getFloat(index));
    		case SQLiteCursor.FIELD_TYPE_INTEGER:return String.valueOf(cs.getInt(index));
    		case SQLiteCursor.FIELD_TYPE_NULL:return "null";
    		case SQLiteCursor.FIELD_TYPE_STRING:return cs.getString(index);
    		default:return "unkonw type";
    	}
    }
    private String getContent(Object o){
    	if(o instanceof Cursor){
    		Cursor cs = (Cursor)o;
    		String[] sarray = cs.getColumnNames();
    		StringBuilder sb = new StringBuilder();
    		for(cs.moveToFirst();!cs.isAfterLast();cs.moveToNext()){
        		for(int i = 0; i<sarray.length; i++){
        			sb.append(sarray[i]+":"+columnToString(cs,cs.getColumnIndex(sarray[i]))+" && ");
        		}
    			sb.append("\n");
    		}
    		return sb.toString();
    	}
    	if(o instanceof SQLiteCursor){
    		SQLiteCursor cs = (SQLiteCursor)o;
    		String[] sarray = cs.getColumnNames();
    		StringBuilder sb = new StringBuilder();
    		for(cs.moveToFirst();!cs.isAfterLast();cs.moveToNext()){
        		for(int i = 0; i<sarray.length; i++){
        			sb.append(sarray[i]+":"+columnToString(cs,cs.getColumnIndex(sarray[i]))+" && ");
        		}
    			sb.append("%\n");
    		}
    		return sb.toString();
    	}
    	if(o instanceof Integer){
    		return ((Integer)o).toString();
    	}
    	if(o instanceof String){
    		return ((String)o).toString();
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
    		return ((Boolean)o).toString();
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
    

	String getCurProcessName() {
    	 Class<?> activityManagerNative;
    	 Object am;
    	 Object o;
		try {
			activityManagerNative = Class.forName("android.app.ActivityManagerNative");
			 am =activityManagerNative.getMethod("getDefault").invoke(activityManagerNative);  
			 o = am.getClass().getMethod("getRunningAppProcesses").invoke(am);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			XposedBridge.log(e);
			return "error";
		}  
    	
    	 
    	List<RunningAppProcessInfo> runningAppProcessInfo= null;
    	if(o instanceof List){
    		runningAppProcessInfo = (List<RunningAppProcessInfo>)o;
    	}
    	 int pid = android.os.Process.myPid();
    	 for (ActivityManager.RunningAppProcessInfo appProcess : runningAppProcessInfo) {
    	  if (appProcess.pid == pid) {

    	   return appProcess.processName;
    	  }
    	 }
    	 return null;
    	}
    
    public class XC_LOCALE_MethodHook extends XC_MethodHook{

		LoadPackageParam lpparam;
    	public XC_LOCALE_MethodHook(LoadPackageParam lpparam){
    		this.lpparam = lpparam;
    	}
    	
        @Override
        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        	startTime = System.currentTimeMillis();
        	//XposedBridge.log("\ncurrent process::"+getCurProcessName());
        }
        

     
	@Override
	protected void afterHookedMethod(MethodHookParam param)
			throws Throwable {
    	//if(param.args.length <= 1)return ;
		
    	String time = (new SimpleDateFormat("HH:mm:ss:SS",Locale.CHINA)).format(new Date());
    	StringBuilder  tmp = new StringBuilder();
    	 int pid = android.os.Process.myPid();
    	tmp.append("\npid::"+pid);
    	tmp.append("\ncurrent process::"+getCurProcessName());
    	tmp.append("\nprocess::"+lpparam.processName);
    	tmp.append("\ncall time::"+time);
    	tmp.append("\nmethod::"+param.method.toString());
    	StringBuilder tmp2 = new StringBuilder();
    	int l = param.args.length;
		tmp.append("\nparam length::"+l);    
    	int i = 0;
    	try{
    		if(l > 0){   		
    			for(i = 0; i < l; i++)
    				tmp2.append((String) (getContent(param.args[i])+" && "));	                   		
    		}
    	} catch(Exception e) {
        	Log.v("………………<<nhook error>>………………", tmp.toString());
    		XposedBridge.log("………………<<nhook error>>………………"+tmp);
    		XposedBridge.log(e);
    	}
		if(isContain(ignoreSQLVec,tmp2.toString()))
			return ;
        tmp.append("\nparam::"+tmp2.toString());
    	
        if(param.getResult() != null){
        	 String s = getContent(param.getResult());
        	 if(s != "" && s != null)
        	tmp.append("\nresult::" + s);
        }
        tmp.append("\ntime-consuming::"+(System.currentTimeMillis() - startTime)+"ms");
    	Log.v("\n#####################\nhook log", tmp.toString());
		//XposedBridge.log("\n#####################\nhook log"+tmp);
		String content = "\n<HOOK TAG>"+tmp+"\n";
		write(content);
	}
    }
    
}
