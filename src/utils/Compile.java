package utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URI;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.tools.Diagnostic;
import javax.tools.DiagnosticCollector;
import javax.tools.FileObject;
import javax.tools.ForwardingJavaFileManager;
import javax.tools.JavaCompiler;
import javax.tools.JavaCompiler.CompilationTask;
import javax.tools.JavaFileObject;
import javax.tools.JavaFileObject.Kind;
import javax.tools.SimpleJavaFileObject;
import javax.tools.StandardJavaFileManager;
import javax.tools.ToolProvider;

/**
 * An example showing how to use the RuntimeCompiler utility class
 */
public class Compile
{
    // public static void main(String[] args) throws Exception
    // {
    //     simpleExample();
    //     twoClassExample();
    //     useLoadedClassExample();
    // }

    /**
     * Simple example: Shows how to add and compile a class, and then
     * invoke a static method on the loaded class.
     */
    private static void simpleExample()
    {
        String classNameA = "ExampleClass";
        String codeA =
            "public class ExampleClass {" + "\n" + 
            "    public static void exampleMethod(String name) {" + "\n" + 
            "        System.out.println(\"Hello, \"+name);" + "\n" + 
            "    }" + "\n" + 
            "}" + "\n";

        RuntimeCompiler r = new RuntimeCompiler();
        r.addClass(classNameA, codeA);
        r.compile();

        MethodInvocationUtils.invokeStaticMethod(
            r.getCompiledClass(classNameA), 
            "exampleMethod", "exampleParameter");
    }

    /**
     * An example showing how to add two classes (where one refers to the 
     * other), compile them, and invoke a static method on one of them
     */
    private static void twoClassExample()
    {
        String classNameA = "ExampleClassA";
        String codeA =
            "public class ExampleClassA {" + "\n" + 
            "    public static void exampleMethodA(String name) {" + "\n" + 
            "        System.out.println(\"Hello, \"+name);" + "\n" + 
            "    }" + "\n" + 
            "}" + "\n";

        String classNameB = "ExampleClassB";
        String codeB =
            "public class ExampleClassB {" + "\n" + 
            "    public static void exampleMethodB(String name) {" + "\n" + 
            "        System.out.println(\"Passing to other class\");" + "\n" + 
            "        ExampleClassA.exampleMethodA(name);" + "\n" + 
            "    }" + "\n" + 
            "}" + "\n";

        RuntimeCompiler r = new RuntimeCompiler();
        r.addClass(classNameA, codeA);
        r.addClass(classNameB, codeB);
        r.compile();

        MethodInvocationUtils.invokeStaticMethod(
            r.getCompiledClass(classNameB), 
            "exampleMethodB", "exampleParameter");
    }

    /**
     * An example that compiles and loads a class, and then uses an 
     * instance of this class
     */
    private static void useLoadedClassExample() throws Exception
    {
        String classNameA = "ExampleComparator";
        String codeA =
            "import java.util.Comparator;" + "\n" + 
            "public class ExampleComparator " + "\n" + 
            "    implements Comparator<Integer> {" + "\n" + 
            "    @Override" + "\n" + 
            "    public int compare(Integer i0, Integer i1) {" + "\n" + 
            "        System.out.println(i0+\" and \"+i1);" + "\n" + 
            "        return Integer.compare(i0, i1);" + "\n" + 
            "    }" + "\n" + 
            "}" + "\n";

        RuntimeCompiler r = new RuntimeCompiler();
        r.addClass(classNameA, codeA);
        r.compile();

        Class<?> c = r.getCompiledClass("ExampleComparator");
        Comparator<Integer> comparator = (Comparator<Integer>) c.newInstance();

        System.out.println("Sorting...");
        List<Integer> list = new ArrayList<Integer>(Arrays.asList(3,1,2));
        Collections.sort(list, comparator);
        System.out.println("Result: "+list);
    }

}

