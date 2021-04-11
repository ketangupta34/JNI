import java.io.File;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import java.lang.reflect.Method;
import java.lang.reflect.Field;
import java.lang.reflect.Constructor;
import java.lang.Object;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


class Handle{
  Class obj;

  Handle(Class obj){
    this.obj = obj;
  }

  public Class getHandle(){
    return obj;
  }
}

public class bootstrap{
  private static void runProcess(String command) throws Exception {
    Process pro = Runtime.getRuntime().exec(command);
    printLines(command + " stdout:", pro.getInputStream());
    printLines(command + " stderr:", pro.getErrorStream());
    pro.waitFor();
    System.out.println(command + " exitValue() " + pro.exitValue());
  }

  private static void printLines(String cmd, InputStream ins) throws Exception {
    String line = null;
    BufferedReader in = new BufferedReader(new InputStreamReader(ins));
    
    while ((line = in.readLine()) != null) {
      System.out.println(cmd + " " + line);
    }
  }

  public static Handle loadFromFile(String[] paths) { 
    // load all scripts and store them into a Handle class, then return it

    for(int i=0; i<paths.length; i++){
      System.out.println("Path provided"+ paths[i]);

      try {
        File f = new File(paths[i]);
        Path path  = Paths.get(f.getCanonicalPath());

        if(Files.exists(path)){
          System.out.println("File Resolved!");
          String cmd = "javac " + path.toAbsolutePath();
          runProcess(cmd);

          String classname = path.getFileName().toString().split(".java")[0];

          Class c = Class.forName(classname);

          return new Handle(c);
        }
        else{
          System.out.println("ERROR!");
        }
        
      }
      catch (Exception e) {
        System.err.println(e);
      }
    }

    return null;
  }

  public static void DiscoverData(Handle handle) { 
    // for each loaded .java file in the Handle list, get the DiscoverData, which is another class with the list of classes and methods etc
    Class hClass = handle.getHandle();

    System.out.println("ClassName: " + hClass.getName() + "\n");

    Method[] methods = hClass.getMethods();

    for (Method method : methods) {
      System.out.println("Name of the method: " + method.getName());

      Class[] parameters = method.getParameterTypes();

      for(Class parameter : parameters){
        System.out.println("\t parameter: " + parameter.getName());
      }
      System.out.println("\t Return Type: " + method.getReturnType() + "\n");

    }
  }

  public static void main(String[] args){
    String[] path = new String[1];
    path[0] = "./test.java";

    Handle handle = loadFromFile(path);

    DiscoverData(handle);
  }
}