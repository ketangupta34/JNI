import java.io.File;
import javax.tools.*;
import java.util.*;

import java.lang.reflect.Method;
import java.lang.Object;

import java.nio.file.Path;
import java.nio.file.Paths;

class Handle {
  Class<?> obj;

  Handle(Class<?> obj) {
    this.obj = obj;
  }

  public Class<?> getHandle() {
    return obj;
  }
}

public class bootstrap {
  public static void callFunction(Handle handle, String functionName) {
    Class<?> c = handle.getHandle();

    try {
      Method m = c.getDeclaredMethod(functionName, new Class[] { String[].class }); // (name, parameters)
      m.invoke(null, new Object[] { null });

    } catch (Exception e) {
      System.err.println("CallFunction" + e);
    }
  }

  public static Handle[] loadFromFile(String[] paths) {
    // load all scripts and store them into a Handle class, then return it
    Handle[] handleArray = new Handle[paths.length];

    for (int i = 0; i < paths.length; i++) {
      System.out.println("Path provided " + paths[i]);

      try {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        DiagnosticCollector<JavaFileObject> ds = new DiagnosticCollector<>();
        StandardJavaFileManager mgr = compiler.getStandardFileManager(ds, null, null);
        Iterable<String> classOutputPath = Arrays.asList(new String[] { "-d", "." });

        File file1 = new File(paths[i]);
        Iterable<? extends JavaFileObject> sources = mgr.getJavaFileObjectsFromFiles(Arrays.asList(file1));
        JavaCompiler.CompilationTask task = compiler.getTask(null, mgr, ds, classOutputPath, null, sources);
        Boolean call = task.call(); // main method to compile the file into class

        if (call) {
          System.out.println("Compilation Successful");
          Path path = Paths.get(file1.getCanonicalPath());
          String classname = path.getFileName().toString().split(".java")[0];

          handleArray[i] = new Handle(Class.forName(classname));

        } else {
          System.out.println("Compilation Failed");
          handleArray[i] = null;
        }

        for (Diagnostic<? extends JavaFileObject> d : ds.getDiagnostics()) { // diagnostic error printing
          System.out.format("Line: %d, %s in %s", d.getLineNumber(), d.getMessage(null), d.getSource().getName());
        }

        mgr.close();
        System.out.print("\n");

      } catch (Exception e) {
        System.err.println("Load Function" + e);
      }
    }

    return handleArray;
  }

  public static void DiscoverData(Handle handle) {
    // for each loaded .java file in the Handle list, get the DiscoverData, which is
    // another class with the list of classes and methods etc
    Class<?> hClass = handle.getHandle();

    System.out.println("ClassName: " + hClass.getName());

    Method[] methods = hClass.getDeclaredMethods();

    for (Method method : methods) {
      System.out.println("Name of the method: " + method.getName());

      Class<?>[] parameters = method.getParameterTypes();
      if (parameters.length == 0)
        System.out.println("\tparameter: none");
      for (Class<?> parameter : parameters) {
        System.out.println("\tparameter: " + parameter.getSimpleName());
      }
      System.out.println("\tReturn Type: " + method.getReturnType() + "\n");

    }
  }

  public static void main(String[] args) {
    String[] path = new String[2];
    path[0] = "./test.java";
    path[1] = "../test1.java";

    Handle[] handleArr = loadFromFile(path);

    for (Handle curHandle : handleArr) { // iteration over the handle class
      System.out.println("\n******************************");
      DiscoverData(curHandle);
      callFunction(curHandle, "main");
    }
  }
}