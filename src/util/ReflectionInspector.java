package util;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Constructor;
public final class ReflectionInspector {
    private ReflectionInspector() {}
    public static void inspectClass(Class<?> clazz){
        System.out.println("=== Class Inspection:"+ clazz.getName() + " ===");
        Class<?> superClass = clazz.getSuperclass();
        if (superClass!=null){
            System.out.println("super class :" + superClass.getName());
        }
        System.out.println("Interfaces:");
        for (Class<?> inter : clazz.getInterfaces()){
            System.out.println("interface :" + inter.getName());
        }
        System.out.println("Fields:");
        for (Field field : clazz.getDeclaredFields()){
            System.out.println(" - " + Modifier.toString(field.getModifiers()) + " "
                    + field.getType().getSimpleName() + " " + field.getName());
        }
System.out.println("Methods:");
 for (Method method : clazz.getDeclaredMethods()){
     System.out.println(" - " + Modifier.toString(method.getModifiers()) +
             " - "+ method.getReturnType().getSimpleName() + " - " + method.getName() );

 }

  }
  public static Object getFieldValue(Object obj , String fieldName) throws  Exception{
        Class <?> clazz = obj.getClass();
        Field field = clazz.getDeclaredField(fieldName);
        field.setAccessible(true);
        return field.get(obj);
  }
  public static Object invokeMethod(Object obj , String methodName , Class <?>[] paramTypes, Object[] args)throws Exception{
        Class <?> clazz = obj.getClass();
        Method method = clazz.getDeclaredMethod(methodName, paramTypes);
        method.setAccessible(true);
        return method.invoke(obj, args);
  }

}
