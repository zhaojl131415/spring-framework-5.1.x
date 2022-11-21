package com.zhao.proxy;

import javax.tools.JavaCompiler;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;
import java.io.File;
import java.io.FileWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

@SuppressWarnings({"rawtypes", "unchecked"})
public class ZhaoProxy {

	public static Object getInstance(Object target) throws MalformedURLException, ClassNotFoundException, NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException {
		Class clazz = target.getClass().getInterfaces()[0];
		String infName = clazz.getSimpleName();
		String content = "";
		String line = "\n";//换行
		String tab = "\t";//tab
		String packageContent = "package com.baidu;" + line;
		String importContent = "import " + clazz.getName() + ";" + line;
		String clazzFirstLineContent = "public class $ProxyLuban implements " + infName + "{" + line;
		String filedContent = tab + "private " + infName + " target;" + line;

		String constructorContent = tab + "public $ProxyLuban (" + infName + " target){" + line
				+ tab + tab + "this.target =target;"
				+ line + tab + "}" + line;


		String methodContent = "";
		Method[] methods = clazz.getDeclaredMethods();

		for (Method method : methods) {
			//String
			String returnTypeName = method.getReturnType().getSimpleName();
			//query
			String methodName = method.getName();
			// [String.class===class]
			Class args[] = method.getParameterTypes();
			String argsContent = "";
			String paramsContent = "";
			int flag = 0;
			for (Class arg : args) {
				//String
				String temp = arg.getSimpleName();
				//String
				//String p0
				argsContent += temp + " p" + flag + ",";
				//p0
				paramsContent += "p" + flag + ",";
				flag++;
			}
			if (argsContent.length() > 0) {
				argsContent = argsContent.substring(0, argsContent.lastIndexOf(",") - 1);
				paramsContent = paramsContent.substring(0, paramsContent.lastIndexOf(",") - 1);
			}
			//public String query(String p0){
			//		System.out.println("log");
			//		target.query(p0);
			//	}

			methodContent += tab + "public " + returnTypeName + " " + methodName + "(" + argsContent + ") {" + line
					+ tab + tab + "System.out.println(\"log\");" + line
					+ tab + tab + "target." + methodName + "(" + paramsContent + ");" + line
					+ tab + "}" + line;

		}

		content += packageContent + importContent + clazzFirstLineContent + filedContent + constructorContent + methodContent + "}";


		File file = new File("d:\\com\\baidu\\$ProxyLuban.java");

		try {
			if (!file.exists()) {
				file.createNewFile();
			}

			FileWriter fw = new FileWriter(file);
			fw.write(content);
			fw.flush();
			fw.close();
		} catch (Exception e) {

		}


		JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();

		StandardJavaFileManager fileMgr = compiler.getStandardFileManager(null, null, null);
		Iterable units = fileMgr.getJavaFileObjects(file);

		JavaCompiler.CompilationTask t = compiler.getTask(null, fileMgr, null, null, null, units);
		t.call();

		//Class.forName()
		//fileMgr.close();

		URL[] urls = new URL[]{new URL("file:D:\\\\")};
		URLClassLoader urlClassLoader = new URLClassLoader(urls);
		Class cls = urlClassLoader.loadClass("com.baidu.$ProxyLuban");
		Constructor constructor = cls.getConstructor(clazz);
		Object o  = constructor.newInstance(target);

		return  o;

	}







}
