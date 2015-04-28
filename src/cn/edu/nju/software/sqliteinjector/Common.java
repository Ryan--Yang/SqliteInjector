package cn.edu.nju.software.sqliteinjector;

import java.io.File;

import android.os.Environment;

public class Common {
	public static final Boolean DEBUG = false;
	private static Boolean ENABLE_DEBUG;
	
	public static final String PACKAGE_NAME = Common.class.getPackage().getName();
	
	public static final String TORCH_INTENT_ACTION = PACKAGE_NAME + ".TOGGLE_FLASHLIGHT";
	public static final String PREFERENCE_FILE = "config";
	
	public static class LogFile {
		public static final Long SIZE = 1024L*512;
		public static final File HOOK_LOG_DIR = new File(Environment.getDataDirectory()+"/data/" + PACKAGE_NAME + "/hookLog/");
		public static final File MAIN = new File(Environment.getDataDirectory(), "data/" + PACKAGE_NAME + "/cache/error.main.log");
		public static final File STORED = new File(Environment.getDataDirectory(), "data/" + PACKAGE_NAME + "/cache/error.stored.log");
	}
}
