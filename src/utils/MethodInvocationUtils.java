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
 * Utility methods not directly related to the RuntimeCompiler
 */
import java.lang.reflect.Field;

public class MethodInvocationUtils
{
    /**
     * Utility method to invoke the first static method in the given 
     * class that can accept the given parameters.
     *  
     * @param c The class
     * @param methodName The method name
     * @param args The arguments for the method call
     * @return The return value of the method call
     * @throws RuntimeException If either the class or a matching method
     * could not be found
     */
    public static String invokeStaticMethod(
        Class<?> c, String methodName, Object... args)
    {
        Method m = findFirstMatchingStaticMethod(c, methodName, args);
        if (m == null)
        {
            throw new RuntimeException("No matching method found");
        }
        try
        {
            // String return_str = m.invoke(null, args).toString();
            // m.invoke(null, args);
                return m.invoke(null, args).toString();
        // }
        // catch (IllegalAccessException e)
        // {
        //     throw new RuntimeException(e);
        // }
        // catch (IllegalArgumentException e)
        // {
        //     throw new RuntimeException(e);
        // }
        // catch (InvocationTargetException e)
        // {
        //     throw new RuntimeException(e);
        // }
        // catch (SecurityException e)
        // {
        //     throw new RuntimeException(e);
        }catch(Exception e){
            return e.getMessage();
        }
    }

    /**
     * Utility method to find the first static method in the given
     * class that has the given name and can accept the given 
     * arguments. Returns <code>null</code> if no such method 
     * can be found.
     * 
     * @param c The class
     * @param methodName The name of the method 
     * @param args The arguments
     * @return The first matching static method.
     */
    private static Method findFirstMatchingStaticMethod(
        Class<?> c, String methodName, Object ... args)
    {
        Method methods[] = c.getDeclaredMethods();
        for (Method m : methods)
        {
            if (m.getName().equals(methodName) &&
                Modifier.isStatic(m.getModifiers()))
            {
                Class<?>[] parameterTypes = m.getParameterTypes();
                if (areAssignable(parameterTypes, args))
                {
                    return m;
                }
            }
        }
        return null;
    }

    /**
     * Returns whether the given arguments are assignable to the
     * respective types
     * 
     * @param types The types
     * @param args The arguments
     * @return Whether the arguments are assignable
     */
    private static boolean areAssignable(Class<?> types[], Object ...args)
    {
        if (types.length != args.length)
        {
            return false;
        }
        for (int i=0; i<types.length; i++)
        {
            Object arg = args[i];
            Class<?> type = types[i];
            if (arg != null && !type.isAssignableFrom(arg.getClass()))
            {
                return false;
            }
        }
        return true;
    }

}